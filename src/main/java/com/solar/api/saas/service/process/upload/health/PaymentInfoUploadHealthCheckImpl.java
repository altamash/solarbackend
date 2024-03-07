package com.solar.api.saas.service.process.upload.health;

import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.service.PaymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PaymentInfoUploadHealthCheckImpl extends AbstractUploadHealthCheck implements PaymentInfoUploadHealthCheck {

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    void addParseError(List<HealthCheck> checks, Integer line, Map<?, ?> mapping, String issue) {
        checks.add(HealthCheck.builder()
                .line(line + 2)
                .entityId(mapping.get("entity_id") == null ? "" : (String) mapping.get("entity_id"))
                .paymentInfoId(mapping.get("payment_info_id") == null ? "" : (String) mapping.get("payment_info_id"))
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
                        .paymentInfoId("")
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
        checkRequiredFieldsInHeader(checks, mappings.get(0).keySet(), "action", "acct_id", "payment_src_alias", "payment_source", "account_title", "account_no", "routing_no", "account_type");
        checkMandatoryFieldsNotEmpty(checks, mappings, "action", "acct_id", "payment_src_alias", "payment_source", "account_title", "account_no", "routing_no", "account_type");
        checkActions(checks, mappings, "payment_info_id");
        checkUserIsValid(checks, mappings);
        checkPaymentInfoIsValid(checks, mappings);
        checkValuesFromPortalAttribute(checks, mappings, "payment_source|Payment Source", "account_type|AccountType");
        checks.sort(Comparator.comparing(HealthCheck::getLine));
        return healthCheckResult(mappings, checks);
    }

    private int checkPaymentInfoIsValid(List<HealthCheck> checks, List<Map<?, ?>> mappings) {
        int count = 0;
        for (int i = 0; i < mappings.size(); i++) {
            Map<?, ?> mapping = mappings.get(i);
            String paymentInfoId = (String) mapping.get("payment_info_id");
            if (paymentInfoId != null && !paymentInfoId.isEmpty()) {
                PaymentInfo paymentInfo = paymentInfoService.findByIdNoThrow(Long.parseLong(paymentInfoId));
                if (paymentInfo == null) {
                    addParseError(checks, i, mappings.get(i), "Payment info not found with payment_info_id " + paymentInfoId);
                    count++;
                }
            }
        }
        return count;
    }
}
