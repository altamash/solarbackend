package com.solar.api.tenant.mapper.extended.pallet;

import com.solar.api.tenant.model.extended.pallet.PalletDefinition;

import java.util.List;
import java.util.stream.Collectors;

public class PalletDefinitionMapper {

    public static PalletDefinition toPalletDefinition(PalletDefinitionDTO palletDefinitionDTO) {
        if (palletDefinitionDTO == null) {
            return null;
        }
        return PalletDefinition.builder()
                .id(palletDefinitionDTO.getId())
                .palletRefId(palletDefinitionDTO.getPalletRefId())
                .palletTypeId(palletDefinitionDTO.getPalletTypeId())
                .source(palletDefinitionDTO.getSource())
                .sourceRefId(palletDefinitionDTO.getSourceRefId())
                .returnToSourceInd(palletDefinitionDTO.getReturnToSourceInd())
                .status(palletDefinitionDTO.getStatus())
                .returnDatetime(palletDefinitionDTO.getReturnDatetime())
                .inspectedBy(palletDefinitionDTO.getInspectedBy())
                .inspectionDatetime(palletDefinitionDTO.getInspectionDatetime())
                .lockedInd(palletDefinitionDTO.getLockedInd())
                .build();
    }

    public static PalletDefinitionDTO toPalletDefinitionDTO(PalletDefinition palletDefinition) {
        if (palletDefinition == null) {
            return null;
        }
        return PalletDefinitionDTO.builder()
                .id(palletDefinition.getId())
                .palletRefId(palletDefinition.getPalletRefId())
                .palletTypeId(palletDefinition.getPalletTypeId())
                .source(palletDefinition.getSource())
                .sourceRefId(palletDefinition.getSourceRefId())
                .returnToSourceInd(palletDefinition.getReturnToSourceInd())
                .status(palletDefinition.getStatus())
                .returnDatetime(palletDefinition.getReturnDatetime())
                .inspectedBy(palletDefinition.getInspectedBy())
                .inspectionDatetime(palletDefinition.getInspectionDatetime())
                .lockedInd(palletDefinition.getLockedInd())
                .build();
    }

    public static PalletDefinition toUpdatedPalletDefinition(PalletDefinition palletDefinition, PalletDefinition palletDefinitionUpdate) {
        palletDefinition.setPalletRefId(palletDefinitionUpdate.getPalletRefId() == null ? palletDefinition.getPalletRefId() : palletDefinitionUpdate.getPalletRefId());
        palletDefinition.setPalletTypeId(palletDefinitionUpdate.getPalletTypeId() == null ? palletDefinition.getPalletTypeId() : palletDefinitionUpdate.getPalletTypeId());
        palletDefinition.setSource(palletDefinitionUpdate.getSource() == null ? palletDefinition.getSource() : palletDefinitionUpdate.getSource());
        palletDefinition.setSourceRefId(palletDefinitionUpdate.getSourceRefId() == null ? palletDefinition.getSourceRefId() : palletDefinitionUpdate.getSourceRefId());
        palletDefinition.setReturnToSourceInd(palletDefinitionUpdate.getReturnToSourceInd() == null ? palletDefinition.getReturnToSourceInd() : palletDefinitionUpdate.getReturnToSourceInd());
        palletDefinition.setInspectedBy(palletDefinitionUpdate.getInspectedBy() == null ? palletDefinition.getInspectedBy() : palletDefinitionUpdate.getInspectedBy());
        palletDefinition.setInspectionDatetime(palletDefinitionUpdate.getInspectionDatetime() == null ? palletDefinition.getInspectionDatetime() : palletDefinitionUpdate.getInspectionDatetime());
        palletDefinition.setLockedInd(palletDefinitionUpdate.getLockedInd() == null ? palletDefinition.getLockedInd() : palletDefinitionUpdate.getLockedInd());
        return palletDefinition;
    }

    public static List<PalletDefinition> toPalletDefinitions(List<PalletDefinitionDTO> palletDefinitionDTOS) {
        return palletDefinitionDTOS.stream().map(a -> toPalletDefinition(a)).collect(Collectors.toList());
    }

    public static List<PalletDefinitionDTO> toPalletDefinitionDTOs(List<PalletDefinition> palletDefinitions) {
        return palletDefinitions.stream().map(a -> toPalletDefinitionDTO(a)).collect(Collectors.toList());
    }
}
