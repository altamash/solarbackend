package com.solar.api.tenant.mapper.extended;

import com.solar.api.tenant.model.extended.FunctionalRoles;

import java.util.List;
import java.util.stream.Collectors;

public class FunctionalRolesMapper {

    public static FunctionalRoles toFunctionalRoles(FunctionalRolesDTO functionalRolesDTO) {
        if (functionalRolesDTO == null) {
            return null;
        }
        return FunctionalRoles.builder()
                .id(functionalRolesDTO.getId())
                .name(functionalRolesDTO.getName())
                .category(functionalRolesDTO.getCategory())
                .subCategory(functionalRolesDTO.getSubCategory())
                .defaultHierarchyId(functionalRolesDTO.getDefaultHierarchyId())
                .defaultHierarchySeqCode(functionalRolesDTO.getDefaultHierarchySeqCode())
                .defaultPrivilegeLevel(functionalRolesDTO.getDefaultPrivilegeLevel())
                .hierarchyType(functionalRolesDTO.getHierarchyType())
                .status(functionalRolesDTO.getStatus())
                .createdAt(functionalRolesDTO.getCreatedAt())
                .updatedAt(functionalRolesDTO.getUpdatedAt())
                .build();
    }

    public static FunctionalRolesDTO toFunctionalRolesDTO(FunctionalRoles functionalRoles) {
        if (functionalRoles == null) {
            return null;
        }
        return FunctionalRolesDTO.builder()
                .id(functionalRoles.getId())
                .name(functionalRoles.getName())
                .category(functionalRoles.getCategory())
                .subCategory(functionalRoles.getSubCategory())
                .defaultHierarchyId(functionalRoles.getDefaultHierarchyId())
                .defaultHierarchySeqCode(functionalRoles.getDefaultHierarchySeqCode())
                .defaultPrivilegeLevel(functionalRoles.getDefaultPrivilegeLevel())
                .hierarchyType(functionalRoles.getHierarchyType())
                .status(functionalRoles.getStatus())
                .createdAt(functionalRoles.getCreatedAt())
                .updatedAt(functionalRoles.getUpdatedAt())
                .build();
    }

    public static FunctionalRoles toUpdatedFunctionalRoles(FunctionalRoles functionalRoles, FunctionalRoles functionalRolesUpdate) {
        functionalRoles.setId(functionalRolesUpdate.getId() == null ? functionalRoles.getId() : functionalRolesUpdate.getId());
        functionalRoles.setName(functionalRolesUpdate.getName() == null ? functionalRoles.getName() : functionalRolesUpdate.getName());
        functionalRoles.setCategory(functionalRolesUpdate.getCategory() == null ? functionalRoles.getCategory() : functionalRolesUpdate.getCategory());
        functionalRoles.setSubCategory(functionalRolesUpdate.getSubCategory() == null ? functionalRoles.getSubCategory() : functionalRolesUpdate.getSubCategory());
        functionalRoles.setDefaultHierarchyId(functionalRolesUpdate.getDefaultHierarchyId() == null ? functionalRoles.getDefaultHierarchyId() : functionalRolesUpdate.getDefaultHierarchyId());
        functionalRoles.setDefaultHierarchySeqCode(functionalRolesUpdate.getDefaultHierarchySeqCode() == null ? functionalRoles.getDefaultHierarchySeqCode() : functionalRolesUpdate.getDefaultHierarchySeqCode());
        functionalRoles.setDefaultPrivilegeLevel(functionalRolesUpdate.getDefaultPrivilegeLevel() == null ? functionalRoles.getDefaultPrivilegeLevel() : functionalRolesUpdate.getDefaultPrivilegeLevel());
        functionalRoles.setHierarchyType(functionalRolesUpdate.getHierarchyType() == null ? functionalRoles.getHierarchyType() : functionalRolesUpdate.getHierarchyType());
        functionalRoles.setStatus(functionalRolesUpdate.getStatus() == null ? functionalRoles.getStatus() : functionalRolesUpdate.getStatus());
        functionalRoles.setCreatedAt(functionalRolesUpdate.getCreatedAt() == null ? functionalRoles.getCreatedAt() : functionalRolesUpdate.getCreatedAt());
        functionalRoles.setUpdatedAt(functionalRolesUpdate.getUpdatedAt() == null ? functionalRoles.getUpdatedAt() : functionalRolesUpdate.getUpdatedAt());
        return functionalRoles;
    }

    public static List<FunctionalRoles> toFunctionalRoless(List<FunctionalRolesDTO> functionalRolesDTOS) {
        return functionalRolesDTOS.stream().map(a -> toFunctionalRoles(a)).collect(Collectors.toList());
    }

    public static List<FunctionalRolesDTO> toFunctionalRolesDTOs(List<FunctionalRoles> functionalRoles) {
        return functionalRoles.stream().map(a -> toFunctionalRolesDTO(a)).collect(Collectors.toList());
    }
}
