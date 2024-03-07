package com.solar.api.saas.service.process.upload.v2.customer.validation;

import com.solar.api.saas.service.process.upload.BulkUploadService;
import com.solar.api.saas.service.process.upload.EUploadAction;
import com.solar.api.saas.service.process.upload.v2.validation.AbstractValidationCheck;
import com.solar.api.saas.service.process.upload.v2.validation.ValidationDTO;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.solar.api.saas.service.process.upload.v2.validation.ValidationUtils.addParseError;

@Component
public class CustomersValidationCheck extends AbstractValidationCheck {

    private static final String[] MANDATORY_FIELDS = {"First Name", "Last Name", "Phone Number", "Email"};

    @Autowired
    private UserService userService;

    @Override
    public String[] getMandatoryFields() {
        return MANDATORY_FIELDS;
    }

    @Override
    public int checkRequiredFieldsInHeader(List<ValidationDTO> checks, Set<?> fileHeader, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(field -> {
            if (!fileHeader.contains(field)) {
                checks.add(ValidationDTO.builder()
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
    public int checkRequiredFieldsInHeader(List<ValidationDTO> checks, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(field -> {
//            if (!MANDATORY_FIELDS.contains(field)) {
//                checks.add(HealthCheck.builder()
//                        .line(1)
//                        .entityId("")
//                        .action("")
//                        .issue("Missing required header field " + field)
//                        .build());
//                count.getAndIncrement();
//            }
        });
        return count.get();
    }

    private int userNameMustBeUnique(List<ValidationDTO> checks, List<Map<?, ?>> mappings) {
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
                if (!com.solar.api.helper.ValidationUtils.isLong(acctId)) {
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
}
