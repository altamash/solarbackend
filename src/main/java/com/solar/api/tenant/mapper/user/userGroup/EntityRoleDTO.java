package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.contract.EntityDTO;
import com.solar.api.tenant.mapper.extended.FunctionalRolesDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityRoleDTO {

    private Long id;
    private EntityDTO entity;
    private FunctionalRolesDTO functionalRoles;
    private boolean status;
    private boolean isDeleted;
    private String createdBy;
    private String updatedBy;
}
