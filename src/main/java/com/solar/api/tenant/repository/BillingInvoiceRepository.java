package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillingInvoiceRepository extends JpaRepository<BillingInvoice, Long> {

    @Query("SELECT inv FROM BillingInvoice inv WHERE inv.id not in (select invoice.id from PaymentTransactionHead)")
    List<BillingInvoice> findAllWithoutPaymentTransactionHead();
}
