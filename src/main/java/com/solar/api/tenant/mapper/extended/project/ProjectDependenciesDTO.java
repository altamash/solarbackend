package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectDependenciesDTO {

    private Long id;
    private Long activityId;
    private Long taskId;
    private Long projectId;
    private String relatedAt;
    private Long relatedId;
    private String preDepType;
    private Long precedence;
    private String dependencyType;

    private String projectName;
    private String activityName;
    private String taskName;
    private String relatedIdName;
    private String direction;
    private ProjectHeadDTO projectHeadDTO;
    private Long parentId;
    private Long sequenceId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
