package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Builder
//@AllArgsConstructor
public class PaymentTransactionLineItemsDetailMasterDTO {

    @JsonProperty("head_id")
    private Long headId;
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
    //unreconciled cols
    @JsonProperty("garden")
    private String garden;
    @JsonProperty("payment_id")
    private Long paymentId;
    private List<PaymentTransactionLineItemsDetailDTO> paymentTransactionLineItemsDetailDTO;

    public PaymentTransactionLineItemsDetailMasterDTO(Long headId, Double invoiceAmount, Long subscriptionId, Long invoiceId, Double outstandingAmount, String subscriptionName, String garden, Long paymentId, List<PaymentTransactionLineItemsDetailDTO> value) {
        this.headId = headId;
        this.invoiceAmount = invoiceAmount;
        this.subscriptionId = subscriptionId;
        this.invoiceId = invoiceId;
        this.outstandingAmount = outstandingAmount;
        this.subscriptionName = subscriptionName;
        this.garden = garden;
        this.paymentId = paymentId;
        this.paymentTransactionLineItemsDetailDTO = value;
    }


}