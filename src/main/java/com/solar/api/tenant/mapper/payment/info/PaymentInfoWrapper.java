package com.solar.api.tenant.mapper.payment.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentInfoWrapper {

    private Double invoiceAmount;
    private Double paidAmount;
    List<PaymentInfoTemplate> paymentInfoTemplates;
}
