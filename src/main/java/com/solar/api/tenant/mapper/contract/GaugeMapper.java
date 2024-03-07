package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.ApplicationLog;
import com.solar.api.saas.module.com.solar.scheduler.service.JobSchedulerServiceImpl;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingDTO;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingMapper;
import com.solar.api.tenant.model.AnalyticalCalculationArchive;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.RecordedData;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.service.AnalyticalCalculationService;
import com.solar.api.tenant.service.process.pvmonitor.MonitorAPI;
import com.solar.api.tenant.service.process.pvmonitor.MonitorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.solar.api.Constants.E_GAUGE.*;

public class GaugeMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(GaugeMapper.class);

    public static RecordedData toRecords(NodeList nodes) {
        if (nodes.getLength() == 0) {
            return null;
        }
        return RecordedData.builder()
                .min30(new BigDecimal(nodes.item(0).getTextContent()))
                .min25(new BigDecimal(nodes.item(1).getTextContent()))
                .min20(new BigDecimal(nodes.item(2).getTextContent()))
                .min15(new BigDecimal(nodes.item(3).getTextContent()))
                .min10(new BigDecimal(nodes.item(4).getTextContent()))
                .min5(new BigDecimal(nodes.item(5).getTextContent()))
                .min0(new BigDecimal(nodes.item(6).getTextContent()))
                .startOfDay(new BigDecimal(nodes.item(7).getTextContent()))
                .startOfMonth(new BigDecimal(nodes.item(8).getTextContent()))
                .startOfYear(new BigDecimal(nodes.item(9).getTextContent()))
                .build();
    }

    /**
     * @param r (XML fetched data)
     * @param l (Last record on solarAMPs)
     * @return
     */
    public static List<MonitorReading> setMonitorReadings(RecordedData r, MonitorReading l, String subscriptionId) {
        List<MonitorReadingDTO> monitorReadings = new ArrayList<>();
        /**
         * Committing following (m0) line, because it is fetching last entry from SQL instead of XML
         */
//        MonitorReading m0 = performCalculations(r, r.getMin0(), BigDecimal.valueOf(l.getCurrentValue()), l.getCurrentValue(), ZERO);
        /**
         * Adding following (m1) line to fetch data from XML at 0th node
         */
        MonitorReadingDTO m1 = performCalculations(r, r.getMin5(), r.getMin0(), l.getCurrentValue(), FIVE, r.getMin5());
        MonitorReadingDTO m2 = performCalculations(r, r.getMin10(), r.getMin5(), m1.getCurrentValue(), TEN, r.getMin10());
        MonitorReadingDTO m3 = performCalculations(r, r.getMin15(), r.getMin10(), m2.getCurrentValue(), FIFTEEN, r.getMin15());
        MonitorReadingDTO m4 = performCalculations(r, r.getMin20(), r.getMin15(), m3.getCurrentValue(), TWENTY, r.getMin20());
        MonitorReadingDTO m5 = performCalculations(r, r.getMin25(), r.getMin20(), m4.getCurrentValue(), TWENTY_FIVE, r.getMin25());
        MonitorReadingDTO m6 = performCalculations(r, r.getMin30(), r.getMin25(), m5.getCurrentValue(), THIRTY, r.getMin30());
        monitorReadings.add(m1);
        monitorReadings.add(m2);
        monitorReadings.add(m3);
        monitorReadings.add(m4);
        monitorReadings.add(m5);
        monitorReadings.add(m6);
        monitorReadings.forEach(m -> {
            m.setTime(Utility.fromGMT(m.getTime()));
        });
        monitorReadings = MonitorUtils.checkAndUpdateExisting(monitorReadings, subscriptionId);
        return MonitorReadingMapper.toMonitorReading(monitorReadings);
    }

    public static MonitorReading toLastSubscriptionMapping(CustomerSubscription customerSubscription, MonitorReading lastRecord) {
        return MonitorReading.builder().currentValue(lastRecord != null ? lastRecord.getCurrentValue() : 0.00)
                .subscriptionIdMongo(String.valueOf(customerSubscription.getId())).userId(customerSubscription.getUserAccountId())
                .time(lastRecord != null ? lastRecord.getTime() : Utility.getStartOfDate(new Date())).build();
    }

    public static MonitorReading toLastSubscriptionMapping(String subscriptionId, MonitorReading lastRecord, Date time) {
        return MonitorReading.builder().currentValue(lastRecord != null ? lastRecord.getCurrentValue() : 0.00)
                .subscriptionIdMongo(subscriptionId).userId(lastRecord.getUserId()).time(time).build();
    }

    /**
     * If cr == 5th minute, then lastCurrentValue is last value from SQL DB of SolarAMPs
     *
     * @param r                (XML data)
     * @param cr               (XML get minute data from XML) i.e 10
     * @param lr               (XML get last minute data from XML) i.e 5
     * @param lastCurrentValue (Last entry in current_value column in SQL.monitor_reading table of SolarAMPs)
     * @param min              (current interval) i.e, 5, 10, 15, 20..
     * @return
     */
    private static MonitorReadingDTO performCalculations(RecordedData r, BigDecimal cr, BigDecimal lr,
                                                      Double lastCurrentValue, Integer min, BigDecimal rawYield) {
        int CURRENT_VALUE = 300000, DAILY_YIELD = 3600000, MONTHLY_YIELD = 3600000, YEARLY_YIELD = 3600000, ANNUAL_YIELD = 3600000;
        double cvDouble = Utility.subAndDiv(cr, lr, CURRENT_VALUE);
        double cvRunning = Double.sum(cvDouble, lastCurrentValue);
        String startOfDay = String.valueOf(r.getStartOfDay());
        String startOfYear = String.valueOf(r.getStartOfYear());
        Double dailyYield = Utility.subAndDiv(cr, r.getStartOfDay(), DAILY_YIELD);
        Double annualYield = Utility.subAndDiv(cr, r.getStartOfYear(), ANNUAL_YIELD);
        String inverterNumber = r.getInverterNumber();
        Double monthlyYield = Utility.subAndDiv(cr, r.getStartOfMonth(), MONTHLY_YIELD);
        Date time = Utility.addMinutes(r.getLastTime(), min);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("startOfDay", startOfDay);
        requestMessage.put("startOfYear", startOfYear);
        requestMessage.put("currentRecord", cr);
        requestMessage.put("lastRecord", lr);
        requestMessage.put("lastCurrentValue", lastCurrentValue);
        requestMessage.put("dailyYield", dailyYield);
        requestMessage.put("annualYield", annualYield);
        requestMessage.put("monthlyYield", monthlyYield);
        requestMessage.put("rawYield", rawYield);
        requestMessage.put("inverterNumber", inverterNumber);
        requestMessage.put("minute", min);
        requestMessage.put("URL", r.getDurl());
        requestMessage.put("time", String.valueOf(time));
        return MonitorReadingDTO.builder()
                .currentValue(cvDouble < 0 ? 0 : cvDouble)
                .currentValueRunning(cvRunning < 0 ? 0 : cvRunning)
                .currentValueToday(0.00)
                .dailyYield(dailyYield < 0 ? 0 : dailyYield)
                .inverterNumber(inverterNumber)
                .site(null)
                .monthlyYield(monthlyYield)
                .sytemSize(0.00)
                .time(time)
                .annualYield(annualYield)
                .yieldValue(cvDouble < 0 ? 0 : cvDouble)
                .yieldValueRunning(cvRunning < 0 ? 0 : cvRunning)
                .subscriptionIdMongo(r.getSubscriptionId())
                .userId(r.getUserId())
                .peakValue(r.getPeakValue())
                .durl(r.getDurl())
                .grossYield(Utility.subAndDiv(cr, BigDecimal.ZERO, YEARLY_YIELD))
                .rawYield(Double.valueOf(String.valueOf(rawYield)))
                .logs(requestMessage.toPrettyString()).build();
    }

    public static String parseHistoricUrl(String URL, MonitorReading l, Date date, String subStartDate) throws ParseException {
        if (l.getTime() == null) {
            l.setTime(date);
        }
        return URL.concat(URL_PARAM)
                .concat(toMinHistoric(l, THIRTY)).concat(SEPARATOR)
                .concat(toMinHistoric(l, TWENTY_FIVE)).concat(SEPARATOR)
                .concat(toMinHistoric(l, TWENTY)).concat(SEPARATOR)
                .concat(toMinHistoric(l, FIFTEEN)).concat(SEPARATOR)
                .concat(toMinHistoric(l, TEN)).concat(SEPARATOR)
                .concat(toMinHistoric(l, FIVE)).concat(SEPARATOR)
                .concat(toMinHistoric(l, ZERO)).concat(SEPARATOR)
                .concat(getDay(subStartDate, l.getTime())).concat(SEPARATOR)
                .concat(getMonth(subStartDate, l.getTime())).concat(SEPARATOR)
                .concat(getYear(subStartDate, l.getTime()));
    }

    public static String parseUrl(String URL, MonitorReading l, String subStartDate) {
        LocalDateTime thirtiethMinute = getThirtiethMinute(l, THIRTY);
        if (!Utility.isBeforeOrEqual(new Date(), Utility.toDate(thirtiethMinute))) {
            LOGGER.info("lastRecordTime: {}", l.getTime());
            /**
             *
             * If SSDT's year is less than current year, set SSDT as start of year
             */
            return URL.concat(URL_PARAM)
                    .concat(String.valueOf(thirtiethMinute.toEpochSecond(ZoneOffset.UTC))).concat(SEPARATOR)
                    .concat(toMin(l, TWENTY_FIVE)).concat(SEPARATOR)
                    .concat(toMin(l, TWENTY)).concat(SEPARATOR)
                    .concat(toMin(l, FIFTEEN)).concat(SEPARATOR)
                    .concat(toMin(l, TEN)).concat(SEPARATOR)
                    .concat(toMin(l, FIVE)).concat(SEPARATOR)
                    .concat(toMin(l, ZERO)).concat(SEPARATOR)
                    .concat(getDay(subStartDate, l.getTime())).concat(SEPARATOR)
                    .concat(getMonth(subStartDate, l.getTime())).concat(SEPARATOR)
                    .concat(getYear(subStartDate, l.getTime()));
        } else {
            LOGGER.info("lastRecordTime {} is greater than current time {}"
                    , l.getTime(), new Date());
            return null;
        }
    }

    private static LocalDateTime getThirtiethMinute(MonitorReading l, Integer thirty) {
        String newDate = Utility.getZoneFormattedTime(l != null ? l.getTime()
                : new Date(), GMT, Utility.SYSTEM_DATE_TIME_FORMAT);
        Date dateTime = Utility.getDate(newDate, Utility.SYSTEM_DATE_TIME_FORMAT);
        return LocalDateTime.ofInstant(dateTime.toInstant(),
                ZoneId.systemDefault()).plusMinutes(thirty);
    }

    /**
     * Check if SSDT day is before current day
     *
     * @param subStartDate
     * @param time
     * @return
     */
    private static String getDay(String subStartDate, Date time) {
        Date startOfDay = Utility.getStartOfDate(new Date(time.getTime()));
        if (Utility.isBefore(new Date(subStartDate), startOfDay)) {
            return String.valueOf(startOfDay.toInstant().getEpochSecond());
        } else {
            return String.valueOf(new Date(subStartDate).toInstant().getEpochSecond());
        }
    }

    /**
     * Check if SSDT month is before current month
     *
     * @param subStartDate
     * @param time
     * @return
     */
    private static String getMonth(String subStartDate, Date time) {
        Date startOfMonth = Utility.getStartOfMonth(new Date(time.getTime()));
        if (Utility.isBefore(new Date(subStartDate), startOfMonth)) {
            return String.valueOf(startOfMonth.toInstant().getEpochSecond());
        } else {
            return String.valueOf(new Date(subStartDate).toInstant().getEpochSecond());
        }
    }

    /**
     * Check if SSDT year is before current year
     *
     * @param subStartDate
     * @param time
     * @return
     */
    private static String getYear(String subStartDate, Date time) {
        Date startOfYear = Utility.getStartOfYear(new Date(time.getTime()));
        if (Utility.isBefore(new Date(subStartDate), startOfYear)) {
            return String.valueOf(startOfYear.toInstant().getEpochSecond());
        } else {
            return String.valueOf(new Date(subStartDate).toInstant().getEpochSecond());
        }
    }

    public static String toMin(MonitorReading lastRecord, Integer min) {
        String newDate = Utility.getZoneFormattedTime(lastRecord != null ? lastRecord.getTime()
                : new Date(), GMT, Utility.SYSTEM_DATE_TIME_FORMAT);
        Date dateTime = Utility.getDate(newDate, Utility.SYSTEM_DATE_TIME_FORMAT);
        return String.valueOf(Utility.addMinstoEpoc(Objects.requireNonNull(dateTime), min));
    }

    public static String toMinHistoric(MonitorReading lastRecord, Integer min) {
        return String.valueOf(Utility.addMinstoEpoc(lastRecord.getTime(), min));
    }

    public static String toStartOfDay(Date startOfDay) {
        return String.valueOf(Utility.getStartOfDate(startOfDay).toInstant().getEpochSecond());
    }

    public static String toStartOfDay() {
        return String.valueOf(Utility.getStartOfDate(new Date()).toInstant().getEpochSecond());
    }

    public static String toStartOfMonth() throws ParseException {
        return String.valueOf(Utility.getStartOfMonth(new Date()).toInstant().getEpochSecond());
    }

    public static String toStartOfMonth(Date toStartOfMonth) throws ParseException {
        return String.valueOf(Utility.getStartOfMonth(toStartOfMonth).toInstant().getEpochSecond());
    }

    public static String toStartOfYear() throws ParseException {
        return String.valueOf(Utility.getStartOfYear(new Date()).toInstant().getEpochSecond());
    }


}
