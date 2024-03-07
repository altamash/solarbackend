package com.solar.api.tenant.mapper.user.role;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.permission.PermissionGroupDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleDTO {
    private Long id;
    private String name;
    private String description;
    private String userLevel;
    private Set<PermissionGroupDTO> permissionGroups;
    private Set<PermissionGroupDTO> remainingPermissionGroups;
}
