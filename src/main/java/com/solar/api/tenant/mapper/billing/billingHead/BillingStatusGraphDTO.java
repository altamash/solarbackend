package com.solar.api.tenant.mapper.billing.billingHead;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingStatusGraphDTO {
    private String label;
    private List<Long> data;
}
