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
public class CalculationGroupDTO {

    private Long id;
    private String name;
    private String alias;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
