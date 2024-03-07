package com.solar.api.tenant.mapper.billing.billingHead;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingHeadHistoryDTO {

    private Long id;
    private Double amount;
    private String billingMonthYear;
    private String billStatus;
    private Long subscriptionId;
}
