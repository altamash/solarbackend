package com.solar.api.saas.mapper.extended;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComponentsByRoleDTO {

    private Long id;
    private Long componentId;
    private String roleId;
    private String permissions;
    private String approverRole;
    private Double minimumThreshold;
    private Double maximumThreshold;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
