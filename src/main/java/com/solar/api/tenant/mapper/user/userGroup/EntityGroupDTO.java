package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityGroupDTO {

    private Long id;
    private boolean status;
    private EntityRoleDTO entityRole;
    private UserGroupDTO userGroup;
    private String createdBy;
    private String updatedBy;

}
