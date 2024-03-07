package com.solar.api.tenant.mapper.extended.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class ProjectDatesValidationDTO {

    private Long projectId;
    private Long activityId;
    private Long taskId;
    private String level;
    private Date estStartDate;
    private Date estEndDate;
    private String method;
    private String message;

}
