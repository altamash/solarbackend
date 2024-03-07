package com.solar.api.tenant.mapper.extended.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResourcesDTO {

    private Long id;
    private Long assignmentId;
    private Long empId;
    private String assignedTo;
    private Long assignmentRefId;
    private String role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
