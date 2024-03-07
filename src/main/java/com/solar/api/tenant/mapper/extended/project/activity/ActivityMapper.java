package com.solar.api.tenant.mapper.extended.project.activity;

import com.solar.api.tenant.mapper.extended.project.activity.task.TaskMapper;
import com.solar.api.tenant.model.extended.project.activity.ActivityDetail;
import com.solar.api.tenant.model.extended.project.activity.ActivityHead;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityMapper {

    // ActivityHead ////////////////////////////////////////////////
    public static ActivityHead toActivityHead(ActivityHeadDTO activityHeadDTO) {
        if (activityHeadDTO == null) {
            return null;
        }
        return ActivityHead.builder()
                .id(activityHeadDTO.getId())
                .projectId(activityHeadDTO.getProjectId() == null ?
                        activityHeadDTO.getProjectHead().getId() : activityHeadDTO.getProjectId())
                .phaseName(activityHeadDTO.getPhaseName())
                .phaseId(activityHeadDTO.getPhaseId() == null ?
                        activityHeadDTO.getPhase().getId() : activityHeadDTO.getPhaseId())
//                .projectHead(ProjectMapper.toProjectHead(activityHeadDTO.getProjectHead()))
                .activityEstBudgetCap(activityHeadDTO.getActivityEstBudgetCap())
                .actualEndDate(activityHeadDTO.getActualEndDate())
                .actualStartDate(activityHeadDTO.getActualStartDate())
                .estEndDate(activityHeadDTO.getEstEndDate())
                .estStartDate(activityHeadDTO.getEstStartDate())
                .status(activityHeadDTO.getStatus())
                .assigneeId(activityHeadDTO.getAssigneeId())
                .site(activityHeadDTO.getSite())
                .summary(activityHeadDTO.getSummary())
                .type(activityHeadDTO.getType())
                .registerId(activityHeadDTO.getRegisterId())
                .totalHoursUsed(activityHeadDTO.getTotalHoursUsed())
                .budgetedHours(activityHeadDTO.getBudgetedHours())
                .description(activityHeadDTO.getDescription())
                .activityDetails(activityHeadDTO.getActivityDetails() != null ?
                        new HashSet(toActivityDetails(activityHeadDTO.getActivityDetails())) : null)
                .build();
    }

    public static ActivityHeadDTO toActivityHeadDTO(ActivityHead activityHead) {
        if (activityHead == null) {
            return null;
        }
        return ActivityHeadDTO.builder()
                .id(activityHead.getId())
                .projectId(activityHead.getProjectId() == null ?
                        activityHead.getProjectHead().getId() : activityHead.getProjectId())
                .phaseId(activityHead.getPhaseId() == null ?
                        activityHead.getPhase() == null ? null : activityHead.getPhase().getId() : activityHead.getPhaseId())
                .phaseName(activityHead.getPhaseName())
                .activityEstBudgetCap(activityHead.getActivityEstBudgetCap())
                .actualEndDate(activityHead.getActualEndDate())
                .actualStartDate(activityHead.getActualStartDate())
                .estEndDate(activityHead.getEstEndDate())
                .estStartDate(activityHead.getEstStartDate())
                .assigneeId(activityHead.getAssigneeId())
                .site(activityHead.getSite())
                .status(activityHead.getStatus())
                .summary(activityHead.getSummary())
                .type(activityHead.getType())
                .registerId(activityHead.getRegisterId())
                .totalHoursUsed(activityHead.getTotalHoursUsed())
                .budgetedHours(activityHead.getBudgetedHours())
                .description(activityHead.getDescription())
                .bgColor(activityHead.getBgColor())
                .direction(activityHead.getDirection())
                .isDependent(activityHead.getIsDependent())
                .isDisable(activityHead.getIsDisable())
                .dependentId(activityHead.getDependentId())
                .preDepType(activityHead.getPreDepType())
                .dependencyType(activityHead.getDependencyType())
                .activityDetails(activityHead.getActivityDetails() != null ?
                        toActivityDetailDTOs(new ArrayList(activityHead.getActivityDetails())) : null)
                .taskHeadDTOs(activityHead.getTaskHeads() != null ?
                        TaskMapper.toTaskHeadDTOs(new ArrayList(activityHead.getTaskHeads())) : null)
                .build();
    }

    public static ActivityHead toUpdatedActivityHead(ActivityHead activityHead, ActivityHead activityHeadUpdate) {
        ///ActivityHead.setId(ActivityHeadUpdate.getId() == null ? ActivityHead.getId() : ActivityHeadUpdate.getId());
        activityHead.setActivityDetails(activityHeadUpdate.getActivityDetails() == null ?
                activityHead.getActivityDetails() : activityHeadUpdate.getActivityDetails());
        activityHead.setActivityEstBudgetCap(activityHeadUpdate.getActivityEstBudgetCap() == null ?
                activityHead.getActivityEstBudgetCap() : activityHeadUpdate.getActivityEstBudgetCap());
        activityHead.setActualEndDate(activityHeadUpdate.getActualEndDate() == null ?
                activityHead.getActualEndDate() : activityHeadUpdate.getActualEndDate());
        activityHead.setStatus(activityHeadUpdate.getStatus() == null ? activityHead.getStatus() :
                activityHeadUpdate.getStatus());
        activityHead.setTotalHoursUsed(activityHeadUpdate.getTotalHoursUsed() == null ? activityHead.getTotalHoursUsed() :
                activityHeadUpdate.getTotalHoursUsed());
        activityHead.setBudgetedHours(activityHeadUpdate.getBudgetedHours() == null ? activityHead.getBudgetedHours() :
                activityHeadUpdate.getBudgetedHours());
        activityHead.setAssigneeId(activityHeadUpdate.getAssigneeId() == null ?
                activityHead.getAssigneeId() : activityHeadUpdate.getAssigneeId());
        activityHead.setSite(activityHeadUpdate.getSite() == null ? activityHead.getSite() :
                activityHeadUpdate.getSite());
        activityHead.setActualStartDate(activityHeadUpdate.getActualStartDate() == null ?
                activityHead.getActualStartDate() : activityHeadUpdate.getActualStartDate());
        activityHead.setEstEndDate(activityHeadUpdate.getEstEndDate() == null ? activityHead.getEstEndDate() :
                activityHeadUpdate.getEstEndDate());
        activityHead.setEstStartDate(activityHeadUpdate.getEstStartDate() == null ? activityHead.getEstStartDate() :
                activityHeadUpdate.getEstStartDate());
        activityHead.setPhase(activityHeadUpdate.getPhase() == null ? activityHead.getPhase() :
                activityHeadUpdate.getPhase());
        activityHead.setProjectHead(activityHeadUpdate.getProjectHead() == null ? activityHead.getProjectHead() :
                activityHeadUpdate.getProjectHead());
        activityHead.setSummary(activityHeadUpdate.getSummary() == null ? activityHead.getSummary() :
                activityHeadUpdate.getSummary());
        activityHead.setType(activityHeadUpdate.getType() == null ? activityHead.getType() :
                activityHeadUpdate.getType());
        activityHead.setRegisterId(activityHeadUpdate.getRegisterId() == null ? activityHead.getRegisterId() :
                activityHeadUpdate.getRegisterId());
        activityHead.setDescription(activityHeadUpdate.getDescription() == null ? activityHead.getDescription() :
                activityHeadUpdate.getDescription());
        activityHead.setDependencyType(activityHeadUpdate.getDependencyType() == null ? activityHead.getDependencyType() :
                activityHeadUpdate.getDependencyType());
        activityHead.setPhaseName(activityHeadUpdate.getPhaseName() == null ? activityHead.getPhaseName() :
                activityHeadUpdate.getPhaseName());
        activityHead.setProjectId(activityHeadUpdate.getProjectId() == null ?
                activityHead.getProjectHead().getId() : activityHeadUpdate.getProjectId());
        activityHead.setPhaseId(activityHeadUpdate.getPhaseId() == null ?
                activityHead.getPhase() == null ? null : activityHead.getPhase().getId() : activityHeadUpdate.getPhaseId());
        return activityHead;
    }

    public static List<ActivityHead> toActivityHeads(List<ActivityHeadDTO> activityHeadDTOS) {
        return activityHeadDTOS.stream().map(a -> toActivityHead(a)).collect(Collectors.toList());
    }

    public static List<ActivityHeadDTO> toActivityHeadDTOs(List<ActivityHead> activityHeads) {
        return activityHeads.stream().map(a -> toActivityHeadDTO(a)).collect(Collectors.toList());
    }

    // ActivityDetail ////////////////////////////////////////////////
    public static ActivityDetail toActivityDetail(ActivityDetailDTO activityDetailDTO) {
        if (activityDetailDTO == null) {
            return null;
        }
        return ActivityDetail.builder()
                .id(activityDetailDTO.getId())
                .activityId(activityDetailDTO.getActivityId())
                .measureId(activityDetailDTO.getMeasureId())
                .validationParams(activityDetailDTO.getValidationParams())
                .validationRule(activityDetailDTO.getValidationRule())
                .value(activityDetailDTO.getValue())
                .lastUpdateBy(activityDetailDTO.getLastUpdateBy())
                .build();
    }

    public static ActivityDetailDTO toActivityDetailDTO(ActivityDetail activityDetail) {
        if (activityDetail == null) {
            return null;
        }
        return ActivityDetailDTO.builder()
                .id(activityDetail.getId())
                .activityId(activityDetail.getActivityId())
                .measureId(activityDetail.getMeasureId())
                .validationParams(activityDetail.getValidationParams())
                .validationRule(activityDetail.getValidationRule())
                .value(activityDetail.getValue())
                .lastUpdateBy(activityDetail.getLastUpdateBy())
                .build();
    }

    public static ActivityDetail toUpdatedActivityDetail(ActivityDetail activityDetail,
                                                         ActivityDetail activityDetailUpdate) {
        //activityDetail.setActivityId(activityDetail.getActivityId());
        activityDetail.setMeasureId(activityDetailUpdate.getMeasureId() == null ? activityDetail.getMeasureId() :
                activityDetailUpdate.getMeasureId());
        activityDetail.setValue(activityDetailUpdate.getValue() == null ? activityDetail.getValue() :
                activityDetailUpdate.getValue());
        activityDetail.setValidationParams(activityDetailUpdate.getValidationParams() == null ?
                activityDetail.getValidationParams() : activityDetailUpdate.getValidationParams());
        activityDetail.setValidationRule(activityDetailUpdate.getValidationRule() == null ?
                activityDetail.getValidationRule() : activityDetailUpdate.getValidationRule());
        activityDetail.setLastUpdateBy(activityDetailUpdate.getLastUpdateBy() == null ?
                activityDetail.getLastUpdateBy() : activityDetailUpdate.getLastUpdateBy());
        return activityDetail;
    }

    public static List<ActivityDetail> toActivityDetails(List<ActivityDetailDTO> activityDetailDTOS) {
        return activityDetailDTOS.stream().map(a -> toActivityDetail(a)).collect(Collectors.toList());
    }

    public static List<ActivityDetailDTO> toActivityDetailDTOs(List<ActivityDetail> activityDetails) {
        return activityDetails.stream().map(a -> toActivityDetailDTO(a)).collect(Collectors.toList());
    }
}
