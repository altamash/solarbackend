package com.solar.api.saas.service.process.upload.health;

import java.util.List;
import java.util.Map;

public interface BillingCreditsUploadHealthCheck {

    HealthCheckResult validate(List<Map<?, ?>> mappings);
}
