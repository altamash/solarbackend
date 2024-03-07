package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinancialAccrualDTO {

    private Long id;
    private String category;
    private Long refId; //projectId
    private Long subRefId; //Optional -> taskId
    private Long accrualCategoryId; //resourceId, partnerId
    private String accrualCategory;
    private String accrualDatetime;
    private String accrualAdjustment;
    private String accrualPeriod;
    private double accruedAmount;
    private String rate;
    private String type;
    private String postingDate;
    private String status;
    private Long orgId;
}
