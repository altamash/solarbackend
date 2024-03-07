package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
//@AllArgsConstructor
public class MasterPaymentTransactionGraphDTO {

    @JsonProperty("unpaid_amount")
    private Double unpaid_amount;
    @JsonProperty("unreconciled_amount")
    private Double unreconciled_amount;
    @JsonProperty("reconciled_amount")
    private Double reconciled_amount;
    @JsonProperty("billing_month")
    private String billing_month;

}