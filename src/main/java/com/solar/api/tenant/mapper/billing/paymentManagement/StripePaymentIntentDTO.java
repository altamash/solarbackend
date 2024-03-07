package com.solar.api.tenant.mapper.billing.paymentManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StripePaymentIntentDTO {
    private String customerEmail;
    private Long tenantId;
    private Long amount;
    private List<String> billHeadId_InvoiceId;
    private String customerId;

}
