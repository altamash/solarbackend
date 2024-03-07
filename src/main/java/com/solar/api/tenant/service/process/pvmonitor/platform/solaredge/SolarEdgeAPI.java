package com.solar.api.tenant.service.process.pvmonitor.platform.solaredge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.batch.service.MonitorPlatformUtilityService;
import com.solar.api.tenant.mapper.contract.SolarEdgeMapper;
import com.solar.api.tenant.mapper.pvmonitor.*;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.MetersValues;
import com.solar.api.tenant.mapper.pvmonitor.solaredge.energy.SiteEnergyResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDailyDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import com.solar.api.tenant.service.process.pvmonitor.MonitorAPI;
import com.solar.api.tenant.service.process.pvmonitor.MonitorReadingHistoricDates;
import com.solar.api.tenant.service.process.pvmonitor.MonitorUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.Constants.E_GAUGE.THIRTY;

@Service
public class SolarEdgeAPI implements MonitorAPI {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
    @Autowired
    private Utility utility;
    @Autowired
    private MonitorReadingRepository readingRepository;
    @Autowired
    MonitorReadingRepository monitorReadingRepository;
    @Autowired
    MonitorReadingDailyRepository monitorReadingDailyRepository;
    @Autowired
    private MonitorPlatformUtilityService monitorPlatformUtilityService;
    @Autowired
    private MonitorReadingHistoricDates historicDates;

    private SimpleDateFormat formatDateTimeSystem = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_FORMAT);
    private SimpleDateFormat formatDate = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
    private SimpleDateFormat format = new SimpleDateFormat(Utility.MONTH_DATE_YEAR_FORMAT);
    private SimpleDateFormat formatPacDateTime = new SimpleDateFormat(Utility.SYSTEM_DATE_TIME_SIMPLE_FORMAT);
    private SimpleDateFormat formatYearMonthDt = new SimpleDateFormat(Utility.YEAR_MONTH_DATE_FORMAT);

    @Override
    public MonitorAPIAuthResponse getAuthData(Object... params) throws NoSuchAlgorithmException {
        return null;
    }

    @Override
    public MonitorAPIResponse getCurrentData(CustomerSubscription cs, Object... params) throws NoSuchAlgorithmException, JsonProcessingException, UnsupportedEncodingException, ParseException {
        return null;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForCsComparison(MonitorAPIAuthBody body, CustomerSubscription cs) throws ParseException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(Long userId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<MonitorReading> getMonitorReadingDataForUserComparison(String projectId, MonitorAPIAuthBody body, List<CustomerSubscription> customerSubscriptionList) throws ParseException {
        return null;
    }

    // Important
    @Override
    public List<MonitorReading> getMonitorReadingDataForMongoComparison(MonitorAPIAuthBody body, String subsId) throws ParseException {
        List<Date> labelDates = getDateTimes(body.getTime());
        List<MonitorReading> daysData = readingRepository.findBySubscriptionIdMongoAndTimeIn(subsId, labelDates);
        return daysData;
    }

    @Override
    public boolean savesBulk() {
        return true;
    }


    private List<Date> getDateTimes(String time) throws ParseException {
        int LINE_GRAPH_INCREMENT = 30;
        String dateTimeString = time != null ? time : formatDateTimeSystem.format(new Date());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
        Date date;
        date = Date.from(ZonedDateTime.of(LocalDateTime.parse(dateTimeString, formatter), ZoneId.of(AppConstants.EZone.US_CENTRAL.getName())).toInstant());
        List<Date> dateTimes = new ArrayList<>();
        for (int i = 0; i < 1440 / LINE_GRAPH_INCREMENT; i++) {
            dateTimes.add(date);
            date = Utility.addMinutes(date, LINE_GRAPH_INCREMENT);
        }
        return dateTimes;
    }

    // Important
    @Override
    public MonitorAPIResponse getCurrentData(ExtDataStageDefinition ext, Object... params)
            throws NoSuchAlgorithmException, ParseException {
        MonitorAPIAuthBody authBody = (MonitorAPIAuthBody) params[0];

        String ssdt = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SUB_START_DATE);
        MonitorReading lastRec = readingRepository.getLastRecord(ext.getSubsId());
        List<String> dates = getAdjustedDateRange(ext.getSubsId(), authBody.getFromDateTime(), authBody.getToDateTime(),
                ZoneId.of(AppConstants.EZone.US_CENTRAL.getName()), ssdt, lastRec, formatDateTimeSystem,
                15, true, false);
        authBody.setFromDateTime(dates.get(0));
        authBody.setToDateTime(dates.get(1));
        try {
            return getDataBetween(ext, dates.get(0), dates.get(1));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return MonitorAPIResponse.builder()
                    .monitorReadingDTOs(Collections.EMPTY_LIST)
                    .bulkDailyRecords(Collections.EMPTY_LIST)
                    .build();
        }
    }

    private MonitorAPIResponse getDataBetween(ExtDataStageDefinition ext, String fromDateTime, String toDateTime) {
        try {
            String inverterNumber = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
            String siteId = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SITE_ID);
            String token = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.S_AUTK);

            ResponseStatus responseStatus = ResponseStatus.builder().success(Boolean.TRUE).build();
            MonitorAPIResponse response = MonitorAPIResponse.builder().build();
            if (token != null && !token.isEmpty() || inverterNumber != null && !inverterNumber.isEmpty()) {
                MonitorReading lastRecord = subscriptionLastRecord(ext.getSubsId(), inverterNumber, siteId,
                        Utility.toDate(LocalDateTime.parse(fromDateTime, dateTimeFormatter)));
                List<MonitorReadingDTO> monitorReadingDTOs = performCalculations(responseStatus, ext, siteId,
                        inverterNumber, fromDateTime, toDateTime, lastRecord, token);
                String fromDate = dateFormat.format(formatDateTimeSystem.parse(fromDateTime));
                String toDate;
                if (toDateTime != null) {
                    toDate = dateFormat.format(formatDateTimeSystem.parse(toDateTime));
                } else {
                    toDate = dateFormat.format(new Date());
                }
                List<MonitorReadingDailyDTO> readingDailyDTOs = getMonitorReadingDailyData(responseStatus, ext, siteId, inverterNumber, fromDate, toDate, token);
                monitorReadingDTOs = MonitorUtils.checkAndUpdateExisting(monitorReadingDTOs, ext.getSubsId());
                response.setMonitorReadingDTOs(monitorReadingDTOs);
                response.setBulkDailyRecords(readingDailyDTOs);
            }
            if (!responseStatus.getSuccess()) {
                monitorPlatformUtilityService.outages(ext);
                LOGGER.error("Error triggering outages for SolarEdge. SubscriptionId: {}, Status code: {}", ext.getSubsId(), responseStatus.getStatus());
            }
            return response;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return MonitorAPIResponse.builder().build();
        }
    }

    private MonitorReading subscriptionLastRecord(String subscriptionId, String inverterNumber, String siteId, Date date) {
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

    private List<MonitorReadingDTO> performCalculations(ResponseStatus responseStatus, ExtDataStageDefinition ext, String siteId, String inverterNumber,
                                                        String fromDateTime, String toDateTime, MonitorReading lr,
                                                        String token) throws ParseException {
        String subscriptionId = ext.getSubsId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
        String fromDate = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_FORMAT).format(Utility.getDateFromZoneToZone(
                fromDateTime, formatter, "UTC", AppConstants.EZone.US_CENTRAL.getName()));
//        toDateTime = toDateTime == null ? formatDateTimeSystem.format(new Date()) : toDateTime;
//        toDateTime = formatDateTimeSystem.format(Utility.getZonedDate(formatDateTimeSystem.parse(toDateTime), "UTC"));
//        String toDate = dateFormat.format(formatDateTimeSystem.parse(toDateTime));
        String toDate = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_FORMAT).format(Utility.getDateFromZoneToZone(
                toDateTime, formatter, "UTC", AppConstants.EZone.US_CENTRAL.getName()));
        List<MonitorReadingDTO> monitorReadings = new ArrayList<>();
        List<MetersValues> meterData = new ArrayList<>();
        SiteEnergyResponse data = null;
        while (data == null && formatDateTimeSystem.parse(fromDateTime).before(formatDateTimeSystem.parse(toDateTime))) {
            try {
                data = getHistoricYieldBySiteId(responseStatus, ext, siteId, fromDate, toDate, "QUARTER_OF_AN_HOUR", token);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                fromDateTime = formatDateTimeSystem.format(Utility.addDays(formatDate.parse(fromDate), 1));
                fromDate = dateFormat.format(formatDateTimeSystem.parse(fromDateTime));
            }
        }
        if (data == null) {
            return Collections.EMPTY_LIST;
        }
        if (data.getEnergy() != null) {
            meterData.addAll(data.getEnergy().getValues());
        }
        if (meterData.size() != 0) {
            LocalDateTime dateTime = Utility.getUTCForZone(meterData.get(0).getDate(), formatter,
                    AppConstants.EZone.US_CENTRAL.getName());
            if (meterData.get(0).getValue() == null) {
                meterData.get(0).setValue(lr.getCurrentValue());
            }
            double currentValue = (meterData.get(0).getValue() / Constants.SOLAR_EDGE.THOUSAND) * 4;
            meterData.get(0).setPeakValue(currentValue);
//            meterData.get(0).setGrossYield(lr.getGrossYield() + (meterData.get(0).getValue() / SOLAR_EDGE.THOUSAND)); // TODO: already divided in wrapper service
            meterData.get(0).setGrossYield(lr.getGrossYield() + meterData.get(0).getValue());
            meterData.get(0).setCurrentValueRunning(lr.getCurrentValueRunning() + currentValue);
            List<Date> times = new ArrayList<>();
//            Date time = Utility.localDateTimeToDate(dateTime);
            Date time = Utility.localDateTimeToDate(dateTime, "UTC");
            MonitorReadingDTO monitorReading = MonitorReadingDTO.builder()
                    .subscriptionIdMongo(subscriptionId)
                    .site(siteId)
                    .inverterNumber(inverterNumber)
                    .userId(lr.getUserId() != null ? lr.getUserId() : null)
                    .sytemSize(lr.getSytemSize()) // TODO: check
                    .time(time)
                    .currentValue(currentValue)
                    .currentValueRunning(meterData.get(0).getCurrentValueRunning())
                    .yieldValue(currentValue)
                    .yieldValueRunning(meterData.get(0).getCurrentValueRunning())
                    .grossYield(meterData.get(0).getGrossYield())
                    .peakValue(meterData.get(0).getPeakValue())
//                    .dailyYield(Utility.subtract(grossYield, edgeDaily.getGrossYield())).durl("")
                    .currentValueToday(0.0)
                    .build();

            monitorReadings.add(monitorReading);
            times.add(time);

            if (meterData.size() > 1) {
                for (int i = 1; i < meterData.size(); i++) {
                    dateTime = Utility.getUTCForZone(meterData.get(i).getDate(), formatter,
                            AppConstants.EZone.US_CENTRAL.getName());
                    if (meterData.get(i).getValue() == null) {
                        meterData.get(i).setValue(meterData.get(i - 1).getValue());
                    }
                    currentValue = (meterData.get(i).getValue() / Constants.SOLAR_EDGE.THOUSAND) * 4;
                    if (currentValue > meterData.get(i - 1).getPeakValue()) {
                        meterData.get(i).setPeakValue(currentValue);
                    } else {
                        meterData.get(i).setPeakValue(meterData.get(i - 1).getPeakValue());
                    }
                    meterData.get(i).setGrossYield(meterData.get(i - 1).getGrossYield() + meterData.get(i).getValue());
                    meterData.get(i).setCurrentValueRunning(meterData.get(i - 1).getCurrentValueRunning() + currentValue);
//                    time = Utility.localDateTimeToDate(dateTime);
                    time = Utility.localDateTimeToDate(dateTime, "UTC");
                    monitorReading = MonitorReadingDTO.builder()
                            .subscriptionIdMongo(subscriptionId)
                            .site(siteId)
                            .inverterNumber(inverterNumber)
                            .userId(lr.getUserId() != null ? lr.getUserId() : null)
                            .sytemSize(lr.getSytemSize())
                            .time(time)
                            .currentValue(currentValue)
                            .currentValueRunning(meterData.get(i).getCurrentValueRunning())
                            .yieldValue(currentValue)
                            .yieldValueRunning(meterData.get(i).getCurrentValueRunning())
                            .grossYield(meterData.get(i).getGrossYield())
                            .peakValue(meterData.get(i).getPeakValue())
//                    .dailyYield(Utility.subtract(grossYield, edgeDaily.getGrossYield())).durl("")
                            .currentValueToday(0.0)
                            .build();

                    monitorReadings.add(monitorReading);
                    times.add(time);
                }
            }

            String fromDateTimeFinal = fromDateTime;
            String toDateTimeFinal = toDateTime;
            monitorReadings = monitorReadings.stream().filter(m -> {
                Date fromDT = Utility.toDate(LocalDateTime.parse(fromDateTimeFinal, dateTimeFormatter), "UTC");
                Date toDt = Utility.toDate(LocalDateTime.parse(toDateTimeFinal, dateTimeFormatter), "UTC");
                return (m.getTime().after(fromDT) && (m.getTime().before(toDt))) || m.getTime().equals(fromDT) || m.getTime().equals(toDt);
            }).collect(Collectors.toList());

            getDailyMonthlyYearlyValues(responseStatus, ext, monitorReadings, siteId, fromDate, toDate, token);
            roundMonitorReadings(monitorReadings);
            /*if (monitorReadings.size() > 0) {
                MonitorReadingDTO lastReadingFetched = monitorReadings.get(monitorReadings.size() - 1);
                List<MonitorReading> readingsFollowing = readingRepository.findBySubscriptionIdMongoAndTimeGreaterThan(ext.getSubsId(), lastReadingFetched.getTime());
                readingsFollowing.forEach(m -> {
                    m.setGrossYield(lastReadingFetched.getGrossYield() + (m.getCurrentValue() / 4) * Constants.SOLAR_EDGE.THOUSAND);
                });
                monitorReadings.addAll(MonitorReadingMapper.toMonitorReadingDTOs(readingsFollowing));
            }*/
        }
        return monitorReadings;
    }

    private void getDailyMonthlyYearlyValues(ResponseStatus responseStatus, ExtDataStageDefinition ext, List<MonitorReadingDTO> monitorReadings,
                                             String siteId, String fromDate, String toDate, String token) {
        List<MetersValues> daysYields = Collections.EMPTY_LIST;
        List<MetersValues> monthsYields = Collections.EMPTY_LIST;
        List<MetersValues> yearsYields = Collections.EMPTY_LIST;
        try {
            daysYields = getHistoricYieldBySiteId(responseStatus, ext, siteId, fromDate, toDate, "DAY", token).getEnergy().getValues();
            monthsYields = getHistoricYieldBySiteId(responseStatus, ext, siteId, fromDate, toDate, "MONTH", token).getEnergy().getValues();
            yearsYields = getHistoricYieldBySiteId(responseStatus, ext, siteId, fromDate, toDate, "YEAR", token).getEnergy().getValues();
        } catch (UnirestException e) {
            LOGGER.error(e.getMessage(), e);
        }
        Map<String, Double> daysYieldsMap = new HashMap<>();
        Map<String, Double> monthsYieldsMap = new HashMap<>();
        Map<String, Double> yearsYieldsMap = new HashMap<>();
        int rounding = utility.getCompanyPreference().getRounding();
        for (MetersValues metersValues : daysYields) {
            daysYieldsMap.put(metersValues.getDate().split(" ")[0], utility.round((metersValues.getValue() == null ? 0 : metersValues.getValue()) / Constants.SOLAR_EDGE.THOUSAND, rounding));
        }
        for (MetersValues metersValues : monthsYields) {
            monthsYieldsMap.put(metersValues.getDate().split(" ")[0], utility.round((metersValues.getValue() == null ? 0 : metersValues.getValue()) / Constants.SOLAR_EDGE.THOUSAND, rounding));
        }
        for (MetersValues metersValues : yearsYields) {
            yearsYieldsMap.put(metersValues.getDate().split(" ")[0], metersValues.getValue());
        }
        monitorReadings.forEach(reading -> {
            reading.setDailyYield(daysYieldsMap.get(Utility.getZonedDateTime(reading.getTime(), AppConstants.EZone.US_CENTRAL.getName()).toLocalDate().toString()));
            reading.setMonthlyYield(monthsYieldsMap.get(dateFormat.format(utility.getStartOfMonth(reading.getTime()))));
            reading.setAnnualYield(yearsYieldsMap.get(dateFormat.format(utility.getStartOfYear(reading.getTime()))));
        });
    }

    private void roundMonitorReadings(List<MonitorReadingDTO> monitorReadings) {
        int rounding = utility.getCompanyPreference().getRounding();
        monitorReadings.forEach(m -> {
            m.setCurrentValue(utility.round(m.getCurrentValue(), rounding));
            m.setCurrentValueRunning(utility.round(m.getCurrentValueRunning(), rounding));
            m.setYieldValue(utility.round(m.getYieldValue(), rounding));
            m.setYieldValueRunning(utility.round(m.getYieldValueRunning(), rounding));
            m.setGrossYield(utility.round(m.getGrossYield(), rounding));
            m.setPeakValue(utility.round(m.getPeakValue(), rounding));
        });
    }

    private List<MonitorReadingDailyDTO> getMonitorReadingDailyData(ResponseStatus responseStatus, ExtDataStageDefinition ext, String siteId,
                                                                    String inverterNumber, String fromDateString,
                                                                    String toDateString, String token) {
        String subscriptionId = ext.getSubsId();
        List<MonitorReadingDailyDTO> readingDailyList = new ArrayList<>();
        List<MonitorReadingDailyDTO> readingDailyListToUpdate = new ArrayList<>();
        List<MetersValues> meterData = new ArrayList<>();
        try {
            SiteEnergyResponse data = getHistoricYieldBySiteId(responseStatus, ext, siteId, fromDateString, toDateString, "DAY", token);
            if (data.getEnergy() != null) {
                meterData.addAll(data.getEnergy().getValues());
            }
        } catch (UnirestException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT);
        int rounding = utility.getCompanyPreference().getRounding();
        meterData.forEach(data -> {
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
                readingDaily.setCurrentValue(utility.round((data.getValue() == null ? 0.0 : data.getValue()) / Constants.SOLAR_EDGE.THOUSAND, rounding));
                readingDaily.setYieldValue(utility.round((data.getValue() == null ? 0.0 : data.getValue()) / Constants.SOLAR_EDGE.THOUSAND, rounding));
                readingDaily.setPeakValue(peakValue);
                readingDailyListToUpdate.add(MonitorReadingDailyMapper.toMonitorReadingDailyDTO(readingDaily));
            } else {
                readingDailyList.add(MonitorReadingDailyDTO.builder()
                        //                    .userId()
                        .subscriptionIdMongo(subscriptionId)
                        .site(siteId)
                        .inverterNumber(inverterNumber)
                        .currentValue(utility.round((data.getValue() == null ? 0.0 : data.getValue()) / Constants.SOLAR_EDGE.THOUSAND, rounding))
                        .yieldValue(utility.round((data.getValue() == null ? 0.0 : data.getValue()) / Constants.SOLAR_EDGE.THOUSAND, rounding))
                        .peakValue(peakValue)
                        .day(date)
                        .build());
            }
        });
        readingDailyList.addAll(readingDailyListToUpdate);
        return readingDailyList;
    }

    @Override
    public MonitorReadingHistoricDates getHistoricDates() {
        return historicDates;
    }

    private SiteEnergyResponse getHistoricYieldBySiteId(ResponseStatus responseStatus, ExtDataStageDefinition ext, String siteId, String fromDate, String toDate, String timeUnit, String token) throws UnirestException {
        HttpResponse<String> response = SolarEdgeMapper.getHistoricYieldURL(fromDate, toDate, token, siteId, timeUnit);
        String status = String.valueOf(response.getStatus());
        if (!status.startsWith("20")) {
            responseStatus.setSuccess(false);
            responseStatus.setStatus(status);
        }
        return new Gson().fromJson(response.getBody(), SiteEnergyResponse.class);
    }

    @Getter
    @Setter
    @Builder
    static class ResponseStatus {
        Boolean success;
        String status;
    }
}
