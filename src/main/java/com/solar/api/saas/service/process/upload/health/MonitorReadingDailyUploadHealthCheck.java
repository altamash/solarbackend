package com.solar.api.saas.service.process.upload.health;

import com.solar.api.tenant.model.externalFile.ExternalFile;

import java.util.List;
import java.util.Map;

public interface MonitorReadingDailyUploadHealthCheck {

    HealthCheckResult validate(List<Map<?, ?>> mappings, ExternalFile  externalFile);
}
