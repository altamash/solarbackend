package com.solar.api.tenant.mapper.billing.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationDetailsDTO {
    private Long id;
    private String source;
    private Long sourceId;
    private CalculationTrackerDTO calculationTrackerDTO; //FK
    private String state;
    private int attemptCount;
    private String errorInd;
    private String errorMessage;
    private Boolean lockedInd;
    private Long invoiceId;
    private String prevInvHtmlView;
    private String publishState;
    private Boolean reCalcInd;
    private Long calcId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
