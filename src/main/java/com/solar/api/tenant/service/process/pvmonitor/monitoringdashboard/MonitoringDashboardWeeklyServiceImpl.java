package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.GraphDataDTO;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.mapper.pvmonitor.MonitorReadingMapper;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingMonthWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingWeekWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import com.solar.api.tenant.model.pvmonitor.WeekInfoDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.repository.MonitoringDashboardWeekWiseRepository;
import com.solar.api.tenant.repository.MonitoringDashboardYearWiseRepository;
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

@Service("monitoringWeeklyService")
public class MonitoringDashboardWeeklyServiceImpl implements MonitoringDashboardService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private MonitoringDashboardWeekWiseRepository monitoringDashboardWeeklyRepository;
    @Autowired
    private ExtDataStageDefinitionService extDataStageDefinitionService;
    @Autowired
    private Utility utility;


    @Override
    public ResponseEntity<?> getCumulativeGraphData(MonitorAPIAuthBody body) {
        Map<String, List<MonitorAPIResponse>> cumulativeMap = new TreeMap<>();
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingWeekWise>> cumulativeMapMongo = new TreeMap<>();
        List<MonitorAPIResponse> responses = new ArrayList<>();
        List<String> weeksForMonth = null;
        try {
            //this will run for variant ids when not null (site dashboard)
            if(body.getVariantIds()!= null && body.getVariantIds().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllByRefIdIn(body.getVariantIds(),Constants.CUSTOMER_SUBSCRIPTION_STATUS.ACTIVE);
                //this will run for variant ids when not null (customer and subscription dashboard)
            }else if(body.getSubscriptionIdsMongo()!= null &&  body.getSubscriptionIdsMongo().size() > 0) {
                extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            }
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                //below code get subscription monitorReadingDaily data and fill its missing data
                cumulativeMapMongo.put(ext.getSubsId(), getWeeklyDBDataForSubscription(ext, body.getYearMonth()));
            }
            //grouping the different subscriptions on the same date
            Map<String, List<MonitorAPIResponse>> groupedData = cumulativeMapMongo.values().stream()
                    .flatMap(Collection::stream)
                    .map(MonitorReadingMapper::toMonitorAPIResponse) // Convert to MonitorAPIResponse
                    .collect(Collectors.groupingBy(
                            MonitorAPIResponse::getDay,
                            TreeMap::new,  // Use TreeMap with custom comparator for sorted keys
                            Collectors.toList()
                    ));
             // getting axis for the graph
            weeksForMonth = new ArrayList<>(groupedData.keySet()).stream().sorted().collect(Collectors.toList());
           // sum subscription on same date and adding it in the response
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
                .xAxis(weeksForMonth).yAxis("kWh").build());
    }

    @Override
    public ResponseEntity<?> getSubscriptionComparativeGraphData(MonitorAPIAuthBody body) {
        List<ExtDataStageDefinition> extDataStageDefinitionList = null;
        Map<String, List<MonitorReadingWeekWise>> cumulativeMapMongo = new TreeMap<>();
        Map<String, List<MonitorAPIResponse>> response = null;
        try {
            extDataStageDefinitionList = extDataStageDefinitionService.findAllBySubsIdIn(body.getSubscriptionIdsMongo());
            for (ExtDataStageDefinition ext : extDataStageDefinitionList) {
                cumulativeMapMongo.put(ext.getSubsId(), getWeeklyDBDataForSubscription(ext,body.getYearMonth()));
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
                Map<String, List<MonitorReadingWeekWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingWeekWise> subscriptionWeeklyGraphData = getWeeklyDBDataForSubscription(ext, body.getYearMonth() );
                    if (cumulativeMapMongo.get(String.valueOf(acctId)) != null) {
                        List<MonitorReadingWeekWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(acctId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionWeeklyGraphData);
                        cumulativeMapMongo.put(String.valueOf(acctId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(acctId), subscriptionWeeklyGraphData);
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
                Map<String, List<MonitorReadingWeekWise>> cumulativeMapMongo = new TreeMap<>();
                List<MonitorAPIResponse> responses = new ArrayList<>();
                for (ExtDataStageDefinition ext : stageDefinitionList) {
                    List<MonitorReadingWeekWise> subscriptionWeeklyGraphData = getWeeklyDBDataForSubscription(ext, body.getYearMonth() );
                    if (cumulativeMapMongo.get(String.valueOf(refId)) != null) {
                        List<MonitorReadingWeekWise> exsistingProjectSubGraphDataList = cumulativeMapMongo.get(String.valueOf(refId));
                        exsistingProjectSubGraphDataList.addAll(subscriptionWeeklyGraphData);
                        cumulativeMapMongo.put(String.valueOf(refId), exsistingProjectSubGraphDataList);
                    } else {
                        cumulativeMapMongo.put(String.valueOf(refId), subscriptionWeeklyGraphData);
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
    private List<MonitorReadingWeekWise> getWeeklyDBDataForSubscription(ExtDataStageDefinition ext, String yearMonthStr) throws ParseException {
        String inverterNumber;
        inverterNumber = utility.getInverterNumber(ext);
        String year = yearMonthStr.substring(0, 4);
        String month = yearMonthStr.substring(5, 7);
        //this query will return all the data for the given inverter number and date range
        List<MonitorReadingWeekWise> weeklyMrdData = monitoringDashboardWeeklyRepository.findByInverterNumberAndYearAndSubId(year, month, inverterNumber, ext.getSubsId());
        List<WeekInfoDTO> weekInfoDTOS = Utility.getWeeksList(yearMonthStr);
        /*filling the zero yield for subscriptions that do not have any data
         * because if we found any subscription months data then it will be with all weeks of the month data so, we don't need to check existing*/
        List<MonitorReadingWeekWise> weeklyMrdDataResponseList = new ArrayList<>();
        List<MonitorReadingWeekWise> finalWeeklyMrdData = weeklyMrdData;
        weekInfoDTOS.stream().forEach(week -> {
            Optional<MonitorReadingWeekWise> mrdWeekWise = finalWeeklyMrdData.stream().filter(data -> data.getDay().equals(week.getWeekNumber() + "-" + month + "-" + year)).findFirst();
            if (mrdWeekWise.isPresent()) {
                MonitorReadingWeekWise mrdWeek = mrdWeekWise.get();
                mrdWeek.setDay("Week " + week.getWeekNumber() +" ("+week.getStartDay() +" "+week.getMonthName()+ " - " + week.getEndDay()+" "+week.getMonthName()+")");
                weeklyMrdDataResponseList.add(mrdWeek);

            } else {
                weeklyMrdDataResponseList.add(MonitorReadingWeekWise.builder()
                        .day("Week " + week.getWeekNumber() +" ("+week.getStartDay() +" "+week.getMonthName()+ " - " + week.getEndDay()+" "+week.getMonthName()+")")
                        .yield(0d)
                        .inverterNumber(inverterNumber)
                        .subscriptionIdMongo(ext.getSubsId())
                        .build());
            }
        });
        return weeklyMrdDataResponseList;
    }

    private List<String> getXAxis(MonitorAPIAuthBody body) {
        List<WeekInfoDTO> weekInfoDTOS = Utility.getWeeksList(body.getYearMonth());
        List<String> xAxis = new ArrayList<>();
              weekInfoDTOS.stream().forEach(week -> {
              String weekStr = "Week " + week.getWeekNumber() +" ("+week.getStartDay() +" "+week.getMonthName()+ " - " + week.getEndDay()+" "+week.getMonthName()+")";
                  xAxis.add(weekStr);
        });
        return xAxis;
    }

}
