package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.GraphDataDTO;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingMapper;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDayWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingMonthWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingQuarterWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.repository.MonitoringDashboardMonthWiseRepository;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service("monitoringMonthlyService")
public class MonitoringDashboardMonthlyServiceImpl implements MonitoringDashboardService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private MonitoringDashboardMonthWiseRepository monitoringDashboardMonthWiseRepository;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    @Autowired
    private Utility utility;

    @Override
    public ResponseEntity<?> getCumulativeGraphData(MonitorAPIAuthBody body) {
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingMonthWise>> cumulativeMapMongo = new TreeMap<>();
        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<String> monthlyForYear = null;

        //this below part will be used to bring subscription monitorReadingDaily data and fill its missing data
        try { //this will run for variant ids when not null (site dashboard)
            if (body.getVariantIds() != null && body.getVariantIds().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(), Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE);
                //this will run for variant ids when not null (customer and subscription dashboard)
            } else if (body.getSubscriptionIdsMongo() != null && body.getSubscriptionIdsMongo().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            }
                for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                    cumulativeMapMongo.put(ext.getSubsId(), getMonthlyDBDataForSubscription(ext, body.getStartYear()));
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

                //get axis labels
                monthlyForYear = new ArrayList<>(groupedData.keySet()).stream().sorted().collect(Collectors.toList());
                //this code sum the different subscriptions on the same date
                groupedData.forEach((day, responsesForDay) -> {
                    List<String> inverterNumbers = responsesForDay.stream().map(MonitorAPIResponse::getInverterNumber).filter(Objects::nonNull).collect(Collectors.toList());
                    Double yieldValueSum = responsesForDay.stream().mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                    responses.add(MonitorAPIResponse.builder()
                            .day(day)
                            .yieldValue(yieldValueSum)
                            .inverterNumbers(inverterNumbers)
                            .build());
                });
            } catch(Exception e){
                LOGGER.error(e.getMessage(), e);
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

            }
            cumulativeMap.put(String.valueOf(-1L), responses);
            return ResponseEntity.ok(GraphDataDTO.builder()
                    .graphData(cumulativeMap)
                    .xAxis(monthlyForYear).yAxis("kWh").build());
        }

    @Override
    public ResponseEntity<?> getSubscriptionComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingMonthWise>> cumulativeMapMongo = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> response = null;
        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                cumulativeMapMongo.put(ext.getSubsId(), getMonthlyDBDataForSubscription(ext, body.getStartYear()));
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
        } catch (Exception e) {
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

        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllSubsAndCustomerBySubIds(body.getSubscriptionIdsMongo());
            Map<Long, List<ExtDataStageDefinitionDTO>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinitionDTO::getAcctId, toList()));
            for (Long acctId : extDataStageDefinitionMap.keySet()) {
                List<ExtDataStageDefinition> stageDefinitionList = extDataStageDefinitionMap.get(acctId).stream().map(MonitorReadingMapper::toExtDataStageDefinition).collect(toList());
                Map<String, List<MonitorReadingMonthWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingMonthWise> subscriptionMonthlyGraphData = getMonthlyDBDataForSubscription(ext, body.getStartYear() );
                    if (cumulativeMapMongo.get(String.valueOf(acctId)) != null) {
                        List<MonitorReadingMonthWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(acctId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionMonthlyGraphData);
                        cumulativeMapMongo.put(String.valueOf(acctId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(acctId), subscriptionMonthlyGraphData);
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
                    responses.add(MonitorAPIResponse.builder()
                            .day(day)
                            .yieldValue(yieldValueSum)
                            .inverterNumbers(inverterNumbers)
                            .build());
                });
                response.put(String.valueOf(acctId), responses);
            }
        }catch  (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(response)
                .xAxis(getXAxis(body)).yAxis("kWh").build());
    }

    @Override
    public ResponseEntity<?> getSitesComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> response =  new TreeMap<>();

        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(),Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE);
            Map<String, List<ExtDataStageDefinition>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinition::getRefId, toList()));
            for (String refId : extDataStageDefinitionMap.keySet()) {
                List<ExtDataStageDefinition> stageDefinitionList = extDataStageDefinitionMap.get(refId);//.stream().map(MonitorReadingMapper::toExtDataStageDefinition).collect(toList());
                Map<String, List<MonitorReadingMonthWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingMonthWise> subscriptionMonthlyGraphData = getMonthlyDBDataForSubscription(ext, body.getStartYear() );
                    if (cumulativeMapMongo.get(String.valueOf(refId)) != null) {
                        List<MonitorReadingMonthWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(refId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionMonthlyGraphData);
                        cumulativeMapMongo.put(String.valueOf(refId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(refId), subscriptionMonthlyGraphData);
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
                    responses.add(MonitorAPIResponse.builder()
                            .day(day)
                            .yieldValue(yieldValueSum)
                            .inverterNumbers(inverterNumbers)
                            .build());
                });
                response.put(String.valueOf(refId), responses);
            }
        }catch  (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(response)
                .xAxis(getXAxis(body)).yAxis("kWh").build());
    }

    //this below part will be used to bring subscription monitorReadingDaily data and fill its missing data
    private List<MonitorReadingMonthWise> getMonthlyDBDataForSubscription(ExtDataStageDefinition ext, String yearStr) throws ParseException {
        String inverterNumber = utility.getInverterNumber(ext);
        //this query will return all the data for the given inverter number and date range
        List<MonitorReadingMonthWise> monthlyMrdData = monitoringDashboardMonthWiseRepository.findByInverterNumberAndYearAndSubId(yearStr, inverterNumber, ext.getSubsId());
        List<String> monthsForYear = Utility.getMonthsForYear(Integer.parseInt(yearStr));

        /*filling the zero yield for subscriptions that do not have any data
         * because if we found any subscription months data then it will be with all 12months of the year data so, we don't need to check existing*/
        if (monthlyMrdData == null || monthlyMrdData.size() == 0) {
            monthlyMrdData = monthsForYear.stream()
                    .map(month -> MonitorReadingMonthWise.builder()
                            .day(month)
                            .yield(0d)
                            .inverterNumber(inverterNumber)
                            .subscriptionIdMongo(ext.getSubsId())
                            .build())
                    .collect(Collectors.toList());
        }
        return monthlyMrdData;
    }

    private List<String> getXAxis(MonitorAPIAuthBody body) {
        List<String> monthsForYear = Utility.getMonthsForYear(Integer.parseInt(body.getStartYear()));
        return monthsForYear;
    }
}
