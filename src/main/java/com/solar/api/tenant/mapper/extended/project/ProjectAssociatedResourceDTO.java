package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectAssociatedResourceDTO {

    private Long projectId;
    private Long projectEngagementId;
    private Long engagementRoleId;
    private Long resourceId;
    private String encodedId;
    private Long taskId;
    private String taskStatus;
    private String taskSummary;
    private String taskDetails;
    private String resourceName;
    private List<RolesAssociatedWithResourceDTO> roles;
    private String resourceType;
    private String roleName;
    private List<CheckInCheckOutDTO> checkInCheckOutDTOList;
    private String clockIn;
    private String clockOut;
    private Double hours;

}
