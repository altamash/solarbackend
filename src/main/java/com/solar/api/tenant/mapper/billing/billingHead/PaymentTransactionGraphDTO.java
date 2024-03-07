package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentTransactionGraphDTO {

    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("unpaidAmount")
    private Double unpaidAmount;
    @JsonProperty("unreconciledAmount")
    private Double unreconciledAmount;
    @JsonProperty("reconciledAmount")
    private Double reconciledAmount;
    @JsonProperty("billing_month")
    private String billing_month;
    @JsonProperty("label")
    private String label;
    @JsonProperty("data")
    Double[] data;

    public PaymentTransactionGraphDTO(Double amount, String billing_month) {
        this.amount = amount;
        this.billing_month = billing_month;
    }

    public PaymentTransactionGraphDTO(String label , Double data[]) {
        this.label = label;
        this.data = data;
    }
    public PaymentTransactionGraphDTO( Double unpaidAmount, Double unreconciledAmount, Double reconciledAmount, String billing_month) {
        this.billing_month = billing_month;
        this.unpaidAmount = unpaidAmount;
        this.unreconciledAmount = unreconciledAmount;
        this.reconciledAmount = reconciledAmount;
    }

    public PaymentTransactionGraphDTO(Double amount, Double unpaidAmount, Double unreconciledAmount, Double reconciledAmount, String billing_month, String label, Double data[]) {
        this.amount = amount;
        this.unpaidAmount = unpaidAmount;
        this.unreconciledAmount = unreconciledAmount;
        this.reconciledAmount = reconciledAmount;
        this.billing_month = billing_month;
        this.label = label;
        this.data = data;
    }
}