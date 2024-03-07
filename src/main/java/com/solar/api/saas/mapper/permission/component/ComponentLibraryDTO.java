package com.solar.api.saas.mapper.permission.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentLibraryDTO {

    private Long id;
    private String componentName;
    private String description;
    private Integer level;
    private Long parentId;
    private Long moduleId;
    private Long subModuleId;
    private ComponentTypeProvisionDTO compReference;
    private String compType;
    private String source;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
