package com.solar.api.tenant.model.paymentDetailView;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.payment.billing.PaymentDetailsView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDetailReturnObject {

    private List<PaymentDetailsView> paymentDetailsViewList;
    private Double billedAmountSum;
    private Double totalAmountSum;
}
