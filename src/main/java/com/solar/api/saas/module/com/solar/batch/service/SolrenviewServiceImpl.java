package com.solar.api.saas.module.com.solar.batch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.tenant.mapper.BaseResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.SolrenviewResponseDTO;
import com.solar.api.tenant.model.stage.monitoring.chart;
import com.solar.api.tenant.model.stage.monitoring.data;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static com.solar.api.helper.Utility.toLocalDateTimeEastUs;

@Service
public class SolrenviewServiceImpl implements SolrenviewService {

    protected final Logger LOGGER = LoggerFactory.getLogger(SolrenviewServiceImpl.class);
    @Autowired
    MonitorReadingRepository monitorReadingRepository;
    @Autowired
    private MonitorReadingDailyRepository dailyRepository;
    @Autowired
    private StageMonitorService stageMonitorService;

    @Autowired
    private MonitorPlatformUtilityService monitorPlatformUtilityService;

    @Autowired
    private EGaugeService eGaugeService;

    /**
     * Author: Shaikh M. SHariq
     * Date: 5th May 2023
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public ResponseEntity<String> getSVHistoricData(String startTime, String endTime) {
        LOGGER.info("Entering getSVHistoricData: startTime: {}, endTime: {}", startTime, endTime);
        Map<String, String> subscriptionSuccessMessage = new HashMap<>();
        Map<String, List<String>> subscriptionErrorMessage = new HashMap<>();
        for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(Constants.MONITOR_PLATFORM.SOLRENVIEW)) {
            try {
                //Getting required measures
                String deviceUrl = Utility.getDeviceUrl(subscription.getMpJson());
                String svdId = Utility.getMeasureAsJson(subscription.getMpJson(), Constants.RATE_CODES.SVDID);
                String subStartDate = Utility.getMeasureAsJson(subscription.getMpJson(), Constants.RATE_CODES.SUB_START_DATE);
                checkMandatoryMeasures(deviceUrl, svdId, subStartDate, subscription.getSubsId(), subscriptionErrorMessage);
                if (deviceUrl == null || deviceUrl.isEmpty() || svdId == null || svdId.isEmpty() || subStartDate == null
                        || subStartDate.isEmpty()) {
                    continue;
                }

                List<Date> dateTimestamps = Utility.getTimeStamps(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startTime),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endTime), 30);

                for (Date STDate : dateTimestamps) {
                    jSonDecoding(STDate, deviceUrl, svdId, subStartDate, subscription);
                }
                subscriptionSuccessMessage.put(subscription.getSubsId(), "Data imported for dateTimestamps " + dateTimestamps);
            } catch (Exception e) {
                composeCaughtException(e, subscription, subscriptionErrorMessage);
            }
        }
        LOGGER.info("Exiting getSVHistoricData: startTime: {}, endTime: {}", startTime, endTime);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void composeCaughtException(Exception e, ExtDataStageDefinition subscription, Map<String, List<String>> subscriptionErrorMessage) {
        if (e == null) {
            Utility.batchNotification("Solrenview", null, subscription.getMpJson() + " " + subscription.getBrand() + " " + subscription.getSubsId(), "EXCEPTION");
            composeErrorLog(subscription.getSubsId(), String.format("Exception is null for mpJson %s brand %s",
                    subscription.getMpJson(), subscription.getBrand()), subscriptionErrorMessage);
        }
        Utility.batchNotification("Solrenview", null, e.getMessage(), "EXCEPTION");
        composeErrorLog(subscription.getSubsId(), e.getMessage(), subscriptionErrorMessage);
    }

    @Override
    public BaseResponse getSVData() {
        Map<String, String> subscriptionSuccessMessage = new HashMap<>();
        Map<String, List<String>> subscriptionErrorMessage = new HashMap<>();
        for (ExtDataStageDefinition subscription : stageMonitorService.getAllSubscriptions(Constants.MONITOR_PLATFORM.SOLRENVIEW)) {
            try {

                //Getting required measures
                String deviceUrl = Utility.getDeviceUrl(subscription.getMpJson());
                String svdId = Utility.getMeasureAsJson(subscription.getMpJson(), Constants.RATE_CODES.SVDID);
                String subStartDate = Utility.getMeasureAsJson(subscription.getMpJson(), Constants.RATE_CODES.SUB_START_DATE);
                checkMandatoryMeasures(deviceUrl, svdId, subStartDate, subscription.getSubsId(), subscriptionErrorMessage);
                if (deviceUrl == null || deviceUrl.isEmpty() || svdId == null || svdId.isEmpty() || subStartDate == null
                        || subStartDate.isEmpty()) {
                    continue;
                }

                MonitorReading subscriptionLastRecord = subscriptionLastRecord(subscription.getSubsId(), svdId);
                Date startDate = validateSubStartDate(subscriptionLastRecord, subStartDate);
                List<Date> dateTimestamps = Utility.getTimeStamps(startDate, Utility.addMinutes(startDate, 30), 30);

                for (Date STDate : dateTimestamps) {
                    jSonDecoding(STDate, deviceUrl, svdId, subStartDate, subscription);
                }
                subscriptionSuccessMessage.put(subscription.getSubsId(), "Data imported for dateTimestamps " + dateTimestamps);
            } catch (Exception e) {
                composeCaughtException(e, subscription, subscriptionErrorMessage);
            }
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).response(subscriptionSuccessMessage)
                .errors(subscriptionErrorMessage).build();
    }

    private void checkMandatoryMeasures(String deviceUrl, String svdId, String subStartDate, String subsId,
                                        Map<String, List<String>> subscriptionMessage) {
        if (deviceUrl == null || deviceUrl.isEmpty()) {
            composeErrorLog(subsId, "DURL is not defined", subscriptionMessage);
        }
        if (svdId == null || svdId.isEmpty()) {
            composeErrorLog(subsId, "SVDID is not defined", subscriptionMessage);
        }
        if (subStartDate == null || subStartDate.isEmpty()) {
            composeErrorLog(subsId, "S_SSDT is not defined", subscriptionMessage);
        }
    }

    private void composeErrorLog(String subsId, String message, Map<String, List<String>> subscriptionMessage) {
        if (subscriptionMessage.get(subsId) == null) {
            subscriptionMessage.put(subsId, new ArrayList<>());
        }
        subscriptionMessage.get(subsId).add(message);
        LOGGER.error(message + " for Solrenview subscription with id {}" + subsId);
    }

    @Override
    public SolrenviewResponseDTO hitURL() throws JsonProcessingException {
        String deviceUrl = "https://www.solrenview.com/srvp/FSC/SRV/getSumInfoData1.php?queryNo=1&startIntervalTs=2023-06-02T00:00&endIntervalTs=2023-06-02T15:30&dataInterval=300&deviceindices=38412&par=ecount&hasRg=0&maxLimit=25000&gauge=0&IrrPrct=1";
        ResponseEntity<String> query = WebUtils.submitRequest(HttpMethod.GET, deviceUrl, null, new HashMap<>(), String.class);
        return new ObjectMapper().readValue(query.getBody()
                , SolrenviewResponseDTO.class);
    }

    @Override
    public MonitorReading subscriptionLastRecord(String subsId, String inverterNumber) {
        return eGaugeService.subscriptionLastRecord(subsId, inverterNumber);
    }

    @Override
    public void save(MonitorReading monitorReading) {
        eGaugeService.save(monitorReading);
    }

    @Override
    public void saveMonitorDaily(MonitorReading monitorReading, ExtDataStageDefinition subscription, Date date) {
        if (monitorReading != null) {
            try {
                MonitorReadingDaily mrd = dailyRepository.getLastSavedRecordByMongoSubIdAndDate(subscription.getSubsId(), Utility.getDateString(date, Utility.SYSTEM_DATE_FORMAT));
                if (mrd == null) {
                    dailyRepository.save(MonitorReadingDaily.builder()
                            .subscriptionIdMongo(subscription.getSubsId())
                            .inverterNumber(monitorReading.getInverterNumber())
                            .yieldValue(monitorReading.getDailyYield() < 0 ? 0 : monitorReading.getDailyYield())
                            .day(monitorReading.getTime())
                            .build());
                } else {
                    if (monitorReading.getDailyYield() != null) {
                        mrd.setYieldValue(monitorReading.getDailyYield() > mrd.getYieldValue()
                                        ? monitorReading.getDailyYield() : mrd.getYieldValue());
                        dailyRepository.save(mrd);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private Date validateSubStartDate(MonitorReading subscriptionLastRecord, String subStartDate) {
        if (Utility.isBefore(new Date(subStartDate), subscriptionLastRecord.getTime())) {
            return subscriptionLastRecord.getTime();
        } else {
            return new Date(subStartDate);
        }
    }

    private LocalDateTime getConvertedDate(Date stDate, String differentialHours) {
        differentialHours = "-,04,00";
        String[] arr1 = differentialHours.split(",", 2);
        String operator = arr1[0];
        String[] arr2 = arr1[1].split(",", 2);
        String hour = arr2[0];
        String min = arr2[1];
        return Utility.subtractHours(stDate, operator, hour, min);

    }

    private ResponseEntity<String> jSonDecoding(Date rawDate, String deviceUrl, String svdId,
                                                String subStartDate, ExtDataStageDefinition subscription) {
        List<MonitorReading> monitorReadings = new ArrayList<>();
        Date hexDate = null;
        if (rawDate.getMinutes() == 25 || rawDate.getMinutes() == 55) {
            hexDate = Utility.addMinutes(rawDate, 5);
        } else {
            hexDate = rawDate;
        }

        //The following function will fulfill the requirement for 7 iteration indexing
        // as implemented in the for loop below
        List<String> timeParams = validateDate(rawDate);
        String startTime = timeParams.get(0);
        String endTime = timeParams.get(1);
        if (deviceUrl != null && !deviceUrl.isEmpty()) {
            //Setting up URLs for Query1 and Query2
            String deviceUrlQ1 = setQ1URL(deviceUrl, startTime, endTime, svdId);
            String deviceUrlQ2 = setQ2URL(deviceUrlQ1);

            //Fetching data for Solrenview platform APIs
            SolrenviewResponseDTO responseQuery1 = getSVResponse(deviceUrlQ1, subscription);
            SolrenviewResponseDTO responseQuery2 = getSVResponse(deviceUrlQ2, subscription);
            if (responseQuery1 == null || responseQuery2 == null) {
                LOGGER.error("Cannot get valid response from client");
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            MonitorReading subscriptionLastRecord = subscriptionLastRecord(subscription.getSubsId(), svdId);
            chart chart = responseQuery1.getChart();
            Double dailyYield = 0.0;
            Double grossYield = 0.0;
            boolean chartFlag = false;
            if (chart != null && chart.getCaption() != null && responseQuery2.getChart() != null
                    && responseQuery2.getChart().getCaption() != null) {
                chartFlag = true;
                dailyYield = Double.valueOf(StringUtils.substringBetween(chart.getCaption(), "[", "kWh]"));
                grossYield = Double.valueOf(StringUtils.substringBetween(responseQuery2.getChart().getCaption(), "[", "kWh]"));
            }

            List<data> data = responseQuery1.getData();
            for (int i = 1; i <= 6; i++) {
                monitorReadings.add(solrenviewMapper(data.size() <= 6 && i == 6 ? data.get(data.size() - 1) : data.get(i), dailyYield, grossYield,
                        hexDate, subscriptionLastRecord, svdId, subscription, deviceUrl, startTime, endTime, deviceUrlQ1, deviceUrlQ2, data.size(), chartFlag));
                if (i == 6) {
                    saveMonitorDaily(monitorReadings.get(data.size() <= 6 ? (data.size() - 1) : (i - 1)), subscription, hexDate);
                }
                hexDate = Utility.addMinutes(hexDate, 5);
            }
            eGaugeService.save(monitorReadings);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Data is SolrenView API doesn't handle day end changes
     * The following function will mark startTime as 00:00 instead of 23:55
     *
     * @param date
     * @return
     */
    private List<String> validateDate(Date date) {
        List<String> timeParams = new ArrayList<>();
        LocalDateTime convertedDate = toLocalDateTimeEastUs(date); //00:00

        if (convertedDate.getMinute() == 0 || convertedDate.getMinute() == 30) {
            //either 00:25 or 00:55
            convertedDate = convertedDate.minusMinutes(5L);
        }

        //01-01-2023 23:55 endTime: 02-01-2023 00:25
        if (Utility.isDayBefore(convertedDate, convertedDate.plusMinutes(30L))) {
            timeParams.add(convertedDate.plusMinutes(5L).toString());
            timeParams.add(convertedDate.plusMinutes(30L).toString());
        } else if (convertedDate.getMinute() == 25 || convertedDate.getMinute() == 55) {
            timeParams.add(convertedDate.toString());
            timeParams.add(convertedDate.plusMinutes(30L).toString());
        }
        return timeParams;
    }

    private MonitorReading solrenviewMapper(data data, Double dailyYield, Double grossYield,
                                            Date hex, MonitorReading subscriptionLastRecord, String svdId,
                                            ExtDataStageDefinition subscription, String deviceUrl, String startTime,
                                            String endTime, String deviceUrlQ1, String deviceUrlQ2, int size, boolean chartFlag) {
        Double cv = getCurrentValue(data.getDisplayValue());
        Double monthlyYield = getMonthlyYield(hex, dailyYield, subscriptionLastRecord);
        Double yearlyYield = getYearlyYield(hex, subscriptionLastRecord);
        return MonitorReading.builder()
                .currentValue(cv)
                .dailyYield(dailyYield)
                .inverterNumber(svdId)
                .site(null)
                .monthlyYield(monthlyYield)
                .sytemSize(0.00)
                .time(hex)
                .annualYield(yearlyYield)
                .yieldValue(cv)
                .subscriptionIdMongo(subscription.getSubsId())
                .durl(deviceUrl)
                .grossYield(grossYield)
                .logs(getLogs(hex, startTime, endTime, cv, dailyYield, monthlyYield, yearlyYield, grossYield,
                        deviceUrlQ1, deviceUrlQ2, size, chartFlag))
                .build();
    }

    private String getLogs(Date hex, String startTime, String endTime, Double cv, Double dailyYield, Double monthlyYield,
                           Double yearlyYield, Double grossYield, String deviceUrlQ1, String deviceUrlQ2, int size, boolean chartFlag) {
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("rawDate", hex.toString());
        requestMessage.put("startTime", startTime);
        requestMessage.put("endTime", endTime);
        requestMessage.put("currentValue", cv);
        requestMessage.put("dailyYield", dailyYield);
        requestMessage.put("monthlyYield", monthlyYield);
        requestMessage.put("yearlyYield", yearlyYield);
        requestMessage.put("grossYield", grossYield);
        requestMessage.put("deviceUrlQ1", deviceUrlQ1);
        requestMessage.put("deviceUrlQ2", deviceUrlQ2);
        requestMessage.put("ObjectSize", size);
        requestMessage.put("Chart Data exist", chartFlag);
        return requestMessage.toPrettyString();
    }

    @Override
    public Double getMonthlyYield(Date date, Double dailyYield, MonitorReading lastRecord) {
        if (date.getDate() == 1 && date.getHours() == 00 && date.getMinutes() == 00) {
            return 0.0;
        } else if (date.getDate() == 1) {
            return dailyYield;
        } else if (date.getDate() > 1) {
            return getLastDayOfLastMonth(date, lastRecord) + getDailyYield(date, lastRecord.getInverterNumber(), lastRecord.getSubscriptionIdMongo());
        } else {
            return 0.0;
        }
    }

    @Override
    public Double getDailyYield(Date date, String inverterNo, String subsID) {
        Double val = eGaugeService.getHighestValueOfTheDay(date, inverterNo, subsID);
        return val == null ? 0.0 : val;
    }

    @Override
    public Double getHighestCurrentValueOfTheDay(Date date, String inverterNo, String subsID) {
        Double val = eGaugeService.getHighestCurrentValueOfTheDay(date, inverterNo, subsID);
        return val == null ? 0.0 : val;
    }

    @Override
    public Double getYearlyYield(Date date, MonitorReading lastRecord) {
        if (date.getMonth() == 0 && date.getDate() == 1 && date.getHours() == 00 && date.getMinutes() == 00) {
            return 0.0;
        } else if (date.getMonth() == 1) {
            return lastRecord.getMonthlyYield() == null ? 0.0 : lastRecord.getMonthlyYield();
        } else if (date.getMonth() > 1) {
            return lastRecord.getMonthlyYield() == null
                    ? 0.0 + getLastDayOfLastYear(date, lastRecord)
                    : lastRecord.getMonthlyYield() + getLastDayOfLastYear(date, lastRecord);
        } else {
            return 0.0;
        }
    }

    private Double getLastDayOfLastMonth(Date date, MonitorReading lastRecord) {
//        Date tempDate = date;
//        tempDate.setMonth(tempDate.getMonth() - 1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, 55);
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        MonitorReading monitorReading = eGaugeService.subscriptionLastRecord(lastRecord.getSubscriptionIdMongo(), lastRecord.getInverterNumber(), cal.getTime());
        return monitorReading != null ? monitorReading.getMonthlyYield() != null
                ? monitorReading.getMonthlyYield() : 0.0 : 0.0;
    }

    private Double getLastDayOfLastYear(Date date, MonitorReading lastRecord) {
//        Date tempDate = date;
//        tempDate.setMonth(tempDate.getMonth() - 1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, 55);
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        cal.add(Calendar.YEAR, -1);
        cal.add(Calendar.MONTH, 12);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        MonitorReading monitorReading = eGaugeService.subscriptionLastRecord(lastRecord.getSubscriptionIdMongo(), lastRecord.getInverterNumber(), cal.getTime());
        if (monitorReading != null && monitorReading.getMonthlyYield() != null) {
            return monitorReading.getMonthlyYield();
        } else {
            return 0.0;
        }
    }

    private Double getCurrentValue(String displayValue) {
        if (!displayValue.equals("")) {
            return (Double.valueOf(displayValue) * 60) / 5;
        }
        return 0.0;
    }

    private SolrenviewResponseDTO getSVResponse(String deviceUrl, ExtDataStageDefinition subscription) {
        ResponseEntity<String> query = null;
        try {
            query = WebUtils.submitRequest(HttpMethod.GET, deviceUrl, null, new HashMap<>(), String.class);
            if (!String.valueOf(query.getStatusCodeValue()).contains("20")) {
                triggerOutages(query.getStatusCodeValue(), subscription);
            } else {
                try {
                    return new ObjectMapper().readValue(query.getBody()
                            , SolrenviewResponseDTO.class);
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        } catch (Exception exception) {
            if((((HttpServerErrorException.InternalServerError) exception).getRawStatusCode() != 200)) {
                triggerOutages(((HttpServerErrorException.InternalServerError) exception).getRawStatusCode(),subscription);
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
            LOGGER.error("STATUS CODE IS : {}", statusCodeValue);
        }
    }

    private String setQ2URL(String deviceUrlQ1) {
        return deviceUrlQ1.replaceAll(Constants.RATE_CODES.QUERYNUMBER1, Constants.RATE_CODES.QUERYNUMBER2);
    }

    private String setQ1URL(String deviceUrl, String startTime, String endTime, String svdId) {
        return deviceUrl
                .replaceAll("\\{\\{" + Constants.RATE_CODES.QUERYNUMBER + "\\}\\}", Constants.RATE_CODES.QUERY1)
                .replaceAll("\\{\\{" + Constants.RATE_CODES.START_TIME_INTERVAL + "\\}\\}", startTime)
                .replaceAll("\\{\\{" + Constants.RATE_CODES.END_TIME_INTERVAL + "\\}\\}", endTime)
                .replaceAll("\\{\\{" + Constants.RATE_CODES.SVDID + "\\}\\}", svdId);
    }
}
