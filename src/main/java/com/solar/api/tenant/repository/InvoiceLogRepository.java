package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billingInvoicing.InvoiceLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceLogRepository extends JpaRepository<InvoiceLog, Long> {

    InvoiceLog findBySequenceNo(String sequenceNo);
    InvoiceLog findByBillId(Long billId);
    InvoiceLog findByInvoiceId(Long invoiceId);
}
