package com.solar.api.saas.mapper.attribute.portalAttribute;

import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PortalAttributeSAASMapper {

    // PortalAttribute //////////////////////////////////////////////////////////////////
    public static PortalAttributeSAAS toPortalAttribute(PortalAttributeSAASDTO portalAttributeSAASDTO) {
        if (portalAttributeSAASDTO == null) {
            return null;
        }
        return PortalAttributeSAAS.builder()
                .id(portalAttributeSAASDTO.getId())
                .name(portalAttributeSAASDTO.getName())
                .parent(portalAttributeSAASDTO.getParent())
                .associateTo(portalAttributeSAASDTO.getAssociateTo())
                .attributeType(portalAttributeSAASDTO.getAttributeType())
                .locked(portalAttributeSAASDTO.getLocked())
                .wfId(portalAttributeSAASDTO.getWfId())
                .build();
    }

    public static PortalAttributeSAAS toPortalAttribute(PortalAttributeTenantDTO portalAttributeTenantDTO) {
        if (portalAttributeTenantDTO == null) {
            return null;
        }
        return PortalAttributeSAAS.builder()
                .id(portalAttributeTenantDTO.getId())
                .name(portalAttributeTenantDTO.getName())
                .parent(portalAttributeTenantDTO.getParent())
                .associateTo(portalAttributeTenantDTO.getAssociateTo())
                .attributeType(portalAttributeTenantDTO.getAttributeType())
                .locked(portalAttributeTenantDTO.getLocked())
                .wfId(portalAttributeTenantDTO.getWfId())
                .build();
    }

    public static PortalAttributeSAAS fromAttributeTenantToPortalAttributeSAAS(PortalAttributeTenant portalAttributeTenant) {
        if (portalAttributeTenant == null) {
            return null;
        }
        return PortalAttributeSAAS.builder()
                .id(portalAttributeTenant.getId())
                .name(portalAttributeTenant.getName())
                .parent(portalAttributeTenant.getParent())
                .associateTo(portalAttributeTenant.getAssociateTo())
                .attributeType(portalAttributeTenant.getAttributeType())
                .locked(portalAttributeTenant.getLocked())
                .wfId(portalAttributeTenant.getWfId())
                .build();
    }

    public static PortalAttributeSAASDTO toPortalAttributeDTO(PortalAttributeSAAS portalAttributeSAAS) {
        if (portalAttributeSAAS == null) {
            return null;
        }
        return PortalAttributeSAASDTO.builder()
                .id(portalAttributeSAAS.getId())
                .name(portalAttributeSAAS.getName())
                .parent(portalAttributeSAAS.getParent())
                .associateTo(portalAttributeSAAS.getAssociateTo())
                .attributeType(portalAttributeSAAS.getAttributeType())
                .locked(portalAttributeSAAS.getLocked())
                .wfId(portalAttributeSAAS.getWfId())
                .portalAttributeValues(toPortalAttributeValueDTOs(portalAttributeSAAS.getPortalAttributeValuesSAAS()))
                .build();
    }

    public static PortalAttributeSAAS toUpdatedPortalAttribute(PortalAttributeSAAS portalAttributeSAAS,
                                                               PortalAttributeSAAS portalAttributeSAASUpdate) {
        portalAttributeSAAS.setName(portalAttributeSAASUpdate.getName() == null ? portalAttributeSAAS.getName() :
                portalAttributeSAASUpdate.getName());
        portalAttributeSAAS.setParent(portalAttributeSAASUpdate.getParent() == null ? portalAttributeSAAS.getParent() :
                portalAttributeSAASUpdate.getParent());
        portalAttributeSAAS.setAssociateTo(portalAttributeSAASUpdate.getAssociateTo() == null ?
                portalAttributeSAAS.getAssociateTo() : portalAttributeSAASUpdate.getAssociateTo());
        portalAttributeSAAS.setAttributeType(portalAttributeSAASUpdate.getAttributeType() == null ?
                portalAttributeSAAS.getAttributeType() : portalAttributeSAASUpdate.getAttributeType());
        portalAttributeSAAS.setLocked(portalAttributeSAASUpdate.getLocked() == null ? portalAttributeSAAS.getLocked() :
                portalAttributeSAASUpdate.getLocked());
        portalAttributeSAAS.setWfId(portalAttributeSAASUpdate.getWfId() == null ? portalAttributeSAAS.getWfId() :
                portalAttributeSAASUpdate.getWfId());
        return portalAttributeSAAS;
    }

    public static List<PortalAttributeSAAS> toPortalAttributes(List<PortalAttributeSAASDTO> portalAttributeSAASDTOS) {
        return portalAttributeSAASDTOS.stream().map(pa -> toPortalAttribute(pa)).collect(Collectors.toList());
    }

    public static List<PortalAttributeSAASDTO> toPortalAttributeDTOs(List<PortalAttributeSAAS> portalAttributeSAAS) {
        return portalAttributeSAAS.stream().map(pa -> toPortalAttributeDTO(pa)).collect(Collectors.toList());
    }

    // PortalAttributeValue //////////////////////////////////////////////////////////////////
    public static PortalAttributeValueSAAS toPortalAttributeValue(PortalAttributeValueSAASDTO portalAttributeValueSAASDTO) {
        return PortalAttributeValueSAAS.builder()
                .id(portalAttributeValueSAASDTO.getId())
                .attributeName(portalAttributeValueSAASDTO.getAttributeName())
                .attributeValue(portalAttributeValueSAASDTO.getAttributeValue())
                .sequenceNumber(portalAttributeValueSAASDTO.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueSAASDTO.getParentReferenceValue())
                .description(portalAttributeValueSAASDTO.getDescription())
                .resourceInterval(portalAttributeValueSAASDTO.getResourceInterval())
                .status(portalAttributeValueSAASDTO.getStatus())
                .build();
    }

    public static PortalAttributeValueSAAS FromTenantToPortalAttributeValueSAAS(PortalAttributeValueTenant portalAttributeValueTenant) {
        return PortalAttributeValueSAAS.builder()
                .id(portalAttributeValueTenant.getId())
                .attributeName(portalAttributeValueTenant.getAttributeName())
                .attributeValue(portalAttributeValueTenant.getAttributeValue())
                .attribute(fromAttributeTenantToPortalAttributeSAAS(portalAttributeValueTenant.getAttribute()))
                .sequenceNumber(portalAttributeValueTenant.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueTenant.getParentReferenceValue())
                .description(portalAttributeValueTenant.getDescription())
                .build();
    }

    public static PortalAttributeValueSAASDTO toPortalAttributeValueDTO(PortalAttributeValueSAAS portalAttributeValueSAAS) {
        if (portalAttributeValueSAAS == null) {
            return null;
        }
        return PortalAttributeValueSAASDTO.builder()
                .id(portalAttributeValueSAAS.getId())
                .attributeName(portalAttributeValueSAAS.getAttributeName())
                .attributeId(portalAttributeValueSAAS.getAttribute() == null ? null :
                        portalAttributeValueSAAS.getAttribute().getId())
                .attributeValue(portalAttributeValueSAAS.getAttributeValue())
                .sequenceNumber(portalAttributeValueSAAS.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueSAAS.getParentReferenceValue())
                .description(portalAttributeValueSAAS.getDescription())
                .resourceInterval(portalAttributeValueSAAS.getResourceInterval())
                .status(portalAttributeValueSAAS.getStatus())
                .build();
    }

    public static PortalAttributeValueSAASDTO toPortalAttributeValueDTO(PortalAttributeValueTenantDTO portalAttributeValueTenantDTO) {
        if (portalAttributeValueTenantDTO == null) {
            return null;
        }
        return PortalAttributeValueSAASDTO.builder()
                .id(portalAttributeValueTenantDTO.getId())
                .attributeName(portalAttributeValueTenantDTO.getAttributeName())
                .attributeId(portalAttributeValueTenantDTO.getAttributeId())
                .attributeValue(portalAttributeValueTenantDTO.getAttributeValue())
                .sequenceNumber(portalAttributeValueTenantDTO.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueTenantDTO.getParentReferenceValue())
                .description(portalAttributeValueTenantDTO.getDescription())
                .build();
    }

    public static PortalAttributeValueSAAS toUpdatedPortalAttributeValue(PortalAttributeValueSAAS portalAttributeValueSAAS,
                                                                         PortalAttributeValueSAAS portalAttributeValueSAASUpdate) {
        portalAttributeValueSAAS.setId(portalAttributeValueSAASUpdate.getId() == null ?
                portalAttributeValueSAAS.getId() : portalAttributeValueSAASUpdate.getId());
        portalAttributeValueSAAS.setAttributeName(portalAttributeValueSAASUpdate.getAttributeName() == null ?
                portalAttributeValueSAAS.getAttributeName() : portalAttributeValueSAASUpdate.getAttributeName());
        portalAttributeValueSAAS.setAttributeValue(portalAttributeValueSAASUpdate.getAttributeValue() == null ?
                portalAttributeValueSAAS.getAttributeValue() : portalAttributeValueSAASUpdate.getAttributeValue());
        portalAttributeValueSAAS.setSequenceNumber(portalAttributeValueSAASUpdate.getSequenceNumber() == null ?
                portalAttributeValueSAAS.getSequenceNumber() : portalAttributeValueSAASUpdate.getSequenceNumber());
        portalAttributeValueSAAS.setParentReferenceValue(portalAttributeValueSAASUpdate.getParentReferenceValue() == null ?
                portalAttributeValueSAAS.getParentReferenceValue() : portalAttributeValueSAASUpdate.getParentReferenceValue());
        portalAttributeValueSAAS.setDescription(portalAttributeValueSAASUpdate.getDescription() == null ?
                portalAttributeValueSAAS.getDescription() : portalAttributeValueSAASUpdate.getDescription());
        portalAttributeValueSAAS.setResourceInterval(portalAttributeValueSAASUpdate.getResourceInterval() == null ?
                portalAttributeValueSAAS.getResourceInterval() : portalAttributeValueSAASUpdate.getResourceInterval());
        portalAttributeValueSAAS.setStatus(portalAttributeValueSAASUpdate.getStatus() == null ?
                portalAttributeValueSAAS.getStatus() : portalAttributeValueSAASUpdate.getStatus());
        return portalAttributeValueSAAS;
    }

    public static List<PortalAttributeValueSAAS> toPortalAttributeValues(List<PortalAttributeValueSAASDTO> portalAttributeValueSAASDTOS) {
        if (portalAttributeValueSAASDTOS == null) {
            return Collections.emptyList();
        }
        return portalAttributeValueSAASDTOS.stream().map(pa -> toPortalAttributeValue(pa)).collect(Collectors.toList());
    }

    public static List<PortalAttributeValueSAASDTO> toPortalAttributeValueDTOs(List<PortalAttributeValueSAAS> portalAttributeValueSAAS) {
        if (portalAttributeValueSAAS == null) {
            return Collections.emptyList();
        }
        return portalAttributeValueSAAS.stream().map(pa -> toPortalAttributeValueDTO(pa)).collect(Collectors.toList());
    }

    public static List<PortalAttributeValueSAASDTO> toPortalAttributeValueDTOsFromTenant(List<PortalAttributeValueTenantDTO> portalAttributeValueTenantDTOs) {
        if (portalAttributeValueTenantDTOs == null) {
            return Collections.emptyList();
        }
        return portalAttributeValueTenantDTOs.stream().map(pa -> toPortalAttributeValueDTO(pa)).collect(Collectors.toList());
    }
}
