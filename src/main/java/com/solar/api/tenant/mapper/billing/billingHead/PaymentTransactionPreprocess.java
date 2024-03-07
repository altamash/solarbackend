package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentTransactionPreprocess {

    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("invoice_ref_id")
    private Long invoiceRefId;
    @JsonProperty("payment_code")
    private String paymentCode;
    private Double amt;
}
