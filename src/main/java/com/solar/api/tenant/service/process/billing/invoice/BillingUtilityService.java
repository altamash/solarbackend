package com.solar.api.tenant.service.process.billing.invoice;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.Response;

import java.io.IOException;
import java.util.Date;

public interface BillingUtilityService {

    String invoicing(String subscriptionCode, String rateMatrixHeadIds,
                                   String billingMonthYear, Date invoiceDate, String type, Long compKey) throws Exception;

    void invoicing(Long billingHeadId, Date invoiceDate, Long compKey) throws Exception;

    ObjectNode emailInvoicing(Long billingHeadId, Long compKey) throws Exception;

    Response batchNotification(String jobName, Long jobId, String stackTrace, String subject) throws IOException;
}
