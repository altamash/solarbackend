package com.solar.api.tenant.model.extended.project;

import com.solar.api.tenant.mapper.extended.project.ProjectHeadDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_dependencies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDependencies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long activityId;
    private Long taskId;
    private Long projectId;
    private String relatedAt;
    private Long relatedId;
    private String preDepType;
    private Long precedence;
    private String dependencyType;

    @Transient
    private ProjectHeadDTO projectHeadDTO;
    @Transient
    private String direction;
    @Transient
    private String projectName;
    @Transient
    private String activityName;
    @Transient
    private String taskName;
    @Transient
    private String relatedIdName;
    @Transient
    private Long parentId; //always projectId
    @Transient
    private Long sequenceId;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
