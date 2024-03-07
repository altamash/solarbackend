package com.solar.api.tenant.mapper.subscription;

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
public class SubscriptionTemplate {

    private Long subscriptionId;
    private Long subscriptionRateMatrixId;
    private String templateName;
}
