package com.solar.api.saas.model.extended;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_definition")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //TODO: add field for code and call batch from code
    private Long compKey;
    private String taskId;
    private String jobName;
    private String functionalArea;
    private String type;
    private String bean;
    private String phase;
    private String preDependency;
    private String cronExpression;
    private String postDependency;
    private String runNotes;
    private String frequency;
    private String parameters;
    private String startTime;
    private String endTime;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
