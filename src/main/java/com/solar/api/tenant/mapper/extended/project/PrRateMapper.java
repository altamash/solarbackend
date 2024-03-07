package com.solar.api.tenant.mapper.extended.project;

import com.solar.api.tenant.model.extended.project.PrRateDefinition;
import com.solar.api.tenant.model.extended.project.PrRateGroup;

import java.util.List;
import java.util.stream.Collectors;

public class PrRateMapper {

    public static PrRateDefinition toPrRateDefinition(PrRateDefinitionDTO prRateDefinitionDTO) {
        if (prRateDefinitionDTO == null) {
            return null;
        }
        return PrRateDefinition.builder()
                .id(prRateDefinitionDTO.getId())
                .name(prRateDefinitionDTO.getName())
                .uniqueCode(prRateDefinitionDTO.getUniqueCode())
                .rateCategory(prRateDefinitionDTO.getRateCategory())
                .description(prRateDefinitionDTO.getDescription())
                .rateType(prRateDefinitionDTO.getRateType())
                .ratePeriod(prRateDefinitionDTO.getRatePeriod())
                .termLengthInDays(prRateDefinitionDTO.getTermLengthInDays())
                .rate(prRateDefinitionDTO.getRate())
                .fixedAmount(prRateDefinitionDTO.getFixedAmount())
                .overtimePeriod(prRateDefinitionDTO.getOvertimePeriod())
                .overtimeRate(prRateDefinitionDTO.getOvertimeRate())
                .overtimeFixedAmount(prRateDefinitionDTO.getOvertimeFixedAmount())
                .calculationFactor(prRateDefinitionDTO.getCalculationFactor())
                .calculationFrequency(prRateDefinitionDTO.getCalculationFrequency())
                .category(prRateDefinitionDTO.getCategory())
                .notes(prRateDefinitionDTO.getNotes())
                .build();
    }

    public static PrRateDefinitionDTO toPrRateDefinitionDTO(PrRateDefinition prRateDefinition) {
        if (prRateDefinition == null) {
            return null;
        }
        return PrRateDefinitionDTO.builder()
                .id(prRateDefinition.getId())
                .name(prRateDefinition.getName())
                .uniqueCode(prRateDefinition.getUniqueCode())
                .rateCategory(prRateDefinition.getRateCategory())
                .description(prRateDefinition.getDescription())
                .rateType(prRateDefinition.getRateType())
                .ratePeriod(prRateDefinition.getRatePeriod())
                .termLengthInDays(prRateDefinition.getTermLengthInDays())
                .rate(prRateDefinition.getRate())
                .fixedAmount(prRateDefinition.getFixedAmount())
                .overtimePeriod(prRateDefinition.getOvertimePeriod())
                .overtimeRate(prRateDefinition.getOvertimeRate())
                .overtimeFixedAmount(prRateDefinition.getOvertimeFixedAmount())
                .calculationFactor(prRateDefinition.getCalculationFactor())
                .calculationFrequency(prRateDefinition.getCalculationFrequency())
                .category(prRateDefinition.getCategory())
                .notes(prRateDefinition.getNotes())
                .build();
    }

    public static PrRateDefinition toUpdatedPrRateDefinition(PrRateDefinition prRateDefinition, PrRateDefinition prRateDefinitionHeadUpdate) {
        prRateDefinition.setName(prRateDefinitionHeadUpdate.getName() == null ? prRateDefinition.getName() : prRateDefinitionHeadUpdate.getName());
        prRateDefinition.setUniqueCode(prRateDefinitionHeadUpdate.getUniqueCode() == null ? prRateDefinition.getUniqueCode() : prRateDefinitionHeadUpdate.getUniqueCode());
        prRateDefinition.setRateCategory(prRateDefinitionHeadUpdate.getRateCategory() == null ? prRateDefinition.getCategory() : prRateDefinitionHeadUpdate.getRateCategory());
        prRateDefinition.setDescription(prRateDefinitionHeadUpdate.getDescription() == null ? prRateDefinitionHeadUpdate.getDescription() : prRateDefinition.getDescription());
        prRateDefinition.setRateType(prRateDefinitionHeadUpdate.getRateType() == null ? prRateDefinition.getRate() : prRateDefinitionHeadUpdate.getRateType());
        prRateDefinition.setRatePeriod(prRateDefinitionHeadUpdate.getRatePeriod() == null ? prRateDefinition.getRate() : prRateDefinitionHeadUpdate.getRatePeriod());
        prRateDefinition.setTermLengthInDays(prRateDefinitionHeadUpdate.getTermLengthInDays() == null ? prRateDefinitionHeadUpdate.getTermLengthInDays() : prRateDefinition.getTermLengthInDays());
        prRateDefinition.setRate(prRateDefinitionHeadUpdate.getRate() == null ? prRateDefinition.getRate() : prRateDefinitionHeadUpdate.getRate());
        prRateDefinition.setFixedAmount(prRateDefinitionHeadUpdate.getFixedAmount() == null ? prRateDefinition.getFixedAmount() : prRateDefinitionHeadUpdate.getFixedAmount());
        prRateDefinition.setOvertimePeriod(prRateDefinitionHeadUpdate.getOvertimePeriod() == null ? prRateDefinitionHeadUpdate.getOvertimePeriod() : prRateDefinition.getOvertimePeriod());
        prRateDefinition.setOvertimeRate(prRateDefinitionHeadUpdate.getOvertimeRate() == null ? prRateDefinition.getOvertimeRate() : prRateDefinitionHeadUpdate.getOvertimeRate());
        prRateDefinition.setOvertimeFixedAmount(prRateDefinitionHeadUpdate.getOvertimeFixedAmount() == null ? prRateDefinition.getFixedAmount() : prRateDefinitionHeadUpdate.getOvertimeFixedAmount());
        prRateDefinition.setCalculationFactor(prRateDefinitionHeadUpdate.getCalculationFactor() == null ? prRateDefinitionHeadUpdate.getCalculationFactor() : prRateDefinition.getCalculationFactor());
        prRateDefinition.setCalculationFrequency(prRateDefinitionHeadUpdate.getCalculationFrequency() == null ? prRateDefinition.getCalculationFrequency() : prRateDefinitionHeadUpdate.getCalculationFrequency());
        prRateDefinition.setCategory(prRateDefinitionHeadUpdate.getCategory() == null ? prRateDefinition.getCategory() : prRateDefinitionHeadUpdate.getCategory());
        prRateDefinition.setName(prRateDefinitionHeadUpdate.getName() == null ? prRateDefinitionHeadUpdate.getName() : prRateDefinition.getName());
        return prRateDefinition;
    }

    public static List<PrRateDefinition> toPrRateDefinitions(List<PrRateDefinitionDTO> prRateDefinitionDTOS) {
        return prRateDefinitionDTOS.stream().map(a -> toPrRateDefinition(a)).collect(Collectors.toList());
    }

    public static List<PrRateDefinitionDTO> toPrRateDefinitionDTOs(List<PrRateDefinition> prRateDefinitions) {
        return prRateDefinitions.stream().map(a -> toPrRateDefinitionDTO(a)).collect(Collectors.toList());
    }
    
    //PR RATE GROUP
    public static PrRateGroup toPrRateGroup(PrRateGroupDTO prRateGroupDTO) {
        if (prRateGroupDTO == null) {
            return null;
        }
        return PrRateGroup.builder()
                .id(prRateGroupDTO.getId())
                .groupName(prRateGroupDTO.getGroupName())
                .description(prRateGroupDTO.getDescription())
                .prRateId(prRateGroupDTO.getPrRateId())
                .sequenceNumber(prRateGroupDTO.getSequenceNumber())
                .overtimeApplicableInd(prRateGroupDTO.getOvertimeApplicableInd())
                .category(prRateGroupDTO.getCategory())
                .referenceFunction(prRateGroupDTO.getReferenceFunction())
                .notes(prRateGroupDTO.getNotes())
                .build();
    }

    public static PrRateGroupDTO toPrRateGroupDTO(PrRateGroup prRateGroup) {
        if (prRateGroup == null) {
            return null;
        }
        return PrRateGroupDTO.builder()
                .id(prRateGroup.getId())
                .groupName(prRateGroup.getGroupName())
                .description(prRateGroup.getDescription())
                .prRateId(prRateGroup.getPrRateId())
                .sequenceNumber(prRateGroup.getSequenceNumber())
                .overtimeApplicableInd(prRateGroup.getOvertimeApplicableInd())
                .category(prRateGroup.getCategory())
                .referenceFunction(prRateGroup.getReferenceFunction())
                .notes(prRateGroup.getNotes())
                .build();
    }

    public static PrRateGroup toUpdatedPrRateGroup(PrRateGroup prRateGroup, PrRateGroup prRateGroupHeadUpdate) {
        prRateGroup.setGroupName(prRateGroupHeadUpdate.getGroupName() == null ? prRateGroup.getGroupName() : prRateGroupHeadUpdate.getGroupName());
        prRateGroup.setDescription(prRateGroupHeadUpdate.getDescription() == null ? prRateGroup.getDescription() : prRateGroupHeadUpdate.getDescription());
        prRateGroup.setPrRateId(prRateGroupHeadUpdate.getPrRateId() == null ? prRateGroup.getPrRateId() : prRateGroupHeadUpdate.getPrRateId());
        prRateGroup.setSequenceNumber(prRateGroupHeadUpdate.getSequenceNumber() == null ? prRateGroupHeadUpdate.getSequenceNumber() : prRateGroup.getSequenceNumber());
        prRateGroup.setOvertimeApplicableInd(prRateGroupHeadUpdate.getOvertimeApplicableInd() == null ? prRateGroup.getOvertimeApplicableInd() : prRateGroupHeadUpdate.getOvertimeApplicableInd());
        prRateGroup.setCategory(prRateGroupHeadUpdate.getCategory() == null ? prRateGroup.getCategory() : prRateGroupHeadUpdate.getCategory());
        prRateGroup.setReferenceFunction(prRateGroupHeadUpdate.getReferenceFunction() == null ? prRateGroup.getReferenceFunction() : prRateGroupHeadUpdate.getReferenceFunction());
        prRateGroup.setNotes(prRateGroupHeadUpdate.getNotes() == null ? prRateGroupHeadUpdate.getNotes() : prRateGroup.getNotes());
        return prRateGroup;
    }

    public static List<PrRateGroup> toPrRateGroups(List<PrRateGroupDTO> prRateGroupDTOS) {
        return prRateGroupDTOS.stream().map(a -> toPrRateGroup(a)).collect(Collectors.toList());
    }

    public static List<PrRateGroupDTO> toPrRateGroupDTOs(List<PrRateGroup> prRateGroups) {
        return prRateGroups.stream().map(a -> toPrRateGroupDTO(a)).collect(Collectors.toList());
    }
}
