package com.solar.api.tenant.model.extended.project.activity.task;

import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "task_head")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskType;
    private String status;
    private String summary;
    private String description;
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private ActivityHead activityHead;
    private String site;
    private String type;
    private Long locationId;
    private String phase;
    private Date estStartDate;
    private Date estEndDate;
    private Date actualStartDate;
    private Date actualEndDate;
    private Long activityEstBudgetCap;
    private Long totalHoursUsed;
    private Long budgetedHours;

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
    @OneToMany(mappedBy = "taskHead", cascade = CascadeType.MERGE)
    private Set<TaskDetail> taskDetails;

}
