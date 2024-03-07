package com.solar.api.tenant.mapper.payment.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentTransactionSummaryTemplate {

    private Long customerId;
    private String firstName;
    private String lastName;
    private Long subsId;
    private Long invoiceId;
    private Double billedAmount;
    private Double amountAlreadyPaid;
    private Double paymentAmount;
    private Double balance;
}
