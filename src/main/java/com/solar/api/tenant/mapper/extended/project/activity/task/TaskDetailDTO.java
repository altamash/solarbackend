package com.solar.api.tenant.mapper.extended.project.activity.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDetailDTO {

    private Long id;
    private Long taskId;
    private Long measureId;
    private Long lastUpdateBy;
    private String value;
    private String validationRule;
    private String validationParams;
    private Date lastUpdateOn;
}
