package com.solar.api.saas.service.process.upload.health;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BillingCreditsUploadHealthCheckImpl extends AbstractUploadHealthCheck implements BillingCreditsUploadHealthCheck {

    @Override
    void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue) {
        checks.add(HealthCheck.builder()
                .line(line + 2)
                .premiseNo(mapping.get("Premise Number") == null ? "" : (String) mapping.get("Premise Number"))
                .gardenID(mapping.get("Garden ID") == null ? "" : (String) mapping.get("Garden ID"))
                .calendarMonth(mapping.get("Calendar Month") == null ? "" : (String) mapping.get("Calendar Month"))
                .mpa(mapping.get("Monthly Production Allocation in kWh") == null ? 0d : Double.parseDouble((String)mapping.get("Monthly Production Allocation in kWh")))
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
                        .premiseNo("")
                        .mpa(0d)
                        .gardenID("")
                        .calendarMonth("")
                        .issue("Missing required header field " + field)
                        .build());
                count.getAndIncrement();
            }
        });
        return count.get();
    }

    @Override
    public HealthCheckResult validate(List<Map<?, ?>> mappings) {
        List<HealthCheck> checks = new ArrayList<>();
        checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "Payment Type", "Debtor Number", "Premise Number", "Subscriber Allocation History: Subscriber Name", "Monthly Production Allocation in kWh", "Tariff Rate", "Bill Credit", "Garden ID", "Name Plate Capacity (kW DC)", "Calendar Month");
        checkMandatoryFieldsNotEmpty(checks, mappings, "Premise Number", "Monthly Production Allocation in kWh", "Garden ID", "Calendar Month");
        checkDateFormatIsValid(checks, mappings);
        checks.sort(Comparator.comparing(HealthCheck::getLine));
        return healthCheckResult(mappings, checks);
    }
}
