package com.solar.api.saas.service.process.upload.v2.customer.validation;

import com.solar.api.saas.service.process.upload.v2.validation.AbstractIntegrityCheck;
import com.solar.api.saas.service.process.upload.v2.validation.ValidationDTO;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.solar.api.saas.service.process.upload.v2.validation.ValidationUtils.addParseError;

@Component
public class CustomersIntegrityCheck extends AbstractIntegrityCheck {

    private final UserService userService;

    public CustomersIntegrityCheck(UserService userService) {
        this.userService = userService;
    }

    public int checkDuplication(List<ValidationDTO> checks, List<Map> mappings, String... fields) {
        AtomicInteger count = new AtomicInteger();
        Arrays.asList(fields).forEach(f -> {

            for (int i = 0; i < mappings.size(); i++) {
                Map<?, ?> mapping = mappings.get(i);
                String fieldValue = (String) mapping.get(f);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    User u = userService.findByUserName(fieldValue);
                    if (u != null) {
                        addParseError(checks, i, mappings.get(i), f + " " + fieldValue + " already exists");
                        count.getAndIncrement();
                    }
                }
            }
        });
        return count.get();
    }
}
