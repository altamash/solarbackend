package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIAuthBody;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MonitoringDashboardWidgetService {

    ResponseEntity<?> getWidgetData(MonitorAPIAuthBody body);

    Double getTreesPlanted(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear);

    Double getBarrels(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear);

    Double getMilesCover(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear);

    Double getPhoneCharges(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear);

    Double getCO2Reduction(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear);

    Double getCarCharges(List<ExtDataStageDefinition> extDataStageDefinitionList, String... monthYear);

    ResponseEntity<?> getSitesWidgetData(MonitorAPIAuthBody body);

    ResponseEntity<?> getSitesWidgetDataDetail(MonitorAPIAuthBody body);

    ResponseEntity<?> validateSitesSelectionCountAllowed(Long count);

    ResponseEntity<?> getYieldWidgetData(MonitorAPIAuthBody body);

    YieldDataDTO getYearlyYield(List<ExtDataStageDefinition> extDataStageDefinitionList);

    YieldDataDTO getMonthlyYield(List<ExtDataStageDefinition> extDataStageDefinitionList);

    YieldDataDTO getDailyYield(List<ExtDataStageDefinition> extDataStageDefinitionList);

    /*this method is used to get the yearly yield sum for the given list of subscriptions*/
    YieldDataDTO getLifeTimeYield(List<String> refIds);

    YieldWidgetDataDTO getYieldWidgetDataBySubscriptionId(String subscriptionId);

    WidgetDataDTO getSystemInformationBySubscriptionId(String subscriptionId);
}
