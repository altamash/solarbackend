package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solar.api.tenant.mapper.extended.project.activity.ActivityHeadDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class ProjectHeadDTO {

    private Long id;
    private Long registerId;
    private String projectName;
    private String description;
    private Long primarySponsorId;
    private String type;
    private String status;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date estStartDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date estEndDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date actualStartDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date actualEndDate;
    private Set<PhaseDTO> phases;
    private Long estBudgetCap;
    private String relatedProject;
    private String currency;
    private List<ProjectDetailDTO> projectDetailDTOs;
    private List<ActivityHeadDTO> activityHeadDTOs;
    private Long estHours;
    private Double totalHoursUsed;
    private String externalReferenceId;
    private String projectManager;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long systemSizeAc; //watts
    private Long systemSizeDc;
    private String bgColor;
    private String direction;
    private String preDepType;
    private Boolean isDependent;
    private Boolean isDisable = false;
    private Long dependentId;
    private Boolean isActivityLevel;
    private Boolean isTaskLevel;
    private String dependencyType;
}
