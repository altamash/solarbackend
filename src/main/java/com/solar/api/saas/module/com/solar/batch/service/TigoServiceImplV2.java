package com.solar.api.saas.module.com.solar.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.module.com.solar.batch.service.tigo.TigoServiceV2;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.tigo.Series;
import com.solar.api.tenant.model.stage.monitoring.tigo.SeriesData;
import com.solar.api.tenant.model.stage.monitoring.tigo.TigoResponseV2;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.Constants.RATE_CODES.*;
import static com.solar.api.Constants.REGULAR_EXP.VAR_BRACKET_CLOSE;
import static com.solar.api.Constants.REGULAR_EXP.VAR_BRACKET_OPEN;

/**
 * @author Shariq
 * <p>
 * For more detailed information, see the [Tigo.md](src/main/java/com/solar/api/saas/module/com/solar/batch/service/tigo/Tigo.md) file.
 * Created on: 2023-07-07
 * Updated at: 2023-08-03
 * Updated By: Shariq
 */
@Service
public class TigoServiceImplV2 implements TigoServiceV2 {

    protected final Logger LOGGER = LoggerFactory.getLogger(TigoServiceImplV2.class);

    private final StageMonitorService stageMonitorService;
    private final SolrenviewService solrenviewService;
    private final MonitorPlatformUtilityService monitorPlatformUtilityService;
    private final EGaugeService eGaugeService;
    private final MonitorReadingRepository readingRepository;
    private final MonitorReadingDailyRepository dailyRepository;
    private final Utility utility;

    public TigoServiceImplV2(StageMonitorService stageMonitorService, SolrenviewService solrenviewService, MonitorPlatformUtilityService monitorPlatformUtilityService, EGaugeService eGaugeService, MonitorReadingRepository readingRepository, MonitorReadingDailyRepository dailyRepository, Utility utility) {
        this.stageMonitorService = stageMonitorService;
        this.solrenviewService = solrenviewService;
        this.monitorPlatformUtilityService = monitorPlatformUtilityService;
        this.eGaugeService = eGaugeService;
        this.readingRepository = readingRepository;
        this.dailyRepository = dailyRepository;
        this.utility = utility;
    }

    /**
     * Gets the current data from TIGO Inverter
     * Used for Half Hour Schedule
     *
     * @return response
     */
    @Async
    @Override
    public ResponseEntity<String> getMinuteData(String startTime, String endTime, String subsIds, Boolean forceUpdate) {
        LOGGER.info("Entering getMinuteData() for TIGO Inverter");
        Set<String> subsIdList = subsIds == null ? null : Arrays.stream(subsIds.split(",")).map(id -> id.trim()).filter(id -> !id.trim().equalsIgnoreCase("null")).collect(Collectors.toSet());
        Map<String, String> subscriptionSuccessMessage = new HashMap<>();
        Map<String, List<String>> subscriptionErrorMessage = new HashMap<>();
        ResponseEntity<String> stringResponseEntity = null;
        for (ExtDataStageDefinition subscription : subsIds == null ? stageMonitorService.getAllSubscriptions(BRAND_TIGO) : stageMonitorService.getAllSubscriptionsByMpAndSubsIds(BRAND_TIGO, new ArrayList<>(subsIdList))) {
            try {
                LocalDateTime lastRecordDate = null;
                String deviceUrl = Utility.getDeviceUrl(subscription.getMpJson());
                String deviceNo = Utility.getMeasureAsJson(subscription.getMpJson(), DEVICE_NUMBER);
                String sysId = Utility.getMeasureAsJson(subscription.getMpJson(), STID);
                String sAuth = Utility.getMeasureAsJson(subscription.getMpJson(), S_AUTK);
                String subStartDate = Utility.getMeasureAsJson(subscription.getMpJson(), Constants.RATE_CODES.SUB_START_DATE);
                checkMandatoryMeasures(deviceNo, sysId, sAuth, deviceUrl, subStartDate, subscription.getSubsId(), subscriptionErrorMessage);
                if (deviceNo == null || sysId == null || sAuth == null || subStartDate == null || deviceNo.isEmpty()
                        || sysId.isEmpty() || sAuth.isEmpty() || subStartDate.isEmpty() || deviceUrl.isEmpty()) {
                    continue;
                }
                //Current Data
                if (startTime == null && endTime == null) {
                    stringResponseEntity = getCurrentData(subscription, sysId, deviceUrl, deviceNo, sAuth);
                    //Historic Data
                } else {
                    MonitorReading monitorReading = readingRepository.getLastRecordByTime(subscription.getSubsId(), deviceNo.equals("-1") ? sysId : deviceNo);
                    if (monitorReading != null) {
                        lastRecordDate = Utility.toLocalDateTime(monitorReading.getTime());
                    }
                    Pair<String, String> datePair = getEffectiveDateRangeAsString(startTime, endTime, subStartDate, lastRecordDate, forceUpdate);
                    startTime = datePair.getLeft();
                    endTime = datePair.getRight();
                    if (forceUpdate != null && forceUpdate) {
                        eGaugeService.deleteRecordsInRangeAndCondition(startTime, endTime, BRAND_TIGO, subscription.getSubsId());
                    }
                    stringResponseEntity = getHistoricMinuteData(startTime, endTime, deviceUrl, sysId, subscription, deviceNo, sAuth);
                }
            } catch (Exception e) {
                if (e == null) {
                    LOGGER.warn("Exception is null for mpJson {} brand {} subscription {}", subscription.getMpJson(), subscription.getBrand(), subscription.getSubsId());
                    Utility.batchNotification("Tigo", null, subscription.getMpJson() + " " + subscription.getBrand() + " " + subscription.getSubsId(), "EXCEPTION");
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                LOGGER.error(e.getMessage(), e);
                Utility.batchNotification("Tigo", null, e.getMessage(), "EXCEPTION");
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
        LOGGER.info("Exiting getMinuteData()");
        return stringResponseEntity;
    }

    /**
     * @param startTime    -- start time
     * @param endTime      -- end time
     * @param deviceUrl    -- device URL
     * @param sysId        -- Site Id
     * @param subscription -- Subscription
     * @param deviceNo     -- device number
     * @param sAuth        -- authentication key
     * @return response
     */
    private ResponseEntity<String> getHistoricMinuteData(String startTime, String endTime, String deviceUrl, String sysId,
                                                         ExtDataStageDefinition subscription, String deviceNo, String sAuth) {
        LocalDateTime historyStartTime = Utility.toLocalDateTimeEastUs(Objects.requireNonNull(Utility.toDate(startTime)));
        LocalDateTime historyEndTime = Utility.toLocalDateTimeEastUs(Objects.requireNonNull(Utility.toDate(endTime)));
        String deviceUrlQ1 = setMinuteHistoricQURL(deviceUrl, historyStartTime, historyEndTime, sysId, deviceNo, sAuth);
        TigoResponseV2 minuteResponse = getTigoResponse(deviceUrlQ1, subscription);
        if (minuteResponse == null) {
            LOGGER.error("Cannot get valid response from client");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        List<Date> timeList = Utility.getTimeStamps(Utility.toDate(startTime), Objects.requireNonNull(Utility.toDate(endTime)), 15);
        tigoInitializer(timeList, deviceUrl, sysId, deviceUrlQ1, subscription, minuteResponse, deviceNo, sAuth);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param deviceUrl    -- device URL
     * @param sysId        -- Site Id
     * @param subscription -- Subscription
     * @param deviceNo     -- device number
     * @param sAuth        -- authentication key
     * @return response
     */
    private ResponseEntity<String> getCurrentData(ExtDataStageDefinition subscription, String sysId, String deviceUrl,
                                                  String deviceNo, String sAuth) {
        // Fetching last record
        // if no data -> currentDate (startOfDay)
        // if data -> lastRecord in DB
        MonitorReading subscriptionLastRecord = solrenviewService.subscriptionLastRecord(subscription.getSubsId(), deviceNo.equals("-1") ? sysId : deviceNo);
        String subStartDate = Utility.getMeasureAsJson(subscription.getMpJson(), SUB_START_DATE);

        // if id == null -> (Means, it's currentDate (StartOfDay))
        // if id != null -> (Means, it's last record's date)
        Date date = subscriptionLastRecord.getId() == null ?
                validateSubStartDate(subscriptionLastRecord, subStartDate) : subscriptionLastRecord.getTime();
        LocalDateTime localDateTime = Utility.toLocalDateTimeEastUs(date);
        String deviceUrlQ1 = setQURL(deviceUrl, localDateTime, sysId, MINUTE, deviceNo, sAuth);
        TigoResponseV2 minuteResponse = getTigoResponse(deviceUrlQ1, subscription);
        if (minuteResponse == null) {
            LOGGER.error("Cannot get valid response from client");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        List<Date> timeList = Utility.getTimeStamps(Utility.addMinutes(date, 15), Utility.addMinutes(date, 45), 15);
        tigoInitializer(timeList, deviceUrl, sysId, deviceUrlQ1, subscription, minuteResponse, deviceNo, sAuth);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param timeList       -- dateTime list
     * @param deviceUrl      -- device URL
     * @param sysId          -- site ID
     * @param deviceUrlQ1    -- query URL
     * @param subscription   -- subscription
     * @param minuteResponse -- API response for minutes
     * @param deviceNo       -- device number
     * @param sAuth          -- authentication key
     */
    private void tigoInitializer(List<Date> timeList, String deviceUrl, String sysId, String deviceUrlQ1,
                                 ExtDataStageDefinition subscription, TigoResponseV2 minuteResponse, String deviceNo, String sAuth) {
        for (Date targetTime : timeList) {
            LocalDateTime localDateTime = Utility.toLocalDateTimeEastUs(targetTime);
            TigoResponseV2 hourResponse = localDateTime.getMinute() == 0 ? generateHourResponse(deviceUrl, localDateTime, sysId, deviceNo, sAuth, subscription) : null;
            jSonDecoding(targetTime, deviceUrlQ1, sysId, subscription,
                    solrenviewService.subscriptionLastRecord(subscription.getSubsId(), deviceNo.equals("-1") ? sysId : deviceNo), minuteResponse, hourResponse, localDateTime, deviceNo);
            try {
                saveMonitorReadingDaily(deviceUrl, localDateTime, sysId, deviceNo, sAuth, subscription);
            } catch (Exception e) {
                LOGGER.error("Error dumping data in Monitor Reading Daily, Could be non unique result in the table");
            }
        }
    }

    /**
     * @param targetTime             -- start date
     * @param deviceUrl              -- device url
     * @param sysId                  -- device id
     * @param subscription           -- subscription
     * @param subscriptionLastRecord -- subscription last record
     * @param minuteResponse         -- minute response
     * @param hourResponse           -- hour response
     * @param deviceNo
     */
    private void jSonDecoding(Date targetTime, String deviceUrl, String sysId,
                              ExtDataStageDefinition subscription, MonitorReading subscriptionLastRecord,
                              TigoResponseV2 minuteResponse, TigoResponseV2 hourResponse, LocalDateTime localDateTime, String deviceNo) {

        if (deviceUrl != null && !deviceUrl.isEmpty()) {
            //Setting up URLs for Query
            solrenviewService.save(tigoMapper(Objects.requireNonNull(minuteResponse.getSeriesList().stream()
                            .filter(s -> s.getId().toLowerCase().contains(Constants.RATE_CODES.TOTAL.toLowerCase())).findFirst().orElse(null)),
                    targetTime,
                    sysId,
                    deviceUrl,
                    subscription,
                    subscriptionLastRecord,
                    hourResponse,
                    localDateTime,
                    deviceNo));
        }
    }

    /**
     * @param series                 -- series
     * @param targetTime             -- start date
     * @param sysId                  -- device
     * @param deviceUrl              -- device url
     * @param extDataStageDefinition -- ext data stage definition object
     * @param subscriptionLastRecord -- subscription last record
     * @param hourResponse           -- hour response
     * @param deviceNo
     * @return -- MonitorReading
     */
    private MonitorReading tigoMapper(Series series, Date targetTime, String sysId, String deviceUrl,
                                      ExtDataStageDefinition extDataStageDefinition, MonitorReading subscriptionLastRecord,
                                      TigoResponseV2 hourResponse, LocalDateTime localDateTime, String deviceNo) {
        SeriesData minuteData = series.getData().stream().filter(s -> s.getName().equals(Utility.toLocalDateTimeEastUs(targetTime).toString() + SECOND_FORMAT)).findFirst().orElse(null);
        SeriesData hourData = null;
        if (hourResponse != null) {
            Optional<Series> hourSeriesData = hourResponse.getSeriesList().stream().filter(
                    s -> s.getId().toLowerCase().contains(Constants.RATE_CODES.TOTAL.toLowerCase())).findFirst();
            hourData = hourSeriesData.flatMap(value -> value.getData().stream().filter(s -> s.getName().equals(Utility.toLocalDateTimeEastUs(targetTime).toString() + SECOND_FORMAT)).findFirst()).orElse(null);
        }
        String inverterNumber = deviceNo.equals("-1") ? sysId : deviceNo;
        Map<String, String> data = getValidData(subscriptionLastRecord, hourData, targetTime);
        Double currentValue = (minuteData == null ? 0.0 : Double.parseDouble(String.valueOf(minuteData.getValue()[1]))) / 1000d;
        MonitorReading monitorReading = MonitorReading.builder()
                .currentValue(currentValue)
                .inverterNumber(inverterNumber)
                .time(targetTime)
                .yieldValue(currentValue)
                .subscriptionIdMongo(subscriptionLastRecord.getSubscriptionIdMongo())
                .durl(deviceUrl)
                .monthlyYield(Double.parseDouble(data.get(MONTHLY_YIELD)))
                .dailyYield(Double.parseDouble(data.get(DAILY_YIELD)))
                .annualYield(Double.parseDouble(data.get(ANNUAL_YIELD)))
                .grossYield(Double.parseDouble(data.get(GROSS_YIELD)))
                .peakValue(solrenviewService.getHighestCurrentValueOfTheDay(Utility.toDate(localDateTime), inverterNumber, subscriptionLastRecord.getSubscriptionIdMongo()))
                .logs(getLogs(null, targetTime, currentValue, Double.parseDouble(data.get(ANNUAL_YIELD)),
                        Double.parseDouble(data.get(GROSS_YIELD)), deviceUrl)).build();
        return monitorReading;
    }

    /**
     * @param subscriptionLastRecord -- subscription last record
     * @param hourData               -- hour data
     * @param date                   -- date
     * @return Calculated Values
     */
    private Map<String, String> getValidData(MonitorReading subscriptionLastRecord, SeriesData hourData, Date date) {
        Map<String, String> map = new HashMap<>();
        double lastAnnualYield = subscriptionLastRecord.getAnnualYield() == null ? 0.0 : subscriptionLastRecord.getAnnualYield();
        double lastGrossYield = subscriptionLastRecord.getGrossYield() == null ? 0.0 : subscriptionLastRecord.getGrossYield();
        double lastDailyYield = subscriptionLastRecord.getDailyYield() == null ? 0.0 : subscriptionLastRecord.getDailyYield();
        double lastMonthlyYield = subscriptionLastRecord.getMonthlyYield() == null ? 0.0 : subscriptionLastRecord.getMonthlyYield();
        double currentAnnualYield = (hourData == null ? lastAnnualYield : lastAnnualYield + (Double.parseDouble(String.valueOf(hourData.getValue()[1])) / 1000));
        double currentGrossYield = (hourData == null ? lastGrossYield : lastGrossYield + (Double.parseDouble(String.valueOf(hourData.getValue()[1])) / 1000));
        double dailyYield = (hourData == null ? lastDailyYield : lastDailyYield + (Double.parseDouble(String.valueOf(hourData.getValue()[1])) / 1000));
        double monthlyYield = (hourData == null ? lastMonthlyYield : lastMonthlyYield + (Double.parseDouble(String.valueOf(hourData.getValue()[1])) / 1000));
        if (subscriptionLastRecord.getId() != null && Utility.toLocalDateTimeEastUs(subscriptionLastRecord.getTime()).getDayOfMonth() != Utility.toLocalDateTimeEastUs(date).getDayOfMonth()) {
            dailyYield = 0.0;
        }
        if (Utility.toLocalDateTimeEastUs(subscriptionLastRecord.getTime()).getMonth() != Utility.toLocalDateTimeEastUs(date).getMonth()) {
            monthlyYield = 0.0;
        }
        if (Utility.toLocalDateTimeEastUs(subscriptionLastRecord.getTime()).getYear() != Utility.toLocalDateTimeEastUs(date).getYear()) {
            currentAnnualYield = 0.0;
        }
        map.put(ANNUAL_YIELD, String.valueOf(currentAnnualYield));
        map.put(GROSS_YIELD, String.valueOf(currentGrossYield));
        map.put(DAILY_YIELD, String.valueOf(dailyYield));
        map.put(DATE, String.valueOf(date));
        map.put(MONTHLY_YIELD, String.valueOf(monthlyYield));
        return map;
    }

    private void saveMonitorReadingDaily(String deviceUrl, LocalDateTime localDateTime, String sysId, String deviceNo,
                                         String sAuth, ExtDataStageDefinition subscription) {
        if (deviceUrl != null && !deviceUrl.isEmpty()) {
            LocalDateTime callDateTime = Utility.getDateFromZoneToZone(localDateTime,
                    AppConstants.EZone.AMERICA_NEW_YORK.getName(), "UTC");
            TigoResponseV2 dayResponse = generateDayResponse(deviceUrl, callDateTime, sysId, deviceNo, sAuth, subscription);
            Optional<Series> daySeriesData = dayResponse.getSeriesList().stream().filter(
                    s -> s.getId().toLowerCase().contains(Constants.RATE_CODES.TOTAL.toLowerCase())).findFirst();
            SeriesData dayData = daySeriesData.flatMap(value -> value.getData().stream()
                    .findFirst()).orElse(null);
            if (dayData != null) {
                if (dayData.getValue().length > 0) {
                    Double dailyYield = utility.round((double) dayData.getValue()[1] / 1000, 4);
                    try {
                        Date zonedDate = Utility.toDate(localDateTime, AppConstants.EZone.AMERICA_NEW_YORK.getName());
                        MonitorReadingDaily mrd = dailyRepository.getLastSavedRecordByMongoSubIdAndDate(subscription.getSubsId(),
                                Utility.getDateString(zonedDate, Utility.SYSTEM_DATE_FORMAT));
                        if (mrd == null) {
                            dailyRepository.save(MonitorReadingDaily.builder()
                                    .subscriptionIdMongo(subscription.getSubsId())
                                    .inverterNumber(deviceNo.equals("-1") ? sysId : deviceNo)
                                    .yieldValue(dailyYield)
                                    .day(zonedDate)
                                    .build());
                        } else {
                            if (dailyYield != null) {
                                mrd.setYieldValue(dailyYield > mrd.getYieldValue() ? dailyYield : mrd.getYieldValue());
                                dailyRepository.save(mrd);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * @param stDate      -- start date
     * @param cv          -- current value
     * @param annualYield -- annual yield
     * @param grossYield  -- gross yield
     * @param deviceUrl   -- device url
     * @return -- logs
     */
    private String getLogs(LocalDateTime localDateTime, Date stDate, Double cv, Double annualYield, Double grossYield, String deviceUrl) {
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put(STARTDATE, String.valueOf(stDate));
        requestMessage.put(LOCALDATETIME, String.valueOf(localDateTime));
        requestMessage.put(CURRENT_VALUE, cv);
        requestMessage.put(ANNUAL_YIELD, annualYield);
        requestMessage.put(GROSS_YIELD, grossYield);
        requestMessage.put(DEVICE_URL_QUERY, deviceUrl);
        return requestMessage.toPrettyString();
    }

    /**
     * @param deviceUrl    -- device url
     * @param stDate       -- start date
     * @param sysId        -- device id
     * @param deviceNo     -- device number
     * @param sAuth        -- authentication key
     * @param subscription
     * @return -- setup URL for response from TIGO INVERTER
     */
    private TigoResponseV2 generateHourResponse(String deviceUrl, LocalDateTime stDate, String sysId, String deviceNo, String sAuth, ExtDataStageDefinition subscription) {
        String deviceUrlQ2 = setQURL(deviceUrl, stDate, sysId, com.solar.api.Constants.RATE_CODES.HOUR, deviceNo, sAuth);
        return getTigoResponse(deviceUrlQ2, subscription);
    }

    private TigoResponseV2 generateDayResponse(String deviceUrl, LocalDateTime stDate, String sysId, String deviceNo, String sAuth, ExtDataStageDefinition subscription) {
        String deviceUrlQ2 = setQURL(deviceUrl, stDate, sysId, Constants.RATE_CODES.DAY, deviceNo, sAuth);
        return getTigoResponse(deviceUrlQ2, subscription);
    }

    /**
     * @param deviceUrlQ   -- device url
     * @param subscription
     * @return -- response from TIGO INVERTER
     */
    private TigoResponseV2 getTigoResponse(String deviceUrlQ, ExtDataStageDefinition subscription) {
        ResponseEntity<String> query = null;
        try {
            query = WebUtils.submitRequest(HttpMethod.GET, deviceUrlQ, null, new HashMap<>(), String.class);
            if (!String.valueOf(query.getStatusCodeValue()).contains("20")) {
                triggerOutages(query.getStatusCodeValue(), subscription);
            } else {
                try {
                    return new ObjectMapper().readValue(query.getBody()
                            , TigoResponseV2.class);
                } catch (JsonProcessingException e) {
                    LOGGER.error("TIGO ERROR, Status Code {}, url {},stackTrace {}", query.getStatusCodeValue(), deviceUrlQ, e);
                    LOGGER.error(e.getMessage(), e);
                }
            }
        } catch (Exception exception) {
            if ((((HttpServerErrorException.InternalServerError) exception).getRawStatusCode() != 200)) {
                triggerOutages(((HttpServerErrorException.InternalServerError) exception).getRawStatusCode(), subscription);
            }
            LOGGER.error(exception.getMessage(), exception);

        }
        return null;
    }

    private void triggerOutages(int statusCodeValue, ExtDataStageDefinition subscription) {
        try {
            monitorPlatformUtilityService.outages(subscription);
        } catch (Exception e) {
            LOGGER.error("Error triggering outages" + e.getMessage(), e);
        }
    }

    /**
     * @param deviceUrl     -- device url
     * @param localDateTime -- start date
     * @param sysId         -- device id
     * @param agg           -- aggregation type
     * @param deviceNo      -- device number
     * @param sAuth         -- authentication key
     * @return -- generate URL for response from TIGO INVERTER
     */
    private String setQURL(String deviceUrl, LocalDateTime localDateTime, String sysId, String agg, String deviceNo, String sAuth) {
        return deviceNo == null || deviceNo.equals("-1") || deviceNo.isBlank() ? deviceUrl
                .replaceAll(VAR_BRACKET_OPEN + S_AUTK + VAR_BRACKET_CLOSE, sAuth)
                .replaceAll(VAR_BRACKET_OPEN + SYSID + VAR_BRACKET_CLOSE, sysId)
                .replaceAll(VAR_BRACKET_OPEN + STARTDATE + VAR_BRACKET_CLOSE, String.valueOf(localDateTime.toLocalDate()))
                .replaceAll(VAR_BRACKET_OPEN + ENDDATE + VAR_BRACKET_CLOSE, String.valueOf(localDateTime.toLocalDate()))
                .replaceAll(VAR_BRACKET_OPEN + AGG + VAR_BRACKET_CLOSE, agg)
                .replaceAll("&ids=" + VAR_BRACKET_OPEN + "DEVNO" + VAR_BRACKET_CLOSE, "") :
                deviceUrl
                        .replaceAll(VAR_BRACKET_OPEN + S_AUTK + VAR_BRACKET_CLOSE, sAuth)
                        .replaceAll(VAR_BRACKET_OPEN + DEVICE_NUMBER + VAR_BRACKET_CLOSE, deviceNo)
                        .replaceAll(VAR_BRACKET_OPEN + SYSID + VAR_BRACKET_CLOSE, sysId)
                        .replaceAll(VAR_BRACKET_OPEN + STARTDATE + VAR_BRACKET_CLOSE, String.valueOf(localDateTime.toLocalDate()))
                        .replaceAll(VAR_BRACKET_OPEN + ENDDATE + VAR_BRACKET_CLOSE, String.valueOf(localDateTime.toLocalDate()))
                        .replaceAll(VAR_BRACKET_OPEN + AGG + VAR_BRACKET_CLOSE, agg);
    }

    private String setMinuteHistoricQURL(String deviceUrl, LocalDateTime startTime, LocalDateTime endTime, String sysId,
                                         String deviceNo, String sAuth) {
        return deviceNo == null || deviceNo.equals("-1") || deviceNo.isBlank() ? deviceUrl
                .replaceAll(VAR_BRACKET_OPEN + S_AUTK + VAR_BRACKET_CLOSE, sAuth)
                .replaceAll(VAR_BRACKET_OPEN + SYSID + VAR_BRACKET_CLOSE, sysId)
                .replaceAll(VAR_BRACKET_OPEN + STARTDATE + VAR_BRACKET_CLOSE, String.valueOf(startTime.toLocalDate()))
                .replaceAll(VAR_BRACKET_OPEN + ENDDATE + VAR_BRACKET_CLOSE, String.valueOf(endTime.toLocalDate()))
                .replaceAll(VAR_BRACKET_OPEN + AGG + VAR_BRACKET_CLOSE, com.solar.api.Constants.RATE_CODES.MINUTE)
                .replaceAll("&ids=" + VAR_BRACKET_OPEN + "DEVNO" + VAR_BRACKET_CLOSE, "") :
                deviceUrl
                        .replaceAll(VAR_BRACKET_OPEN + S_AUTK + VAR_BRACKET_CLOSE, sAuth)
                        .replaceAll(VAR_BRACKET_OPEN + DEVICE_NUMBER + VAR_BRACKET_CLOSE, deviceNo)
                        .replaceAll(VAR_BRACKET_OPEN + SYSID + VAR_BRACKET_CLOSE, sysId)
                        .replaceAll(VAR_BRACKET_OPEN + STARTDATE + VAR_BRACKET_CLOSE, String.valueOf(startTime.toLocalDate()))
                        .replaceAll(VAR_BRACKET_OPEN + ENDDATE + VAR_BRACKET_CLOSE, String.valueOf(endTime.toLocalDate()))
                        .replaceAll(VAR_BRACKET_OPEN + AGG + VAR_BRACKET_CLOSE, com.solar.api.Constants.RATE_CODES.MINUTE);
    }

    /**
     * @param subscriptionLastRecord -- subscription last record
     * @param subStartDate           -- subscription start date
     * @return -- valid subscription start date
     */
    private Date validateSubStartDate(MonitorReading subscriptionLastRecord, String subStartDate) {
        if (Utility.isBefore(new Date(subStartDate), subscriptionLastRecord.getTime())) {
            return subscriptionLastRecord.getTime();
        } else {
            return new Date(subStartDate);
        }
    }

    private void checkMandatoryMeasures(String deviceNo, String sysId, String sAuth, String deviceURL, String subStartDate,
                                        String subsId, Map<String, List<String>> subscriptionMessage) {
        if (deviceNo == null || deviceNo.isEmpty()) {
            LOGGER.error("deviceNo is not defined for Tigo subscription with id {}" + subsId);
            eGaugeService.composeErrorLog(subsId, "deviceNo is not defined", subscriptionMessage);
        }
        if (sysId == null || sysId.isEmpty()) {
            LOGGER.error("sysId is not defined for Tigo subscription with id {}" + subsId);
            eGaugeService.composeErrorLog(subsId, "sysId is not defined", subscriptionMessage);
        }
        if (sAuth == null || sAuth.isEmpty()) {
            LOGGER.error("sAuth is not defined for Tigo subscription with id {}" + subsId);
            eGaugeService.composeErrorLog(subsId, "sAuth is not defined", subscriptionMessage);
        }
        if (deviceURL == null || deviceURL.isEmpty()) {
            LOGGER.error("DURL is not defined for Tigo subscription with id {}" + subsId);
            eGaugeService.composeErrorLog(subsId, "DURL is not defined", subscriptionMessage);
        }
        if (subStartDate == null || subStartDate.isEmpty()) {
            LOGGER.error("SubStartDate is not defined for Tigo subscription with id {}" + subsId);
            eGaugeService.composeErrorLog(subsId, "DURL is not defined", subscriptionMessage);
        }
    }

    private Pair<LocalDateTime, LocalDateTime> getEffectiveDateRange(String fromDate, String toDate,
                                                                     String subscriptionStartDate, LocalDateTime lastRecDate, Boolean force) {
        LocalDateTime subStartDate = LocalDateTime.parse(subscriptionStartDate, Utility.DAY_MON_DATE_HH_MM_SS_ZONE_YEAR_FORMATTER);
        LocalDateTime startDate = fromDate == null ? subStartDate : LocalDateTime.parse(fromDate, Utility.YEAR_MON_DATE_HH_MM_SS_FORMATTER);
        LocalDateTime endDate = toDate == null ? LocalDateTime.now() : LocalDateTime.parse(toDate, Utility.YEAR_MON_DATE_HH_MM_SS_FORMATTER);

        if (!force) {
            if (lastRecDate != null) {
                if (lastRecDate.isAfter(startDate) && lastRecDate.isAfter(subStartDate)) {
                    startDate = lastRecDate;
                }
            } else {
                if (startDate.isBefore(subStartDate)) {
                    startDate = subStartDate;
                }
            }
            if (endDate.isAfter(LocalDateTime.now())) {
                endDate = LocalDateTime.now();
            }
        }
        return Pair.of(startDate, endDate);
    }

    public Pair<String, String> getEffectiveDateRangeAsString(String fromDate, String toDate,
                                                              String subscriptionStartDate, LocalDateTime lastRecordDate, Boolean force) {
        Pair<LocalDateTime, LocalDateTime> dateRange = getEffectiveDateRange(fromDate, toDate, subscriptionStartDate, lastRecordDate, force);
        return Pair.of(dateRange.getLeft().format(Utility.YEAR_MON_DATE_HH_MM_SS_FORMATTER), dateRange.getRight().format(Utility.YEAR_MON_DATE_HH_MM_SS_FORMATTER));
    }
}
