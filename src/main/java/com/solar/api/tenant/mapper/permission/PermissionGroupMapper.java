package com.solar.api.tenant.mapper.permission;

import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PermissionGroupMapper {

    // AvailablePermissionSet /////////////////////////////////
    public static AvailablePermissionSet toAvailablePermissionSet(AvailablePermissionSetDTO availablePermissionSetDTO) {
        if (availablePermissionSetDTO == null) {
            return null;
        }
        return AvailablePermissionSet.builder()
                .id(availablePermissionSetDTO.getId())
                .permissionSetId(availablePermissionSetDTO.getPermissionSetId())
                .name(availablePermissionSetDTO.getName())
                .description(availablePermissionSetDTO.getDescription())
                .enabled(availablePermissionSetDTO.isEnabled())
                .build();
    }

    public static AvailablePermissionSetDTO toAvailablePermissionSetDTO(AvailablePermissionSet availablePermissionSet) {
        if (availablePermissionSet == null) {
            return null;
        }
        return AvailablePermissionSetDTO.builder()
                .id(availablePermissionSet.getId())
                .permissionSetId(availablePermissionSet.getPermissionSetId())
                .name(availablePermissionSet.getName())
                .description(availablePermissionSet.getDescription())
                .enabled(availablePermissionSet.isEnabled())
                .build();
    }

    public static AvailablePermissionSet toUpdatedAvailablePermissionSet(AvailablePermissionSet availablePermissionSet, AvailablePermissionSet availablePermissionSetUpdate) {
        availablePermissionSet.setPermissionSetId(availablePermissionSetUpdate.getPermissionSetId() == null ? availablePermissionSet.getPermissionSetId() : availablePermissionSetUpdate.getPermissionSetId());
        availablePermissionSet.setName(availablePermissionSetUpdate.getName() == null ? availablePermissionSet.getName() : availablePermissionSetUpdate.getName());
        availablePermissionSet.setDescription(availablePermissionSetUpdate.getDescription() == null ? availablePermissionSet.getDescription() : availablePermissionSetUpdate.getDescription());
        availablePermissionSet.setEnabled(availablePermissionSetUpdate.isEnabled());
        return availablePermissionSet;
    }

    public static Set<AvailablePermissionSet> toAvailablePermissionSets(Set<AvailablePermissionSetDTO> availablePermissionSetDTOs) {
        return availablePermissionSetDTOs.stream().map(p -> toAvailablePermissionSet(p)).collect(Collectors.toSet());
    }

    public static Set<AvailablePermissionSetDTO> toAvailablePermissionSetDTOs(Set<AvailablePermissionSet> availablePermissionSets) {
        return availablePermissionSets.stream().map(p -> toAvailablePermissionSetDTO(p)).collect(Collectors.toSet());
    }

    // PermissionGroup /////////////////////////////////
    public static PermissionGroup toPermissionGroup(PermissionGroupDTO permissionGroupDTO) {
        if (permissionGroupDTO == null) {
            return null;
        }
        return PermissionGroup.builder()
                .id(permissionGroupDTO.getId())
                .name(permissionGroupDTO.getName())
                .description(permissionGroupDTO.getDescription())
                .userLevelName(permissionGroupDTO.getUserLevel())
                .permissionSets(CollectionUtils.isNotEmpty(permissionGroupDTO.getPermissionSets()) ? toAvailablePermissionSets(permissionGroupDTO.getPermissionSets()) : Collections.EMPTY_SET)
                .build();
    }

    public static PermissionGroupDTO toPermissionGroupDTO(PermissionGroup permissionGroup) {
        if (permissionGroup == null) {
            return null;
        }
        return PermissionGroupDTO.builder()
                .id(permissionGroup.getId())
                .name(permissionGroup.getName())
                .description(permissionGroup.getDescription())
                .userLevel(permissionGroup.getUserLevelName())
                .permissionSets(CollectionUtils.isNotEmpty(permissionGroup.getPermissionSets()) ? toAvailablePermissionSetDTOs(permissionGroup.getPermissionSets()) : Collections.EMPTY_SET)
                .remainingPermissionSets(CollectionUtils.isNotEmpty(permissionGroup.getRemainingPermissionSets()) ? toAvailablePermissionSetDTOs(permissionGroup.getRemainingPermissionSets()) : Collections.EMPTY_SET)
                .build();
    }

    public static PermissionGroup toUpdatedPermissionGroup(PermissionGroup permissionGroup, PermissionGroup permissionGroupUpdate) {
        permissionGroup.setName(permissionGroupUpdate.getName() == null ? permissionGroup.getName() : permissionGroupUpdate.getName());
        permissionGroup.setDescription(permissionGroupUpdate.getDescription() == null ? permissionGroup.getDescription() : permissionGroupUpdate.getDescription());
        permissionGroup.setPermissionSets(permissionGroupUpdate.getPermissionSets() == null ? permissionGroup.getPermissionSets() : permissionGroupUpdate.getPermissionSets());
        permissionGroup.setUserLevel(permissionGroupUpdate.getUserLevel() == null ? permissionGroup.getUserLevel() : permissionGroupUpdate.getUserLevel());
        return permissionGroup;
    }

    public static Set<PermissionGroup> toPermissionGroups(Set<PermissionGroupDTO> permissionGroupDTOs) {
        return permissionGroupDTOs.stream().map(p -> toPermissionGroup(p)).collect(Collectors.toSet());
    }

    public static Set<PermissionGroupDTO> toPermissionGroupDTOs(Set<PermissionGroup> permissionGroups) {
        return permissionGroups.stream().map(p -> toPermissionGroupDTO(p)).collect(Collectors.toSet());
    }

    public static List<PermissionGroupDTO> toPermissionGroupDTOs(List<PermissionGroup> permissionGroups) {
        return permissionGroups.stream().map(p -> toPermissionGroupDTO(p)).collect(Collectors.toList());
    }
}
