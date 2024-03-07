package com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionRateMatrixDetailDTO {

    private Long id;
    private Long subscriptionRateMatrixId;
    private String subscriptionCode;
    private String rateCode;
    private String defaultValue;
    private Integer level;
    private Integer sequenceNumber;
    private Boolean mandatory;
    private Boolean maintainBillHistory;
    private String flags;
    private Boolean allowOthersToEdit;
    private MeasureDefinitionTenantDTO measureDefinition;
    private Boolean systemUsed; // If it is used anywhere in the calculation logic (administrative)
    private Boolean varyByCustomer; // If yes, present in subscription mapping
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
