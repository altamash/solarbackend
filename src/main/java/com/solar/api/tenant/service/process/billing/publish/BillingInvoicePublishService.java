package com.solar.api.tenant.service.process.billing.publish;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;

public interface BillingInvoicePublishService {

    void publishInvoiceByMonth(String subscriptionCode, String rateMatrixHeadIds, String billingMonth, String type, Long jobId);

    void validateInvoices();

    ObjectNode publishIndividualInvoice(Long headId);

    /**
     * To publish html invoices created in the billing process
     * CreatedBy: Ibtehaj
     * CreatedAt: 03/24/2023
     * @param billingHead
     * @param calculationDetails
     * @return
     */
    ObjectNode publishIndividualHTMLInvoice(BillingHead billingHead, CalculationDetails calculationDetails);
}
