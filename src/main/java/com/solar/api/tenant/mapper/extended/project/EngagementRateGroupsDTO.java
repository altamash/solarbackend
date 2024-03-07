package com.solar.api.tenant.mapper.extended.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class EngagementRateGroupsDTO {

    private Long id;
    private String groupName;
    private String rateType;
    private String ratePeriod;
    private String rate;
    private String fixedAmount;
    private String overtimePeriod;
    private String overtimeRate;
    private String overtimeFixedAmount;

    private Long resourceId;
    private Long projectId;
    private Long taskId;
    private String rateCategory;
    private String description;
    private Long termLengthInDays;
    private String calculationFactor;
    private String category;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
