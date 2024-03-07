package com.solar.api.tenant.model.extended.project;

import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;


@Entity
@Table(name = "project_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long registerId;
    private String projectName;
    private String description;
    private Long primarySponsorId;
    private String type;
    private String phase;
    private String status;
    private Date estStartDate;
    private Date estEndDate;
    private Date actualStartDate;
    private Date actualEndDate;

    @OneToMany(mappedBy = "projectHead", cascade = CascadeType.MERGE)
    private Set<Phase> phases;

    private Long estBudgetCap;
    private String relatedProject;
    private String currency;
    private Long estHours;
    private Double totalHoursUsed;
    private String externalReferenceId;
    private String projectManager;
    private Long systemSizeAc;
    private Long systemSizeDc;

    @OneToMany(mappedBy = "projectHead", cascade = CascadeType.MERGE)
    private Set<ProjectDetail> projectDetails;

    @OneToMany(mappedBy = "projectHead", cascade = CascadeType.MERGE)
    private Set<ActivityHead> activityHeads;

    @Transient
    private String bgColor;
    @Transient
    private String direction;
    @Transient
    private Boolean isDependent = false;
    @Transient
    private Long dependentId;
    @Transient
    private String preDepType;
    @Transient
    private Boolean isDisable = false;
    @Transient
    private String dependencyType;
    @Column(columnDefinition = "boolean default false")
    private Boolean isActivityLevel; //ripple down the dates to its low level
    @Column(columnDefinition = "boolean default false")
    private Boolean isTaskLevel;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
