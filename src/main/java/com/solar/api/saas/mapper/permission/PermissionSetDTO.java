package com.solar.api.saas.mapper.permission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.model.permission.Permission;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionSetDTO {

    private Long id;
    private String name;
    private String description;
    private Permission permission;
    private Set<String> userLevels;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
