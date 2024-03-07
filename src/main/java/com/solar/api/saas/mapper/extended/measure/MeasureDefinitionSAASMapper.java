package com.solar.api.saas.mapper.extended.measure;

import com.solar.api.saas.model.extended.MeasureDefinitionSAAS;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;

import java.util.List;
import java.util.stream.Collectors;

public class MeasureDefinitionSAASMapper {
    public static MeasureDefinitionSAAS toMeasureDefinition(MeasureDefinitionSAASDTO measureDefinitionSAASDTO) {
        if (measureDefinitionSAASDTO == null) {
            return null;
        }
        return MeasureDefinitionSAAS.builder()
                .id(measureDefinitionSAASDTO.getId())
                .measure(measureDefinitionSAASDTO.getMeasure())
                .code(measureDefinitionSAASDTO.getCode())
                .format(measureDefinitionSAASDTO.getFormat())
                .uom(measureDefinitionSAASDTO.getUom())
                .pct(measureDefinitionSAASDTO.getPct())
                .attributeIdRef(measureDefinitionSAASDTO.getAttributeIdRef())
                .attributeIdRefId(measureDefinitionSAASDTO.getAttributeIdRefId())
                .locked(measureDefinitionSAASDTO.getLocked())
                .mandatory(measureDefinitionSAASDTO.getMandatory())
                .relatedMeasure(measureDefinitionSAASDTO.getRelatedMeasure())
                .alias(measureDefinitionSAASDTO.getAlias())
                .type(measureDefinitionSAASDTO.getType())
                .regModule(measureDefinitionSAASDTO.getRegModule())
                .regModuleId(measureDefinitionSAASDTO.getRegModuleId())
                .validationRule(measureDefinitionSAASDTO.getValidationRule())
                .validationParams(measureDefinitionSAASDTO.getValidationParams())
                .actions(measureDefinitionSAASDTO.getActions())
                .visibilityLevel(measureDefinitionSAASDTO.getVisibilityLevel())
                .compEvents(measureDefinitionSAASDTO.getCompEvents())
                .systemUsed(measureDefinitionSAASDTO.getSystemUsed())
                .notes(measureDefinitionSAASDTO.getNotes())
                .visible(measureDefinitionSAASDTO.getVisible())
                .build();
    }

    public static MeasureDefinitionSAAS toMeasureDefinition(MeasureDefinitionTenantDTO measureDefinitionTenantDTO) {
        if (measureDefinitionTenantDTO == null) {
            return null;
        }
        return MeasureDefinitionSAAS.builder()
                .id(measureDefinitionTenantDTO.getId())
                .measure(measureDefinitionTenantDTO.getMeasure())
                .code(measureDefinitionTenantDTO.getCode())
                .format(measureDefinitionTenantDTO.getFormat())
                .uom(measureDefinitionTenantDTO.getUom())
                .pct(measureDefinitionTenantDTO.getPct())
                .attributeIdRef(measureDefinitionTenantDTO.getAttributeIdRef())
                .attributeIdRefId(measureDefinitionTenantDTO.getAttributeIdRefId())
                .locked(measureDefinitionTenantDTO.getLocked())
                .mandatory(measureDefinitionTenantDTO.getMandatory())
                .relatedMeasure(measureDefinitionTenantDTO.getRelatedMeasure())
                .alias(measureDefinitionTenantDTO.getAlias())
                .type(measureDefinitionTenantDTO.getType())
                .regModule(measureDefinitionTenantDTO.getRegModule())
                .regModuleId(measureDefinitionTenantDTO.getRegModuleId())
                .validationRule(measureDefinitionTenantDTO.getValidationRule())
                .validationParams(measureDefinitionTenantDTO.getValidationParams())
                .actions(measureDefinitionTenantDTO.getActions())
                .visibilityLevel(measureDefinitionTenantDTO.getVisibilityLevel())
                .compEvents(measureDefinitionTenantDTO.getCompEvents())
                .systemUsed(measureDefinitionTenantDTO.getSystemUsed())
                .notes(measureDefinitionTenantDTO.getNotes())
                .visible(measureDefinitionTenantDTO.getVisible())
                .build();
    }

    public static MeasureDefinitionSAAS toMeasureDefinitionSAAS(MeasureDefinitionTenant measureDefinitionTenant) {
        if (measureDefinitionTenant == null) {
            return null;
        }
        return MeasureDefinitionSAAS.builder()
                .id(measureDefinitionTenant.getId())
                .measure(measureDefinitionTenant.getMeasure())
                .code(measureDefinitionTenant.getCode())
                .format(measureDefinitionTenant.getFormat())
                .uom(measureDefinitionTenant.getUom())
                .pct(measureDefinitionTenant.getPct())
                .attributeIdRef(measureDefinitionTenant.getAttributeIdRef())
                .attributeIdRefId(measureDefinitionTenant.getAttributeIdRefId())
                .locked(measureDefinitionTenant.getLocked())
                .mandatory(measureDefinitionTenant.getMandatory())
                .relatedMeasure(measureDefinitionTenant.getRelatedMeasure())
                .alias(measureDefinitionTenant.getAlias())
                .type(measureDefinitionTenant.getType())
                .regModule(measureDefinitionTenant.getRegModule())
                .regModuleId(measureDefinitionTenant.getRegModuleId())
                .validationRule(measureDefinitionTenant.getValidationRule())
                .validationParams(measureDefinitionTenant.getValidationParams())
                .actions(measureDefinitionTenant.getActions())
                .visibilityLevel(measureDefinitionTenant.getVisibilityLevel())
                .compEvents(measureDefinitionTenant.getCompEvents())
                .systemUsed(measureDefinitionTenant.getSystemUsed())
                .notes(measureDefinitionTenant.getNotes())
                .visible(measureDefinitionTenant.getVisible())
                .build();
    }

    public static MeasureDefinitionSAASDTO toMeasureDefinitionDTO(MeasureDefinitionSAAS measureDefinitionSAAS) {
        if (measureDefinitionSAAS == null) {
            return null;
        }
        return MeasureDefinitionSAASDTO.builder()
                .id(measureDefinitionSAAS.getId())
                .measure(measureDefinitionSAAS.getMeasure())
                .code(measureDefinitionSAAS.getCode())
                .format(measureDefinitionSAAS.getFormat())
                .uom(measureDefinitionSAAS.getUom())
                .pct(measureDefinitionSAAS.getPct())
                .attributeIdRef(measureDefinitionSAAS.getAttributeIdRef())
                .attributeIdRefId(measureDefinitionSAAS.getAttributeIdRefId())
                .portalAttributeValues(measureDefinitionSAAS.getPortalAttributeValues())
                .locked(measureDefinitionSAAS.getLocked())
                .mandatory(measureDefinitionSAAS.getMandatory())
                .relatedMeasure(measureDefinitionSAAS.getRelatedMeasure())
                .alias(measureDefinitionSAAS.getAlias())
                .type(measureDefinitionSAAS.getType())
                .regModule(measureDefinitionSAAS.getRegModule())
                .regModuleId(measureDefinitionSAAS.getRegModuleId())
                .validationRule(measureDefinitionSAAS.getValidationRule())
                .validationParams(measureDefinitionSAAS.getValidationParams())
                .actions(measureDefinitionSAAS.getActions())
                .visibilityLevel(measureDefinitionSAAS.getVisibilityLevel())
                .compEvents(measureDefinitionSAAS.getCompEvents())
                .systemUsed(measureDefinitionSAAS.getSystemUsed())
                .notes(measureDefinitionSAAS.getNotes())
                .visible(measureDefinitionSAAS.getVisible())
                .build();
    }

    public static MeasureDefinitionSAAS toUpdatedMeasureDefinition(MeasureDefinitionSAAS measureDefinitionSAAS,
                                                                   MeasureDefinitionSAAS measureDefinitionSAASUpdate) {
        // measureDefinition.setMeasure(measureDefinitionUpdate.getMeasure() == null ? measureDefinition.getMeasure()
        // : measureDefinitionUpdate.getMeasure());
        measureDefinitionSAAS.setCode(measureDefinitionSAASUpdate.getCode() == null ? measureDefinitionSAAS.getCode() :
                measureDefinitionSAASUpdate.getCode());
        measureDefinitionSAAS.setFormat(measureDefinitionSAASUpdate.getFormat() == null ? measureDefinitionSAAS.getFormat() :
                measureDefinitionSAASUpdate.getFormat());
        measureDefinitionSAAS.setUom(measureDefinitionSAASUpdate.getUom() == null ? measureDefinitionSAAS.getUom() :
                measureDefinitionSAASUpdate.getUom());
        measureDefinitionSAAS.setPct(measureDefinitionSAASUpdate.getPct() == null ? measureDefinitionSAAS.getPct() :
                measureDefinitionSAASUpdate.getPct());
        measureDefinitionSAAS.setAttributeIdRef(measureDefinitionSAASUpdate.getAttributeIdRef() == null ?
                measureDefinitionSAAS.getAttributeIdRef() : measureDefinitionSAASUpdate.getAttributeIdRef());
        measureDefinitionSAAS.setAttributeIdRefId(measureDefinitionSAASUpdate.getAttributeIdRefId() == null ?
                measureDefinitionSAAS.getAttributeIdRefId() : measureDefinitionSAASUpdate.getAttributeIdRefId());
        measureDefinitionSAAS.setLocked(measureDefinitionSAASUpdate.getLocked() == null ? measureDefinitionSAAS.getLocked() :
                measureDefinitionSAASUpdate.getLocked());
        measureDefinitionSAAS.setMandatory(measureDefinitionSAASUpdate.getMandatory() == null ?
                measureDefinitionSAAS.getMandatory() : measureDefinitionSAASUpdate.getMandatory());
        measureDefinitionSAAS.setRelatedMeasure(measureDefinitionSAASUpdate.getRelatedMeasure() == null ?
                measureDefinitionSAAS.getRelatedMeasure() : measureDefinitionSAASUpdate.getRelatedMeasure());
        measureDefinitionSAAS.setAlias(measureDefinitionSAASUpdate.getAlias() == null ? measureDefinitionSAAS.getAlias() :
                measureDefinitionSAASUpdate.getAlias());
        measureDefinitionSAAS.setType(measureDefinitionSAASUpdate.getType() == null ? measureDefinitionSAAS.getType() :
                measureDefinitionSAASUpdate.getType());
        measureDefinitionSAAS.setRegModule(measureDefinitionSAASUpdate.getRegModule() == null ?
                measureDefinitionSAAS.getRegModule() : measureDefinitionSAASUpdate.getRegModule());
        measureDefinitionSAAS.setRegModuleId(measureDefinitionSAASUpdate.getRegModuleId() == null ?
                measureDefinitionSAAS.getRegModuleId() : measureDefinitionSAASUpdate.getRegModuleId());
        measureDefinitionSAAS.setValidationRule(measureDefinitionSAASUpdate.getValidationRule() == null ?
                measureDefinitionSAAS.getValidationRule() : measureDefinitionSAASUpdate.getValidationRule());
        measureDefinitionSAAS.setValidationParams(measureDefinitionSAASUpdate.getValidationParams() == null ?
                measureDefinitionSAAS.getValidationParams() : measureDefinitionSAASUpdate.getValidationParams());
        measureDefinitionSAAS.setActions(measureDefinitionSAASUpdate.getActions() == null ? measureDefinitionSAAS.getActions() :
                measureDefinitionSAASUpdate.getActions());
        measureDefinitionSAAS.setVisibilityLevel(measureDefinitionSAASUpdate.getVisibilityLevel() == null ?
                measureDefinitionSAAS.getVisibilityLevel() : measureDefinitionSAASUpdate.getVisibilityLevel());
        measureDefinitionSAAS.setCompEvents(measureDefinitionSAASUpdate.getCompEvents() == null ?
                measureDefinitionSAAS.getCompEvents() : measureDefinitionSAASUpdate.getCompEvents());
        measureDefinitionSAAS.setSystemUsed(measureDefinitionSAASUpdate.getSystemUsed() == null ?
                measureDefinitionSAAS.getSystemUsed() : measureDefinitionSAASUpdate.getSystemUsed());
        measureDefinitionSAAS.setNotes(measureDefinitionSAASUpdate.getNotes() == null ? measureDefinitionSAAS.getNotes() :
                measureDefinitionSAASUpdate.getNotes());
        measureDefinitionSAAS.setVisible(measureDefinitionSAASUpdate.getVisible() == null ? measureDefinitionSAAS.getVisible() :
                measureDefinitionSAASUpdate.getVisible());
        return measureDefinitionSAAS;
    }

    public static List<MeasureDefinitionSAAS> toMeasureDefinitions(List<MeasureDefinitionSAASDTO> measureDefinitionSAASDTOS) {
        return measureDefinitionSAASDTOS.stream().map(m -> toMeasureDefinition(m)).collect(Collectors.toList());
    }

    public static List<MeasureDefinitionSAASDTO> toMeasureDefinitionDTOs(List<MeasureDefinitionSAAS> measureDefinitionSAAS) {
        return measureDefinitionSAAS.stream().map(m -> toMeasureDefinitionDTO(m)).collect(Collectors.toList());
    }

}
