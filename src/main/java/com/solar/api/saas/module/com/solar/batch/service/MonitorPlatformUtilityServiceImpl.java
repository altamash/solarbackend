package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.service.alerts.AlertService;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.repository.PhysicalLocationRepository;
import com.solar.api.tenant.repository.TenantConfigRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataStageDefinitionRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataTempStageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.solar.api.Constants.ALERTS.*;
import static com.solar.api.Constants.RATE_CODES.S_GS;
import static com.solar.api.helper.Utility.getCurrentMonthName;

@Service
public class MonitorPlatformUtilityServiceImpl implements MonitorPlatformUtilityService {

    protected final Logger LOGGER = LoggerFactory.getLogger(MonitorPlatformUtilityServiceImpl.class);
    private final AlertService alertService;
    private final ExtDataStageDefinitionRepository extDataStageDefinitionRepository;
    private final PhysicalLocationRepository physicalLocationRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final ExtDataTempStageRepository extDataTempStageRepository;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final DataExchange dataExchange;

    public MonitorPlatformUtilityServiceImpl(AlertService alertService,
                                             ExtDataStageDefinitionRepository extDataStageDefinitionRepository,
                                             PhysicalLocationRepository physicalLocationRepository,
                                             TenantConfigRepository tenantConfigRepository,
                                             ExtDataTempStageRepository extDataTempStageRepository,
                                             CustomerSubscriptionRepository customerSubscriptionRepository,
                                             DataExchange dataExchange) {
        this.alertService = alertService;
        this.extDataStageDefinitionRepository = extDataStageDefinitionRepository;
        this.physicalLocationRepository = physicalLocationRepository;
        this.tenantConfigRepository = tenantConfigRepository;
        this.extDataTempStageRepository = extDataTempStageRepository;
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.dataExchange = dataExchange;
    }

    @Override
    public BaseResponse outages(ExtDataStageDefinition subscription) {
        Optional<PhysicalLocation> physicalLocation = getLocation(subscription.getSiteLocationId());
        Map<String, String> json = setJSONOutages(subscription, physicalLocation);
        return triggerEmailForOutages(json);
    }

    private BaseResponse triggerEmailForOutages(Map<String, String> json) {
        String emailTOs = "";
        Optional<TenantConfig> tenantConfig = tenantConfigRepository.findByParameter(OUTAGE_TOEMAIL_TENANT_CONFIG_PARAM);
        emailTOs = "tos=" + tenantConfig.get().getText().replace(",", "&tos=");
        try {
            return alertService.superSendEmailTrigger(getTenant(OUTAGES_TENANT_CONFIG_PARAM), "Outages-Alert",
                    emailTOs, "", "", json);
        } catch (Exception e) {
            getExceptionLogs(e.getMessage());
            return BaseResponse.builder()
                    .code(500)
                    .message(e.getMessage()).build();
        }
    }

    private Map<String, String> setJSONOutages(ExtDataStageDefinition extDataStageDefinition, Optional<PhysicalLocation> physicalLocation) {
        Map<String, String> json = new HashMap<>();
        String systemSize = Utility.getMeasureAsJson(extDataStageDefinition.getMpJson(), S_GS);
        json.put(GARDEN_NAME, extDataStageDefinition.getRefType());
        json.put(MP, extDataStageDefinition.getMonPlatform());
        json.put(SYSTEM_SIZE, systemSize == null ? NOT_FOUND : systemSize);
        json.put(LOCATION, physicalLocation.isPresent() ? physicalLocation.get().getAdd1() : NOT_FOUND);
        json.put(CATEGORY, OUTAGES_CATEGORY_TEXT);
        json.put(TYPE, OUTAGES_TYPE_TEXT);
        json.put(ALERT_DURATION, getCurrentMonthName());
        json.put(DESCRIPTION, OUTAGES_DECSRIPTION_TEXT);
        json.put(IMPACT, OUTAGES_IMPACT);
        json.put(RESOLUTION, OUTAGES_RESOLUTION);
        json.put(ALERT_IMPACT, "high");
        json.put(REPORT_DATE, String.valueOf(new Date()));
        return json;
    }

    private void getExceptionLogs(String message) {
        LOGGER.error("EXCEPTION {} in superSendEmailTrigger()", message);
    }

    private TenantConfig getTenant(String configParam) {
        return tenantConfigRepository.findByParameter(configParam).orElse(null);
    }

    private Optional<PhysicalLocation> getLocation(Long siteLocationId) {
        return physicalLocationRepository.findById(siteLocationId);
    }
}
