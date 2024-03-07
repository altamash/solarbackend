package com.solar.api.tenant.model.extended.project;

public interface ProjectGanttChartTemplate {

    Long getProjectId();
    Long getActivityId();
    Long getTaskId();
    String getProjectName();
    String getActivitySummary();
    String getTaskSummary();
    String getTaskType();
    String getProjectStatus();
    String getActivityStatus();
    String getTaskStatus();
    String getTaskProgress();
    String getProjectManager();
    String getProjectActualStartDate();
    String getProjectActualEndDate();
    String getActivityActualStartDate();
    String getActivityActualEndDate();
    String getTaskActualStartDate();
    String getTaskActualEndDate();

    String getProjectEstStartDate();
    String getProjectEstEndDate();
    String getActivityEstStartDate();
    String getActivityEstEndDate();
    String getTaskEstStartDate();
    String getTaskEstEndDate();
    String getTaskOverdue();
    String getProjectActualDuration();
    String getProjectEstDuration();
    String getActivityActualDuration();
    String getActivityEstDuration();
    String getTaskActualDuration();
    String getTaskEstDuration();
    //Long getProjectProgress();
    //Long getActivityProgress();

}
