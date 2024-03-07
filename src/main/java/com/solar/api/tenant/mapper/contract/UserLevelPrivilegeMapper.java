package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.model.contract.UserLevelPrivilege;

import java.util.List;
import java.util.stream.Collectors;

public class UserLevelPrivilegeMapper {
    public static UserLevelPrivilege toUserLevelPrivilege(UserLevelPrivilegeDTO userLevelPrivilegeDTO) {
        return UserLevelPrivilege.builder()
                .id(userLevelPrivilegeDTO.getId())
                .createdAt(userLevelPrivilegeDTO.getCreatedAt())
                .updatedAt(userLevelPrivilegeDTO.getUpdatedAt())
                .build();
    }

    public static UserLevelPrivilegeDTO toUserLevelPrivilegeDTO(UserLevelPrivilege userLevelPrivilege) {
        if (userLevelPrivilege == null) {
            return null;
        }

        return UserLevelPrivilegeDTO.builder()
                .id(userLevelPrivilege.getId())
                .acctId(userLevelPrivilege.getUser() != null ? userLevelPrivilege.getUser().getAcctId() : null)
                .contractId(userLevelPrivilege.getContract() != null ? userLevelPrivilege.getContract().getId() : null)
                .entityId(userLevelPrivilege.getEntity() != null ? userLevelPrivilege.getEntity().getId() : null)
                .organizationId(userLevelPrivilege.getOrganization() != null ? userLevelPrivilege.getOrganization().getId() : null)
                .roleId(userLevelPrivilege.getRole() != null ? userLevelPrivilege.getRole().getId() : null)
                .updatedAt(userLevelPrivilege.getUpdatedAt())
                .createdAt(userLevelPrivilege.getCreatedAt())
                .build();
    }

    public static List<UserLevelPrivilege> toUserLevelPrivilegeList(List<UserLevelPrivilegeDTO> userLevelPrivilegeDTOList) {
        return userLevelPrivilegeDTOList.stream().map(UserLevelPrivilegeMapper::toUserLevelPrivilege).collect(Collectors.toList());
    }

    public static List<UserLevelPrivilegeDTO> toUserLevelPrivilegeDTOList(List<UserLevelPrivilege> userLevelPrivilegeList) {
        return userLevelPrivilegeList.stream().map(UserLevelPrivilegeMapper::toUserLevelPrivilegeDTO).collect(Collectors.toList());
    }
}
