package com.solar.api.tenant.model.extended.project.activity.task;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "task_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private Long taskId;
    private Long measureId;
    private String value;
    private Date lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;

    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private TaskHead taskHead;

}
