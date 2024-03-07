package com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionRateMatrixHeadDTO {

    private Long id;
    private String subscriptionCode;
    private String subscriptionTemplate;
    private Boolean active;
    private List<SubscriptionRateMatrixDetailDTO> subscriptionRateMatrixDetails;
}
