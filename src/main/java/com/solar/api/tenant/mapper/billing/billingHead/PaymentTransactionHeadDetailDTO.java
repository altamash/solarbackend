package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
//@AllArgsConstructor
public class PaymentTransactionHeadDetailDTO {

    @JsonProperty("head_id")
    private Long headId;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("invoice_amount")
    private Double invoiceAmount;
    @JsonProperty("subscription_id")
    private Long subscriptionId;
    @JsonProperty("invoice_id")
    private Long invoiceId;
    @JsonProperty("outstanding_amount")
    private Double outstandingAmount;
    @JsonProperty("subscription_name")
    private String subscriptionName;
    @JsonProperty("premise")
    private String premise;
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("payment_mode")
    private String paymentMode;

    public PaymentTransactionHeadDetailDTO(Long headId, String customerName, Double invoiceAmount, Long subscriptionId, Long invoiceId, Double outstandingAmount, String subscriptionName, String premise, String accountNumber,String paymentMode) {
        this.headId = headId;
        this.customerName = customerName;
        this.invoiceAmount = invoiceAmount;
        this.subscriptionId = subscriptionId;
        this.invoiceId = invoiceId;
        this.outstandingAmount = outstandingAmount;
        this.subscriptionName = subscriptionName;
        this.premise = premise;
        this.accountNumber = accountNumber;
        this.paymentMode = paymentMode;
    }

}
