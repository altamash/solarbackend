package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.userGroup.UserGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddResourceTile {

    private UserGroup userGroup;
    private List<Long> entityRoleIds;
}
