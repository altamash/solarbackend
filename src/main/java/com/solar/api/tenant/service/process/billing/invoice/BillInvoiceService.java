package com.solar.api.tenant.service.process.billing.invoice;

import com.solar.api.saas.service.integration.mongo.response.subscription.SubscriptionMapping;
import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.stage.billing.ExtDataStageDefinitionBilling;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BillInvoiceService {

    List<BillingInvoice> getAll();

    BillingInvoice invoice(BillingHead billingHead, Date invoiceDate) throws Exception;

    BillingInvoice invoiceV1(BillingHead billingHead, Date invoiceDate, SubscriptionMapping subscriptionMapping);

    BillingInvoice individualInvoice(Long billingHeadId, Date invoiceDate, Long compKey) throws Exception;

    BillingInvoice individualInvoiceV1(BillingHead billingHead, Date invoiceDate, Long compKey, SubscriptionMapping subscriptionMapping);

    List<BillingInvoice> invoiceByMatrixId(String subscriptionCode, List<Long> rateMatrixHeadIds,
                                           String billingMonthYear, Date invoiceDate, String type,
                                           JobManagerTenant jobManagerTenant, Long compKey) throws Exception;

    Date getDueDate(String subscriptionType, Date invoiceDate);

    BillingInvoice findById(Long invoiceId);

    List<BillingHead> findForInvoicing(String billingMonthYear, String subscriptionCode,
                                       List<Long> rateMatrixIds, List<String> billStatuses);

    void addPaymentTransactionHeadsForInvoices();

    BillingInvoice generatePDF(Long billingHeadId);

    List<BillingInvoice> generatePDFByMatrixId(String subscriptionCode, List<Long> rateMatrixHeadIds,
                                               String billingMonthYear, String type, JobManagerTenant jobManagerTenant);

    /**
     * Strictly for Power Monitoring Readings
     * Batch
     */
    List<Long> findBySubscriptionTypesIn(List<String> subscriptionType);

    void addPaymentTransactionHeadForInvoice(BillingHead billingHead, Long billingInvoiceId,
                                             String billingCode, Long jobId);

    String getBillingCode(String billingCode);

    Map generateInvoiceV1(Long billingHeadId, Long compKey, Boolean isLegacy);

    void generateInvoiceBulkV1(String billingHeadIds, Long compKey);

    void generateInvoiceHTML(BillingHead billingHead);

    void generateDraftHTML(BillingHead billingHead);

    Map publishInvoice(Map response, Long billHeadId, Boolean isLegacy);

    void publishBulkInvoice(String billHeadIds);

    void generateDraftHTMLProjection(List<BillingHead> billingHeads, List<String> months);

    void convertHTMLToPDF(List<BillingHead> billingHeadList);
}
