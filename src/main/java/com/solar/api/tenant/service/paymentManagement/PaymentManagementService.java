package com.solar.api.tenant.service.paymentManagement;


import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.mapper.billing.paymentManagement.StripePaymentIntentDTO;

import java.util.List;
import java.util.Map;

public interface PaymentManagementService {
    Map getCustomerPaymentDashboard(Map response, String groupBy, List<String> periodList);

    Map getNewCustomerGraph();

    Map getCustomerTypeGraph();

    Map getCustomersByProjectGraph();

    Map generatePaymentIntent(StripePaymentIntentDTO stripePaymentIntentDTO, Long compKey);

    Map paymentResponse(String paymentIntentId, Long compKey);

    String decodeBase64String(String encodedString);

    BaseResponse encodeStripeKey(String stripeKey);
}
