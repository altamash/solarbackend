package com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionRatesDerivedDTO {

    private Long id;
    private Long subscriptionRateMatrixId;
    private String subscriptionCode;
    private String refType;
    private String calcGroup;
    private String refCode;
    private Double value;
    private String condition;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
