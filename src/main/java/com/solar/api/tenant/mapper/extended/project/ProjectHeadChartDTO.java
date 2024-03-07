package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectHeadChartDTO {

    private Long taskId; //projectId
    private String taskName;
    private Long progress;
    private Long duration;
    private String projectManager;
    private String status;
    private String level;
    @JsonFormat(pattern="yyyy-MM-dd")
    private String actualStartDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private String actualEndDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private String estStartDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private String estEndDate;
    private String dependency; //predecessor
    private List<SubTasksChartDTO> subTasks; //activities
}
