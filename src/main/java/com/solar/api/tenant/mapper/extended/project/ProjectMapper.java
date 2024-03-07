package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.mapper.extended.project.activity.ActivityMapper;
import com.solar.api.tenant.model.extended.project.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    // ProjectHead ////////////////////////////////////////////////
    public static ProjectHead toProjectHead(ProjectHeadDTO projectHeadDTO) {
        if (projectHeadDTO == null) {
            return null;
        }
        return ProjectHead.builder()
                .id(projectHeadDTO.getId())
                .registerId(projectHeadDTO.getRegisterId())
                .projectName(projectHeadDTO.getProjectName())
                .description(projectHeadDTO.getDescription())
                .primarySponsorId(projectHeadDTO.getPrimarySponsorId())
                .type(projectHeadDTO.getType())
                .status(projectHeadDTO.getStatus())
                .activityHeads(projectHeadDTO.getActivityHeadDTOs() != null ? new HashSet<>(ActivityMapper.toActivityHeadDTOs(new ArrayList(projectHeadDTO.getActivityHeadDTOs())))
                        : Collections.emptySet())
                .estStartDate(projectHeadDTO.getEstStartDate())
                .estEndDate(projectHeadDTO.getEstEndDate())
                .actualStartDate(projectHeadDTO.getActualStartDate())
                .actualEndDate(projectHeadDTO.getActualEndDate())
                .phases(new HashSet(PhaseMapper.toPhases(new ArrayList<>(projectHeadDTO.getPhases()))))
                .estBudgetCap(projectHeadDTO.getEstBudgetCap())
                .relatedProject(projectHeadDTO.getRelatedProject())
                .currency(projectHeadDTO.getCurrency())
                .totalHoursUsed(projectHeadDTO.getTotalHoursUsed())
                .estHours(projectHeadDTO.getEstHours())
                .externalReferenceId(projectHeadDTO.getExternalReferenceId())
                .projectDetails(projectHeadDTO.getProjectDetailDTOs() != null ? new HashSet(toProjectDetails(projectHeadDTO.getProjectDetailDTOs())) : Collections.emptySet())
                .systemSizeAc(projectHeadDTO.getSystemSizeAc())
                .systemSizeDc(projectHeadDTO.getSystemSizeDc())
                .projectManager(projectHeadDTO.getProjectManager())
                .createdAt(projectHeadDTO.getCreatedAt())
                .updatedAt(projectHeadDTO.getUpdatedAt())
                .isActivityLevel(projectHeadDTO.getIsActivityLevel())
                .isTaskLevel(projectHeadDTO.getIsTaskLevel())
                .build();
    }

    public static ProjectHeadDTO toProjectHeadDTO(ProjectHead projectHead) {
        if (projectHead == null) {
            return null;
        }
        return ProjectHeadDTO.builder()
                .id(projectHead.getId())
                .registerId(projectHead.getRegisterId())
                .projectName(projectHead.getProjectName())
                .description(projectHead.getDescription())
                .primarySponsorId(projectHead.getPrimarySponsorId())
                .type(projectHead.getType())
                .status(projectHead.getStatus())
                .estStartDate(projectHead.getEstStartDate())
                .estEndDate(projectHead.getEstEndDate())
                .actualStartDate(projectHead.getActualStartDate())
                .actualEndDate(projectHead.getActualEndDate())
                .phases(new HashSet(PhaseMapper.toPhaseDTOs(new ArrayList<>(projectHead.getPhases()))))
                .estBudgetCap(projectHead.getEstBudgetCap())
                .relatedProject(projectHead.getRelatedProject())
                .currency(projectHead.getCurrency())
                .totalHoursUsed(projectHead.getTotalHoursUsed())
                .estHours(projectHead.getEstHours())
                .externalReferenceId(projectHead.getExternalReferenceId())
                .projectDetailDTOs(projectHead.getProjectDetails() != null ? toProjectDetailDTOs(new ArrayList(projectHead.getProjectDetails())) : Collections.emptyList())
                .systemSizeAc(projectHead.getSystemSizeAc())
                .systemSizeDc(projectHead.getSystemSizeDc())
                .bgColor(projectHead.getBgColor())
                .direction(projectHead.getDirection())
                .isDependent(projectHead.getIsDependent())
                .isDisable(projectHead.getIsDisable())
                .dependentId(projectHead.getDependentId())
                .preDepType(projectHead.getPreDepType())
                .activityHeadDTOs(projectHead.getActivityHeads() != null ? ActivityMapper.toActivityHeadDTOs(new ArrayList(projectHead.getActivityHeads())) : Collections.emptyList())
                .projectManager(projectHead.getProjectManager())
                .createdAt(projectHead.getCreatedAt())
                .updatedAt(projectHead.getUpdatedAt())
                .isActivityLevel(projectHead.getIsActivityLevel())
                .isTaskLevel(projectHead.getIsTaskLevel())
                .dependencyType(projectHead.getDependencyType())
                .build();
    }

    public static ProjectHead toUpdatedProjectHead(ProjectHead projectHead, ProjectHead projectHeadUpdate) {
        projectHead.setRegisterId(projectHeadUpdate.getRegisterId() == null ? projectHead.getRegisterId() :
                projectHeadUpdate.getRegisterId());
        projectHead.setProjectName(projectHeadUpdate.getProjectName() == null ? projectHead.getProjectName() :
                projectHeadUpdate.getProjectName());
        projectHead.setDescription(projectHeadUpdate.getDescription() == null ? projectHead.getDescription() :
                projectHeadUpdate.getDescription());
        projectHead.setPrimarySponsorId(projectHeadUpdate.getPrimarySponsorId() == null ? projectHead.getPrimarySponsorId() :
                projectHeadUpdate.getPrimarySponsorId());
        projectHead.setType(projectHeadUpdate.getType() == null ? projectHead.getType() :
                projectHeadUpdate.getType());
        projectHead.setStatus(projectHeadUpdate.getStatus() == null ? projectHead.getStatus() :
                projectHeadUpdate.getStatus());
        projectHead.setEstStartDate(projectHeadUpdate.getEstStartDate() == null ? projectHead.getEstStartDate() :
                projectHeadUpdate.getEstStartDate());
        projectHead.setEstEndDate(projectHeadUpdate.getEstEndDate() == null ? projectHead.getEstEndDate() :
                projectHeadUpdate.getEstEndDate());
        projectHead.setActualStartDate(projectHeadUpdate.getActualStartDate() == null ? projectHead.getActualStartDate() :
                projectHeadUpdate.getActualStartDate());
        projectHead.setActualEndDate(projectHeadUpdate.getActualEndDate() == null ? projectHead.getActualEndDate() :
                projectHeadUpdate.getActualEndDate());
//        projectHead.setPhases(projectHeadUpdate.getPhase() == null ? projectHead.getPhase() :
//                projectHeadUpdate.getPhase());
        projectHead.setEstBudgetCap(projectHeadUpdate.getEstBudgetCap() == null ? projectHead.getEstBudgetCap() :
                projectHeadUpdate.getEstBudgetCap());
        projectHead.setRelatedProject(projectHeadUpdate.getRelatedProject() == null ? projectHead.getRelatedProject() :
                projectHeadUpdate.getRelatedProject());
        projectHead.setCurrency(projectHeadUpdate.getCurrency() == null ? projectHead.getCurrency() :
                projectHeadUpdate.getCurrency());
        projectHead.setTotalHoursUsed(projectHeadUpdate.getTotalHoursUsed() == null ? projectHead.getTotalHoursUsed() :
                projectHeadUpdate.getTotalHoursUsed());
        projectHead.setEstHours(projectHeadUpdate.getEstHours() == null ? projectHead.getEstHours() :
                projectHeadUpdate.getEstHours());
        projectHead.setProjectManager(projectHeadUpdate.getProjectManager() == null ? projectHead.getProjectManager() :
                projectHeadUpdate.getProjectManager());
        projectHead.setSystemSizeAc(projectHeadUpdate.getSystemSizeAc() == null ? projectHead.getSystemSizeAc() :
                projectHeadUpdate.getSystemSizeAc());
        projectHead.setSystemSizeDc(projectHeadUpdate.getSystemSizeDc() == null ? projectHead.getSystemSizeDc() :
                projectHeadUpdate.getSystemSizeDc());
        projectHead.setIsActivityLevel(projectHeadUpdate.getIsActivityLevel() == null ? projectHead.getIsActivityLevel() :
                projectHeadUpdate.getIsActivityLevel());
        projectHead.setIsTaskLevel(projectHeadUpdate.getIsTaskLevel() == null ? projectHead.getIsTaskLevel() :
                projectHeadUpdate.getIsTaskLevel());
        return projectHead;
    }

    public static List<ProjectHead> toProjectHeads(List<ProjectHeadDTO> projectHeadDTOS) {
        return projectHeadDTOS.stream().map(p -> toProjectHead(p)).collect(Collectors.toList());
    }

    public static List<ProjectHeadDTO> toProjectHeadDTOs(List<ProjectHead> projectHeads) {
        return projectHeads.stream().map(p -> toProjectHeadDTO(p)).collect(Collectors.toList());
    }

    // ProjectDetail //////////////////////////////////////////////
    public static ProjectDetail toProjectDetail(ProjectDetailDTO projectDetailDTO) {
        if (projectDetailDTO == null) {
            return null;
        }
        return ProjectDetail.builder()
                .id(projectDetailDTO.getId())
                .projectId(projectDetailDTO.getProjectId())
                .measureCodeId(projectDetailDTO.getMeasureCodeId())
                .measure(projectDetailDTO.getMeasure())
                .value(projectDetailDTO.getValue())
                //.measureDefinition(projectDetailDTO.getMeasureDefinition() != null ? projectDetailDTO.getMeasureDefinition() : null)
                .category(projectDetailDTO.getCategory())
                .filterByInd(projectDetailDTO.getFilterByInd())
                .validationParams(projectDetailDTO.getValidationParams())
                .validationRule(projectDetailDTO.getValidationRule())
                .lastUpdateOn(projectDetailDTO.getLastUpdateOn())
                .lastUpdateBy(projectDetailDTO.getLastUpdateBy())
                .build();
    }

    public static ProjectDetailDTO toProjectDetailDTO(ProjectDetail projectDetail) {
        if (projectDetail == null) {
            return null;
        }
        return ProjectDetailDTO.builder()
                .id(projectDetail.getId())
                .projectId(projectDetail.getProjectId())
                .measureCodeId(projectDetail.getMeasureCodeId())
                .measure(projectDetail.getMeasure())
                .value(projectDetail.getValue())
                .category(projectDetail.getCategory())
                .measureDefinition(projectDetail.getMeasureDefinitionTenant() != null ? projectDetail.getMeasureDefinitionTenant() : null)
                .filterByInd(projectDetail.getFilterByInd())
                .validationParams(projectDetail.getValidationParams())
                .validationRule(projectDetail.getValidationRule())
                .lastUpdateOn(projectDetail.getLastUpdateOn())
                .lastUpdateBy(projectDetail.getLastUpdateBy())
                .createdAt(projectDetail.getCreatedAt())
                .updatedAt(projectDetail.getUpdatedAt())
                .build();
    }

    public static ProjectDetail toUpdatedProjectDetail(ProjectDetail projectDetail, ProjectDetail projectDetailUpdate) {
        projectDetail.setProjectHead(projectDetailUpdate.getProjectHead() == null ? projectDetail.getProjectHead() :
                projectDetailUpdate.getProjectHead());
//        projectDetail.setProjectId(projectDetailUpdate.getProjectId() == null ? projectDetail.getProjectId() :
//                projectDetailUpdate.getProjectId());
        projectDetail.setMeasureCodeId(projectDetailUpdate.getMeasureCodeId() == null ? projectDetail.getMeasureCodeId() :
                projectDetailUpdate.getMeasureCodeId());
        projectDetail.setMeasure(projectDetailUpdate.getMeasure() == null ? projectDetail.getMeasure() :
                projectDetailUpdate.getMeasure());
        projectDetail.setValue(projectDetailUpdate.getValue() == null ? projectDetail.getValue() :
                projectDetailUpdate.getValue());
        projectDetail.setCategory(projectDetailUpdate.getCategory() == null ? projectDetail.getCategory() :
                projectDetailUpdate.getCategory());
        projectDetail.setFilterByInd(projectDetailUpdate.getFilterByInd() == null ? projectDetail.getFilterByInd() :
                projectDetailUpdate.getFilterByInd());
        projectDetail.setValidationParams(projectDetailUpdate.getValidationParams() == null ? projectDetail.getValidationParams() :
                projectDetailUpdate.getValidationParams());
        projectDetail.setValidationParams(projectDetailUpdate.getValidationParams() == null ? projectDetail.getValidationParams() :
                projectDetailUpdate.getValidationParams());
        projectDetail.setValidationRule(projectDetailUpdate.getValidationRule() == null ? projectDetail.getValidationRule() :
                projectDetailUpdate.getValidationRule());
        projectDetail.setValidationRule(projectDetailUpdate.getValidationRule() == null ? projectDetail.getValidationRule() :
                projectDetailUpdate.getValidationRule());
        return projectDetail;
    }

    public static List<ProjectDetail> toProjectDetails(List<ProjectDetailDTO> projectDetailDTOS) {
        return projectDetailDTOS.stream().map(p -> toProjectDetail(p)).collect(Collectors.toList());
    }

    public static List<ProjectDetailDTO> toProjectDetailDTOs(List<ProjectDetail> projectDetails) {
        return projectDetails.stream().map(p -> toProjectDetailDTO(p)).collect(Collectors.toList());
    }

    public static CommunicationLog toCommunicationLog(CommunicationLogDTO communicationLogDTO) {
        return CommunicationLog.builder()
                .id(communicationLogDTO.getId())
                .level(communicationLogDTO.getLevel())
                .levelId(communicationLogDTO.getLevelId())
                .severity(communicationLogDTO.getSeverity())
                .type(communicationLogDTO.getType())
                .approvalRequired(communicationLogDTO.getApprovalRequired())
                .approver(communicationLogDTO.getApprover())
                .approvalDate(communicationLogDTO.getApprovalDate())
                .requester(communicationLogDTO.getRequester())
                .message(communicationLogDTO.getMessage())
                .docId(communicationLogDTO.getDocId())
                .recSeqNo(communicationLogDTO.getRecSeqNo())
                .build();
    }

    public static CommunicationLogDTO toCommunicationLogDTO(CommunicationLog communicationLog) {
        if (communicationLog == null) {
            return null;
        }
        return CommunicationLogDTO.builder()
                .id(communicationLog.getId())
                .level(communicationLog.getLevel())
                .levelId(communicationLog.getLevelId())
                .severity(communicationLog.getSeverity())
                .type(communicationLog.getType())
                .approvalRequired(communicationLog.getApprovalRequired())
                .approver(communicationLog.getApprover())
                .approvalDate(communicationLog.getApprovalDate())
                .requester(communicationLog.getRequester())
                .message(communicationLog.getMessage())
                .docId(communicationLog.getDocId())
                .recSeqNo(communicationLog.getRecSeqNo())
                .build();
    }

    public static CommunicationLog toUpdatedCommunicationLog(CommunicationLog communicationLog,
                                                             CommunicationLog communicationLogUpdate) {
        communicationLog.setLevel(communicationLogUpdate.getLevel() == null ? communicationLog.getLevel() :
                communicationLogUpdate.getLevel());
        communicationLog.setLevelId(communicationLogUpdate.getLevelId() == null ? communicationLog.getLevelId() :
                communicationLogUpdate.getLevelId());
        communicationLog.setSeverity(communicationLogUpdate.getSeverity() == null ? communicationLog.getSeverity() :
                communicationLogUpdate.getSeverity());
        communicationLog.setType(communicationLogUpdate.getType() == null ? communicationLog.getType() :
                communicationLogUpdate.getType());
        communicationLog.setApprovalRequired(communicationLogUpdate.getApprovalRequired() == null ?
                communicationLog.getApprovalRequired() : communicationLogUpdate.getApprovalRequired());
        communicationLog.setApprover(communicationLogUpdate.getApprover() == null ? communicationLog.getApprover() :
                communicationLogUpdate.getApprover());
        communicationLog.setApprovalDate(communicationLogUpdate.getApprovalDate() == null ?
                communicationLog.getApprovalDate() : communicationLogUpdate.getApprovalDate());
        communicationLog.setRequester(communicationLogUpdate.getRequester() == null ?
                communicationLog.getRequester() : communicationLogUpdate.getRequester());
        communicationLog.setMessage(communicationLogUpdate.getMessage() == null ? communicationLog.getMessage() :
                communicationLogUpdate.getMessage());
        communicationLog.setDocId(communicationLogUpdate.getDocId() == null ? communicationLog.getDocId() :
                communicationLogUpdate.getDocId());
        communicationLog.setRecSeqNo(communicationLogUpdate.getRecSeqNo() == null ? communicationLog.getRecSeqNo() :
                communicationLogUpdate.getRecSeqNo());
        return communicationLog;
    }

    public static List<CommunicationLog> toCommunicationLogs(List<CommunicationLogDTO> communicationLogDTOS) {
        return communicationLogDTOS.stream().map(cl -> toCommunicationLog(cl)).collect(Collectors.toList());
    }

    public static List<CommunicationLogDTO> toCommunicationLogDTOs(List<CommunicationLog> communicationLogs) {
        return communicationLogs.stream().map(cl -> toCommunicationLogDTO(cl)).collect(Collectors.toList());
    }

    public static FinancialAccrual toFinancialAccrual(FinancialAccrualDTO financialAccrualDTO) {
        return FinancialAccrual.builder()
                .id(financialAccrualDTO.getId())
                .category(financialAccrualDTO.getCategory())
                .refId(financialAccrualDTO.getRefId())
                .subRefId(financialAccrualDTO.getSubRefId())
                .accrualCategory(financialAccrualDTO.getAccrualCategory())
                .accrualDatetime(financialAccrualDTO.getAccrualDatetime())
                .accrualAdjustment(financialAccrualDTO.getAccrualAdjustment())
                .accrualPeriod(financialAccrualDTO.getAccrualPeriod())
                .accruedAmount(financialAccrualDTO.getAccruedAmount())
                .rate(financialAccrualDTO.getRate())
                .postingDate(financialAccrualDTO.getPostingDate())
                .status(financialAccrualDTO.getStatus())
                .orgId(financialAccrualDTO.getOrgId())
                .build();
    }

    public static FinancialAccrualDTO toFinancialAccrualDTO(FinancialAccrual financialAccrual) {
        if (financialAccrual == null) {
            return null;
        }
        return FinancialAccrualDTO.builder()
                .id(financialAccrual.getId())
                .category(financialAccrual.getCategory())
                .refId(financialAccrual.getRefId())
                .subRefId(financialAccrual.getSubRefId())
                .accrualCategory(financialAccrual.getAccrualCategory())
                .accrualDatetime(financialAccrual.getAccrualDatetime())
                .accrualAdjustment(financialAccrual.getAccrualAdjustment())
                .accrualPeriod(financialAccrual.getAccrualPeriod())
                .accruedAmount(financialAccrual.getAccruedAmount())
                .rate(financialAccrual.getRate())
                .postingDate(financialAccrual.getPostingDate())
                .status(financialAccrual.getStatus())
                .orgId(financialAccrual.getOrgId())
                .build();
    }

    public static FinancialAccrual toUpdatedFinancialAccrual(FinancialAccrual financialAccrual,
                                                             FinancialAccrual financialAccrualUpdate) {
        financialAccrual.setCategory(financialAccrualUpdate.getCategory() == null ? financialAccrual.getCategory() :
                financialAccrualUpdate.getCategory());
        financialAccrual.setRefId(financialAccrualUpdate.getRefId() == null ? financialAccrual.getRefId() :
                financialAccrualUpdate.getRefId());
        financialAccrual.setSubRefId(financialAccrualUpdate.getSubRefId() == null ? financialAccrual.getSubRefId() :
                financialAccrualUpdate.getSubRefId());
        financialAccrual.setAccrualCategory(financialAccrualUpdate.getAccrualCategory() == null ?
                financialAccrual.getAccrualCategory() : financialAccrualUpdate.getAccrualCategory());
        financialAccrual.setAccrualDatetime(financialAccrualUpdate.getAccrualDatetime() == null ?
                financialAccrual.getAccrualDatetime() : financialAccrualUpdate.getAccrualDatetime());
        financialAccrual.setAccrualAdjustment(financialAccrualUpdate.getAccrualAdjustment() == null ?
                financialAccrual.getAccrualAdjustment() : financialAccrualUpdate.getAccrualAdjustment());
        financialAccrual.setAccrualPeriod(financialAccrualUpdate.getAccrualPeriod() == null ?
                financialAccrual.getAccrualPeriod() : financialAccrualUpdate.getAccrualPeriod());
        financialAccrual.setRate(financialAccrualUpdate.getRate() == null ? financialAccrual.getRate() :
                financialAccrualUpdate.getRate());
        financialAccrual.setPostingDate(financialAccrualUpdate.getPostingDate() == null ?
                financialAccrual.getPostingDate() : financialAccrualUpdate.getPostingDate());
        financialAccrual.setStatus(financialAccrualUpdate.getStatus() == null ? financialAccrual.getStatus() :
                financialAccrualUpdate.getStatus());
        financialAccrual.setOrgId(financialAccrualUpdate.getOrgId() == null ? financialAccrual.getOrgId() :
                financialAccrualUpdate.getOrgId());
        return financialAccrual;
    }

    public static List<FinancialAccrual> toFinancialAccruals(List<FinancialAccrualDTO> financialAccrualDTOS) {
        return financialAccrualDTOS.stream().map(fa -> toFinancialAccrual(fa)).collect(Collectors.toList());
    }

    public static List<FinancialAccrualDTO> toFinancialAccrualDTOs(List<FinancialAccrual> financialAccruals) {
        return financialAccruals.stream().map(fa -> toFinancialAccrualDTO(fa)).collect(Collectors.toList());
    }

    public static ProjectLevelDoc toProjectLevelDoc(ProjectLevelDocDTO projectLevelDocDTO) {
        return ProjectLevelDoc.builder()
                .level(projectLevelDocDTO.getLevel())
                .levelId(projectLevelDocDTO.getLevelId())
                .docId(projectLevelDocDTO.getDocId())
                .build();
    }

    public static ProjectLevelDocDTO toProjectLevelDocDTO(ProjectLevelDoc projectLevelDoc) {
        if (projectLevelDoc == null) {
            return null;
        }
        return ProjectLevelDocDTO.builder()
                .level(projectLevelDoc.getLevel())
                .levelId(projectLevelDoc.getLevelId())
                .docId(projectLevelDoc.getDocId())
                .build();
    }

    public static ProjectLevelDoc toUpdatedProjectLevelDoc(ProjectLevelDoc projectLevelDoc,
                                                           ProjectLevelDoc projectLevelDocUpdate) {
        projectLevelDoc.setLevel(projectLevelDocUpdate.getLevel() == null ? projectLevelDoc.getLevel() :
                projectLevelDocUpdate.getLevel());
        projectLevelDoc.setLevelId(projectLevelDocUpdate.getLevelId() == null ? projectLevelDoc.getLevelId() :
                projectLevelDocUpdate.getLevelId());
        projectLevelDoc.setDocId(projectLevelDocUpdate.getDocId() == null ? projectLevelDoc.getDocId() :
                projectLevelDocUpdate.getDocId());
        return projectLevelDoc;
    }

    public static List<ProjectLevelDoc> toProjectLevelDocs(List<ProjectLevelDocDTO> projectLevelDocDTOS) {
        return projectLevelDocDTOS.stream().map(pld -> toProjectLevelDoc(pld)).collect(Collectors.toList());
    }

    public static List<ProjectLevelDocDTO> toProjectLevelDocDTOs(List<ProjectLevelDoc> projectLevelDocs) {
        return projectLevelDocs.stream().map(pld -> toProjectLevelDocDTO(pld)).collect(Collectors.toList());
    }

    public static ProjectPartner toProjectPartner(ProjectPartnerDTO projectPartnerDTO) {
        return ProjectPartner.builder()
                .id(projectPartnerDTO.getId())
                .projectId(projectPartnerDTO.getProjectId())
                .partnerId(projectPartnerDTO.getPartnerId())
                .partnerHead(projectPartnerDTO.getPartnerHead())
                .associationType(projectPartnerDTO.getAssociationType())
                .revenueCap(projectPartnerDTO.getRevenueCap())
                .actualRevenue(projectPartnerDTO.getActualRevenue())
                .actualRevenueUsed(projectPartnerDTO.getActualRevenueUsed())
                .estimatedStartDate(projectPartnerDTO.getEstimatedStartDate())
                .estimatedEndDate(projectPartnerDTO.getEstimatedEndDate())
                .build();
    }

    public static ProjectPartnerDTO toProjectPartnerDTO(ProjectPartner projectPartner) {
        if (projectPartner == null) {
            return null;
        }
        return ProjectPartnerDTO.builder()
                .id(projectPartner.getId())
                .projectId(projectPartner.getProjectId())
                .partnerId(projectPartner.getPartnerId())
                .partnerHead(projectPartner.getPartnerHead())
                .associationType(projectPartner.getAssociationType())
                .revenueCap(projectPartner.getRevenueCap())
                .actualRevenue(projectPartner.getActualRevenue())
                .actualRevenueUsed(projectPartner.getActualRevenueUsed())
                .estimatedStartDate(projectPartner.getEstimatedStartDate())
                .estimatedEndDate(projectPartner.getEstimatedEndDate())
                .build();
    }

    public static ProjectPartner toUpdatedProjectPartner(ProjectPartner projectPartner,
                                                         ProjectPartner projectPartnerUpdate) {
        projectPartner.setProjectId(projectPartnerUpdate.getProjectId() == null ? projectPartner.getProjectId() :
                projectPartnerUpdate.getProjectId());
        projectPartner.setPartnerId(projectPartnerUpdate.getPartnerId() == null ? projectPartner.getPartnerId() :
                projectPartnerUpdate.getPartnerId());
        projectPartner.setAssociationType(projectPartnerUpdate.getAssociationType() == null ?
                projectPartner.getAssociationType() : projectPartnerUpdate.getAssociationType());
        projectPartner.setRevenueCap(projectPartnerUpdate.getRevenueCap() == null ? projectPartner.getRevenueCap() :
                projectPartnerUpdate.getRevenueCap());
        projectPartner.setActualRevenue(projectPartnerUpdate.getActualRevenue() == null ?
                projectPartner.getActualRevenue() : projectPartnerUpdate.getActualRevenue());
        projectPartner.setActualRevenueUsed(projectPartnerUpdate.getActualRevenueUsed() == null ?
                projectPartner.getActualRevenueUsed() : projectPartnerUpdate.getActualRevenueUsed());
        projectPartner.setEstimatedStartDate(projectPartnerUpdate.getEstimatedStartDate() == null ?
                projectPartner.getEstimatedStartDate() : projectPartnerUpdate.getEstimatedStartDate());
        return projectPartner;
    }

    public static List<ProjectPartner> toProjectPartners(List<ProjectPartnerDTO> projectPartnerDTOS) {
        return projectPartnerDTOS.stream().map(pp -> toProjectPartner(pp)).collect(Collectors.toList());
    }

    public static List<ProjectPartnerDTO> toProjectPartnerDTOs(List<ProjectPartner> projectPartners) {
        return projectPartners.stream().map(pp -> toProjectPartnerDTO(pp)).collect(Collectors.toList());
    }

    public static ProjectResourceEngagement toProjectResourceEngagement(ProjectResourceEngagementDTO projectResourceEngagementDTO) {
        return ProjectResourceEngagement.builder()
                .id(projectResourceEngagementDTO.getId())
                .projectId(projectResourceEngagementDTO.getProjectId())
                .resourceId(projectResourceEngagementDTO.getResourceId())
                .activityId(projectResourceEngagementDTO.getActivityId())
                .taskId(projectResourceEngagementDTO.getTaskId())
                .partnerId(projectResourceEngagementDTO.getPartnerId())
                .hoursAccrualPeriod(projectResourceEngagementDTO.getHoursAccrualPeriod())
                .estimatedHoursPerDay(projectResourceEngagementDTO.getEstimatedHoursPerDay())
                .officialHoursPerDay(projectResourceEngagementDTO.getOfficialHoursPerDay())
                .engagementChecklistId(projectResourceEngagementDTO.getEngagementChecklistId())
                .externalReferenceId(projectResourceEngagementDTO.getExternalReferenceId())
                .engagementRateGroupId(projectResourceEngagementDTO.getEngagementRateGroupId())
                .engagementRoleId(projectResourceEngagementDTO.getEngagementRoleId())
                .manageRoleLevels(projectResourceEngagementDTO.getManageRoleLevels())
                .permissions(projectResourceEngagementDTO.getPermissions())
                .status(projectResourceEngagementDTO.getStatus())
                .workingOnHolidayAllowed(projectResourceEngagementDTO.getWorkingOnHolidayAllowed())
                .mobileAppAllowed(projectResourceEngagementDTO.getMobileAppAllowed())
                .relatedProject(projectResourceEngagementDTO.getRelatedProject())
                .currency(projectResourceEngagementDTO.getCurrency())
                .notes(projectResourceEngagementDTO.getNotes())
                .startDate(projectResourceEngagementDTO.getStartDate())
                .endDate(projectResourceEngagementDTO.getEndDate())
                .engagementRateGroups(projectResourceEngagementDTO.getEngagementRateGroupsDTO())
                .designation(projectResourceEngagementDTO.getDesignation())
                .build();
    }

    public static ProjectResourceEngagementDTO toProjectResourceEngagementDTO(ProjectResourceEngagement projectResourceEngagement) {
        if (projectResourceEngagement == null) {
            return null;
        }
        return ProjectResourceEngagementDTO.builder()
                .id(projectResourceEngagement.getId())
                .projectId(projectResourceEngagement.getProjectId())
                .resourceId(projectResourceEngagement.getResourceId())
                .name(projectResourceEngagement.getName())
                .activityId(projectResourceEngagement.getActivityId())
                .taskId(projectResourceEngagement.getTaskId())
                .partnerId(projectResourceEngagement.getPartnerId())
                .hoursAccrualPeriod(projectResourceEngagement.getHoursAccrualPeriod())
                .estimatedHoursPerDay(projectResourceEngagement.getEstimatedHoursPerDay())
                .officialHoursPerDay(projectResourceEngagement.getOfficialHoursPerDay())
                .engagementChecklistId(projectResourceEngagement.getEngagementChecklistId())
                .externalReferenceId(projectResourceEngagement.getExternalReferenceId())
                .engagementRateGroupId(projectResourceEngagement.getEngagementRateGroupId())
                .engagementRoleId(projectResourceEngagement.getEngagementRoleId())
                .manageRoleLevels(projectResourceEngagement.getManageRoleLevels())
                .permissions(projectResourceEngagement.getPermissions())
                .status(projectResourceEngagement.getStatus())
                .workingOnHolidayAllowed(projectResourceEngagement.getWorkingOnHolidayAllowed())
                .mobileAppAllowed(projectResourceEngagement.getMobileAppAllowed())
                .relatedProject(projectResourceEngagement.getRelatedProject())
                .currency(projectResourceEngagement.getCurrency())
                .notes(projectResourceEngagement.getNotes())
                .startDate(projectResourceEngagement.getStartDate())
                .endDate(projectResourceEngagement.getEndDate())
                .engagementRateGroupsDTO(projectResourceEngagement.getEngagementRateGroups())
                .projectHeadDTO(projectResourceEngagement.getProjectHeadDTO()!=null ? projectResourceEngagement.getProjectHeadDTO() : null)
                .engagementRateGroupsDTO(projectResourceEngagement.getEngagementRateGroups())
                .designation(projectResourceEngagement.getDesignation())
                .build();
    }

    public static ProjectResourceEngagement toUpdatedProjectResourceEngagement(ProjectResourceEngagement projectResourceEngagement, ProjectResourceEngagement projectResourceEngagementUpdate) {
        projectResourceEngagement.setProjectId(projectResourceEngagementUpdate.getProjectId() == null ? projectResourceEngagement.getProjectId() : projectResourceEngagementUpdate.getProjectId());
        projectResourceEngagement.setResourceId(projectResourceEngagementUpdate.getResourceId() == null ? projectResourceEngagement.getResourceId() : projectResourceEngagementUpdate.getResourceId());
        projectResourceEngagement.setActivityId(projectResourceEngagementUpdate.getActivityId() == null ? projectResourceEngagement.getActivityId() : projectResourceEngagementUpdate.getActivityId());
        projectResourceEngagement.setTaskId(projectResourceEngagementUpdate.getTaskId() == null ? projectResourceEngagement.getTaskId() : projectResourceEngagementUpdate.getTaskId());
        projectResourceEngagement.setPartnerId(projectResourceEngagementUpdate.getPartnerId() == null ? projectResourceEngagement.getResourceId() : projectResourceEngagementUpdate.getPartnerId());
        projectResourceEngagement.setHoursAccrualPeriod(projectResourceEngagementUpdate.getHoursAccrualPeriod() == null ? projectResourceEngagement.getHoursAccrualPeriod() : projectResourceEngagementUpdate.getHoursAccrualPeriod());
        projectResourceEngagement.setEstimatedHoursPerDay(projectResourceEngagementUpdate.getEstimatedHoursPerDay() == null ? projectResourceEngagement.getEngagementRateGroupId() : projectResourceEngagementUpdate.getEstimatedHoursPerDay());
        projectResourceEngagement.setOfficialHoursPerDay(projectResourceEngagementUpdate.getOfficialHoursPerDay() == null ? projectResourceEngagement.getOfficialHoursPerDay() : projectResourceEngagementUpdate.getOfficialHoursPerDay());
        projectResourceEngagement.setEngagementChecklistId(projectResourceEngagementUpdate.getEngagementChecklistId() == null ? projectResourceEngagement.getEngagementChecklistId() : projectResourceEngagementUpdate.getEngagementChecklistId());
        projectResourceEngagement.setExternalReferenceId(projectResourceEngagementUpdate.getExternalReferenceId() == null ? projectResourceEngagement.getExternalReferenceId() : projectResourceEngagementUpdate.getExternalReferenceId());
        projectResourceEngagement.setEngagementRateGroupId(projectResourceEngagementUpdate.getEngagementRateGroupId() == null ? projectResourceEngagement.getEngagementRateGroupId() : projectResourceEngagementUpdate.getEngagementRateGroupId());
        projectResourceEngagement.setEngagementRoleId(projectResourceEngagementUpdate.getEngagementRoleId() == null ? projectResourceEngagement.getEngagementRoleId() : projectResourceEngagementUpdate.getEngagementRoleId());
        projectResourceEngagement.setManageRoleLevels(projectResourceEngagementUpdate.getManageRoleLevels() == null ? projectResourceEngagement.getManageRoleLevels() : projectResourceEngagementUpdate.getManageRoleLevels());
        projectResourceEngagement.setPermissions(projectResourceEngagementUpdate.getPermissions() == null ? projectResourceEngagement.getPermissions() : projectResourceEngagementUpdate.getPermissions());
        projectResourceEngagement.setStatus(projectResourceEngagementUpdate.getStatus() == null ? projectResourceEngagement.getStatus() : projectResourceEngagementUpdate.getStatus());
        projectResourceEngagement.setWorkingOnHolidayAllowed(projectResourceEngagementUpdate.getWorkingOnHolidayAllowed() == null ? projectResourceEngagement.getWorkingOnHolidayAllowed() : projectResourceEngagementUpdate.getWorkingOnHolidayAllowed());
        projectResourceEngagement.setMobileAppAllowed(projectResourceEngagementUpdate.getMobileAppAllowed() == null ? projectResourceEngagement.getMobileAppAllowed() : projectResourceEngagementUpdate.getMobileAppAllowed());
        projectResourceEngagement.setRelatedProject(projectResourceEngagementUpdate.getRelatedProject() == null ? projectResourceEngagement.getRelatedProject() : projectResourceEngagementUpdate.getRelatedProject());
        projectResourceEngagement.setCurrency(projectResourceEngagementUpdate.getCurrency() == null ? projectResourceEngagement.getCurrency() : projectResourceEngagementUpdate.getCurrency());
        projectResourceEngagement.setNotes(projectResourceEngagementUpdate.getNotes() == null ? projectResourceEngagement.getNotes() : projectResourceEngagementUpdate.getNotes());
        projectResourceEngagement.setStartDate(projectResourceEngagementUpdate.getStartDate() == null ? projectResourceEngagement.getStartDate() : projectResourceEngagementUpdate.getStartDate());
        projectResourceEngagement.setEndDate(projectResourceEngagementUpdate.getEndDate() == null ? projectResourceEngagement.getEndDate() : projectResourceEngagementUpdate.getEndDate());
        return projectResourceEngagement;
    }

    public static List<ProjectResourceEngagement> toProjectResourceEngagements(List<ProjectResourceEngagementDTO> projectResourceEngagementDTOS) {
        return projectResourceEngagementDTOS.stream().map(pre -> toProjectResourceEngagement(pre)).collect(Collectors.toList());
    }

    public static List<ProjectResourceEngagementDTO> toProjectResourceEngagementDTOs(List<ProjectResourceEngagement> projectResourceEngagements) {
        return projectResourceEngagements.stream().map(pre -> toProjectResourceEngagementDTO(pre)).collect(Collectors.toList());
    }

    public static ProjectSite toProjectSite(ProjectSiteDTO projectSiteDTO) {
        return ProjectSite.builder()
                .id(projectSiteDTO.getId())
                .projectId(projectSiteDTO.getProjectId())
                .siteId(projectSiteDTO.getSiteId())
                .readiness(projectSiteDTO.getReadiness())
                .siteManagerId(projectSiteDTO.getSiteManagerId())
                .manpowerValue(projectSiteDTO.getManpowerValue())
                .inventoryValue(projectSiteDTO.getInventoryValue())
                .site(projectSiteDTO.getSite())
                .build();
    }

    public static ProjectSiteDTO toProjectSiteDTO(ProjectSite projectSite) {
        if (projectSite == null) {
            return null;
        }
        return ProjectSiteDTO.builder()
                .id(projectSite.getId())
                .projectId(projectSite.getProjectId())
                .siteId(projectSite.getSiteId())
                .readiness(projectSite.getReadiness())
                .siteManagerId(projectSite.getSiteManagerId())
                .manpowerValue(projectSite.getManpowerValue())
                .inventoryValue(projectSite.getInventoryValue())
                .site(projectSite.getSite())
                .build();
    }

    public static ProjectSite toUpdatedProjectSite(ProjectSite projectSite, ProjectSite projectSiteUpdate) {
        projectSite.setProjectId(projectSiteUpdate.getProjectId() == null ? projectSite.getProjectId() :
                projectSiteUpdate.getProjectId());
        projectSite.setSiteId(projectSiteUpdate.getSiteId() == null ? projectSite.getSiteId() :
                projectSiteUpdate.getSiteId());
        projectSite.setReadiness(projectSiteUpdate.getReadiness() == null ? projectSite.getReadiness() :
                projectSiteUpdate.getReadiness());
        projectSite.setSiteManagerId(projectSiteUpdate.getSiteManagerId() == null ? projectSite.getSiteManagerId() :
                projectSiteUpdate.getSiteManagerId());
        projectSite.setManpowerValue(projectSiteUpdate.getManpowerValue() == null ? projectSite.getManpowerValue() :
                projectSiteUpdate.getManpowerValue());
        projectSite.setInventoryValue(projectSiteUpdate.getInventoryValue() == null ?
                projectSite.getInventoryValue() : projectSiteUpdate.getInventoryValue());
        return projectSite;
    }

    public static List<ProjectSite> toProjectSites(List<ProjectSiteDTO> projectSiteDTOS) {
        return projectSiteDTOS.stream().map(ps -> toProjectSite(ps)).collect(Collectors.toList());
    }

    public static List<ProjectSiteDTO> toProjectSiteDTOs(List<ProjectSite> projectSites) {
        return projectSites.stream().map(ps -> toProjectSiteDTO(ps)).collect(Collectors.toList());
    }

    public static EngagementRateGroups toEngagementRateGroup(EngagementRateGroupsDTO resourceEngagementRateDTO) {
        return EngagementRateGroups.builder()
                .id(resourceEngagementRateDTO.getId())
                .groupName(resourceEngagementRateDTO.getGroupName())
                .description(resourceEngagementRateDTO.getDescription())
                .projectId(resourceEngagementRateDTO.getProjectId())
                .category(resourceEngagementRateDTO.getCategory())
                .termLengthInDays(resourceEngagementRateDTO.getTermLengthInDays())
                .rateType(resourceEngagementRateDTO.getRateType())
                .ratePeriod(resourceEngagementRateDTO.getRatePeriod())
                .rate(resourceEngagementRateDTO.getRate())
                .fixedAmount(resourceEngagementRateDTO.getFixedAmount())
                .overtimePeriod(resourceEngagementRateDTO.getOvertimePeriod())
                .overtimeRate(resourceEngagementRateDTO.getOvertimeRate())
                .overtimeFixedAmount(resourceEngagementRateDTO.getOvertimeFixedAmount())
                .build();
    }

    public static EngagementRateGroupsDTO toEngagementRateGroupsDTO(EngagementRateGroups resourceEngagementRate) {
        if (resourceEngagementRate == null) {
            return null;
        }
        return EngagementRateGroupsDTO.builder()
                .id(resourceEngagementRate.getId())
                .groupName(resourceEngagementRate.getGroupName())
                .description(resourceEngagementRate.getDescription())
                .projectId(resourceEngagementRate.getProjectId())
                .category(resourceEngagementRate.getCategory())
                .termLengthInDays(resourceEngagementRate.getTermLengthInDays())
                .rateType(resourceEngagementRate.getRateType())
                .ratePeriod(resourceEngagementRate.getRatePeriod())
                .rate(resourceEngagementRate.getRate())
                .fixedAmount(resourceEngagementRate.getFixedAmount())
                .overtimePeriod(resourceEngagementRate.getOvertimePeriod())
                .overtimeRate(resourceEngagementRate.getOvertimeRate())
                .overtimeFixedAmount(resourceEngagementRate.getOvertimeFixedAmount())
                .build();
    }

    public static EngagementRateGroups toUpdatedEngagementRateGroups(EngagementRateGroups resourceEngagementRate, EngagementRateGroups resourceEngagementRateUpdate) {
        resourceEngagementRate.setGroupName(resourceEngagementRateUpdate.getGroupName() == null ?
                resourceEngagementRate.getGroupName() : resourceEngagementRateUpdate.getGroupName());
        resourceEngagementRate.setDescription(resourceEngagementRateUpdate.getDescription() == null ?
                resourceEngagementRate.getDescription() : resourceEngagementRateUpdate.getDescription());
        resourceEngagementRate.setCategory(resourceEngagementRateUpdate.getCategory() == null ?
                resourceEngagementRate.getCategory() : resourceEngagementRateUpdate.getCategory());
        resourceEngagementRate.setTermLengthInDays(resourceEngagementRateUpdate.getTermLengthInDays() == null ?
                resourceEngagementRate.getTermLengthInDays() : resourceEngagementRateUpdate.getTermLengthInDays());
        resourceEngagementRate.setRateType(resourceEngagementRateUpdate.getRateType() == null ?
                resourceEngagementRate.getRateType() : resourceEngagementRateUpdate.getRateType());
        resourceEngagementRate.setRatePeriod(resourceEngagementRateUpdate.getRatePeriod() == null ?
                resourceEngagementRate.getRatePeriod() : resourceEngagementRateUpdate.getRatePeriod());
        resourceEngagementRate.setRate(resourceEngagementRateUpdate.getRate() == null ?
                resourceEngagementRate.getRate() : resourceEngagementRateUpdate.getRate());
        resourceEngagementRate.setFixedAmount(resourceEngagementRateUpdate.getFixedAmount() == null ?
                resourceEngagementRate.getFixedAmount() : resourceEngagementRateUpdate.getFixedAmount());
        resourceEngagementRate.setOvertimePeriod(resourceEngagementRateUpdate.getOvertimePeriod() == null ?
                resourceEngagementRate.getOvertimePeriod() : resourceEngagementRateUpdate.getOvertimePeriod());
        resourceEngagementRate.setOvertimeFixedAmount(resourceEngagementRateUpdate.getOvertimeFixedAmount() == null ?
                resourceEngagementRate.getOvertimeFixedAmount() :
                resourceEngagementRateUpdate.getOvertimeFixedAmount());
        return resourceEngagementRate;
    }

    public static List<EngagementRateGroups> toEngagementRateGroups(List<EngagementRateGroupsDTO> resourceEngagementRateDTOS) {
        return resourceEngagementRateDTOS.stream().map(rer -> toEngagementRateGroup(rer)).collect(Collectors.toList());
    }

    public static List<EngagementRateGroupsDTO> toEngagementRateGroupsDTOs(List<EngagementRateGroups> resourceEngagementRates) {
        return resourceEngagementRates.stream().map(rer -> toEngagementRateGroupsDTO(rer)).collect(Collectors.toList());
    }


    public static ProjectInventory toProjectInventory(ProjectInventoryDTO projectInventoryDTO) {
        return ProjectInventory.builder()
                .id(projectInventoryDTO.getId())
                .projectId(projectInventoryDTO.getProjectId())
                .assetId(projectInventoryDTO.getAssetId())
                .modelNumber(projectInventoryDTO.getModelNumber())
                .listId(projectInventoryDTO.getListId())
                .assetSerialNumber(projectInventoryDTO.getAssetSerialNumber())
                .quantity(projectInventoryDTO.getQuantity())
                .status(projectInventoryDTO.getStatus())
                .taskId(projectInventoryDTO.getTaskId())
                .location(projectInventoryDTO.getLocation())
                .installDate(projectInventoryDTO.getInstallDate())
                .activationDate(projectInventoryDTO.getActivationDate())
                .inOperation(projectInventoryDTO.getInOperation())
                .expirationDate(projectInventoryDTO.getExpirationDate())
                .relatedProject(projectInventoryDTO.getRelatedProject())
                .currency(projectInventoryDTO.getCurrency())
                .projectInventorySerials(projectInventoryDTO.getProjectInventorySerialDTOs() != null ? toProjectInventorySerials(projectInventoryDTO.getProjectInventorySerialDTOs()) : Collections.emptyList())
                .build();
    }

    public static ProjectInventoryDTO toProjectInventoryDTO(ProjectInventory projectInventory) {
        if (projectInventory == null) {
            return null;
        }
        return ProjectInventoryDTO.builder()
                .id(projectInventory.getId())
                .projectId(projectInventory.getProjectId())
                .assetId(projectInventory.getAssetId())
                .modelNumber(projectInventory.getModelNumber())
                .listId(projectInventory.getListId())
                .assetSerialNumber(projectInventory.getAssetSerialNumber())
                .quantity(projectInventory.getQuantity())
                .status(projectInventory.getStatus())
                .taskId(projectInventory.getTaskId())
                .location(projectInventory.getLocation())
                .installDate(projectInventory.getInstallDate())
                .activationDate(projectInventory.getActivationDate())
                .inOperation(projectInventory.getInOperation())
                .expirationDate(projectInventory.getExpirationDate())
                .relatedProject(projectInventory.getRelatedProject())
                .currency(projectInventory.getCurrency())
                .serialNumberCount(projectInventory.getSerialNumberCount())
                .assetHeadDTO(projectInventory.getAssetHeadDTO()!=null ? projectInventory.getAssetHeadDTO() : null)
                .projectInventorySerialDTOs(projectInventory.getProjectInventorySerials()!=null ? toProjectInventorySerialDTOs(projectInventory.getProjectInventorySerials()) : Collections.emptyList())
                .build();
    }

    public static ProjectInventory toUpdatedProjectInventory(ProjectInventory projectInventory,
                                                             ProjectInventory projectInventoryUpdate) {
        projectInventory.setProjectId(projectInventoryUpdate.getProjectId() == null ?
                projectInventory.getProjectId() : projectInventoryUpdate.getProjectId());
        projectInventory.setAssetId(projectInventoryUpdate.getAssetId() == null ? projectInventory.getAssetId() :
                projectInventoryUpdate.getAssetId());
        projectInventory.setModelNumber(projectInventoryUpdate.getModelNumber() == null ?
                projectInventory.getModelNumber() : projectInventoryUpdate.getModelNumber());
        projectInventory.setListId(projectInventoryUpdate.getListId() == null ? projectInventory.getListId() :
                projectInventoryUpdate.getListId());
        projectInventory.setQuantity(projectInventoryUpdate.getQuantity() == null ? projectInventory.getQuantity() :
                projectInventoryUpdate.getQuantity());
        projectInventory.setStatus(projectInventoryUpdate.getStatus() == null ? projectInventory.getStatus() :
                projectInventoryUpdate.getStatus());
        projectInventory.setTaskId(projectInventoryUpdate.getTaskId() == null ? projectInventory.getTaskId() :
                projectInventoryUpdate.getTaskId());
        projectInventory.setLocation(projectInventoryUpdate.getLocation() == null ? projectInventory.getLocation() :
                projectInventoryUpdate.getLocation());
        projectInventory.setInstallDate(projectInventoryUpdate.getInstallDate() == null ?
                projectInventory.getInstallDate() : projectInventoryUpdate.getInstallDate());
        projectInventory.setActivationDate(projectInventoryUpdate.getActivationDate() == null ?
                projectInventory.getActivationDate() : projectInventoryUpdate.getActivationDate());
        projectInventory.setInOperation(projectInventoryUpdate.getInOperation() == null ?
                projectInventory.getInOperation() : projectInventoryUpdate.getInOperation());
        projectInventory.setExpirationDate(projectInventoryUpdate.getExpirationDate() == null ?
                projectInventory.getExpirationDate() : projectInventoryUpdate.getExpirationDate());
        projectInventory.setRelatedProject(projectInventoryUpdate.getRelatedProject() == null ?
                projectInventory.getRelatedProject() : projectInventoryUpdate.getRelatedProject());
        projectInventory.setCurrency(projectInventoryUpdate.getCurrency() == null ? projectInventory.getCurrency() :
                projectInventoryUpdate.getCurrency());
        return projectInventory;
    }

    public static List<ProjectInventory> toProjectInventories(List<ProjectInventoryDTO> projectInventoryDTOS) {
        return projectInventoryDTOS.stream().map(pi -> toProjectInventory(pi)).collect(Collectors.toList());
    }

    public static List<ProjectInventoryDTO> toProjectInventoryDTOs(List<ProjectInventory> projectInventories) {
        return projectInventories.stream().map(pi -> toProjectInventoryDTO(pi)).collect(Collectors.toList());
    }

    public static ProjectInventorySerial toProjectInventorySerial(ProjectInventorySerialDTO projectInventorySerialDTO) {
        return ProjectInventorySerial.builder()
                .id(projectInventorySerialDTO.getId())
                .assetSerialNumberId(projectInventorySerialDTO.getAssetSerialNumberId())
                .projectInventoryId(projectInventorySerialDTO.getProjectInventoryId())
                .status(projectInventorySerialDTO.getStatus())
                .locationId(projectInventorySerialDTO.getLocationId())
                .createdAt(projectInventorySerialDTO.getCreatedAt())
                .updatedAt(projectInventorySerialDTO.getUpdatedAt())
                .build();
    }

    public static ProjectInventorySerialDTO toProjectInventorySerialDTO(ProjectInventorySerial projectInventorySerial) {
        return ProjectInventorySerialDTO.builder()
                .id(projectInventorySerial.getId())
                .assetSerialNumberId(projectInventorySerial.getAssetSerialNumberId())
                .serialNumber(projectInventorySerial.getSerialNumber())
                .palletNumber(projectInventorySerial.getPalletNumber())
                .projectInventoryId(projectInventorySerial.getProjectInventoryId())
                .status(projectInventorySerial.getStatus())
                .locationId(projectInventorySerial.getLocationId())
                .createdAt(projectInventorySerial.getCreatedAt())
                .updatedAt(projectInventorySerial.getUpdatedAt())
                .build();
    }

    public static ProjectInventorySerial toUpdatedProjectInventorySerial(ProjectInventorySerial projectInventorySerial,
                                                                         ProjectInventorySerial projectInventoryUpdateSerial) {
        projectInventorySerial.setAssetSerialNumberId(projectInventoryUpdateSerial.getAssetSerialNumberId() == null ?
                projectInventorySerial.getAssetSerialNumberId() : projectInventoryUpdateSerial.getAssetSerialNumberId());
        projectInventorySerial.setProjectInventory(projectInventoryUpdateSerial.getProjectInventory() == null ? projectInventorySerial.getProjectInventory() :
                projectInventoryUpdateSerial.getProjectInventory());
        projectInventorySerial.setStatus(projectInventoryUpdateSerial.getStatus() == null ?
                projectInventorySerial.getStatus() : projectInventoryUpdateSerial.getStatus());
        projectInventorySerial.setLocationId(projectInventoryUpdateSerial.getLocationId() == null ? projectInventoryUpdateSerial.getLocationId() :
                projectInventoryUpdateSerial.getLocationId());
        return projectInventorySerial;
    }

    public static List<ProjectInventorySerial> toProjectInventorySerials(List<ProjectInventorySerialDTO> projectInventorySerialDTOS) {
        return projectInventorySerialDTOS.stream().map(pi -> toProjectInventorySerial(pi)).collect(Collectors.toList());
    }

    public static List<ProjectInventorySerialDTO> toProjectInventorySerialDTOs(List<ProjectInventorySerial> projectInventorySerials) {
        return projectInventorySerials.stream().map(pi -> toProjectInventorySerialDTO(pi)).collect(Collectors.toList());
    }

    public static EngagementRole toEngagementRole(EngagementRoleDTO engagementRoleDTO) {
        if (engagementRoleDTO == null) {
            return null;
        }
        return EngagementRole.builder()
                .id(engagementRoleDTO.getId())
                .roleName(engagementRoleDTO.getRoleName())
                .description(engagementRoleDTO.getDescription())
                .appliesTo(engagementRoleDTO.getAppliesTo())
                .glReferenceCode(engagementRoleDTO.getGlReferenceCode())
                .build();
    }

    public static EngagementRoleDTO toEngagementRoleDTO(EngagementRole engagementRole) {
        if (engagementRole == null) {
            return null;
        }
        return EngagementRoleDTO.builder()
                .id(engagementRole.getId())
                .roleName(engagementRole.getRoleName())
                .description(engagementRole.getDescription())
                .appliesTo(engagementRole.getAppliesTo())
                .glReferenceCode(engagementRole.getGlReferenceCode())
                .build();
    }

    public static EngagementRole toUpdatedEngagementRole(EngagementRole engagementRole, EngagementRole engagementRoleUpdate) {
        engagementRole.setRoleName(engagementRoleUpdate.getRoleName() == null ?
                engagementRole.getRoleName() : engagementRoleUpdate.getRoleName());
        engagementRole.setDescription(engagementRoleUpdate.getDescription() == null ?
                engagementRole.getDescription() : engagementRoleUpdate.getDescription());
        engagementRole.setAppliesTo(engagementRoleUpdate.getAppliesTo() == null ?
                engagementRole.getAppliesTo() : engagementRoleUpdate.getAppliesTo());
        engagementRole.setGlReferenceCode(engagementRole.getGlReferenceCode() == null ? engagementRole.getGlReferenceCode() :
                engagementRoleUpdate.getGlReferenceCode());
        return engagementRole;
    }

    public static List<EngagementRole> toEngagementRoles(List<EngagementRoleDTO> engagementRoleDTOS) {
        return engagementRoleDTOS.stream().map(a -> toEngagementRole(a)).collect(Collectors.toList());
    }

    public static List<EngagementRoleDTO> toEngagementRoleDTOs(List<EngagementRole> engagementRoles) {
        return engagementRoles.stream().map(a -> toEngagementRoleDTO(a)).collect(Collectors.toList());
    }

    // ProjectDependencies //////////////////
    public static ProjectDependencies toProjectDependencies(ProjectDependenciesDTO projectDependenciesDTO) {
        if (projectDependenciesDTO == null) {
            return null;
        }
        return ProjectDependencies.builder()
                .id(projectDependenciesDTO.getId())
                .activityId(projectDependenciesDTO.getActivityId())
                .activityName(projectDependenciesDTO.getActivityName())
                .taskId(projectDependenciesDTO.getTaskId())
                .taskName(projectDependenciesDTO.getTaskName())
                .projectId(projectDependenciesDTO.getProjectId())
                .projectName(projectDependenciesDTO.getProjectName())
                .relatedAt(projectDependenciesDTO.getRelatedAt())
                .relatedId(projectDependenciesDTO.getRelatedId())
                .relatedIdName(projectDependenciesDTO.getRelatedIdName())
                .preDepType(projectDependenciesDTO.getPreDepType())
                .precedence(projectDependenciesDTO.getPrecedence())
                .parentId(projectDependenciesDTO.getParentId())
                .sequenceId(projectDependenciesDTO.getSequenceId())
                .dependencyType(projectDependenciesDTO.getDependencyType())
                .build();
    }

    public static ProjectDependenciesDTO toProjectDependenciesDTO(ProjectDependencies projectDependencies) {
        if (projectDependencies == null) {
            return null;
        }
        return ProjectDependenciesDTO.builder()
                .id(projectDependencies.getId())
                .activityId(projectDependencies.getActivityId())
                .activityName(projectDependencies.getActivityName())
                .taskId(projectDependencies.getTaskId())
                .taskName(projectDependencies.getTaskName())
                .projectId(projectDependencies.getProjectId())
                .projectName(projectDependencies.getProjectName())
                .relatedAt(projectDependencies.getRelatedAt())
                .relatedId(projectDependencies.getRelatedId())
                .relatedIdName(projectDependencies.getRelatedIdName())
                .preDepType(projectDependencies.getPreDepType())
                .precedence(projectDependencies.getPrecedence())
                .dependencyType(projectDependencies.getDependencyType())
                .build();
    }

    public static ProjectDependencies toUpdatedProjectDependencies(ProjectDependencies projectDependencies, ProjectDependencies projectDependenciesUpdate) {
        projectDependencies.setActivityId(projectDependenciesUpdate.getActivityId() == null ? projectDependencies.getActivityId() :
                projectDependencies.getActivityId());
        projectDependencies.setTaskId(projectDependenciesUpdate.getTaskId() == null ? projectDependencies.getTaskId() :
                projectDependencies.getTaskId());
        projectDependencies.setProjectId(projectDependenciesUpdate.getProjectId() == null ? projectDependencies.getProjectId() :
                projectDependencies.getProjectId());
        projectDependencies.setRelatedAt(projectDependenciesUpdate.getRelatedAt() == null ? projectDependencies.getRelatedAt() :
                projectDependencies.getRelatedAt());
        projectDependencies.setRelatedId(projectDependenciesUpdate.getRelatedId() == null ? projectDependencies.getRelatedId() :
                projectDependencies.getRelatedId());
        projectDependencies.setPreDepType(projectDependenciesUpdate.getPreDepType() == null ? projectDependencies.getPreDepType() :
                projectDependencies.getPreDepType());
        projectDependencies.setPrecedence(projectDependenciesUpdate.getPrecedence() == null ? projectDependencies.getPrecedence() :
                projectDependencies.getPrecedence());
        projectDependencies.setDependencyType(projectDependenciesUpdate.getDependencyType() == null ? projectDependencies.getDependencyType() :
                projectDependencies.getDependencyType());
        return projectDependencies;
    }

    public static List<ProjectDependencies> toProjectDependenciesList(List<ProjectDependenciesDTO> projectDependenciesDTOS) {
        return projectDependenciesDTOS.stream().map(p -> toProjectDependencies(p)).collect(Collectors.toList());
    }

    public static List<ProjectDependenciesDTO> toProjectDependenciesDTOs(List<ProjectDependencies> projectDependencies) {
        return projectDependencies.stream().map(p -> toProjectDependenciesDTO(p)).collect(Collectors.toList());
    }
}
