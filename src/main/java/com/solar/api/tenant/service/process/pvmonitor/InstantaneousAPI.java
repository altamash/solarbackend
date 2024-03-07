package com.solar.api.tenant.service.process.pvmonitor;

import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;

public interface InstantaneousAPI {

    MonitorAPIResponse getInstantaneousData(ExtDataStageDefinition ext);
}
