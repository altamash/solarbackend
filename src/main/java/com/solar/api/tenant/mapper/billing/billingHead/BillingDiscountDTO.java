package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingDiscountDTO {

    private Long headId;
    private String billingCode;
    private Double value;
    private String notes;
    private Boolean addToBill;
    private String period;
}
