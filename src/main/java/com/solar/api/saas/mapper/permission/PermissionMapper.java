package com.solar.api.saas.mapper.permission;

import com.solar.api.saas.model.permission.Permission;
import com.solar.api.saas.model.permission.PermissionSet;

import java.util.List;
import java.util.stream.Collectors;

public class PermissionMapper {

    // Permission /////////////////////////////////////////////////
    public static Permission toPermission(PermissionDTO permissionDTO) {
        if (permissionDTO == null) {
            return null;
        }
        return Permission.builder()
                .id(permissionDTO.getId())
                .name(permissionDTO.getName())
                .description(permissionDTO.getDescription())
                .componentLibrary(permissionDTO.getComponentLibrary())
                .build();
    }

    public static PermissionDTO toPermissionDTO(Permission permission) {
        if (permission == null) {
            return null;
        }
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .componentLibrary(permission.getComponentLibrary())
                .build();
    }

    public static Permission toUpdatedPermission(Permission permission,
                                                       Permission permissionUpdate) {
        permission.setName(permissionUpdate.getName() == null ? permission.getName() : permissionUpdate.getName());
        permission.setDescription(permissionUpdate.getDescription() == null ? permission.getDescription() : permissionUpdate.getDescription());
        permission.setComponentLibrary(permissionUpdate.getComponentLibrary() == null ? permission.getComponentLibrary() : permissionUpdate.getComponentLibrary());
        return permission;
    }

    public static List<Permission> toPermissions(List<PermissionDTO> permissionDTOs) {
        return permissionDTOs.stream().map(p -> toPermission(p)).collect(Collectors.toList());
    }

    public static List<PermissionDTO> toPermissionDTOs(List<Permission> permissions) {
        return permissions.stream().map(p -> toPermissionDTO(p)).collect(Collectors.toList());
    }

    // PermissionSet /////////////////////////////////////////////////
    public static PermissionSet toPermissionSet(PermissionSetDTO permissionSetDTO) {
        if (permissionSetDTO == null) {
            return null;
        }
        return PermissionSet.builder()
                .id(permissionSetDTO.getId())
                .name(permissionSetDTO.getName())
                .description(permissionSetDTO.getDescription())
                .userLevelNames(permissionSetDTO.getUserLevels())
//                .permissions(permissionSetDTO.getPermission())
//                .permissionGroups(permissionSetDTO.getPermissionGroups())
                .build();
    }

    public static PermissionSetDTO toPermissionSetDTO(PermissionSet permissionSet) {
        if (permissionSet == null) {
            return null;
        }
        return PermissionSetDTO.builder()
                .id(permissionSet.getId())
                .name(permissionSet.getName())
                .description(permissionSet.getDescription())
                .userLevels(permissionSet.getUserLevelNames())
//                .permission(permissionSet.getPermission())
//                .permissionGroups(permissionSet.getPermissionGroups())
                .build();
    }

    public static PermissionSet toUpdatedPermissionSet(PermissionSet permissionSet,
                                                       PermissionSet permissionSetUpdate) {
        permissionSet.setName(permissionSetUpdate.getName() == null ? permissionSet.getName() : permissionSetUpdate.getName());
        permissionSet.setDescription(permissionSetUpdate.getDescription() == null ? permissionSet.getDescription() : permissionSetUpdate.getDescription());
        permissionSet.setUserLevels(permissionSetUpdate.getUserLevels() == null ? permissionSet.getUserLevels() : permissionSetUpdate.getUserLevels());
//        permissionSet.setPermission(permissionSetUpdate.getPermission() == null ? permissionSet.getPermission() : permissionSetUpdate.getPermission());
//        permissionSet.setPermissionGroups(permissionSetUpdate.getPermissionGroups() == null ? permissionSet.getPermissionGroups() : permissionSetUpdate.getPermissionGroups());
        return permissionSet;
    }

    public static List<PermissionSet> toComponentLibraries(List<PermissionSetDTO> permissionSetDTOs) {
        return permissionSetDTOs.stream().map(p -> toPermissionSet(p)).collect(Collectors.toList());
    }

    public static List<PermissionSetDTO> toPermissionSetDTOs(List<PermissionSet> permissionSets) {
        return permissionSets.stream().map(p -> toPermissionSetDTO(p)).collect(Collectors.toList());
    }
}
