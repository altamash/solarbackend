package com.solar.api.tenant.service.alerts;

import com.solar.api.helper.Utility;
import com.solar.api.saas.module.com.solar.batch.service.EProjectionStatus;
import com.solar.api.saas.module.com.solar.batch.service.StageMonitorService;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataTempStage;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataStageDefinitionRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataTempStageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.Constants.ALERTS.MP;
import static com.solar.api.Constants.ALERTS.*;
import static com.solar.api.Constants.RATE_CODES.PROJECTION_EFFICIENCY;
import static com.solar.api.Constants.RATE_CODES.*;

@Component
public class ProjectionUtils {

    Logger LOGGER = LoggerFactory.getLogger(ProjectionUtils.class);

    private final MonitorReadingDailyRepository monitorReadingDailyRepository;
    private final ExtDataStageDefinitionRepository extDataStageDefinitionRepository;
    private final ExtDataTempStageRepository extDataTempStageRepository;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final StageMonitorService stageMonitorService;
    private final DataExchange dataExchange;

    public ProjectionUtils(MonitorReadingDailyRepository monitorReadingDailyRepository, ExtDataStageDefinitionRepository extDataStageDefinitionRepository, ExtDataTempStageRepository extDataTempStageRepository, CustomerSubscriptionRepository customerSubscriptionRepository, StageMonitorService stageMonitorService, DataExchange dataExchange) {
        this.monitorReadingDailyRepository = monitorReadingDailyRepository;
        this.extDataStageDefinitionRepository = extDataStageDefinitionRepository;
        this.extDataTempStageRepository = extDataTempStageRepository;
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.stageMonitorService = stageMonitorService;
        this.dataExchange = dataExchange;
    }

    protected List<Map<String, String>> mapProjections(List<ExtDataStageDefinition> extDefinitions, String projectionPeriod, Date yearMonth) {
        List<Map<String, String>> subsProjectionList = new ArrayList<>();
        extDefinitions = extDefinitions.stream()
                .filter(m -> m.getMpJson() != null && Utility.getMeasureAsJson(m.getMpJson(), PROJECTION_INDICATOR).equals("true") &&
                        Utility.getMeasureAsJson(m.getMpJson(), PROJECTION_PERIOD).equals(projectionPeriod))
                .collect(Collectors.toList());
        for (ExtDataStageDefinition extDataStageDefinition : extDefinitions) {
            try {
                Map<String, String> subsProjection = mapProjections(extDataStageDefinition, yearMonth);
                subsProjectionList.add(subsProjection);
            } catch (Exception e) {
                LOGGER.info("Exception {}", e.getMessage());
            }
        }
        return subsProjectionList;
    }

    private Map<String, String> mapProjections(ExtDataStageDefinition extDefinitions, Date yearMonth) {
        Map<String, String> subsProjection = new HashMap<>();
        subsProjection.put(PRJTNM, Utility.getMeasureAsJson(extDefinitions.getMpJson(), PRJTNM));
        subsProjection.put(MONGO_GARDEN_ID, extDefinitions.getRefId());
        subsProjection.put(GARDEN_NAME, extDefinitions.getRefType());
        subsProjection.put(SYSTEM_SIZE, Utility.getMeasureAsJson(extDefinitions.getMpJson(), S_GS));
        subsProjection.put(SITE_LOCATION_ID, String.valueOf(extDefinitions.getSiteLocationId()));
        subsProjection.put(MP, String.valueOf(extDefinitions.getMonPlatform()));
        subsProjection.put(MONGO_SUBSCRIPTION_ID, extDefinitions.getSubsId());
        MonitorReadingDaily reading = monitorReadingDailyRepository.findBySubscriptionIdMongoAndDay(extDefinitions.getSubsId(), yearMonth);
        subsProjection.put(PROJECTION, reading != null ? String.valueOf(reading.getYieldValue()) : "");
        subsProjection.put(PROJECTION_EFFICIENCY, Utility.getMeasureAsJson(extDefinitions.getMpJson(), PROJECTION_EFFICIENCY));
        subsProjection.put(PROJECTION_EFFICIENCY_AT_100, Utility.getMeasureAsJson(extDefinitions.getMpJson(), PROJECTION_EFFICIENCY_AT_100));
        return subsProjection;
    }

    public BaseResponse enableDisableProjection(String subscriptionIds, Boolean status, String variantId, String tenantId) {
        try {
            ExtDataStageDefinition extDataStageDefinitions = extDataStageDefinitionRepository.findBySubscriptionIds(subscriptionIds);
            ExtDataTempStage extDataTempStage = extDataTempStageRepository.findBySubscriptionIds(subscriptionIds);
            CustomerSubscription customerSubscription = customerSubscriptionRepository.findBySubscriptionIds(subscriptionIds);
            if(!status) {
                extDataStageDefinitions.setSubsStatus(EProjectionStatus.INACTIVE.getStatus());
                extDataTempStage.setSubsStatus(EProjectionStatus.INACTIVE.getStatus());
                customerSubscription.setSubscriptionStatus(EProjectionStatus.INACTIVE.getStatus());
                dataExchange.disableProjectionInMongo(variantId, subscriptionIds, status, tenantId);
            } else {
                extDataStageDefinitions.setSubsStatus(EProjectionStatus.ACTIVE.getStatus());
                extDataTempStage.setSubsStatus(EProjectionStatus.ACTIVE.getStatus());
                customerSubscription.setSubscriptionStatus(EProjectionStatus.ACTIVE.getStatus());
                dataExchange.disableProjectionInMongo(variantId, subscriptionIds, status, tenantId);
            }
            extDataStageDefinitionRepository.save(extDataStageDefinitions);
            extDataTempStageRepository.save(extDataTempStage);
            customerSubscriptionRepository.save(customerSubscription);
            stageMonitorService.getMongoSubscriptionsAndMeasures();
            stageMonitorService.transferSubscriptionsToStageDefinition();
            return com.solar.api.saas.service.integration.BaseResponse.ok("Projection status updated successfully");

        } catch (Exception e) {
            return com.solar.api.saas.service.integration.BaseResponse.error(422, e.getMessage());
        }
    }
}
