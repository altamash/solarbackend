package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billing.BillingInvoice.InvoiceCustomerV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceCustomerViewRepository extends JpaRepository<InvoiceCustomerV, Long> {

    List<InvoiceCustomerV> findByAccountId(Long accountId);

    List<InvoiceCustomerV> findByAccountIdAndCustomerSubscriptionId(Long accountId, Long customerSubscriptionId);

}
