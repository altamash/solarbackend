package com.solar.api.saas.service.process.upload.health;

import com.solar.api.AppConstants;
import com.solar.api.tenant.model.externalFile.ExternalFile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MonitorReadingDailyUploadHealthCheckImpl extends AbstractUploadHealthCheck implements MonitorReadingDailyUploadHealthCheck {

    @Override
    void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue) {
        checks.add(HealthCheck.builder()
                .line(line + 2)
                .years((mapping.get("years") == null ? "0" : (String) mapping.get("years")))
                .months((mapping.get("months") == null ? "0" : (String) mapping.get("months")))
                .days((mapping.get("days") == null ? "0" : (String) mapping.get("days")))
                .projected((mapping.get("projected") == null ? "0" : (String) mapping.get("projected")))
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
                        .years("0")
                        .months("0")
                        .days("0")
                        .projected("0")
                        .issue("Missing required header field " + field)
                        .build());
                count.getAndIncrement();
            }
        });
        return count.get();
    }

    @Override
    public HealthCheckResult validate(List<Map<?, ?>> mappings, ExternalFile externalFile) {
        List<HealthCheck> checks = new ArrayList<>();
        String colName = externalFile.getName();
        switch (colName) {
            case AppConstants.FileMapperName.PROJECTION_YEARLY_FILE:
                checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "years", "projected");
                checkMandatoryFieldsNotEmpty(checks, mappings, "years", "projected");
                checkYearIsValid(checks, mappings);
                checkProjectedFieldIsValid(checks, mappings);
                break;
            case AppConstants.FileMapperName.PROJECTION_MONTHLY_FILE:
                checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "months", "projected");
                checkMandatoryFieldsNotEmpty(checks, mappings, "months", "projected");
                checkMonthYearIsValid(checks, mappings);
                checkProjectedFieldIsValid(checks, mappings);
                break;
            case AppConstants.FileMapperName.PROJECTION_DAILY_FILE:
                checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "days", "projected");
                checkMandatoryFieldsNotEmpty(checks, mappings, "days", "projected");
                checkDayMonthYearIsValid(checks, mappings);
                checkProjectedFieldIsValid(checks, mappings);
                break;
            case AppConstants.FileMapperName.PROJECTION_QUARTERLY_FILE:
                checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "years", "projected");
                checkMandatoryFieldsNotEmpty(checks, mappings, "years", "projected");
                checkYearQuarterIsValid(checks, mappings);
                checkProjectedFieldIsValid(checks, mappings);
                break;
            default:
                break;
        }


        checks.sort(Comparator.comparing(HealthCheck::getLine));
        return healthCheckResult(mappings, checks);
    }


}
