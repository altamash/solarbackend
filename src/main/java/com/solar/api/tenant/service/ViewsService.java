package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billing.BillingInvoice.InvoiceCustomerV;

import java.util.List;

public interface ViewsService {

    List<InvoiceCustomerV> getCustomerInvoiceData(Long accountId, Long customerSubscriptionId);
}
