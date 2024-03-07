package com.solar.api.saas.service.process.upload.health;

import java.util.List;
import java.util.Map;

public interface ProjectUploadHealthCheck {

    HealthCheckResult validate(List<Map<?, ?>> mappings, Long registerId, String action, Long assetId, Long projectId);

    //HealthCheckResult validateProjectInventory(List<Map<?, ?>> mappings, Long assetId, Long projectInventoryId);
}
