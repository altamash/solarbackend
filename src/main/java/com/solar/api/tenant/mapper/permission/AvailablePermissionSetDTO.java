package com.solar.api.tenant.mapper.permission;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.saas.mapper.permission.PermissionSetDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailablePermissionSetDTO {

    private Long id;
    private Long permissionSetId;
    @Column(unique = true)
    private String name;
    private String description;
    private PermissionSetDTO permissionSet;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
