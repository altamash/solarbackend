package com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix;

import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.CalculationGroup;

import java.util.List;
import java.util.stream.Collectors;

public class CalculationGroupMapper {

    public static CalculationGroup toCalculationGroup(CalculationGroupDTO calculationGroupDTO) {
        if (calculationGroupDTO == null) {
            return null;
        }
        return CalculationGroup.builder()
                .id(calculationGroupDTO.getId())
                .name(calculationGroupDTO.getName())
                .description(calculationGroupDTO.getDescription())
                .alias(calculationGroupDTO.getAlias())
                .build();
    }


    public static CalculationGroupDTO toCalculationGroupDTO(CalculationGroup calculationGroup) {
        if (calculationGroup == null) {
            return null;
        }
        return CalculationGroupDTO.builder()
                .id(calculationGroup.getId())
                .name(calculationGroup.getName())
                .description(calculationGroup.getDescription())
                .alias(calculationGroup.getAlias())
                .createdAt(calculationGroup.getCreatedAt())
                .updatedAt(calculationGroup.getUpdatedAt())
                .build();
    }

    public static CalculationGroup toUpdatedCalculationGroup(CalculationGroup calculationGroup, CalculationGroup calculationGroupUpdate) {
        calculationGroup.setId(calculationGroupUpdate.getId() == null ?
                calculationGroup.getId() :
                calculationGroupUpdate.getId());
        calculationGroup.setName(calculationGroupUpdate.getName() == null ?
                calculationGroup.getName() :
                calculationGroupUpdate.getName());
        calculationGroup.setAlias(calculationGroupUpdate.getAlias() == null ?
                calculationGroup.getAlias() : calculationGroupUpdate.getAlias());
        calculationGroup.setDescription(calculationGroupUpdate.getDescription() == null ?
                calculationGroup.getDescription() : calculationGroupUpdate.getDescription());
        return calculationGroup;
    }

    public static List<CalculationGroup> toCalculationGroups(List<CalculationGroupDTO> calculationGroupDTOList) {
        if (calculationGroupDTOList == null) {
            return null;
        }
        return calculationGroupDTOList.stream().map(cr -> toCalculationGroup(cr)).collect(Collectors.toList());
    }

    public static List<CalculationGroupDTO> toCalculationGroupDTOs(List<CalculationGroup> calculationGroupList) {
        if (calculationGroupList == null) {
            return null;
        }
        return calculationGroupList.stream().map(cr -> toCalculationGroupDTO(cr)).collect(Collectors.toList());
    }
}
