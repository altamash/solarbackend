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
public class ComponentTypeProvisionDTO {

    private Long id;
    private String compReference;
    private Boolean readAll;
    private Boolean read;
    private Boolean write;
    private Boolean execute;
    private Boolean delete;
    private Boolean update;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
