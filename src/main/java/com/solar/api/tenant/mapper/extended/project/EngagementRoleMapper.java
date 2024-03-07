package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.model.extended.project.EngagementRole;
import com.solar.api.tenant.model.extended.project.EngagementRoleMap;

import java.util.List;
import java.util.stream.Collectors;

public class EngagementRoleMapper {

    public static EngagementRole toEngagementRole(EngagementRoleDTO engagementRoleDTO) {
        if (engagementRoleDTO == null) {
            return null;
        }
        return EngagementRole.builder()
                .id(engagementRoleDTO.getId())
                .externalRoleId(engagementRoleDTO.getExternalRoleId())
                .roleName(engagementRoleDTO.getRoleName())
                .description(engagementRoleDTO.getDescription())
                .appliesTo(engagementRoleDTO.getAppliesTo())
                .glReferenceCode(engagementRoleDTO.getGlReferenceCode())
                .build();
    }

    public static EngagementRoleDTO toEngagementRoleDTO(EngagementRole engagementRole) {
        if (engagementRole == null) {
            return null;
        }
        return EngagementRoleDTO.builder()
                .id(engagementRole.getId())
                .externalRoleId(engagementRole.getExternalRoleId())
                .roleName(engagementRole.getRoleName())
                .description(engagementRole.getDescription())
                .appliesTo(engagementRole.getAppliesTo())
                .glReferenceCode(engagementRole.getGlReferenceCode())
                .build();
    }

    public static EngagementRole toUpdatedEngagementRole(EngagementRole engagementRole, EngagementRole engagementRoleHeadUpdate) {
        engagementRole.setExternalRoleId(engagementRoleHeadUpdate.getExternalRoleId() == null ?
                engagementRole.getExternalRoleId() : engagementRoleHeadUpdate.getExternalRoleId());
        engagementRole.setRoleName(engagementRoleHeadUpdate.getRoleName() == null ?
                engagementRole.getRoleName() : engagementRoleHeadUpdate.getRoleName());
        engagementRole.setDescription(engagementRoleHeadUpdate.getDescription() == null ?
                engagementRole.getDescription() : engagementRoleHeadUpdate.getDescription());
        engagementRole.setAppliesTo(engagementRoleHeadUpdate.getAppliesTo() == null ?
                engagementRole.getAppliesTo() : engagementRoleHeadUpdate.getAppliesTo());
        engagementRole.setGlReferenceCode(engagementRoleHeadUpdate.getGlReferenceCode() == null ? engagementRoleHeadUpdate.getGlReferenceCode() :
                engagementRole.getGlReferenceCode());
        return engagementRole;
    }

    public static List<EngagementRole> toEngagementRoles(List<EngagementRoleDTO> engagementRoleDTOS) {
        return engagementRoleDTOS.stream().map(a -> toEngagementRole(a)).collect(Collectors.toList());
    }

    public static List<EngagementRoleDTO> toEngagementRoleDTOs(List<EngagementRole> engagementRoles) {
        return engagementRoles.stream().map(a -> toEngagementRoleDTO(a)).collect(Collectors.toList());
    }

    //Engagement Role Map
    public static EngagementRoleMap toEngagementRoleMap(EngagementRoleMapDTO engagementRoleMapDTO) {
        if (engagementRoleMapDTO == null) {
            return null;
        }
        return EngagementRoleMap.builder()
                .id(engagementRoleMapDTO.getId())
                .engagementRoleId(engagementRoleMapDTO.getEngagementRoleId())
                .prRateGroupId(engagementRoleMapDTO.getPrRateGroupId())
                .prRateId(engagementRoleMapDTO.getPrRateId())
                .overrideRate(engagementRoleMapDTO.getOverrideRate())
                .sequence(engagementRoleMapDTO.getSequence())
                .overrideOtRate(engagementRoleMapDTO.getOverrideOtRate())
                .status(engagementRoleMapDTO.getStatus())
                .build();
    }

    public static EngagementRoleMapDTO toEngagementRoleMapDTO(EngagementRoleMap engagementRoleMap) {
        if (engagementRoleMap == null) {
            return null;
        }
        return EngagementRoleMapDTO.builder()
                .id(engagementRoleMap.getId())
                .engagementRoleId(engagementRoleMap.getEngagementRoleId())
                .prRateGroupId(engagementRoleMap.getPrRateGroupId())
                .prRateId(engagementRoleMap.getPrRateId())
                .overrideRate(engagementRoleMap.getOverrideRate())
                .sequence(engagementRoleMap.getSequence())
                .overrideOtRate(engagementRoleMap.getOverrideOtRate())
                .status(engagementRoleMap.getStatus())
                .build();
    }

    public static EngagementRoleMap toUpdatedEngagementRoleMap(EngagementRoleMap engagementRoleMap, EngagementRoleMap engagementRoleMapUpdate) {
        engagementRoleMap.setEngagementRoleId(engagementRoleMapUpdate.getEngagementRoleId() == null ?
                engagementRoleMap.getEngagementRoleId() : engagementRoleMapUpdate.getEngagementRoleId());
        engagementRoleMap.setPrRateGroupId(engagementRoleMapUpdate.getPrRateGroupId() == null ?
                engagementRoleMap.getPrRateGroupId() : engagementRoleMapUpdate.getPrRateGroupId());
        engagementRoleMap.setPrRateId(engagementRoleMapUpdate.getPrRateId() == null ?
                engagementRoleMap.getPrRateId() : engagementRoleMapUpdate.getPrRateId());
        engagementRoleMap.setOverrideRate(engagementRoleMapUpdate.getOverrideRate() == null ? engagementRoleMapUpdate.getOverrideRate() :
                engagementRoleMap.getOverrideRate());
        engagementRoleMap.setOverrideOtRate(engagementRoleMapUpdate.getOverrideOtRate() == null ? engagementRoleMapUpdate.getOverrideOtRate() :
                engagementRoleMap.getOverrideOtRate());
        engagementRoleMap.setSequence(engagementRoleMapUpdate.getSequence() == null ? engagementRoleMapUpdate.getSequence() :
                engagementRoleMap.getSequence());
        engagementRoleMap.setStatus(engagementRoleMapUpdate.getStatus() == null ? engagementRoleMapUpdate.getStatus() :
                engagementRoleMap.getStatus());
        return engagementRoleMap;
    }

    public static List<EngagementRoleMap> toEngagementRoleMaps(List<EngagementRoleMapDTO> engagementRoleMapDTOList) {
        return engagementRoleMapDTOList.stream().map(a -> toEngagementRoleMap(a)).collect(Collectors.toList());
    }

    public static List<EngagementRoleMapDTO> toEngagementRoleMapDTOs(List<EngagementRoleMap> engagementRoleMaps) {
        return engagementRoleMaps.stream().map(a -> toEngagementRoleMapDTO(a)).collect(Collectors.toList());
    }
}
