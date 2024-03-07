package com.solar.api.saas.module.com.solar.scheduler.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "job_scheduler")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobScheduler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long batchDefinitionId;
    private Long jobInstanceId;
    private String taskId;
    private String jobName;
    private String cronExpression;        //<<"0 0 12 5 1/1 ? *">>
    private Date scheduled;    //<<2021-01-05 Tue 12:00:00>>
    private Date startedAt;
    private Date endedAt;
    private Date lastExecutionTime;    //<<2021-01-05 Tue 12:00:00>>
    private Date nextExecutionTime;    //<<2021-01-05 Tue 12:00:00>>
    private Long duration;
    private Long attemptCount;
    private String logUrl;
    private String status;
    @Column(length = 1000)
    private String message;

    @Column(length = 3000)
    private String logs;
    private String state;
    private Long tenantId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
