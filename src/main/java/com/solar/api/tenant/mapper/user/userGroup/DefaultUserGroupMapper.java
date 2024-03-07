package com.solar.api.tenant.mapper.user.userGroup;

import com.solar.api.tenant.mapper.billing.billingHead.BillingHeadDTO;
import com.solar.api.tenant.mapper.contract.EntityMapper;
import com.solar.api.tenant.mapper.extended.FunctionalRolesMapper;
import com.solar.api.tenant.model.billing.billingHead.BillingHead;
import com.solar.api.tenant.model.userGroup.EntityRole;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultUserGroupMapper {

    public static DefaultUserGroup toDefaultUserGroup(DefaultUserGroupDTO defaultUserGroupDTO) {
        if (defaultUserGroupDTO == null) {
            return null;
        }
        return DefaultUserGroup.builder()
                .id(defaultUserGroupDTO.getId())
                .fkEntityId(defaultUserGroupDTO.getFkEntityId())
                .fkFunctionRoleId(defaultUserGroupDTO.getFkFunctionRoleId())
                .status(defaultUserGroupDTO.getStatus())
                .createdBy(defaultUserGroupDTO.getCreatedBy())
                .updatedBy(defaultUserGroupDTO.getUpdatedBy())
                .build();
    }

    public static DefaultUserGroupDTO toDefaultUserGroupDTO(DefaultUserGroup defaultUserGroup) {
        if (defaultUserGroup == null) {
            return null;
        }

        return DefaultUserGroupDTO.builder()
                .id(defaultUserGroup.getId())
                .fkEntityId(defaultUserGroup.getEntity() != null ? defaultUserGroup.getEntity().getId() : null)
                .fkFunctionRoleId(defaultUserGroup.getFunctionalRoles() != null ? defaultUserGroup.getFunctionalRoles().getId() : null)
                .status(defaultUserGroup.getStatus())
                .createdBy(defaultUserGroup.getCreatedBy())
                .updatedBy(defaultUserGroup.getUpdatedBy())
                .build();

    }

    public static List<DefaultUserGroup> toDefaultUserGroups(List<DefaultUserGroupDTO> defaultUserGroupDTOS) {
        return defaultUserGroupDTOS.stream().map(bh -> toDefaultUserGroup(bh)).collect(Collectors.toList());
    }
}
