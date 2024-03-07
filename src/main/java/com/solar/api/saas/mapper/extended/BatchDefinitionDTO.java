package com.solar.api.saas.mapper.extended;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchDefinitionDTO {

    private Long id;
    //TODO: add compKey in mapper
    private Long compKey;
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
