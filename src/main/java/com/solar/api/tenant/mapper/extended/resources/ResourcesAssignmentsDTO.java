package com.solar.api.tenant.mapper.extended.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourcesAssignmentsDTO {

    private Long id;
    private Long empId;
    private String assignedTo;
    private Long assignmentRefId;
    private Long roleId; //watcher,contributor,manager
}
