package com.solar.api.tenant.mapper.extended.measure;

import com.solar.api.saas.mapper.extended.measure.MeasureDefinitionSAASDTO;
import com.solar.api.saas.model.extended.MeasureDefinitionSAAS;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantMapper;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;

import java.util.List;
import java.util.stream.Collectors;

public class MeasureDefinitionTenantMapper {
    public static MeasureDefinitionTenant toMeasureDefinition(MeasureDefinitionTenantDTO measureDefinitionTenantDTO) {
        if (measureDefinitionTenantDTO == null) {
            return null;
        }
        return MeasureDefinitionTenant.builder()
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

    public static MeasureDefinitionTenantDTO toMeasureDefinitionDTO(MeasureDefinitionTenant measureDefinitionTenant) {
        if (measureDefinitionTenant == null) {
            return null;
        }
        return MeasureDefinitionTenantDTO.builder()
                .id(measureDefinitionTenant.getId())
                .measure(measureDefinitionTenant.getMeasure())
                .code(measureDefinitionTenant.getCode())
                .format(measureDefinitionTenant.getFormat())
                .uom(measureDefinitionTenant.getUom())
                .pct(measureDefinitionTenant.getPct())
                .attributeIdRef(measureDefinitionTenant.getAttributeIdRef())
                .attributeIdRefId(measureDefinitionTenant.getAttributeIdRefId())
//                .portalAttributeValues(measureDefinitionTenant.getPortalAttributeValues() != null ?
//                        measureDefinitionTenant.getPortalAttributeValues() : null)
                .portalAttributeValues(measureDefinitionTenant.getPortalAttributeValues() != null ?
                                measureDefinitionTenant.getPortalAttributeValues().stream().map(pa -> pa.getAttributeValue()).collect(Collectors.toList()) : null)
                .portalAttributeValueDTOs(measureDefinitionTenant.getPortalAttributeValues())
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

    public static MeasureDefinitionTenantDTO toMeasureDefinitionDTO(MeasureDefinitionSAAS measureDefinitionSAAS) {
        if (measureDefinitionSAAS == null) {
            return null;
        }
        return MeasureDefinitionTenantDTO.builder()
                .id(measureDefinitionSAAS.getId())
                .measure(measureDefinitionSAAS.getMeasure())
                .code(measureDefinitionSAAS.getCode())
                .format(measureDefinitionSAAS.getFormat())
                .uom(measureDefinitionSAAS.getUom())
                .pct(measureDefinitionSAAS.getPct())
                .attributeIdRef(measureDefinitionSAAS.getAttributeIdRef())
                .attributeIdRefId(measureDefinitionSAAS.getAttributeIdRefId())
//        PortalAttributeValueTenantDTO
                .portalAttributeValues(measureDefinitionSAAS.getPortalAttributeValues() != null ?
                        measureDefinitionSAAS.getPortalAttributeValues().stream().map(pa -> pa.getAttributeValue()).collect(Collectors.toList()) : null)
                .portalAttributeValueDTOs(
                        PortalAttributeTenantMapper.toPortalAttributeValueDTOs(
                        PortalAttributeTenantMapper.toPortalAttributeValuesFromSAAS(measureDefinitionSAAS.getPortalAttributeValues())))
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

    public static MeasureDefinitionTenant toMeasureDefinition(MeasureDefinitionSAAS measureDefinitionSAAS) {
        if (measureDefinitionSAAS == null) {
            return null;
        }
        return MeasureDefinitionTenant.builder()
                .id(measureDefinitionSAAS.getId())
                .measure(measureDefinitionSAAS.getMeasure())
                .code(measureDefinitionSAAS.getCode())
                .format(measureDefinitionSAAS.getFormat())
                .uom(measureDefinitionSAAS.getUom())
                .pct(measureDefinitionSAAS.getPct())
                .attributeIdRef(measureDefinitionSAAS.getAttributeIdRef())
                .attributeIdRefId(measureDefinitionSAAS.getAttributeIdRefId())
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

    public static MeasureDefinitionTenantDTO toMeasureDefinitionDTO(MeasureDefinitionSAASDTO measureDefinitionSAASDTO) {
        if (measureDefinitionSAASDTO == null) {
            return null;
        }
        return MeasureDefinitionTenantDTO.builder()
                .id(measureDefinitionSAASDTO.getId())
                .measure(measureDefinitionSAASDTO.getMeasure())
                .code(measureDefinitionSAASDTO.getCode())
                .format(measureDefinitionSAASDTO.getFormat())
                .uom(measureDefinitionSAASDTO.getUom())
                .pct(measureDefinitionSAASDTO.getPct())
                .attributeIdRef(measureDefinitionSAASDTO.getAttributeIdRef())
                .attributeIdRefId(measureDefinitionSAASDTO.getAttributeIdRefId())
//        PortalAttributeValueTenantDTO
                .portalAttributeValues(measureDefinitionSAASDTO.getPortalAttributeValues() != null ?
                        measureDefinitionSAASDTO.getPortalAttributeValues().stream().map(pa -> pa.getAttributeValue()).collect(Collectors.toList()) : null)
                .portalAttributeValueDTOs(
                        PortalAttributeTenantMapper.toPortalAttributeValueDTOs(
                        PortalAttributeTenantMapper.toPortalAttributeValuesFromSAAS(measureDefinitionSAASDTO.getPortalAttributeValues())))
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

    public static MeasureDefinitionTenant toUpdatedMeasureDefinition(MeasureDefinitionTenant measureDefinitionTenant,
                                                                     MeasureDefinitionTenant measureDefinitionTenantUpdate) {
        // measureDefinition.setMeasure(measureDefinitionUpdate.getMeasure() == null ? measureDefinition.getMeasure()
        // : measureDefinitionUpdate.getMeasure());
        measureDefinitionTenant.setCode(measureDefinitionTenantUpdate.getCode() == null ? measureDefinitionTenant.getCode() :
                measureDefinitionTenantUpdate.getCode());
        measureDefinitionTenant.setFormat(measureDefinitionTenantUpdate.getFormat() == null ? measureDefinitionTenant.getFormat() :
                measureDefinitionTenantUpdate.getFormat());
        measureDefinitionTenant.setUom(measureDefinitionTenantUpdate.getUom() == null ? measureDefinitionTenant.getUom() :
                measureDefinitionTenantUpdate.getUom());
        measureDefinitionTenant.setPct(measureDefinitionTenantUpdate.getPct() == null ? measureDefinitionTenant.getPct() :
                measureDefinitionTenantUpdate.getPct());
        measureDefinitionTenant.setAttributeIdRef(measureDefinitionTenantUpdate.getAttributeIdRef() == null ?
                measureDefinitionTenant.getAttributeIdRef() : measureDefinitionTenantUpdate.getAttributeIdRef());
        measureDefinitionTenant.setAttributeIdRefId(measureDefinitionTenantUpdate.getAttributeIdRefId() == null ?
                measureDefinitionTenant.getAttributeIdRefId() : measureDefinitionTenantUpdate.getAttributeIdRefId());
        measureDefinitionTenant.setLocked(measureDefinitionTenantUpdate.getLocked() == null ? measureDefinitionTenant.getLocked() :
                measureDefinitionTenantUpdate.getLocked());
        measureDefinitionTenant.setMandatory(measureDefinitionTenantUpdate.getMandatory() == null ?
                measureDefinitionTenant.getMandatory() : measureDefinitionTenantUpdate.getMandatory());
        measureDefinitionTenant.setRelatedMeasure(measureDefinitionTenantUpdate.getRelatedMeasure() == null ?
                measureDefinitionTenant.getRelatedMeasure() : measureDefinitionTenantUpdate.getRelatedMeasure());
        measureDefinitionTenant.setAlias(measureDefinitionTenantUpdate.getAlias() == null ? measureDefinitionTenant.getAlias() :
                measureDefinitionTenantUpdate.getAlias());
        measureDefinitionTenant.setType(measureDefinitionTenantUpdate.getType() == null ? measureDefinitionTenant.getType() :
                measureDefinitionTenantUpdate.getType());
        measureDefinitionTenant.setRegModule(measureDefinitionTenantUpdate.getRegModule() == null ?
                measureDefinitionTenant.getRegModule() : measureDefinitionTenantUpdate.getRegModule());
        measureDefinitionTenant.setRegModuleId(measureDefinitionTenantUpdate.getRegModuleId() == null ?
                measureDefinitionTenant.getRegModuleId() : measureDefinitionTenantUpdate.getRegModuleId());
        measureDefinitionTenant.setValidationRule(measureDefinitionTenantUpdate.getValidationRule() == null ?
                measureDefinitionTenant.getValidationRule() : measureDefinitionTenantUpdate.getValidationRule());
        measureDefinitionTenant.setValidationParams(measureDefinitionTenantUpdate.getValidationParams() == null ?
                measureDefinitionTenant.getValidationParams() : measureDefinitionTenantUpdate.getValidationParams());
        measureDefinitionTenant.setActions(measureDefinitionTenantUpdate.getActions() == null ? measureDefinitionTenant.getActions() :
                measureDefinitionTenantUpdate.getActions());
        measureDefinitionTenant.setVisibilityLevel(measureDefinitionTenantUpdate.getVisibilityLevel() == null ?
                measureDefinitionTenant.getVisibilityLevel() : measureDefinitionTenantUpdate.getVisibilityLevel());
        measureDefinitionTenant.setCompEvents(measureDefinitionTenantUpdate.getCompEvents() == null ?
                measureDefinitionTenant.getCompEvents() : measureDefinitionTenantUpdate.getCompEvents());
        measureDefinitionTenant.setSystemUsed(measureDefinitionTenantUpdate.getSystemUsed() == null ?
                measureDefinitionTenant.getSystemUsed() : measureDefinitionTenantUpdate.getSystemUsed());
        measureDefinitionTenant.setNotes(measureDefinitionTenantUpdate.getNotes() == null ? measureDefinitionTenant.getNotes() :
                measureDefinitionTenantUpdate.getNotes());
        measureDefinitionTenant.setVisible(measureDefinitionTenantUpdate.getVisible() == null ? measureDefinitionTenant.getVisible() :
                measureDefinitionTenantUpdate.getVisible());
        return measureDefinitionTenant;
    }

    public static List<MeasureDefinitionTenant> toMeasureDefinitions(List<MeasureDefinitionTenantDTO> measureDefinitionTenantDTOS) {
        return measureDefinitionTenantDTOS.stream().map(m -> toMeasureDefinition(m)).collect(Collectors.toList());
    }

    public static List<MeasureDefinitionTenantDTO> toMeasureDefinitionDTOs(List<MeasureDefinitionTenant> measureDefinitionTenant) {
        return measureDefinitionTenant.stream().map(m -> toMeasureDefinitionDTO(m)).collect(Collectors.toList());
    }

    public static List<MeasureDefinitionTenantDTO> toMeasureDefinitionDTOsFromSAAS(List<MeasureDefinitionSAAS> measureDefinitionSAAS) {
        return measureDefinitionSAAS.stream().map(m -> toMeasureDefinitionDTO(m)).collect(Collectors.toList());
    }

}
