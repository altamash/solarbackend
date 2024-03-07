package com.solar.api.tenant.model.extended.project.activity;

import com.solar.api.tenant.model.extended.project.Phase;
import com.solar.api.tenant.model.extended.project.ProjectHead;
import com.solar.api.tenant.model.extended.project.activity.task.TaskHead;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "activity_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long registerId;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectHead projectHead;
    private Long locationId;
    private Long assigneeId;
    private String phaseName;
    private String site;
    private String summary;
    private String detail;
    private String type; //support,project
    private String status;
    private String description;
    @ManyToOne
    @JoinColumn(name = "phase_id")
    private Phase phase;
    private Date estStartDate;
    private Date estEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private Long activityEstBudgetCap;
    private Long totalHoursUsed;
    private Long budgetedHours;

    @OneToMany(mappedBy = "activityHead", cascade = CascadeType.MERGE)
    private Set<ActivityDetail> activityDetails;

    @OneToMany(mappedBy = "activityHead", cascade = CascadeType.MERGE)
    private Set<TaskHead> taskHeads;
    @Transient
    private String bgColor;
    @Transient
    private Long projectId;
    @Transient
    private Long phaseId;
    @Transient
    private String direction;
    @Transient
    private Boolean isDependent = false;
    @Transient
    private Long dependentId;// dependencyId
    @Transient
    private String preDepType;
    @Transient
    private Boolean isDisable = false;
    @Transient
    private String dependencyType;
    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
