package com.solar.api.tenant.mapper.extended.project.activity.task;

import com.solar.api.tenant.model.extended.project.activity.task.TaskDetail;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    // TaskHead /////////////////////////////////////////////////
    public static TaskHead toTaskHead(TaskHeadDTO taskHeadDTO) {
        if (taskHeadDTO == null) {
            return null;
        }
        return TaskHead.builder()
                .id(taskHeadDTO.getId())
                .taskType(taskHeadDTO.getTaskType())
                .status(taskHeadDTO.getStatus())
                .summary(taskHeadDTO.getSummary())
                .description(taskHeadDTO.getDescription())
                .type(taskHeadDTO.getType())
                .locationId(taskHeadDTO.getLocationId())
                .site(taskHeadDTO.getSite())
                .phase(taskHeadDTO.getPhase())
                .estStartDate(taskHeadDTO.getEstStartDate())
                .estEndDate(taskHeadDTO.getEstEndDate())
                .actualStartDate(taskHeadDTO.getActualStartDate())
                .actualEndDate(taskHeadDTO.getActualEndDate())
                .activityEstBudgetCap(taskHeadDTO.getActivityEstBudgetCap())
                .totalHoursUsed(taskHeadDTO.getTotalHoursUsed())
                .budgetedHours(taskHeadDTO.getBudgetedHours())
                .build();
    }

    public static TaskHeadDTO toTaskHeadDTO(TaskHead taskHead) {
        if (taskHead == null) {
            return null;
        }
        return TaskHeadDTO.builder()
                .id(taskHead.getId())
                .taskType(taskHead.getTaskType())
                .status(taskHead.getStatus())
                .summary(taskHead.getSummary())
                .description(taskHead.getDescription())
                .site(taskHead.getSite())
                .type(taskHead.getType())
                .locationId(taskHead.getLocationId())
                .phase(taskHead.getPhase())
                .estStartDate(taskHead.getEstStartDate())
                .estEndDate(taskHead.getEstEndDate())
                .actualStartDate(taskHead.getActualStartDate())
                .actualEndDate(taskHead.getActualEndDate())
                .activityEstBudgetCap(taskHead.getActivityEstBudgetCap())
                .totalHoursUsed(taskHead.getTotalHoursUsed())
                .budgetedHours(taskHead.getBudgetedHours())
                .bgColor(taskHead.getBgColor())
                .direction(taskHead.getDirection())
                .isDependent(taskHead.getIsDependent())
                .isDisable(taskHead.getIsDisable())
                .dependentId(taskHead.getDependentId())
                .preDepType(taskHead.getPreDepType())
                .dependencyType(taskHead.getDependencyType())
                .build();
    }

    public static TaskHead toUpdatedTaskHead(TaskHead taskHead, TaskHead taskHeadUpdate) {
        taskHead.setTaskType(taskHeadUpdate.getTaskType() == null ? taskHead.getTaskType() : taskHeadUpdate.getTaskType());
        taskHead.setStatus(taskHeadUpdate.getStatus() == null ? taskHead.getStatus() : taskHeadUpdate.getStatus());
        taskHead.setSummary(taskHeadUpdate.getSummary() == null ? taskHead.getSummary() : taskHeadUpdate.getSummary());
        taskHead.setDescription(taskHeadUpdate.getDescription() == null ? taskHead.getDescription() : taskHeadUpdate.getDescription());
        taskHead.setSite(taskHeadUpdate.getSite() == null ? taskHead.getSite() : taskHeadUpdate.getSite());
        taskHead.setActivityHead(taskHeadUpdate.getActivityHead() == null ? taskHead.getActivityHead() : taskHeadUpdate.getActivityHead());
        taskHead.setType(taskHeadUpdate.getType() == null ? taskHead.getType() : taskHeadUpdate.getType());
        taskHead.setLocationId(taskHeadUpdate.getLocationId() == null ? taskHead.getLocationId() : taskHeadUpdate.getLocationId());
        taskHead.setPhase(taskHeadUpdate.getPhase() == null ? taskHead.getPhase() : taskHeadUpdate.getPhase());
        taskHead.setEstStartDate(taskHeadUpdate.getEstStartDate() == null ? taskHead.getEstStartDate() : taskHeadUpdate.getEstStartDate());
        taskHead.setEstEndDate(taskHeadUpdate.getEstEndDate() == null ? taskHead.getEstEndDate() : taskHeadUpdate.getEstEndDate());
        taskHead.setActualStartDate(taskHeadUpdate.getActualStartDate() == null ? taskHead.getActualStartDate() : taskHeadUpdate.getActualStartDate());
        taskHead.setActualEndDate(taskHeadUpdate.getActualEndDate() == null ? taskHead.getActualEndDate() : taskHeadUpdate.getActualEndDate());
        taskHead.setActivityEstBudgetCap(taskHeadUpdate.getActivityEstBudgetCap() == null ? taskHead.getActivityEstBudgetCap() : taskHeadUpdate.getActivityEstBudgetCap());
        taskHead.setTotalHoursUsed(taskHeadUpdate.getTotalHoursUsed() == null ? taskHead.getTotalHoursUsed() : taskHeadUpdate.getTotalHoursUsed());
        taskHead.setBudgetedHours(taskHeadUpdate.getBudgetedHours() == null ? taskHead.getBudgetedHours() : taskHeadUpdate.getBudgetedHours());
        taskHead.setDependencyType(taskHeadUpdate.getDependencyType() == null ? taskHead.getDependencyType() : taskHeadUpdate.getDependencyType());
        return taskHead;
    }

    public static List<TaskHead> toTaskHeads(List<TaskHeadDTO> taskHeadDTOS) {
        return taskHeadDTOS.stream().map(a -> toTaskHead(a)).collect(Collectors.toList());
    }

    public static List<TaskHeadDTO> toTaskHeadDTOs(List<TaskHead> taskHeads) {
        return taskHeads.stream().map(a -> toTaskHeadDTO(a)).collect(Collectors.toList());
    }

    // TaskDetail ////////////////////////////////////////////////
    public static TaskDetail toTaskDetail(TaskDetailDTO taskDetailDTO) {
        if (taskDetailDTO == null) {
            return null;
        }
        return TaskDetail.builder()
                .taskId(taskDetailDTO.getTaskId())
                .measureId(taskDetailDTO.getMeasureId())
                .value(taskDetailDTO.getValue())
                .lastUpdateOn(taskDetailDTO.getLastUpdateOn())
                .lastUpdateBy(taskDetailDTO.getLastUpdateBy())
                .validationRule(taskDetailDTO.getValidationRule())
                .validationParams(taskDetailDTO.getValidationParams())
                .build();
    }

    public static TaskDetailDTO toTaskDetailDTO(TaskDetail taskDetail) {
        if (taskDetail == null) {
            return null;
        }
        return TaskDetailDTO.builder()
                .taskId(taskDetail.getTaskId())
                .measureId(taskDetail.getMeasureId())
                .value(taskDetail.getValue())
                .lastUpdateOn(taskDetail.getLastUpdateOn())
                .lastUpdateBy(taskDetail.getLastUpdateBy())
                .validationRule(taskDetail.getValidationRule())
                .validationParams(taskDetail.getValidationParams())
                .build();
    }

    public static TaskDetail toUpdatedTaskDetail(TaskDetail taskDetail, TaskDetail taskDetailUpdate) {
        //activityDetail.setActivityId(activityDetail.getActivityId());
        taskDetail.setTaskId(taskDetailUpdate.getTaskId() == null ? taskDetail.getTaskId() :
                taskDetailUpdate.getTaskId());
        taskDetail.setMeasureId(taskDetailUpdate.getMeasureId() == null ? taskDetail.getMeasureId() :
                taskDetailUpdate.getMeasureId());
        taskDetail.setValue(taskDetailUpdate.getValue() == null ? taskDetail.getValue() : taskDetailUpdate.getValue());
        taskDetail.setValidationParams(taskDetailUpdate.getValidationParams() == null ?
                taskDetail.getValidationParams() : taskDetailUpdate.getValidationParams());
        taskDetail.setValidationRule(taskDetailUpdate.getValidationRule() == null ? taskDetail.getValidationRule() :
                taskDetailUpdate.getValidationRule());
        taskDetail.setLastUpdateBy(taskDetailUpdate.getLastUpdateBy() == null ? taskDetail.getLastUpdateBy() :
                taskDetailUpdate.getLastUpdateBy());
        taskDetail.setLastUpdateOn(taskDetailUpdate.getLastUpdateOn() == null ? taskDetail.getLastUpdateOn() :
                taskDetailUpdate.getLastUpdateOn());
        return taskDetail;
    }

    public static List<TaskDetail> toTaskDetails(List<TaskDetailDTO> taskDetailDTOS) {
        return taskDetailDTOS.stream().map(a -> toTaskDetail(a)).collect(Collectors.toList());
    }

    public static List<TaskDetailDTO> toTaskDetailDTOs(List<TaskDetail> taskDetails) {
        return taskDetails.stream().map(a -> toTaskDetailDTO(a)).collect(Collectors.toList());
    }
}
