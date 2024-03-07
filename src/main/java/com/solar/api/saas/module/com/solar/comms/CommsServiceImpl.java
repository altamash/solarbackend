package com.solar.api.saas.module.com.solar.comms;

import com.solar.api.tenant.model.billingInvoicing.InvoiceLog;
import com.solar.api.tenant.service.InvoiceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class CommsServiceImpl implements CommsService{

    @Autowired
    InvoiceLogService invoiceLogService;

    @Override
    public void emailQueue() {
        List<InvoiceLog> invoiceLogList = invoiceLogService.findAll();
    }
}
