package com.solar.api.tenant.mapper.permission;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionGroupDTO {

    private Long id;
    private String name;
    private String description;
    private String userLevel;
    private Set<AvailablePermissionSetDTO> permissionSets;
    private Set<AvailablePermissionSetDTO> remainingPermissionSets;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
