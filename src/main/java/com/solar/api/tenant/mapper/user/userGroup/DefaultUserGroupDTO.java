package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultUserGroupDTO {

    private Long id;
    private Long fkEntityId;
    private Long fkFunctionRoleId;
    private String status;
    private String createdBy;
    private String updatedBy;
}
