package com.solar.api.tenant.service;

import com.solar.api.tenant.model.billingInvoicing.InvoiceLog;
import com.solar.api.tenant.repository.InvoiceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceLogServiceImpl implements InvoiceLogService {

    @Autowired
    InvoiceLogRepository invoiceLogRepository;

    @Override
    public InvoiceLog save(InvoiceLog invoiceLog) {
        return invoiceLogRepository.save(invoiceLog);
    }

    @Override
    public InvoiceLog getBySequenceNo(String sequenceNo) {
        return invoiceLogRepository.findBySequenceNo(sequenceNo);
    }

    @Override
    public List<InvoiceLog> findAll() {
        return invoiceLogRepository.findAll();
    }

    @Override
    public List<InvoiceLog> findBy() {
        return null;
    }

    @Override
    public InvoiceLog findByBillId(Long billId) {
        return invoiceLogRepository.findByBillId(billId);
    }
}
