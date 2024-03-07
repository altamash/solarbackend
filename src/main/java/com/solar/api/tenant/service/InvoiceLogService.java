package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billingInvoicing.InvoiceLog;

import java.util.List;

public interface InvoiceLogService {

    InvoiceLog save(InvoiceLog invoiceLog);

    InvoiceLog getBySequenceNo(String sequenceNo);

    List<InvoiceLog> findAll();

    List<InvoiceLog> findBy();
    InvoiceLog findByBillId(Long billId);
}
