package com.solar.api.tenant.mapper.extended.project;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResourceEngagementDTO {

    private Long id;
    private Long projectId;
    private Long resourceId;
    private Long activityId;
    private Long taskId;
    private Long partnerId;
    private Long hoursAccrualPeriod;
    private Long estimatedHoursPerDay;
    private Long officialHoursPerDay;
    private Long engagementChecklistId;
    private Long externalReferenceId;
    private Long engagementRateGroupId;
    private Long engagementRoleId;
    private String manageRoleLevels;
    private String permissions;
    private String status;
    private String workingOnHolidayAllowed;
    private String mobileAppAllowed;
    private String relatedProject;
    private String currency;
    private String notes;
    private Date startDate;
    private Date endDate;
    private EngagementRateGroupsDTO engagementRateGroupsDTO;
    private String name;
    private String designation;
    private ProjectHeadDTO projectHeadDTO;

}
