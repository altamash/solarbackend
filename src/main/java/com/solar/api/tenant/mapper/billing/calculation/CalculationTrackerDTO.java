package com.solar.api.tenant.mapper.billing.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationTrackerDTO {
    private Long id;
    private String calcRefType;
    private String refId;
    private String billingPeriod;
    private Date periodStartDate;
    private Date periodEndDate;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
