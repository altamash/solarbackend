package com.solar.api.saas.service.process;

import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExecuteParams {
    CustomerSubscription subscription;
    Long rateMatrixHeadId;
    BillingHead billingHead;
    String billingMonth;
    Long ruleHeadId;
    Long jobId;
    String method;
    List<String> flags;
}
