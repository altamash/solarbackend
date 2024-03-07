package com.solar.api.tenant.service.process.subscription.billHead;

import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.process.JobManagerTenant;

import java.util.List;

public interface BillHeadAction {

    String lockForInvalidation(Long billHeadId, Boolean isLegacy);

    void invalidate(BillingHead billingHeadToInvalidate, Boolean isLegacy);

    void billingBySubscriptionType(String subscriptionCode, String billingMonth, String type,
                                   JobManagerTenant jobManagerTenant, Boolean isLegacy);

    void billingBySubscriptionType(String subscriptionCode, List<Long> rateMatrixHeadIds, List<String> variantIds,
                                   String billingMonth, String type, JobManagerTenant jobManagerTenant, Boolean isLegacy);
}
