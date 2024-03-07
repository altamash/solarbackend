package com.solar.api.saas.mapper.permission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.model.permission.component.ComponentLibrary;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionDTO {

    private Long id;
    private String name;
    private String description;
    private ComponentLibrary componentLibrary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
