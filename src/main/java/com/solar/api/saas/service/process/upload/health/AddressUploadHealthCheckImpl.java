package com.solar.api.saas.service.process.upload.health;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AddressUploadHealthCheckImpl extends AbstractUploadHealthCheck implements AddressUploadHealthCheck {

    @Override
    void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue) {
        checks.add(HealthCheck.builder()
                .line(line + 2)
                .entityId(mapping.get("entity_id") == null ? "" : (String) mapping.get("entity_id"))
                .locationId(mapping.get("location_id") == null ? "" : (String) mapping.get("location_id"))
                .action(mapping.get("action") == null ? "" : (String) mapping.get("action"))
                .issue(issue)
                .build());
    }

    @Override
    int checkRequiredFieldsInHeader(List<HealthCheck> checks, Set<?> fileHeader, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(field -> {
            if (!fileHeader.contains(field)) {
                checks.add(HealthCheck.builder()
                        .line(1)
                        .entityId("")
                        .locationId("")
                        .action("")
                        .issue("Missing required header field " + field)
                        .build());
                count.getAndIncrement();
            }
        });
        return count.get();
    }

    @Override
    public HealthCheckResult validate(List<Map<?, ?>> mappings, Long subscriptionRateMatrixId) {
        List<HealthCheck> checks = new ArrayList<>();
        checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "action", "entity_id", "add1", "location_type", "zip_code");
        checkMandatoryFieldsNotEmpty(checks, mappings, "action", "entity_id", "add1", "location_type", "zip_code");
        checkActions(checks, mappings, "location_id");
        checkUserIsValid(checks, mappings);
        checks.sort(Comparator.comparing(HealthCheck::getLine));
        return healthCheckResult(mappings, checks);
    }
}
