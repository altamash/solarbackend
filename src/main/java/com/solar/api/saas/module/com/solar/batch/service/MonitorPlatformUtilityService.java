package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;

public interface MonitorPlatformUtilityService {

    BaseResponse outages(ExtDataStageDefinition subscription);
}
