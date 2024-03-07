package com.solar.api.saas.module.com.solar.batch.service.tigo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.module.com.solar.batch.service.SolrenviewService;
import com.solar.api.saas.module.com.solar.batch.service.StageMonitorService;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.tigo.Series;
import com.solar.api.tenant.model.stage.monitoring.tigo.SeriesData;
import com.solar.api.tenant.model.stage.monitoring.tigo.TigoResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.solar.api.Constants.RATE_CODES.*;
import static com.solar.api.Constants.REGULAR_EXP.*;

/**
 * @author Shariq
 * <p>
 * For more detailed information, see the [Tigo.md](src/main/java/com/solar/api/saas/module/com/solar/batch/service/tigo/Tigo.md) file.
 * Created on: 2023-07-07
 * Updated at: 2023-08-03
 * Updated By: Shariq
 */
@Service
public class TigoServiceImpl implements TigoService {

    protected final Logger LOGGER = LoggerFactory.getLogger(TigoServiceImpl.class);

    @Autowired
    private StageMonitorService stageMonitorService;

    @Autowired
    private SolrenviewService solrenviewService;

    /**
     * Following is a test Function
     *
     * @return -- function
     * @throws JsonProcessingException -- Exception
     */
    @Override
    public TigoResponseDTO hitURL() throws JsonProcessingException {
        String deviceUrl = "https://ei.tigoenergy.com/p/7iaUqgDxmGZu/system/charts/e-chart-handler?sysid=67112&" +
                "view=gen&sysid=67112&type=line&agg=min&startDate=2023-07-11&endDate=2023-07-11&reclaimed=true";
        ResponseEntity<String> query = WebUtils.submitRequest(HttpMethod.GET, deviceUrl, null, new HashMap<>(), String.class);
        return new ObjectMapper().readValue(query.getBody()
                , TigoResponseDTO.class);
    }

    /**
     * @param startTime -- fetch from start time
     * @param endTime   -- fetch till end time
     * @return -- Response object
     */
    @Deprecated
    @Override
    public ResponseEntity<String> getHistoricData(String startTime, String endTime) {
        try {
            for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(BRAND_TIGO)) {

                String deviceUrl = Utility.getDeviceUrl(subscription.getMpJson());
                if (deviceUrl == null || deviceUrl.equals("")) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                String sysId = Utility.getMeasureAsJson(subscription.getMpJson(), SYSID);
                MonitorReading subscriptionLastRecord = solrenviewService.subscriptionLastRecord(subscription.getSubsId(), sysId);

                //The following conversion is for generating the TIGO request URL
                LocalDateTime startDate = Utility.toLocalDateTime(Utility.getStartOfDate(Utility.toDateWithFormat(startTime, Utility.DATE_TIME_FORMAT)));
                LocalDateTime endDate = Utility.toLocalDateTime(Utility.getStartOfDate(Utility.toDateWithFormat(endTime, Utility.DATE_TIME_FORMAT)));

                //Generating Monthly and Daily Tigo request URL
                String deviceUrlMonthly = setQURLHistoric(deviceUrl, startDate, endDate, sysId, MONTH);
                String deviceUrlDay = setQURLHistoric(deviceUrl, startDate, endDate, sysId, DAY);
                //Fetching Monthly and Daily Response
                TigoResponseDTO monthResponse = getTigoResponse(deviceUrlMonthly);
                TigoResponseDTO dayResponse = getTigoResponse(deviceUrlDay);
                if (monthResponse == null || dayResponse == null) {
                    LOGGER.error("Cannot get valid response from client");
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
                //Dumping monthly data
                dumpForMonths(monthResponse, sysId, subscription, subscriptionLastRecord, startTime, endTime, deviceUrlMonthly);
                //Dumping Daily data
                dumpForDays(dayResponse, sysId, subscription, subscriptionLastRecord, startTime, endTime, deviceUrlDay);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            Utility.batchNotification(BRAND_TIGO, null, e.getMessage(), "EXCEPTION");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * @param deviceUrl -- third party URL of the device
     * @param startTime -- fetch from start time
     * @param endTime   -- fetch till end time
     * @param sysId     -- device ID
     * @param agg       -- aggregation type
     * @return -- String
     */
    private String setQURLHistoric(String deviceUrl, LocalDateTime startTime, LocalDateTime endTime, String sysId, String agg) {
        return deviceUrl
                .replaceAll(VAR_BRACKET_OPEN + SYSID + VAR_BRACKET_CLOSE, sysId)
                .replaceAll(VAR_BRACKET_OPEN + STARTDATE + VAR_BRACKET_CLOSE, String.valueOf(startTime.toLocalDate()))
                .replaceAll(VAR_BRACKET_OPEN + ENDDATE + VAR_BRACKET_CLOSE, String.valueOf(endTime.toLocalDate()))
                .replaceAll(VAR_BRACKET_OPEN + AGG + VAR_BRACKET_CLOSE, agg);
    }

    /**
     * @param dayResponse            -- Response object
     * @param sysId                  -- device ID
     * @param subscription           -- subscription
     * @param subscriptionLastRecord -- last record
     * @param startTime              -- fetch from start time
     * @param endTime                -- fetch till end time
     * @param deviceUrlDay           -- device URL for aggregation "day"
     */
    @Deprecated
    private void dumpForDays(TigoResponseDTO dayResponse, String sysId, ExtDataStageDefinition subscription,
                             MonitorReading subscriptionLastRecord, String startTime, String endTime, String deviceUrlDay) {
        List<LocalDateTime> timeList = Utility.getDays(Utility.toLocalDateTime(Utility.getStartOfDate(
                        Utility.toDateWithFormat(startTime + SECOND_FORMAT, Utility.DATE_TIME_FORMAT))),
                Utility.toLocalDateTime(Utility.getStartOfDate(
                        Utility.toDateWithFormat(endTime + SECOND_FORMAT, Utility.DATE_TIME_FORMAT))));
        for (LocalDateTime STDate : timeList) {
            jSonDecodingForHistoric(STDate, deviceUrlDay, sysId, DAY, subscription, subscriptionLastRecord,
                    dayResponse);
        }
    }

    /**
     * @param monthResponse          -- Response object
     * @param sysId                  -- device ID
     * @param subscription           -- subscription
     * @param subscriptionLastRecord -- last record
     * @param startTime              -- fetch from start time
     * @param endTime                -- fetch from end time
     * @param deviceUrlMonthly       -- device URL for aggregation "month"
     */
    @Deprecated
    private void dumpForMonths(TigoResponseDTO monthResponse, String sysId, ExtDataStageDefinition subscription,
                               MonitorReading subscriptionLastRecord, String startTime, String endTime, String deviceUrlMonthly) {
        //The following conversion is for matching the time parameter in tigo API response
        LocalDateTime startMonth = Utility.toLocalDateTime(Utility.getStartOfMonth(
                Utility.toDateWithFormat(startTime + SECOND_FORMAT, Utility.DATE_TIME_FORMAT)));
        LocalDateTime endMonth = Utility.toLocalDateTime(Utility.getStartOfMonth(
                Utility.toDateWithFormat(endTime + SECOND_FORMAT, Utility.DATE_TIME_FORMAT)));
        List<LocalDateTime> timeList = Utility.getMonths(startMonth, endMonth);
        for (LocalDateTime STDate : timeList) {
            jSonDecodingForHistoric(STDate, deviceUrlMonthly, sysId, MONTH, subscription, subscriptionLastRecord,
                    monthResponse);
        }
    }

    /**
     * @param stDate                 -- start date
     * @param deviceUrlMonthly       -- device url month
     * @param sysId                  -- device ID
     * @param agg                    -- aggregation type
     * @param subscription           -- subscription
     * @param subscriptionLastRecord -- last record
     * @param response               -- Response object
     */
    @Deprecated
    private void jSonDecodingForHistoric(LocalDateTime stDate, String deviceUrlMonthly, String sysId,
                                         String agg, ExtDataStageDefinition subscription,
                                         MonitorReading subscriptionLastRecord, TigoResponseDTO response) {
        if (response != null) {
            String formattedLocalDate = null;
            if (agg.equals(DAY)) {
                formattedLocalDate = Utility.getFormattedDate(stDate);
            }
            //Setting up URLs for Query
            solrenviewService.save(tigoHistoricMapper(Objects.requireNonNull(response.getSeriesList().stream()
                    .filter(s -> s.getName().equals(SYS_ENERGY_BASE))
                    .findFirst().orElse(null)
            ), stDate, sysId, deviceUrlMonthly, subscription, subscriptionLastRecord, formattedLocalDate, agg));
            new ResponseEntity<>(HttpStatus.OK);
            return;
        }
        LOGGER.error("Getting empty response from TIGO URL");
    }

    /**
     * @param series                 -- Response object
     * @param stDate                 -- start date
     * @param sysId                  -- device ID
     * @param deviceUrlMonthly       -- device URL for aggregation "month"
     * @param subscription           -- subscription
     * @param subscriptionLastRecord -- last record
     * @param formattedLocalDate     -- formatted date "yyyy/MM/dd"
     * @param agg                    -- aggregation type
     * @return -- MonitorReading
     */
    @Deprecated
    private MonitorReading tigoHistoricMapper(Series series, LocalDateTime stDate, String sysId, String deviceUrlMonthly,
                                              ExtDataStageDefinition subscription, MonitorReading subscriptionLastRecord,
                                              String formattedLocalDate, String agg) {
        SeriesData seriesData;
        Date date;
        if (agg.equals(DAY)) {
            seriesData = series.getData().stream().filter(s -> s.getName().equals(formattedLocalDate)).findFirst().orElse(null);
            date = Utility.toDate(stDate);
            MonitorReading monitorReading = monitorReadingDTOBuilder(sysId, date, subscriptionLastRecord, deviceUrlMonthly, seriesData, stDate, agg);
            saveMonitorReadingDaily(monitorReading, subscription, date);
            return monitorReading;
        } else {
            seriesData = series.getData().stream().filter(s -> s.getName().equals(stDate + SECOND_FORMAT)).findFirst().orElse(null);
            date = seriesData == null ? Utility.toDate(stDate) : Utility.toDate((String) seriesData.getValue()[0]);
            return monitorReadingDTOBuilder(sysId, date, subscriptionLastRecord, deviceUrlMonthly, seriesData, stDate, agg);
        }
    }

    /**
     * @param sysId                  -- device ID
     * @param date                   -- date
     * @param subscriptionLastRecord -- subscription last record
     * @param deviceUrlMonthly       -- device url month
     * @param seriesData             -- series data
     * @param stDate                 -- start date
     * @param agg                    -- aggregate
     * @return -- MonitorReading
     */
    @Deprecated
    private MonitorReading monitorReadingDTOBuilder(String sysId, Date date, MonitorReading subscriptionLastRecord,
                                                    String deviceUrlMonthly, SeriesData seriesData, LocalDateTime stDate, String agg) {
        return MonitorReading.builder()
                .currentValue(0.0)
                .inverterNumber(sysId)
                .time(date)
                .yieldValue(0.0)
                .subscriptionIdMongo(subscriptionLastRecord.getSubscriptionIdMongo())
                .durl(deviceUrlMonthly)
                .monthlyYield(seriesData == null ? 0.0 : Double.parseDouble(String.valueOf(seriesData.getValue()[1])) / 1000)
                .dailyYield(agg.equals(MONTH) ? 0.0 : seriesData == null ? 0.0 : Double.parseDouble(String.valueOf(seriesData.getValue()[1])))
                .annualYield(0.0)
                .grossYield(0.0)
                .logs(getLogs(stDate, null, seriesData == null ? 0.0 : Double.parseDouble(String.valueOf(seriesData.getValue()[1])) / 1000, 0.0,
                        0.0, deviceUrlMonthly))
                .build();
    }

    /**
     * Gets the current data from TIGO Inverter
     * Used for Half Hour Schedule
     *
     * @return response
     */
    @Async
    @Override
    public ResponseEntity<String> getMinuteData(String startTime, String endTime) {
        LOGGER.info("Entering getMinuteData() for TIGO Inverter");

        ResponseEntity<String> stringResponseEntity = null;
        for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(BRAND_TIGO)) {
            try {
                String deviceUrl = Utility.getDeviceUrl(subscription.getMpJson());
                if (deviceUrl == null || deviceUrl.equals("")) {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
                String deviceNo = Utility.getMeasureAsJson(subscription.getMpJson(), DEVICE_NUMBER);
                String sysId = Utility.getMeasureAsJson(subscription.getMpJson(), STID);
                String sAuth = Utility.getMeasureAsJson(subscription.getMpJson(), S_AUTK);
                //Current Data
                if (startTime == null && endTime == null) {
                    stringResponseEntity = getCurrentData(subscription, sysId, deviceUrl, deviceNo, sAuth);
                    //Historic Data
                } else {
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
        TigoResponseDTO minuteResponse = getTigoResponse(deviceUrlQ1);
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
        TigoResponseDTO minuteResponse = getTigoResponse(deviceUrlQ1);
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
                                 ExtDataStageDefinition subscription, TigoResponseDTO minuteResponse, String deviceNo, String sAuth) {
        for (Date targetTime : timeList) {
            LocalDateTime localDateTime = Utility.toLocalDateTimeEastUs(targetTime);
            TigoResponseDTO hourResponse = localDateTime.getMinute() == 0 ? generateHourResponse(deviceUrl, localDateTime, sysId, deviceNo, sAuth) : null;
            jSonDecoding(targetTime, deviceUrlQ1, sysId, subscription,
                    solrenviewService.subscriptionLastRecord(subscription.getSubsId(), deviceNo.equals("-1") ? sysId : deviceNo), minuteResponse, hourResponse, localDateTime, deviceNo);
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
                              TigoResponseDTO minuteResponse, TigoResponseDTO hourResponse, LocalDateTime localDateTime, String deviceNo) {

        if (deviceUrl != null && !deviceUrl.isEmpty()) {
            //Setting up URLs for Query
            solrenviewService.save(tigoMapper(Objects.requireNonNull(minuteResponse.getSeriesList().stream()
                            .filter(s -> s.getName().toLowerCase().contains(Constants.RATE_CODES.TOTAL.toLowerCase()))
                            .findFirst().orElse(null)),
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
                                      TigoResponseDTO hourResponse, LocalDateTime localDateTime, String deviceNo) {
        SeriesData minuteData = series.getData().stream().filter(s -> s.getName().equals(Utility.toLocalDateTimeEastUs(targetTime).toString() + SECOND_FORMAT)).findFirst().orElse(null);
        SeriesData hourData = null;
        if (hourResponse != null) {
            Optional<Series> hourSeriesData = hourResponse.getSeriesList().stream().filter(
                    s -> s.getName().toLowerCase().contains(Constants.RATE_CODES.TOTAL.toLowerCase())).findFirst();
            hourData = hourSeriesData.flatMap(value -> value.getData().stream().filter(s -> s.getName().equals(Utility.toLocalDateTimeEastUs(targetTime).toString() + SECOND_FORMAT)).findFirst()).orElse(null);
        }
        String inverterNumber = deviceNo.equals("-1") ? sysId : deviceNo;
        Map<String, String> data = getValidData(subscriptionLastRecord, hourData, targetTime);
        MonitorReading monitorReading = MonitorReading.builder()
                .currentValue(minuteData == null ? 0.0 : Double.parseDouble(String.valueOf(minuteData.getValue()[1])))
                .inverterNumber(inverterNumber)
                .time(targetTime)
                .yieldValue(minuteData == null ? 0.0 : Double.parseDouble(String.valueOf(minuteData.getValue()[1])))
                .subscriptionIdMongo(subscriptionLastRecord.getSubscriptionIdMongo())
                .durl(deviceUrl)
                .monthlyYield(Double.parseDouble(data.get(MONTHLY_YIELD)))
                .dailyYield(Double.parseDouble(data.get(DAILY_YIELD)))
                .annualYield(Double.parseDouble(data.get(ANNUAL_YIELD)))
                .grossYield(Double.parseDouble(data.get(GROSS_YIELD)))
                .peakValue(solrenviewService.getHighestCurrentValueOfTheDay(Utility.toDate(localDateTime), inverterNumber, subscriptionLastRecord.getSubscriptionIdMongo()))
                .logs(getLogs(null, targetTime, minuteData == null ? 0.0 : Double.parseDouble(String.valueOf(minuteData.getValue()[1])),
                        Double.parseDouble(data.get(ANNUAL_YIELD)),
                        Double.parseDouble(data.get(GROSS_YIELD)), deviceUrl))
                .build();
        if (Utility.toLocalDateTimeEastUs(targetTime).getMinute() == 0) {
            saveMonitorReadingDaily(monitorReading, extDataStageDefinition, targetTime);
        }
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

    /**
     * @param monitorReading         -- MonitorReading
     * @param extDataStageDefinition -- ext data stage definition object
     * @param date                   -- date
     */
    private void saveMonitorReadingDaily(MonitorReading monitorReading, ExtDataStageDefinition extDataStageDefinition, Date date) {
        solrenviewService.saveMonitorDaily(monitorReading, extDataStageDefinition, date);
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
     * @param deviceUrl -- device url
     * @param stDate    -- start date
     * @param sysId     -- device id
     * @param deviceNo  -- device number
     * @param sAuth     -- authentication key
     * @return -- setup URL for response from TIGO INVERTER
     */
    private TigoResponseDTO generateHourResponse(String deviceUrl, LocalDateTime stDate, String sysId, String deviceNo, String sAuth) {
        String deviceUrlQ2 = setQURL(deviceUrl, stDate, sysId, com.solar.api.Constants.RATE_CODES.HOUR, deviceNo, sAuth);
        return getTigoResponse(deviceUrlQ2);
    }

    /**
     * @param deviceUrlQ -- device url
     * @return -- response from TIGO INVERTER
     */
    private TigoResponseDTO getTigoResponse(String deviceUrlQ) {
        try {
            ResponseEntity<String> query = WebUtils.submitRequest(HttpMethod.GET, deviceUrlQ, null, new HashMap<>(), String.class);
            return new ObjectMapper().readValue(query.getBody()
                    , TigoResponseDTO.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
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
}
