package com.solar.api.tenant.mapper.billing.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingByGardenTableDTO {

    private String _id;
    private String variantAlias;
    private Integer subscriptionCount;
    private String srcNo;
    private Double gardenSize;
}
