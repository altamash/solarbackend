package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.GraphDataDTO;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingMapper;
import com.solar.api.tenant.model.pvmonitor.*;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.repository.MonitoringDashboardDailyWiseRepository;
import com.solar.api.tenant.repository.MonitoringDashboardYearWiseRepository;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service("monitoringDailyService")
public class MonitoringDashboardDailyServiceImpl implements MonitoringDashboardService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private MonitoringDashboardYearWiseRepository monitoringDashboardYearWiseRepository;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    @Autowired
    private MonitoringDashboardDailyWiseRepository monitoringDashboardDailyWiseRepository;
    @Autowired
    private Utility utility;
    @Override
    public ResponseEntity<?> getCumulativeGraphData(MonitorAPIAuthBody body) {
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingDayWise>> cumulativeMapMongo = new TreeMap<>();
        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<String> daysForMonth = null;
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        SimpleDateFormat outputDayFormat = new SimpleDateFormat(Utility.DAY_FORMAT);
        try {
            //this will run for variant ids when not null (site dashboard)
            if(body.getVariantIds()!= null && body.getVariantIds().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(),Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE);
                //this will run for variant ids when not null (customer and subscription dashboard)
            }else if(body.getSubscriptionIdsMongo()!= null &&  body.getSubscriptionIdsMongo().size() > 0){
                extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            }
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                //this code bring subscription monitorReadingDaily data and fill its missing data
                cumulativeMapMongo.put(ext.getSubsId(), getDailyDBDataForSubscription(ext, body.getYearMonth()));
            }
            //this code group the different subscriptions on the same date
            Map<String, List<MonitorAPIResponse>> groupedData = cumulativeMapMongo.values().stream()
                    .flatMap(Collection::stream)
                    .map(MonitorReadingMapper::toMonitorAPIResponse) // Convert to MonitorAPIResponse
                    .collect(Collectors.groupingBy(
                            MonitorAPIResponse::getDay,
                            TreeMap::new,  // Use TreeMap with custom comparator for sorted keys
                            Collectors.toList()
                    ));

            //this code sum the different subscriptions on the same date
            groupedData.forEach((day, responsesForDay) -> {
                List<String> inverterNumbers = responsesForDay.stream().map(MonitorAPIResponse::getInverterNumber).filter(Objects::nonNull).collect(Collectors.toList());
                Double yieldValueSum = responsesForDay.stream().mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                try {
                    Date date = inputDateFormat.parse(day);
                    String formattedDay = outputDayFormat.format(date);

                    responses.add(MonitorAPIResponse.builder()
                            .day(formattedDay)
                            .yieldValue(yieldValueSum)
                            .inverterNumbers(inverterNumbers)
                            .build());
                } catch (ParseException e) {
                    LOGGER.error("Error parsing date: " + day, e);
                }
            });
            //get axis labels
            daysForMonth = groupedData.keySet().stream()
                    .sorted()
                    .map(day -> {
                        try {
                            Date date = inputDateFormat.parse(day);
                            return outputDayFormat.format(date);
                        } catch (ParseException e) {
                            LOGGER.error("Error parsing date: " + day, e);
                            return ""; // Handle error case
                        }
                    })
                    .collect(Collectors.toList());
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        cumulativeMap.put(String.valueOf(-1L), responses);
        return ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(cumulativeMap)
                .xAxis(daysForMonth)
                .yAxis("kWh").build());
    }

    @Override
    public ResponseEntity<?> getSubscriptionComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingDayWise>> cumulativeMapMongo = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> response = null;
        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                cumulativeMapMongo.put(ext.getSubsId(), getDailyDBDataForSubscription(ext, body.getYearMonth()));
            }
            response = cumulativeMapMongo.values().stream()
                    .flatMap(Collection::stream)
                    .map(MonitorReadingMapper::toMonitorAPIComparativeResponse) // Convert to MonitorAPIResponse
                    .collect(Collectors.groupingBy(
                            MonitorAPIResponse::getSubscriptionIdMongo,
                            TreeMap::new,  // Use TreeMap with custom comparator for sorted keys
                            Collectors.toList()
                    ));
            //this code sum the different subscriptions on the same date
        }catch  (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(response)
                .xAxis(getXAxis(body))
                .yAxis("kWh").build());

    }

    @Override
    public ResponseEntity<?> getCustomerComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinitionDTO> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> response =  new TreeMap<>();
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        SimpleDateFormat outputDayFormat = new SimpleDateFormat(Utility.DAY_FORMAT);
        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllSubsAndCustomerBySubIds(body.getSubscriptionIdsMongo());
            Map<Long, List<ExtDataStageDefinitionDTO>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinitionDTO::getAcctId, toList()));
            for (Long acctId : extDataStageDefinitionMap.keySet()) {
                List<ExtDataStageDefinition> stageDefinitionList = extDataStageDefinitionMap.get(acctId).stream().map(MonitorReadingMapper::toExtDataStageDefinition).collect(toList());
                Map<String, List<MonitorReadingDayWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingDayWise> subscriptionDailyGraphData = getDailyDBDataForSubscription(ext, body.getYearMonth() );
                    if (cumulativeMapMongo.get(String.valueOf(acctId)) != null) {
                        List<MonitorReadingDayWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(acctId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionDailyGraphData);
                        cumulativeMapMongo.put(String.valueOf(acctId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(acctId), subscriptionDailyGraphData);
                    }
                }
                //grouping subscriptions on same date
                Map<String, List<MonitorAPIResponse>> groupedData = cumulativeMapMongo.values().stream()
                        .flatMap(Collection::stream)
                        .map(MonitorReadingMapper::toMonitorAPIResponse) // Convert to MonitorAPIResponse
                        .collect(Collectors.groupingBy(
                                MonitorAPIResponse::getDay,
                                TreeMap::new,  // Use TreeMap with custom comparator for sorted keys
                                Collectors.toList()
                        ));
                //summing data on same day
                groupedData.forEach((day, responsesForDay) -> {
                    List<String> inverterNumbers = responsesForDay.stream().map(MonitorAPIResponse::getInverterNumber).filter(Objects::nonNull).collect(Collectors.toList());
                    Double yieldValueSum = responsesForDay.stream().mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                    try {
                        Date date = inputDateFormat.parse(day);
                        String formattedDay = outputDayFormat.format(date);

                        responses.add(MonitorAPIResponse.builder()
                                .day(formattedDay)
                                .yieldValue(yieldValueSum)
                                .inverterNumbers(inverterNumbers)
                                .build());
                    } catch (ParseException e) {
                        LOGGER.error("Error parsing date: " + day, e);
                    }
                });
                response.put(String.valueOf(acctId), responses);
            }
        }catch  (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(response)
                .xAxis(getXAxis(body))
                .yAxis("kWh").build());
    }

    @Override
    public ResponseEntity<?> getSitesComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> response =  new TreeMap<>();
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        SimpleDateFormat outputDayFormat = new SimpleDateFormat(Utility.DAY_FORMAT);
        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(),Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE);
            Map<String, List<ExtDataStageDefinition>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinition::getRefId, toList()));
            for (String refId : extDataStageDefinitionMap.keySet()) {
                List<ExtDataStageDefinition> stageDefinitionList = extDataStageDefinitionMap.get(refId);//.stream().map(MonitorReadingMapper::toExtDataStageDefinition).collect(toList());
                Map<String, List<MonitorReadingDayWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingDayWise> subscriptionDailyGraphData = getDailyDBDataForSubscription(ext, body.getYearMonth() );
                    if (cumulativeMapMongo.get(String.valueOf(refId)) != null) {
                        List<MonitorReadingDayWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(refId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionDailyGraphData);
                        cumulativeMapMongo.put(String.valueOf(refId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(refId), subscriptionDailyGraphData);
                    }
                }
                //grouping subscriptions on same date
                Map<String, List<MonitorAPIResponse>> groupedData = cumulativeMapMongo.values().stream()
                        .flatMap(Collection::stream)
                        .map(MonitorReadingMapper::toMonitorAPIResponse) // Convert to MonitorAPIResponse
                        .collect(Collectors.groupingBy(
                                MonitorAPIResponse::getDay,
                                TreeMap::new,  // Use TreeMap with custom comparator for sorted keys
                                Collectors.toList()
                        ));
                //summing data on same day
                groupedData.forEach((day, responsesForDay) -> {
                    List<String> inverterNumbers = responsesForDay.stream().map(MonitorAPIResponse::getInverterNumber).filter(Objects::nonNull).collect(Collectors.toList());
                    Double yieldValueSum = responsesForDay.stream().mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                    try {
                        Date date = inputDateFormat.parse(day);
                        String formattedDay = outputDayFormat.format(date);

                        responses.add(MonitorAPIResponse.builder()
                                .day(formattedDay)
                                .yieldValue(yieldValueSum)
                                .inverterNumbers(inverterNumbers)
                                .build());
                    } catch (ParseException e) {
                        LOGGER.error("Error parsing date: " + day, e);
                    }
                });
                response.put(String.valueOf(refId), responses);
            }
        }catch  (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(response)
                .xAxis(getXAxis(body))
                .yAxis("kWh").build());
    }

    //this below part will be used to bring subscription monitorReadingDaily data and fill its missing data
    private List<MonitorReadingDayWise> getDailyDBDataForSubscription(ExtDataStageDefinition ext, String yearMonthStr) throws ParseException {
        String inverterNumber  = utility.getInverterNumber(ext);
        String year = yearMonthStr.substring(0, 4);
        String month = yearMonthStr.substring(5, 7);
        //this query will return all the data for the given inverter number and date range
        List<MonitorReadingDayWise> dailyMrdData = monitoringDashboardDailyWiseRepository.findByInverterNumberAndYearAndSubId(year,month, inverterNumber, ext.getSubsId());
        List<String> monthDays = Utility.getMonthDays(Integer.parseInt(year), Integer.parseInt(month));
        /*filling the zero yield for subscriptions that do not have any data
         * because if we found any subscription months data then it will be with all days of the month data so, we don't need to check existing*/
        if (dailyMrdData == null || dailyMrdData.size() == 0) {
            dailyMrdData = monthDays.stream()
                    .map(day -> MonitorReadingDayWise.builder()
                            .day(day)
                            .yield(0d)
                            .inverterNumber(inverterNumber)
                            .subscriptionIdMongo(ext.getSubsId())
                            .build())
                    .collect(Collectors.toList());
        }
        return dailyMrdData;
    }
    private List<String> getXAxis(MonitorAPIAuthBody body) {
        String year = body.getYearMonth().substring(0, 4);
        String month =  body.getYearMonth().substring(5, 7);
        List<String> monthDays = Utility.getDaysOfMonth(Integer.parseInt(year), Integer.parseInt(month));
        return monthDays;
    }
}
