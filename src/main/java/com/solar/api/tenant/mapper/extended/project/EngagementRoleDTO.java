package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EngagementRoleDTO {

    private Long id;
    private String externalRoleId;
    private String roleName;
    private String description;
    private String appliesTo;
    private Long glReferenceCode;
}
