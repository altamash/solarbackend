package com.solar.api.tenant.mapper.tiles.paymentManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.billing.calculation.CustomerDetailDTO;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerPaymentDashboardTile {
    private String source;
    private Long billHeadId;
    private Long invoiceId;
    private String period;
    private String subscriptionId;
    private String billStatus;
    private Double invoicedAmount;
    private Double paidAmount;
    private Double remainingAmount;
    private CustomerDetailDTO customerDetailDTO;

    private String transactionId;
    private String customerId;
    private String subscriptionName;
    private Date dueDate;
    private Long acctId;
    private Date paymentDate;
    private String variantId;
    private String productId;
    private String paymentType;

    public CustomerPaymentDashboardTile(Long billHeadId, Long invoiceId, String period,
                                        String subscriptionId, String billStatus,
                                        Double invoicedAmount, Double paidAmount,
                                        String customerName, String customerType,
                                        String customerEmail, String customerPhone,
                                        String profileUrl, String transactionId,
                                        String customerId,
                                        Date dueDate, Date paymentDate, String variantId, String productId, Long acctId, String paymentType, String source,String subscriptionName) {
        this.billHeadId = billHeadId;
        this.invoiceId = invoiceId;
        this.period = period;
        this.subscriptionId = subscriptionId;
        this.billStatus = billStatus;
        this.invoicedAmount = invoicedAmount;
        this.paidAmount = paidAmount;
        this.remainingAmount = (invoicedAmount != null ? invoicedAmount : 0) - (paidAmount != null ? paidAmount : 0);
        this.customerDetailDTO = new CustomerDetailDTO(customerName, customerType, customerEmail, customerPhone, profileUrl);
        this.transactionId = transactionId;
        this.customerId = customerId;
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
        if (paymentDate != null) {
            this.paymentDate = paymentDate;
        }
        this.variantId = variantId;
        this.productId = productId;
        this.acctId = acctId;
        this.paymentType = paymentType;
        this.source = source;
        this.subscriptionName=subscriptionName;
    }
}
