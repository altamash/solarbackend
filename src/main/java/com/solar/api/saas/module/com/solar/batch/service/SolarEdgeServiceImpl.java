package com.solar.api.saas.module.com.solar.batch.service;

import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.solar.api.AppConstants;
import com.solar.api.Constants.MONITOR_PLATFORM;
import com.solar.api.Constants.RATE_CODES;
import com.solar.api.Constants.SOLAR_EDGE;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.contract.SolarEdgeMapper;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.MeterEnergyDetailResponse;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.Meters;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.MetersValues;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.energy.SiteEnergyResponse;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.overview.Overview;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.overview.SiteOverviewResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.pvmonitor.SolarEdgeDaily;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import com.solar.api.tenant.repository.SolarEdgeDailyRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.Constants.E_GAUGE.THIRTY;

@Service
@Deprecated
public class SolarEdgeServiceImpl implements SolarEdgeService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
    @Autowired
    private StageMonitorService stageMonitorService;
    @Autowired
    MonitorReadingRepository monitorReadingRepository;
    @Autowired
    MonitorReadingDailyRepository monitorReadingDailyRepository;
    @Autowired
    private SolarEdgeDailyRepository solarEdgeDailyRepository;
    @Autowired
    private Utility utility;

    @Deprecated
    @Override
    public void saveDailyEdge(List<SolarEdgeDaily> solarEdgeDailyList) {
        if(solarEdgeDailyList != null) {
            solarEdgeDailyRepository.saveAll(solarEdgeDailyList);
        }
    }

    @Override
    public void saveMonitorReadings(List<MonitorReading> monitorReadings) {
        int rounding = utility.getCompanyPreference().getRounding();
        if(monitorReadings != null && monitorReadings.size() != 0) {
            monitorReadings.forEach(m -> {
//                m.setTime(Utility.fromGMT(m.getTime()));
                m.setCurrentValue(utility.round(m.getCurrentValue(), rounding));
                m.setCurrentValueRunning(utility.round(m.getCurrentValueRunning(), rounding));
                m.setYieldValue(utility.round(m.getYieldValue(), rounding));
                m.setYieldValueRunning(utility.round(m.getYieldValueRunning(), rounding));
                m.setGrossYield(utility.round(m.getGrossYield(), rounding));
                m.setPeakValue(utility.round(m.getPeakValue(), rounding));
            });
            monitorReadingRepository.saveAll(monitorReadings);
        }
    }
    @Deprecated
    @Override
    public ResponseEntity<Object> runDaily(Date date)  {
        try{
            String year = String.valueOf(Utility.toLocalDate(date).getYear());

            for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(MONITOR_PLATFORM.SOLAR_EDGE)) {

                String inverterNumber = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.S_PN);
                String siteId = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.SITE_ID);
                String token = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.S_AUTK);

                if(token != null && !token.isEmpty() || inverterNumber != null && !inverterNumber.isEmpty()) {

                    List<SolarEdgeDaily> solarEdgeData = getAnnualYield(siteId,inverterNumber,subscription.getSubsId(), date, year, token);
                    if (solarEdgeData.size() > 0) {
                        saveDailyEdge(getMonthlyAndGrossYield(solarEdgeData, siteId, date, token));
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Deprecated
    @Override
    public ResponseEntity<Object> runAtIntervals(Date date)  {
        try{

            for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(MONITOR_PLATFORM.SOLAR_EDGE)) {

                String inverterNumber = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.S_PN);
                String siteId = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.SITE_ID);
                String token = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.S_AUTK);

                if(token != null && !token.isEmpty() || inverterNumber != null && !inverterNumber.isEmpty()) {
                    List<MonitorReading> monitorReadings = performCalculations(siteId,
                            subscriptionLastRecord(subscription.getSubsId(), inverterNumber, siteId, date),
                            getTodayRecord(siteId, inverterNumber, subscription.getSubsId(), date), token);
                    saveMonitorReadings(monitorReadings);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    private ResponseEntity<Object> runAtIntervals(String fromDate, String toDate)  {
        try{

            for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(MONITOR_PLATFORM.SOLAR_EDGE)) {

                String inverterNumber = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.S_PN);
                String siteId = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.SITE_ID);
                String token = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.S_AUTK);

                if(token != null && !token.isEmpty() || inverterNumber != null && !inverterNumber.isEmpty()) {
                    LocalDate localDate =  LocalDate.parse(fromDate);
                    LocalDateTime startOfDay = localDate.atStartOfDay();
//                startOfDay = startOfDay.plusMinutes(THIRTY);
                    Date date = Utility.toDate(startOfDay);
                    if (toDate == null) {
                        toDate = dateFormat.format(new Date());
                    }
                    MonitorReading lastRecord = subscriptionLastRecord(subscription.getSubsId(), inverterNumber, siteId, date);
                    List<MonitorReading> monitorReadings = performCalculations(subscription.getSubsId(), siteId,
                            inverterNumber, fromDate, toDate, lastRecord, token);
                    saveMonitorReadings(monitorReadings);
                    saveOrUpdateSummary(subscription.getSubsId(), siteId, inverterNumber, fromDate, toDate, token);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Deprecated
    @Override
    public ResponseEntity<Object> runDailyHistoric(String fromDate, String toDate) {
        try {
            LocalDateTime startOfDay = Utility.toLocalDate(fromDate);
            LocalDateTime endOfDay = Utility.toLocalDate(toDate);

            LOGGER.info("--------------------------------------------------------------------------");
            LOGGER.info("Start Of Day: " +startOfDay);
            LOGGER.info("End Of Day:  "+endOfDay);
            LOGGER.info("--------------------------------------------------------------------------");

            startOfDay = startOfDay.plusDays(1);
            while(startOfDay.isBefore(endOfDay)){
                runDaily(Utility.toDate(startOfDay));
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Async
    @Override
    public ResponseEntity<Object> runAtIntervalsHistoricBetween(String fromDate, String toDate) {
        try {
            runAtIntervals(fromDate, toDate);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Async
    @Override
    public ResponseEntity<Object> getCurrentData() {
        try {
            for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(MONITOR_PLATFORM.SOLAR_EDGE)) {
                String inverterNumber = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.S_PN);
                String siteId = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.SITE_ID);
                String token = Utility.getMeasureAsJson(subscription.getMpJson(), RATE_CODES.S_AUTK);

                if (token != null && !token.isEmpty() || inverterNumber != null && !inverterNumber.isEmpty()) {
                    MonitorReading lr = subscriptionLastRecord(subscription.getSubsId(), inverterNumber, siteId, new Date());
                    Overview overview = getSiteOverviewBySiteId(siteId, token).getOverview();
                    if (overview == null) {
                        continue;
                    }
                    if (overview.getCurrentPower().getPower() == null) {
                        overview.getCurrentPower().setPower(lr.getCurrentValue());
                    }
                    double currentValue = (overview.getCurrentPower().getPower() / SOLAR_EDGE.THOUSAND);
                    if (currentValue > lr.getPeakValue()) {
                        overview.setPeakValue(currentValue);
                    } else {
                        overview.setPeakValue(lr.getPeakValue());
                    }
                    List<MonitorReadingDaily> list = monitorReadingDailyRepository.findBySubscriptionIdMongoAndSiteAndInverterNumberAndDay(
                            subscription.getSubsId(), siteId, inverterNumber, dateTimeFormat.parse(overview.getLastUpdateTime()));
                    double ans;
                    double todaysEnergy = overview.getLastDayData().getEnergy() / SOLAR_EDGE.THOUSAND;
                    MonitorReadingDaily readingDaily = null;
                    if (!list.isEmpty()) {
                        ans = todaysEnergy - list.get(0).getYieldValue();
                        readingDaily = list.get(0);
                    } else {
                        ans = todaysEnergy;
                    }
                    int rounding = utility.getCompanyPreference().getRounding();
                    MonitorReading monitorReading = MonitorReading.builder()
                            .subscriptionIdMongo(subscription.getSubsId())
                            .site(siteId)
                            .inverterNumber(inverterNumber)
                            .userId(lr.getUserId() != null ? lr.getUserId() : null)
                            .sytemSize(lr.getSytemSize())
                            .time(dateTimeFormat.parse(overview.getLastUpdateTime()))
                            .currentValue(utility.round(currentValue, rounding))
                            .currentValueRunning(utility.round(lr.getCurrentValueRunning() + currentValue, rounding))
                            .yieldValue(utility.round(currentValue, rounding))
                            .yieldValueRunning(utility.round(lr.getCurrentValueRunning() + currentValue, rounding))
//                    .monthlyYield(edgeDaily.getMonthlyYield()).subscriptionIdMongo(lr.getSubscriptionIdMongo())
//                    .annualYield(edgeDaily.getAnnualYield())
                            .grossYield(utility.round(lr.getGrossYield() + ans, rounding))
                            .peakValue(utility.round(overview.getPeakValue(), rounding))
//                    .dailyYield(Utility.subtract(grossYield, edgeDaily.getGrossYield())).durl("")
                            .currentValueToday(0.0)
                            .build();
                    monitorReadingRepository.save(monitorReading);

//                    saveOrUpdateSummary(subscription.getSubsId(), siteId, inverterNumber, dateString, dateString, token);
                    saveOrUpdateSummary(subscription.getSubsId(), siteId, inverterNumber, readingDaily, overview.getLastUpdateTime(), ans);
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Deprecated
    @Override
    public ResponseEntity<Object> runAtIntervalsHistoric(String date) {
        try {
            LocalDate localDate =  LocalDate.parse(date);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

            while(startOfDay.isBefore(endOfDay)){
                startOfDay = startOfDay.plusMinutes(THIRTY);
                runAtIntervals(Utility.toDate(startOfDay));
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Deprecated
    @Override
    public List<SolarEdgeDaily> getAnnualYield(String siteId, String inverterNumber, String subscriptionId, Date date, String year, String token) throws UnirestException {
        List<SolarEdgeDaily> edgeDailyList = new ArrayList<>();
        List<Meters> annualYieldMeterData = getAnnualYieldBySiteId(siteId, year, token).getMeterEnergyDetails().getMeters();
        if (annualYieldMeterData.size() != 0) {
            for (Meters meters : annualYieldMeterData) {

                if (meters.getConnectedSolaredgeDeviceSN().equalsIgnoreCase(inverterNumber)) {
                    BigDecimal firstMonth = BigDecimal.valueOf(meters.getValues().get(0).getValue());
                    BigDecimal lastMonth = BigDecimal.valueOf(meters.getValues().get(meters.getValues().size() - 1).getValue());

                    edgeDailyList.add(SolarEdgeDaily.builder().site(siteId).day(date).subscriptionId(subscriptionId).inverterNumber(inverterNumber)
                            .annualYield(Utility.subAndDiv(lastMonth, firstMonth, SOLAR_EDGE.THOUSAND)).build());
                }
            }
        }
        return edgeDailyList;
    }
    @Deprecated
    @Override
    public List<SolarEdgeDaily> getMonthlyAndGrossYield(List<SolarEdgeDaily> solarEdgeDailyList, String siteId, Date date, String token) throws UnirestException, ParseException {
        List<Meters> monthlyYieldData = getMonthlyYieldBySiteId(date, siteId, token).getMeterEnergyDetails().getMeters();
        if (monthlyYieldData.size() != 0) {
            for (SolarEdgeDaily solarDaily : solarEdgeDailyList) {
                for (Meters meters : monthlyYieldData) {

                    if(solarDaily.getInverterNumber().equalsIgnoreCase(meters.getConnectedSolaredgeDeviceSN())) {
                        BigDecimal firstDay = BigDecimal.valueOf(meters.getValues().get(0).getValue());
                        BigDecimal lastDay = BigDecimal.valueOf(meters.getValues().get(meters.getValues().size() - 1).getValue());
                        double monthlyYield = Utility.subAndDiv(lastDay, firstDay, SOLAR_EDGE.THOUSAND);
                        if(monthlyYield < 0){
                            monthlyYield = Utility.divide(BigDecimal.valueOf(Collections.max(meters.getValues().stream()
                                    .map(MetersValues::getValue).collect(Collectors.toList()))), SOLAR_EDGE.THOUSAND);
                        }

                        solarDaily.setMonthlyYield(monthlyYield);

                        Double grossYield = getLastGrossYield(siteId, solarDaily.getInverterNumber(), solarDaily.getSubscriptionId(), new Date());
                        solarDaily.setGrossYield(grossYield != null ? grossYield : 0.0);
                    }
                }
            }
        }
        return solarEdgeDailyList;
    }
    @Deprecated
    @Override
    public Double getLastGrossYield(String siteId, String inverterNumber, String subscriptionId, Date date)  {
        return solarEdgeDailyRepository.getLastGrossYield(siteId, inverterNumber,subscriptionId,
                Utility.getDateString(Utility.deductDays(date, 1), Utility.SYSTEM_DATE_FORMAT));
    }

    @Deprecated
    @Override
    public SolarEdgeDaily getTodayRecord(String siteId, String inverterNumber, String subscriptionId, Date date)  {
        SolarEdgeDaily edgeDaily = solarEdgeDailyRepository.findDailyRecord(siteId, inverterNumber,subscriptionId,
                Utility.getDateString(date, Utility.SYSTEM_DATE_FORMAT));
        if(edgeDaily == null){
            return solarEdgeDailyRepository.findDailyRecord(siteId, inverterNumber,subscriptionId,
                    Utility.getDateString(Utility.deductDays(date, 1), Utility.SYSTEM_DATE_FORMAT));
        }
        else return edgeDaily;
    }

    @Deprecated
    @Override
    public Double findMaxPeakValue(String subscriptionIdMongo) {
        Long peakValue = monitorReadingRepository.getMaxPeakValue(subscriptionIdMongo);
        if (peakValue != null) {
            return peakValue.doubleValue();
        } else return 0.00;
    }
    @Override
    public MonitorReading subscriptionLastRecord(String subscriptionId, String inverterNumber, String siteId, Date date) {
        Optional<MonitorReading> monitorReadingOptional = monitorReadingRepository.getLastRecord(subscriptionId, inverterNumber, siteId, date);
        if (!monitorReadingOptional.isPresent()) {
            return MonitorReading.builder().time(Utility.minusMinutes(Utility.getStartOfDate(date), THIRTY)).sytemSize(0.0).userId(null)
                    .grossYield(0.00).isLastRecord(false).inverterNumber(inverterNumber).subscriptionIdMongo(subscriptionId)
                    .currentValue(0.00).currentValueRunning(0.00).currentValueToday(0.00).dailyYield(0.00).monthlyYield(0.00)
                    .annualYield(0.00).yieldValueRunning(0.00).peakValue(0.00).build();
        } else {
            MonitorReading monitorReading = monitorReadingOptional.get();
            monitorReading.setLastRecord(true);
            if (monitorReading.getCurrentValue() == null) {
                monitorReading.setCurrentValue(0.00);
            }
            if (monitorReading.getCurrentValueRunning() == null) {
                monitorReading.setCurrentValueRunning(0.00);
            }
            if (monitorReading.getYieldValue() == null) {
                monitorReading.setYieldValue(0.00);
            }
            if (monitorReading.getYieldValueRunning() == null) {
                monitorReading.setYieldValueRunning(0.00);
            }
            if (monitorReading.getGrossYield() == null) {
                monitorReading.setGrossYield(0.00);
            }
            if (monitorReading.getPeakValue() == null) {
                monitorReading.setPeakValue(0.00);
            }
            return monitorReading;
        }
    }

    @Deprecated
    @Override
    public List<MonitorReading> performCalculations(String siteId, MonitorReading lr, SolarEdgeDaily edgeDaily, String token) throws UnirestException {
        List<MonitorReading> monitorReadings = new ArrayList<>();

        List<Meters> meterData = getMeterDataBySiteId(siteId, lr, token).getMeterEnergyDetails().getMeters();
        if (meterData.size() != 0) {
            BigDecimal lastCurrentValue = BigDecimal.valueOf(lr.getCurrentValue());

            for (Meters meters : meterData) {
                if (meters.getConnectedSolaredgeDeviceSN().equalsIgnoreCase(lr.getInverterNumber())) {

                    List<Double> currentValueList = meters.getValues().stream()
                            .sorted(Comparator.comparing(MetersValues::getDate))
                            .map(MetersValues::getValue).collect(Collectors.toList());

                    BigDecimal calculatedCurrentValue = Utility.getDifferenceOfIndexValues(currentValueList);
                    double currentValue = Utility.subAndDiv(calculatedCurrentValue, lastCurrentValue, SOLAR_EDGE.THOUSAND);
                    double grossYield = Utility.divide(BigDecimal.valueOf(currentValueList.get(currentValueList.size() -1)), SOLAR_EDGE.THOUSAND);
                    double peakValue = findMaxPeakValue(lr.getSubscriptionIdMongo());

                    MonitorReading monitorReading = MonitorReading.builder().annualYield(edgeDaily.getAnnualYield())
                            .monthlyYield(edgeDaily.getMonthlyYield()).subscriptionIdMongo(lr.getSubscriptionIdMongo())
                            .currentValue(currentValue).grossYield(grossYield).peakValue(peakValue).site(siteId)
                            .dailyYield(Utility.subtract(grossYield, edgeDaily.getGrossYield())).durl("")
                            .userId(lr.getUserId() != null ? lr.getUserId() : null).sytemSize(lr.getSytemSize())
                            .currentValueRunning(Double.sum(currentValue, lastCurrentValue.doubleValue()))
                            .time(Utility.addMinutes(lr.getTime(), THIRTY)).inverterNumber(lr.getInverterNumber())
                            .currentValueToday(0.0).build();

                    monitorReadings.add(monitorReading);
                }
            }
        }
        else{
            monitorReadings.add(MonitorReading.builder().annualYield(lr.getAnnualYield()).monthlyYield(lr.getMonthlyYield()).durl("")
                    .subscriptionIdMongo(lr.getSubscriptionIdMongo()).currentValue(lr.getCurrentValue()).grossYield(lr.getGrossYield())
                    .peakValue(lr.getPeakValue()).site(siteId).dailyYield(lr.getDailyYield()).userId(lr.getUserId() != null ? lr.getUserId() : null)
                    .time(!lr.isLastRecord() ? Utility.addMinutes(lr.getTime(), THIRTY) : Utility.toGMT(Utility.addMinutes(lr.getTime(), THIRTY)))
                    .sytemSize(lr.getSytemSize()).currentValueRunning(lr.getCurrentValueRunning()).inverterNumber(lr.getInverterNumber())
                    .currentValueToday(0.0).build());
        }
        return monitorReadings;
    }

    private List<MonitorReading> performCalculations(String subscriptionId, String siteId, String inverterNumber, String fromDate, String toDate, MonitorReading lr, String token) throws UnirestException, ParseException {
        List<MonitorReading> monitorReadings = new ArrayList<>();
//        List<DateRange> dateRanges = getDateRanges(fromDate, toDate);
        List<MetersValues> meterData = new ArrayList<>();
        /*for (DateRange dateRange : dateRanges) {
            SiteEnergyResponse data = getHistoricYieldBySiteId(siteId, dateFormat.format(dateRange.getFromDate()),
                    dateFormat.format(dateRange.getToDate()), "QUARTER_OF_AN_HOUR", token);
            if (data.getEnergy() != null) {
                meterData.addAll(getHistoricYieldBySiteId(siteId, dateFormat.format(dateRange.getFromDate()),
                        dateFormat.format(dateRange.getToDate()), "QUARTER_OF_AN_HOUR", token).getEnergy().getValues());
            }
        }*/
        SiteEnergyResponse data = getHistoricYieldBySiteId(siteId, fromDate, toDate, "QUARTER_OF_AN_HOUR", token);
        if (data.getEnergy() != null) {
            meterData.addAll(data.getEnergy().getValues());
        }
        if (meterData.size() != 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
            LocalDateTime dateTime = Utility.getUTCForZone(meterData.get(0).getDate(), formatter,
                    AppConstants.EZone.US_CENTRAL.getName());
            if (meterData.get(0).getValue() == null) {
                meterData.get(0).setValue(lr.getCurrentValue());
            }
            double currentValue = (meterData.get(0).getValue() / SOLAR_EDGE.THOUSAND) * 4;
            meterData.get(0).setPeakValue(currentValue);
//            meterData.get(0).setGrossYield(lr.getGrossYield() + (meterData.get(0).getValue() / SOLAR_EDGE.THOUSAND)); // TODO: already divided in wrapper service
            meterData.get(0).setGrossYield(lr.getGrossYield() + meterData.get(0).getValue());
            meterData.get(0).setCurrentValueRunning(lr.getCurrentValueRunning() + currentValue);
            MonitorReading monitorReading = MonitorReading.builder()
                    .subscriptionIdMongo(subscriptionId)
                    .site(siteId)
                    .inverterNumber(inverterNumber)
                    .userId(lr.getUserId() != null ? lr.getUserId() : null)
                    .sytemSize(lr.getSytemSize()) // TODO: check
                    .time(Utility.localDateTimeToDate(dateTime))
                    .currentValue(currentValue)
                    .currentValueRunning(meterData.get(0).getCurrentValueRunning())
                    .yieldValue(currentValue)
                    .yieldValueRunning(meterData.get(0).getCurrentValueRunning())
//                    .monthlyYield(edgeDaily.getMonthlyYield()).subscriptionIdMongo(lr.getSubscriptionIdMongo())
//                    .annualYield(edgeDaily.getAnnualYield())
                    .grossYield(meterData.get(0).getGrossYield())
                    .peakValue(meterData.get(0).getPeakValue())
//                    .dailyYield(Utility.subtract(grossYield, edgeDaily.getGrossYield())).durl("")
                    .currentValueToday(0.0)
                    .build();

            monitorReadings.add(monitorReading);

            if (meterData.size() > 1) {
                for (int i = 1; i < meterData.size(); i++) {
                    dateTime = Utility.getUTCForZone(meterData.get(i).getDate(), formatter,
                            AppConstants.EZone.US_CENTRAL.getName());
                    if (meterData.get(i).getValue() == null) {
                        meterData.get(i).setValue(meterData.get(i - 1).getValue());
                    }
                    currentValue = (meterData.get(i).getValue() / SOLAR_EDGE.THOUSAND) * 4;
                    if (currentValue > meterData.get(i - 1).getPeakValue()) {
                        meterData.get(i).setPeakValue(currentValue);
                    } else {
                        meterData.get(i).setPeakValue(meterData.get(i - 1).getPeakValue());
                    }
//                    meterData.get(i).setGrossYield(meterData.get(i - 1).getGrossYield() + (meterData.get(i).getValue() / SOLAR_EDGE.THOUSAND));
                    meterData.get(i).setGrossYield(meterData.get(i - 1).getGrossYield() + meterData.get(i).getValue());
                    meterData.get(i).setCurrentValueRunning(meterData.get(i - 1).getCurrentValueRunning() + currentValue);
                    monitorReading = MonitorReading.builder()
                            .subscriptionIdMongo(subscriptionId)
                            .site(siteId)
                            .inverterNumber(inverterNumber)
                            .userId(lr.getUserId() != null ? lr.getUserId() : null).sytemSize(lr.getSytemSize())
                            .time(Utility.localDateTimeToDate(dateTime))
                            .currentValue(currentValue)
                            .currentValueRunning(meterData.get(i).getCurrentValueRunning())
                            .yieldValue(currentValue)
                            .yieldValueRunning(meterData.get(i).getCurrentValueRunning())
//                            .monthlyYield(edgeDaily.getMonthlyYield()).subscriptionIdMongo(lr.getSubscriptionIdMongo())
//                            .annualYield(edgeDaily.getAnnualYield())
                            .grossYield(meterData.get(i).getGrossYield())
                            .peakValue(meterData.get(i).getPeakValue())
//                    .dailyYield(Utility.subtract(grossYield, edgeDaily.getGrossYield())).durl("")
                            .currentValueToday(0.0)
                            .build();

                    monitorReadings.add(monitorReading);
//            }
                }
            }
            getDailyMonthlyYearlyValues(monitorReadings, siteId, fromDate, toDate, token);
        }
        return monitorReadings;
    }

    private void getDailyMonthlyYearlyValues(List<MonitorReading> monitorReadings, String siteId, String fromDate,
                                             String toDate, String token) {
        List<MetersValues> daysYields = Collections.EMPTY_LIST;
        List<MetersValues> monthsYields =  Collections.EMPTY_LIST;
        List<MetersValues> yearsYields =  Collections.EMPTY_LIST;
        try {
            daysYields = getHistoricYieldBySiteId(siteId, fromDate, toDate, "DAY", token).getEnergy().getValues();
            monthsYields = getHistoricYieldBySiteId(siteId, fromDate, toDate, "MONTH", token).getEnergy().getValues();
            yearsYields = getHistoricYieldBySiteId(siteId, fromDate, toDate, "YEAR", token).getEnergy().getValues();
        } catch (UnirestException e) {
            LOGGER.error(e.getMessage(), e);
        }
        Map<String, Double> daysYieldsMap = new HashMap<>();
        Map<String, Double> monthsYieldsMap = new HashMap<>();
        Map<String, Double> yearsYieldsMap = new HashMap<>();
        int rounding = utility.getCompanyPreference().getRounding();
        for (MetersValues metersValues : daysYields) {
            daysYieldsMap.put(metersValues.getDate().split(" ")[0], utility.round(metersValues.getValue() / SOLAR_EDGE.THOUSAND, rounding));
        }
        for (MetersValues metersValues : monthsYields) {
            monthsYieldsMap.put(metersValues.getDate().split(" ")[0], utility.round(metersValues.getValue() / SOLAR_EDGE.THOUSAND, rounding));
        }
        for (MetersValues metersValues : yearsYields) {
            yearsYieldsMap.put(metersValues.getDate().split(" ")[0], utility.round(metersValues.getValue() / SOLAR_EDGE.THOUSAND, rounding));
        }
        monitorReadings.forEach(reading -> {
            reading.setDailyYield(daysYieldsMap.get(dateFormat.format(reading.getTime())));
            reading.setMonthlyYield(monthsYieldsMap.get(dateFormat.format(utility.getStartOfMonth(reading.getTime()))));
            reading.setAnnualYield(yearsYieldsMap.get(dateFormat.format(utility.getStartOfYear(reading.getTime()))));
        });
    }

    private void saveOrUpdateSummary(String subscriptionId, String siteId, String inverterNumber, String fromDateString, String toDateString, String token) {
        List<MonitorReadingDaily> readingDailyList = new ArrayList<>();
        List<MonitorReadingDaily> readingDailyListToUpdate = new ArrayList<>();
        List<MetersValues> meterData = new ArrayList<>();
        try {
            SiteEnergyResponse data = getHistoricYieldBySiteId(siteId, fromDateString, toDateString, "DAY", token);
            if (data.getEnergy() != null) {
                meterData.addAll(getHistoricYieldBySiteId(siteId, fromDateString, toDateString, "DAY", token).getEnergy().getValues());
            }
        } catch (UnirestException e) {
            LOGGER.error(e.getMessage(), e);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
        int rounding = utility.getCompanyPreference().getRounding();
        meterData.forEach(data ->
        {
            LocalDateTime dateTime = Utility.getUTCForZone(data.getDate(), formatter,
                    AppConstants.EZone.US_CENTRAL.getName());
            List<MonitorReadingDaily> list = monitorReadingDailyRepository.findBySubscriptionIdMongoAndSiteAndInverterNumberAndDay(
                    subscriptionId, siteId, inverterNumber, Utility.localDateTimeToDate(dateTime));
            Date date = Utility.localDateTimeToDate(dateTime);
            Double peakValue = monitorReadingRepository.getPeakValueForDate(date, subscriptionId, siteId);
            if (!list.isEmpty()) {
                MonitorReadingDaily readingDaily = list.get(0);
                readingDaily.setSubscriptionIdMongo(subscriptionId);
                readingDaily.setSite(siteId);
                readingDaily.setInverterNumber(inverterNumber);
                readingDaily.setCurrentValue(utility.round(data.getValue() / SOLAR_EDGE.THOUSAND, rounding));
                readingDaily.setYieldValue(utility.round(data.getValue() / SOLAR_EDGE.THOUSAND, rounding));
                readingDaily.setPeakValue(peakValue);
                readingDailyListToUpdate.add(readingDaily);
            } else {
                readingDailyList.add(MonitorReadingDaily.builder()
                        //                    .userId()
                        .subscriptionIdMongo(subscriptionId)
                        .site(siteId)
                        .inverterNumber(inverterNumber)
                        .currentValue(utility.round((data.getValue() == null ? 0.0 : data.getValue()) / SOLAR_EDGE.THOUSAND, rounding))
                        .yieldValue(utility.round((data.getValue() == null ? 0.0 : data.getValue()) / SOLAR_EDGE.THOUSAND, rounding))
                        .peakValue(peakValue)
                        .day(date)
                        .build());
            }
        });
        monitorReadingDailyRepository.saveAll(readingDailyList);
        monitorReadingDailyRepository.saveAll(readingDailyListToUpdate);
    }

    private void saveOrUpdateSummary(String subscriptionId, String siteId, String inverterNumber, MonitorReadingDaily readingDaily, String dateString, double value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
        LocalDateTime dateTime = Utility.getUTCForZone(dateString, formatter,
                AppConstants.EZone.US_CENTRAL.getName());
        Date date = Utility.localDateTimeToDate(dateTime);
        Double peakValue = monitorReadingRepository.getPeakValueForDate(date, subscriptionId, siteId);
        int rounding = utility.getCompanyPreference().getRounding();
        peakValue = utility.round(peakValue, rounding);
        value = utility.round(value, rounding);
        if (readingDaily != null) {
            readingDaily.setSubscriptionIdMongo(subscriptionId);
            readingDaily.setSite(siteId);
            readingDaily.setInverterNumber(inverterNumber);
            readingDaily.setCurrentValue(value);
            readingDaily.setYieldValue(value);
            readingDaily.setPeakValue(peakValue);
        } else {
            readingDaily = MonitorReadingDaily.builder()
                    //                    .userId()
                    .subscriptionIdMongo(subscriptionId)
                    .site(siteId)
                    .inverterNumber(inverterNumber)
                    .currentValue(value)
                    .yieldValue(value)
                    .peakValue(peakValue)
                    .day(date)
                    .build();
        }
        monitorReadingDailyRepository.save(readingDaily);
    }

    @Deprecated
    @Override
    public MeterEnergyDetailResponse getMeterDataBySiteId(String siteId, MonitorReading lastRecord, String token) throws UnirestException {
        return new Gson().fromJson(SolarEdgeMapper.getMeterDataURL(token, siteId, lastRecord.getTime()).getBody(), MeterEnergyDetailResponse.class);
    }
    @Deprecated
    @Override
    public MeterEnergyDetailResponse getMonthlyYieldBySiteId(Date date, String siteId, String token) throws UnirestException, ParseException {
        return new Gson().fromJson(SolarEdgeMapper.getMonthlyYieldURL(date, token, siteId).getBody(), MeterEnergyDetailResponse.class);
    }
    @Deprecated
    @Override
    public MeterEnergyDetailResponse getAnnualYieldBySiteId(String siteId, String year, String token) throws UnirestException {
        return new Gson().fromJson(SolarEdgeMapper.getAnnualYieldURL(token, siteId, year).getBody(), MeterEnergyDetailResponse.class);
    }

    @Override
    public SiteEnergyResponse getHistoricYieldBySiteId(String siteId, String fromDate, String toDate, String timeUnit, String token) throws UnirestException {
        return new Gson().fromJson(SolarEdgeMapper.getHistoricYieldURL(fromDate, toDate, token, siteId, timeUnit).getBody(), SiteEnergyResponse.class);
    }

    @Override
    public SiteOverviewResponse getSiteOverviewBySiteId(String siteId, String token) throws UnirestException, ParseException {
        return new Gson().fromJson(SolarEdgeMapper.getSiteOverviewURL(token, siteId).getBody(), SiteOverviewResponse.class);
    }
}
