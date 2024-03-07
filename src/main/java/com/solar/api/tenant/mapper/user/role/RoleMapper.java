package com.solar.api.tenant.mapper.user.role;

import com.solar.api.tenant.mapper.permission.PermissionGroupMapper;
import com.solar.api.tenant.model.user.role.Role;

import java.util.Set;
import java.util.stream.Collectors;

public class RoleMapper {

    public static Role toRole(RoleDTO roleDTO) {
        return Role.builder()
                .id(roleDTO.getId())
                .name(roleDTO.getName())
                .description(roleDTO.getDescription())
                .userLevelName(roleDTO.getUserLevel())
                .permissionGroups(roleDTO.getPermissionGroups() != null ? PermissionGroupMapper.toPermissionGroups(roleDTO.getPermissionGroups()) : null)
//                .permissionSets(roleDTO.getPermissionSets())
                .build();
    }

    public static RoleDTO toRoleDTO(Role role) {
        if (role == null) {
            return null;
        }
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
//                .permissionSets(role.getPermissionSets())
                .userLevel(role.getUserLevelName())
                .permissionGroups(role.getPermissionGroups() != null ? PermissionGroupMapper.toPermissionGroupDTOs(role.getPermissionGroups()) : null)
                .remainingPermissionGroups(role.getRemainingPermissionGroups() != null ? PermissionGroupMapper.toPermissionGroupDTOs(role.getRemainingPermissionGroups()) : null)

                .build();
    }

    public static Role toUpdatedRole(Role role, Role roleUpdate) {
        role.setName(roleUpdate.getName() == null ? role.getName() : roleUpdate.getName());
        role.setDescription(roleUpdate.getDescription() == null ? role.getDescription() : roleUpdate.getDescription());
//        role.setPermissionSets(roleUpdate.getPermissionSets() == null ? role.getPermissionSets() : roleUpdate.getPermissionSets());
        return role;
    }

    public static Set<Role> toRoles(Set<RoleDTO> roleDTOS) {
        return roleDTOS.stream().map(r -> toRole(r)).collect(Collectors.toSet());
    }

    public static Set<RoleDTO> toRoleDTOs(Set<Role> roles) {
        return roles.stream().map(r -> toRoleDTO(r)).collect(Collectors.toSet());
    }
    public static RoleDetailDTO toRoleDetailDTO(Role role) {
        if (role == null) {
            return null;
        }
        return RoleDetailDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .userLevel(role.getUserLevelName())
                .permissionGroups(role.getPermissionGroups() != null ? PermissionGroupMapper.toPermissionGroupDTOs(role.getPermissionGroups()) : null)
                .remainingPermissionGroups(role.getRemainingPermissionGroups() != null ? PermissionGroupMapper.toPermissionGroupDTOs(role.getRemainingPermissionGroups()) : null)
                .permissionGroupsCount(role.getPermissionGroups().stream().count())
                .build();
    }

    public static Set<RoleDetailDTO> toRoleDetailDTOs(Set<Role> roles){
        return roles.stream().map(r-> toRoleDetailDTO(r)).collect(Collectors.toSet());
    }
}
