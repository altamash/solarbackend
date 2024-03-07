package com.solar.api.tenant.mapper.payment.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentMessageDTO {

    private Long invoiceId;
    private Long paymentId;
    private Long paymentDetailId;
    private String message;
    private String error;
    private String warning;
}
