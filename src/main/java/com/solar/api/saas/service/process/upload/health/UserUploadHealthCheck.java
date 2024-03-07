package com.solar.api.saas.service.process.upload.health;

import java.util.List;
import java.util.Map;

public interface UserUploadHealthCheck {

    HealthCheckResult validate(List<Map<?, ?>> mappings);
    HealthCheckResult validateLead(List<Map<?, ?>> mappings);

    }
