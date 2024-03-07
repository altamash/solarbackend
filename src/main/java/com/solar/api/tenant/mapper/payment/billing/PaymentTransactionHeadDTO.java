package com.solar.api.tenant.mapper.payment.billing;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentTransactionHeadDTO {

    private Long paymentId;
    private String paymentCode;
    private Long custAccountId;
    private Long invoiceRefId;
    private Double net;
    private String ccy;
    private String destination;
    private String destId;
    private Long beneficiaryId;
    private Long subsId;
    private String description;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
