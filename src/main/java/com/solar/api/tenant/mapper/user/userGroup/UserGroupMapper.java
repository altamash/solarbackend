package com.solar.api.tenant.mapper.user.userGroup;

import com.solar.api.tenant.model.userGroup.UserGroup;

import java.util.List;
import java.util.stream.Collectors;

public class UserGroupMapper {

    public static UserGroup toUserGroup(UserGroupDTO userGroupDTO) {
        if (userGroupDTO == null) {
            return null;
        }
        return UserGroup.builder()
                .id(userGroupDTO.getId())
                .userGroupName(userGroupDTO.getUserGroupName())
                .userGroupType(userGroupDTO.getUserGroupType())
                .status(userGroupDTO.isStatus())
                .isActive(userGroupDTO.isActive())
                .refType(userGroupDTO.getRefType())
                .refId(userGroupDTO.getRefId())
                .parentId(userGroupDTO.getParentId())
                .createdBy(userGroupDTO.getCreatedBy())
                .updatedBy(userGroupDTO.getUpdatedBy())
                .build();
    }

    public static UserGroupDTO toUserGroupDTO(UserGroup userGroup) {
        if (userGroup == null) {
            return null;
        }

        return UserGroupDTO.builder()
                .id(userGroup.getId())
                .userGroupName(userGroup.getUserGroupName())
                .userGroupType(userGroup.getUserGroupType())
                .status(userGroup.isStatus())
                .isActive(userGroup.isActive())
                .refType(userGroup.getRefType())
                .refId(userGroup.getRefId())
                .parentId(userGroup.getParentId())
                .createdBy(userGroup.getCreatedBy())
                .updatedBy(userGroup.getUpdatedBy())
                .isDeleted(userGroup.getIsDeleted())
                .build();
    }

    public static UserGroup toUpdateUserGroup(UserGroup userGroup, UserGroup userGroupUpdate) {
        userGroup.setUserGroupName(userGroupUpdate.getUserGroupName() == null ? userGroup.getUserGroupName() : userGroupUpdate.getUserGroupName());
        userGroup.setUserGroupType(userGroupUpdate.getUserGroupType() == null ? userGroup.getUserGroupType() : userGroupUpdate.getUserGroupType());
        userGroup.setStatus(userGroupUpdate.isStatus() == userGroup.isStatus() ? userGroup.isStatus() : userGroupUpdate.isStatus());
        userGroup.setActive(userGroupUpdate.isActive() == userGroup.isActive() ? userGroup.isActive() : userGroupUpdate.isActive());
        userGroup.setCreatedBy(userGroupUpdate.getCreatedBy() == null ? userGroup.getCreatedBy() : userGroupUpdate.getCreatedBy());
        userGroup.setUpdatedBy(userGroupUpdate.getUpdatedBy() == null ? userGroup.getUpdatedBy() : userGroupUpdate.getUpdatedBy());

        return userGroup;
    }

    public static List<UserGroup> toUserGroups(List<UserGroupDTO> userGroupDTOs) {
        return userGroupDTOs.stream().map(UserGroupMapper::toUserGroup).collect(Collectors.toList());
    }

    public static List<UserGroupDTO> toUserGroupDTOs(List<UserGroup> userGroups) {
        return userGroups.stream().map(UserGroupMapper::toUserGroupDTO).collect(Collectors.toList());
    }
}
