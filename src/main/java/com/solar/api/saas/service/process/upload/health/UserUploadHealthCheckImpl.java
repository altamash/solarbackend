package com.solar.api.saas.service.process.upload.health;

import com.solar.api.helper.ValidationUtils;
import com.solar.api.saas.service.process.upload.BulkUploadService;
import com.solar.api.saas.service.process.upload.EUploadAction;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserUploadHealthCheckImpl extends AbstractUploadHealthCheck implements UserUploadHealthCheck {

    @Autowired
    private UserService userService;

    @Override
    void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue) {
        checks.add(HealthCheck.builder()
                .line(line + 2)
                .entityId(mapping.get("entity_id") == null ? "" : (String) mapping.get("entity_id"))
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
                        .action("")
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
        checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "action", "acct_id", "user_name", "password");
        checkMandatoryFieldsNotEmpty(checks, mappings, "action");
        checkCustomFieldFormats(checks, mappings, "data_of_birth|DATE", "register_date|DATE", "active_date|DATE");
        checkActions(checks, mappings, "acct_id");
//        checkUserIsValid(checks, mappings);
        userNameMustBeUnique(checks, mappings);
        checkMultipleOccurrences(checks, mappings, "user_name");
        checkAllowedValues(checks, mappings, "ccd|YES,NO");
        checks.sort(Comparator.comparing(HealthCheck::getLine));
        return healthCheckResult(mappings, checks);
    }

    private int userNameMustBeUnique(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String acctId = (String) mapping.get("acct_id");
            String userName = (String) mapping.get("user_name");
            if (userName.equals(BulkUploadService.FINAL_DEFAULT_EMAIL)) {
                continue;
            }
            String action = (String) mapping.get("action");
            List<User> usersWithUserName = null;
            if (userName != null && !userName.isEmpty()) {
                usersWithUserName = userService.findAllByUserName(userName);
            }
            if (action != null && action.equals(EUploadAction.INSERT.getAction())) {
                if (usersWithUserName != null && !usersWithUserName.isEmpty()) {
                    addParseError(checks, i, mappings.get(i), "user " + userName + " is not available (Leave blank " +
                            "for system generated)");
                    count++;
                }
            } else if (action != null && action.equals(EUploadAction.UPDATE.getAction()) && !acctId.isEmpty()) {
                if (!ValidationUtils.isLong(acctId)) {
                    addParseError(checks, i, mappings.get(i), "acct_id must be an integer number");
                    count++;
                } else {
                    User user = userService.findByIdNoThrow(Long.parseLong(acctId));
                    if (user == null) {
                        addParseError(checks, i, mappings.get(i), "Customer account not found with acct_id " + acctId);
                        count++;
                    } else if (userName != null && !userName.isEmpty()) {
                        if (!usersWithUserName.isEmpty() && usersWithUserName.get(0).getAcctId() != user.getAcctId()) {
                            addParseError(checks, i, mappings.get(i), "user " + userName + " is not available (leave " +
                                    "blank for system generated)");
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }
    @Override
    public HealthCheckResult validateLead(List<Map<?, ?>> mappings) {
        List<HealthCheck> checks = new ArrayList<>();
        checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "action", "first_name","last_name","email","phone_number","lead_type","data_of_birth","creation_date","country_code","lead_source");
        checkMandatoryFieldsNotEmpty(checks, mappings, "action", "first_name","last_name","email","lead_type","creation_date","lead_source");
        checkCustomFieldFormats(checks, mappings, "data_of_birth|DATE","creation_date|DATE");
        checkIsValidCountryCodeAndPhoneAndEmail(checks,mappings,"email","phone_number","country_code");
        checkIsValidLeadType(checks,mappings,"lead_type");
        checks.sort(Comparator.comparing(HealthCheck::getLine));
        return healthCheckResult(mappings, checks);
    }
}
