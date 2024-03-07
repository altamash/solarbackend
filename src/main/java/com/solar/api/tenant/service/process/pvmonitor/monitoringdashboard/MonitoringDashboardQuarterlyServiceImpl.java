package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.GraphDataDTO;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingMapper;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingQuarterWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingWeekWise;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.repository.MonitoringDashboardQuarterWiseRepository;
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

@Service("monitoringQuarterlyService")
public class MonitoringDashboardQuarterlyServiceImpl implements MonitoringDashboardService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private MonitoringDashboardQuarterWiseRepository monitoringDashboardQuarterWiseRepository;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    @Autowired
    private Utility utility;

    @Override
    public ResponseEntity<?> getCumulativeGraphData(MonitorAPIAuthBody body) {
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingQuarterWise>> cumulativeMapMongo = new TreeMap<>();
        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<String> quartersForYear = null;
        try {
            //this will run for variant ids when not null (site dashboard)
            if(body.getVariantIds()!= null && body.getVariantIds().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(),Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE);
                //this will run for variant ids when not null (customer and subscription dashboard)
            }else if(body.getSubscriptionIdsMongo()!= null &&  body.getSubscriptionIdsMongo().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            }
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                //this below part will be used to bring subscription monitorReadingDaily data and fill its missing data
                cumulativeMapMongo.put(ext.getSubsId(), getQuarterlyDBDataForSubscription(ext, body.getStartYear()));
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
            quartersForYear = new ArrayList<>(groupedData.keySet()).stream().sorted().collect(Collectors.toList());
            // sum subscriptions data on the same date
            groupedData.forEach((day, responsesForDay) -> {
                List<String> inverterNumbers = responsesForDay.stream().map(MonitorAPIResponse::getInverterNumber).filter(Objects::nonNull).collect(Collectors.toList());
                Double yieldValueSum = responsesForDay.stream().mapToDouble(MonitorAPIResponse::getYieldValue).sum();
                responses.add(MonitorAPIResponse.builder()
                        .day(day)
                        .yieldValue(yieldValueSum)
                        .inverterNumbers(inverterNumbers)
                        .build());
            });
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        }
        cumulativeMap.put(String.valueOf(-1L), responses);
        return  ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(cumulativeMap)
                .xAxis(quartersForYear).yAxis("kWh").build());
    }

    @Override
    public ResponseEntity<?> getSubscriptionComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingQuarterWise>> cumulativeMapMongo = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> response = null;
        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                cumulativeMapMongo.put(ext.getSubsId(), getQuarterlyDBDataForSubscription(ext,body.getStartYear()));
            }
            response = cumulativeMapMongo.values().stream()
                    .flatMap(Collection::stream)
                    .map(MonitorReadingMapper::toMonitorAPIComparativeResponse) // Convert to MonitorAPIResponse
                    .collect(Collectors.groupingBy(
                            MonitorAPIResponse::getSubscriptionIdMongo,
                            TreeMap::new,  // Use TreeMap with custom comparator for sorted keys
                            Collectors.toList()
                    ));
        }catch  (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(response)
                .xAxis(getXAxis(body)).yAxis("kWh").build());        }

    @Override
    public ResponseEntity<?>    getCustomerComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinitionDTO> extDataStageDefinitionList = null;
        Map<String, List<MonitorAPIResponse>> response =  new TreeMap<>();

        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllSubsAndCustomerBySubIds(body.getSubscriptionIdsMongo());
            Map<Long, List<ExtDataStageDefinitionDTO>> extDataStageDefinitionMap = extDataStageDefinitionList.stream().collect(groupingBy(ExtDataStageDefinitionDTO::getAcctId, toList()));
            for (Long acctId : extDataStageDefinitionMap.keySet()) {
                List<ExtDataStageDefinition> stageDefinitionList = extDataStageDefinitionMap.get(acctId).stream().map(MonitorReadingMapper::toExtDataStageDefinition).collect(toList());
                Map<String, List<MonitorReadingQuarterWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingQuarterWise> subscriptionQuarterlyGraphData = getQuarterlyDBDataForSubscription(ext, body.getStartYear() );
                    if (cumulativeMapMongo.get(String.valueOf(acctId)) != null) {
                        List<MonitorReadingQuarterWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(acctId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionQuarterlyGraphData);
                        cumulativeMapMongo.put(String.valueOf(acctId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(acctId), subscriptionQuarterlyGraphData);
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
                Map<String, List<MonitorReadingQuarterWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingQuarterWise> subscriptionQuarterlyGraphData = getQuarterlyDBDataForSubscription(ext, body.getStartYear() );
                    if (cumulativeMapMongo.get(String.valueOf(refId)) != null) {
                        List<MonitorReadingQuarterWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(refId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionQuarterlyGraphData);
                        cumulativeMapMongo.put(String.valueOf(refId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(refId), subscriptionQuarterlyGraphData);
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
    private List<MonitorReadingQuarterWise> getQuarterlyDBDataForSubscription(ExtDataStageDefinition ext, String yearStr) throws ParseException {
        String inverterNumber = utility.getInverterNumber(ext);
        int year = Integer.parseInt(yearStr);
        //this query will return all the data for the given inverter number and date range
        List<MonitorReadingQuarterWise> quarterlyMrdData = monitoringDashboardQuarterWiseRepository.findByInverterNumberAndYearAndSubId(yearStr, inverterNumber, ext.getSubsId());
        List<String> quartersForYears = Utility.getQuartersForYear(year);
        /*filling the zero yield for subscriptions that do not have any data
        * because if we found any subscription quarter data then it will be with all four quarters so we don't need to check existing*/
        if (quarterlyMrdData == null || quarterlyMrdData.size() == 0) {
            quarterlyMrdData = quartersForYears.stream()
                    .map(quarter -> MonitorReadingQuarterWise.builder()
                            .day(quarter)
                            .yield(0d)
                            .inverterNumber(inverterNumber)
                            .subscriptionIdMongo(ext.getSubsId())
                            .build())
                    .collect(Collectors.toList());
        }
        return quarterlyMrdData;
    }
    private List<String> getXAxis(MonitorAPIAuthBody body) {
        int year = Integer.parseInt(body.getStartYear());
        List<String> quartersForYears = Utility.getQuartersForYear(year);
        return quartersForYears;
    }
}
