package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupResponseDTO {

    private Long userGroupId;
    private String userGroupName;
    private int noOfEmployees;
    private List<DefaultUserGroupResponseDTO> defaultUserGroupResponseDTOList;
}
