package com.solar.api.tenant.mapper.extended.project.activity.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskHeadDTO {

    private Long id;
    private String taskType;
    private String status;
    private String summary;
    private String description;
    private String site;
    private String type;
    private String phase;
    private Long locationId;
    private ActivityHead activityHead;
    private Long activityEstBudgetCap;
    private Date estStartDate;
    private Date estEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private List<TaskDetailDTO> taskDetails;
    private Long totalHoursUsed;
    private Long budgetedHours;
    private String bgColor;
    private String direction;
    private Boolean isDependent;
    private String preDepType;
    private Long dependentId;
    private Boolean isDisable;
    private String dependencyType;
}
