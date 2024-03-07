package com.solar.api.tenant.mapper.extended.assetHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.user.role.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryDTO {

    private Long id;
    private Long assetId;
    private Integer count;
    private String locType;
    private Long locationId;
    private String statusCode;
    private String projectId;
    private String shelfCode;
    private List<Role> accessibleTo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
