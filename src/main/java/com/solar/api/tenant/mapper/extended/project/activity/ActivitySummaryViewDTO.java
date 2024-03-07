package com.solar.api.tenant.mapper.extended.project.activity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.project.activity.task.TaskSummaryViewDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivitySummaryViewDTO {

    private Long id;
    private String name;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date estStartDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date estEndDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date actualStartDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date actualEndDate;
    private String assignTo;
    private Long estBudgetCap;
    private Long duration;
    private String status;
    private String projectManager;
    private List<TaskSummaryViewDTO> taskSummaryViewDTOS;
}
