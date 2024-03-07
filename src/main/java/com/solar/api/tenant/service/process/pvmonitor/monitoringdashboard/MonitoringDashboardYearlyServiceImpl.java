package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.*;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.repository.*;
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

@Service("monitoringYearlyService")
public class MonitoringDashboardYearlyServiceImpl implements MonitoringDashboardService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private MonitoringDashboardYearWiseRepository monitoringDashboardYearWiseRepository;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    @Autowired
    private Utility utility;

    @Override
    public ResponseEntity<?> getCumulativeGraphData(MonitorAPIAuthBody body) {
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingYearWise>> cumulativeMapMongo = new TreeMap<>();
        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<String> years = null;

        //this below part will be used to bring subscription monitorReadingDaily data and fill its missing data
        try {
            //this will run for variant ids when not null (site dashboard)
            if(body.getVariantIds()!= null && body.getVariantIds().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(),Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE);
                //this will run for variant ids when not null (customer and subscription dashboard)
            }else if(body.getSubscriptionIdsMongo()!= null &&  body.getSubscriptionIdsMongo().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            }
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                cumulativeMapMongo.put(ext.getSubsId(), getYearlyDBDataForSubscription(ext, body.getStartYear(), body.getEndYear()));
            }
            //this code sum the different subscriptions on the same date
            Map<String, List<MonitorAPIResponse>> groupedData = cumulativeMapMongo.values().stream()
                    .flatMap(Collection::stream)
                    .map(MonitorReadingMapper::toMonitorAPIResponse) // Convert to MonitorAPIResponse
                    .collect(Collectors.groupingBy(
                            MonitorAPIResponse::getDay,
                            TreeMap::new,  // Use TreeMap with custom comparator for sorted keys
                            Collectors.toList()
                    ));
            years = new ArrayList<>(groupedData.keySet()).stream().sorted().collect(Collectors.toList());
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
        return ResponseEntity.ok(GraphDataDTO.builder()
                .graphData(cumulativeMap)
                .xAxis(years).yAxis("kWh").build());
    }

    @Override
    public ResponseEntity<?> getSubscriptionComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingYearWise>> cumulativeMapMongo = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> response = null;
        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                cumulativeMapMongo.put(ext.getSubsId(), getYearlyDBDataForSubscription(ext, body.getStartYear(), body.getEndYear()));
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
                .xAxis(getXAxis(body)).yAxis("kWh").build());
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
                Map<String, List<MonitorReadingYearWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingYearWise> subscriptionYearlyGraphData = getYearlyDBDataForSubscription(ext,  body.getStartYear(), body.getEndYear());
                    if (cumulativeMapMongo.get(String.valueOf(acctId)) != null) {
                        List<MonitorReadingYearWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(acctId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionYearlyGraphData);
                        cumulativeMapMongo.put(String.valueOf(acctId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(acctId), subscriptionYearlyGraphData);
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
                Map<String, List<MonitorReadingYearWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
               //fetching subscription yield data and adding subs of same garden with same refid
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingYearWise> subscriptionYearlyGraphData = getYearlyDBDataForSubscription(ext,  body.getStartYear(), body.getEndYear());
                    if (cumulativeMapMongo.get(String.valueOf(refId)) != null) {
                        List<MonitorReadingYearWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(refId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionYearlyGraphData);
                        cumulativeMapMongo.put(String.valueOf(refId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(refId), subscriptionYearlyGraphData);
                    }
                }
                //grouping same garden subscriptions on same date
                Map<String, List<MonitorAPIResponse>> groupedData = cumulativeMapMongo.values().stream()
                        .flatMap(Collection::stream)
                        .map(MonitorReadingMapper::toMonitorAPIResponse) // Convert to MonitorAPIResponse
                        .collect(Collectors.groupingBy(
                                MonitorAPIResponse::getDay,
                                TreeMap::new,  // Use TreeMap with custom comparator for sorted keys
                                Collectors.toList()
                        ));
                //summing the above grouped data on same day
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
    private List<MonitorReadingYearWise> getYearlyDBDataForSubscription(ExtDataStageDefinition ext, String yearStart, String yearEnd) throws ParseException {
        List<MonitorReadingYearWise> responses = new ArrayList<>();
        String inverterNumber;
        inverterNumber = utility.getInverterNumber(ext);
        //this query will return all the data for the given inverter number and date range
        List<MonitorReadingYearWise> yearlyMrdData = monitoringDashboardYearWiseRepository.findByInverterNumberAndYearAndSubId(yearStart, yearEnd, inverterNumber, ext.getSubsId());
        List<Year> years = Utility.getYearsInRange(Integer.parseInt(yearStart), Integer.parseInt(yearEnd));
        Map<String, MonitorReadingYearWise> yearToDataMap = yearlyMrdData.stream()
                .collect(Collectors.toMap(
                        data -> Year.of(Integer.parseInt(data.getDay())).toString(),
                        data -> data
                ));
        //filling the missing years data as zero yield for given subscription id
        responses.addAll(years.stream().map(year -> {
                    MonitorReadingYearWise data = yearToDataMap.get(year.toString());
                    if (data != null) {
                      // responses.add(data);  // Add data to the responses list
                        return data;
                    } else {
                        MonitorReadingYearWise defaultData = MonitorReadingYearWise.builder()
                                .day(year.toString())
                                .yield(0d)
                                .inverterNumber(inverterNumber)
                                .subscriptionIdMongo(ext.getSubsId())
                                .build();
                        //responses.add(defaultData);  // Add defaultData to the responses list
                        return defaultData;
                    }
                }).collect(Collectors.toList()));
        return responses;
    }

    private List<String> getXAxis(MonitorAPIAuthBody body) {
        List<Year> yearsObjectList = Utility.getYearsInRange(Integer.parseInt(body.getStartYear()), Integer.parseInt(body.getEndYear()));
        List<String> yearsStringList = yearsObjectList.stream()
                .map(Year::toString) // Assuming toString() method provides the string representation
                .collect(Collectors.toList());
        return yearsStringList;
    }

}
