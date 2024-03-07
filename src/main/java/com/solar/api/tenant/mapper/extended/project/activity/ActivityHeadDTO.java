package com.solar.api.tenant.mapper.extended.project.activity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solar.api.tenant.mapper.extended.project.PhaseDTO;
import com.solar.api.tenant.mapper.extended.project.ProjectHeadDTO;
import com.solar.api.tenant.mapper.extended.project.activity.task.TaskHeadDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityHeadDTO {

    private Long id;
    private Long registerId;
    private ProjectHeadDTO projectHead;
    private Long locationId;
    private Long activityEstBudgetCap;
    private Long assigneeId;
    private String site;
    private Long projectId;
    private Long phaseId;
    private String phaseName;
    private String summary;
    private String type; //support,project
    private String status;
    private String description;
    private PhaseDTO phase;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date estStartDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date estEndDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date actualStartDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date actualEndDate;
    private List<ActivityDetailDTO> activityDetails;
    private List<TaskHeadDTO> taskHeadDTOs;
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
