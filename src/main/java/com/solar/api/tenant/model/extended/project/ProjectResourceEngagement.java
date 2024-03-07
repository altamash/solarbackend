package com.solar.api.tenant.model.extended.project;

import com.solar.api.tenant.mapper.extended.project.EngagementRateGroupsDTO;
import com.solar.api.tenant.mapper.extended.project.ProjectHeadDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "project_resource_engagements", uniqueConstraints = @UniqueConstraint(columnNames =
        {"resourceId", "engagementRateGroupId", "startDate", "endDate"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResourceEngagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String designation;

//    @Column(columnDefinition = "boolean default false")
//    private Boolean default; //use it for unique measure value

    @Transient
    private EngagementRateGroupsDTO engagementRateGroups;

    @Transient
    private ProjectHeadDTO projectHeadDTO;

    @Transient
    private String name;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
