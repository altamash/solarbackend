package com.solar.api.tenant.mapper.portalAttribute;

import com.solar.api.saas.mapper.attribute.portalAttribute.PortalAttributeValueSAASDTO;
import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.attribute.PortalAttributeValueTenant;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PortalAttributeTenantMapper {

    // PortalAttribute //////////////////////////////////////////////////////////////////
    public static PortalAttributeTenant toPortalAttribute(PortalAttributeTenantDTO portalAttributeTenantDTO) {
        if (portalAttributeTenantDTO == null) {
            return null;
        }
        return PortalAttributeTenant.builder()
                .id(portalAttributeTenantDTO.getId())
                .name(portalAttributeTenantDTO.getName())
                .parent(portalAttributeTenantDTO.getParent())
                .associateTo(portalAttributeTenantDTO.getAssociateTo())
                .attributeType(portalAttributeTenantDTO.getAttributeType())
                .locked(portalAttributeTenantDTO.getLocked())
                .wfId(portalAttributeTenantDTO.getWfId())
                .build();
    }

    public static PortalAttributeTenantDTO toPortalAttributeDTO(PortalAttributeTenant portalAttributeTenant) {
        if (portalAttributeTenant == null) {
            return null;
        }
        return PortalAttributeTenantDTO.builder()
                .id(portalAttributeTenant.getId())
                .name(portalAttributeTenant.getName())
                .parent(portalAttributeTenant.getParent())
                .associateTo(portalAttributeTenant.getAssociateTo())
                .attributeType(portalAttributeTenant.getAttributeType())
                .locked(portalAttributeTenant.getLocked())
                .wfId(portalAttributeTenant.getWfId())
                .portalAttributeValuesTenant(toPortalAttributeValueDTOs(portalAttributeTenant.getPortalAttributeValuesTenant()))
                .build();
    }

    public static PortalAttributeTenant fromAttributeSAAStoPortalAttributeTenant(PortalAttributeSAAS portalAttributeSAAS) {
        if (portalAttributeSAAS == null) {
            return null;
        }
        return PortalAttributeTenant.builder()
                .id(portalAttributeSAAS.getId())
                .name(portalAttributeSAAS.getName())
                .parent(portalAttributeSAAS.getParent())
                .associateTo(portalAttributeSAAS.getAssociateTo())
                .attributeType(portalAttributeSAAS.getAttributeType())
                .locked(portalAttributeSAAS.getLocked())
                .wfId(portalAttributeSAAS.getWfId())
                .build();
    }

    public static PortalAttributeTenantDTO toPortalAttributeDTO(PortalAttributeSAAS portalAttributeSAAS) {
        if (portalAttributeSAAS == null) {
            return null;
        }
        return PortalAttributeTenantDTO.builder()
                .id(portalAttributeSAAS.getId())
                .name(portalAttributeSAAS.getName())
                .parent(portalAttributeSAAS.getParent())
                .associateTo(portalAttributeSAAS.getAssociateTo())
                .attributeType(portalAttributeSAAS.getAttributeType())
                .locked(portalAttributeSAAS.getLocked())
                .wfId(portalAttributeSAAS.getWfId())
                .portalAttributeValuesTenant(toPortalAttributeValueDTOsFromSAAS(portalAttributeSAAS.getPortalAttributeValuesSAAS()))
                .build();
    }

    public static PortalAttributeTenant toUpdatedPortalAttribute(PortalAttributeTenant portalAttributeTenant,
                                                                 PortalAttributeTenant portalAttributeTenantUpdate) {
        portalAttributeTenant.setName(portalAttributeTenantUpdate.getName() == null ? portalAttributeTenant.getName() :
                portalAttributeTenantUpdate.getName());
        portalAttributeTenant.setParent(portalAttributeTenantUpdate.getParent() == null ? portalAttributeTenant.getParent() :
                portalAttributeTenantUpdate.getParent());
        portalAttributeTenant.setAssociateTo(portalAttributeTenantUpdate.getAssociateTo() == null ?
                portalAttributeTenant.getAssociateTo() : portalAttributeTenantUpdate.getAssociateTo());
        portalAttributeTenant.setAttributeType(portalAttributeTenantUpdate.getAttributeType() == null ?
                portalAttributeTenant.getAttributeType() : portalAttributeTenantUpdate.getAttributeType());
        portalAttributeTenant.setLocked(portalAttributeTenantUpdate.getLocked() == null ? portalAttributeTenant.getLocked() :
                portalAttributeTenantUpdate.getLocked());
        portalAttributeTenant.setWfId(portalAttributeTenantUpdate.getWfId() == null ? portalAttributeTenant.getWfId() :
                portalAttributeTenantUpdate.getWfId());
        return portalAttributeTenant;
    }

    public static List<PortalAttributeTenant> toPortalAttributes(List<PortalAttributeTenantDTO> portalAttributeTenantDTOS) {
        return portalAttributeTenantDTOS.stream().map(pa -> toPortalAttribute(pa)).collect(Collectors.toList());
    }

    public static List<PortalAttributeTenantDTO> toPortalAttributeDTOs(List<PortalAttributeTenant> portalAttributeTenants) {
        return portalAttributeTenants.stream().map(pa -> toPortalAttributeDTO(pa)).collect(Collectors.toList());
    }

    public static List<PortalAttributeTenantDTO> toPortalAttributeDTOsFromSAAS(List<PortalAttributeSAAS> portalAttributeSAAS) {
        return portalAttributeSAAS.stream().map(pa -> toPortalAttributeDTO(pa)).collect(Collectors.toList());
    }

    // PortalAttributeValue //////////////////////////////////////////////////////////////////
    public static PortalAttributeValueTenant toPortalAttributeValue(PortalAttributeValueTenantDTO portalAttributeValueTenantDTO) {
        return PortalAttributeValueTenant.builder()
                .id(portalAttributeValueTenantDTO.getId())
                .attributeName(portalAttributeValueTenantDTO.getAttributeName())
                .attributeValue(portalAttributeValueTenantDTO.getAttributeValue())
                .sequenceNumber(portalAttributeValueTenantDTO.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueTenantDTO.getParentReferenceValue())
                .description(portalAttributeValueTenantDTO.getDescription())
                .build();
    }

    public static PortalAttributeValueTenant toPortalAttributeValue(PortalAttributeValueSAASDTO portalAttributeValueSAASDTO) {
        return PortalAttributeValueTenant.builder()
                .id(portalAttributeValueSAASDTO.getId())
                .attributeName(portalAttributeValueSAASDTO.getAttributeName())
                .attributeValue(portalAttributeValueSAASDTO.getAttributeValue())
                .sequenceNumber(portalAttributeValueSAASDTO.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueSAASDTO.getParentReferenceValue())
                .description(portalAttributeValueSAASDTO.getDescription())
                .build();
    }

    public static PortalAttributeValueTenant fromSAASToPortalAttributeValueTenant(PortalAttributeValueSAAS portalAttributeValueSAAS) {
        return PortalAttributeValueTenant.builder()
                .id(portalAttributeValueSAAS.getId())
                .attributeName(portalAttributeValueSAAS.getAttributeName())
                .attributeValue(portalAttributeValueSAAS.getAttributeValue())
                .attribute(fromAttributeSAAStoPortalAttributeTenant(portalAttributeValueSAAS.getAttribute()))
                .sequenceNumber(portalAttributeValueSAAS.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueSAAS.getParentReferenceValue())
                .description(portalAttributeValueSAAS.getDescription())
                .build();
    }

    public static PortalAttributeValueTenantDTO toPortalAttributeValueDTO(PortalAttributeValueTenant portalAttributeValueTenant) {
        if (portalAttributeValueTenant == null) {
            return null;
        }
        return PortalAttributeValueTenantDTO.builder()
                .id(portalAttributeValueTenant.getId())
                .attributeName(portalAttributeValueTenant.getAttributeName())
                .attributeId(portalAttributeValueTenant.getAttribute() == null ? null :
                        portalAttributeValueTenant.getAttribute().getId())
                .attributeValue(portalAttributeValueTenant.getAttributeValue())
                .sequenceNumber(portalAttributeValueTenant.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueTenant.getParentReferenceValue())
                .description(portalAttributeValueTenant.getDescription())
                .build();
    }

    public static PortalAttributeValueTenantDTO toPortalAttributeValueDTO(PortalAttributeValueSAAS portalAttributeValueSAAS) {
        if (portalAttributeValueSAAS == null) {
            return null;
        }
        return PortalAttributeValueTenantDTO.builder()
                .id(portalAttributeValueSAAS.getId())
                .attributeName(portalAttributeValueSAAS.getAttributeName())
                .attributeId(portalAttributeValueSAAS.getAttribute() == null ? null :
                        portalAttributeValueSAAS.getAttribute().getId())
                .attributeValue(portalAttributeValueSAAS.getAttributeValue())
                .sequenceNumber(portalAttributeValueSAAS.getSequenceNumber())
                .parentReferenceValue(portalAttributeValueSAAS.getParentReferenceValue())
                .description(portalAttributeValueSAAS.getDescription())
                .build();
    }

    public static PortalAttributeValueTenant toUpdatedPortalAttributeValue(PortalAttributeValueTenant portalAttributeValueTenant,
                                                                           PortalAttributeValueTenant portalAttributeValueTenantUpdate) {
        portalAttributeValueTenant.setId(portalAttributeValueTenantUpdate.getId() == null ?
                portalAttributeValueTenant.getId() : portalAttributeValueTenantUpdate.getId());
        portalAttributeValueTenant.setAttributeName(portalAttributeValueTenantUpdate.getAttributeName() == null ?
                portalAttributeValueTenant.getAttributeName() : portalAttributeValueTenantUpdate.getAttributeName());
        portalAttributeValueTenant.setAttributeValue(portalAttributeValueTenantUpdate.getAttributeValue() == null ?
                portalAttributeValueTenant.getAttributeValue() : portalAttributeValueTenantUpdate.getAttributeValue());
        portalAttributeValueTenant.setSequenceNumber(portalAttributeValueTenantUpdate.getSequenceNumber() == null ?
                portalAttributeValueTenant.getSequenceNumber() : portalAttributeValueTenantUpdate.getSequenceNumber());
        portalAttributeValueTenant.setParentReferenceValue(portalAttributeValueTenantUpdate.getParentReferenceValue() == null ?
                portalAttributeValueTenant.getParentReferenceValue() : portalAttributeValueTenantUpdate.getParentReferenceValue());
        portalAttributeValueTenant.setDescription(portalAttributeValueTenantUpdate.getDescription() == null ?
                portalAttributeValueTenant.getDescription() : portalAttributeValueTenantUpdate.getDescription());
        return portalAttributeValueTenant;
    }

    public static List<PortalAttributeValueTenant> toPortalAttributeValues(List<PortalAttributeValueTenantDTO> portalAttributeValueTenantDTOS) {
        if (portalAttributeValueTenantDTOS == null) {
            return Collections.emptyList();
        }
        return portalAttributeValueTenantDTOS.stream().map(pa -> toPortalAttributeValue(pa)).collect(Collectors.toList());
    }

    public static List<PortalAttributeValueTenant> toPortalAttributeValuesFromSAAS(List<PortalAttributeValueSAASDTO> portalAttributeValueSAASDTOs) {
        if (portalAttributeValueSAASDTOs == null) {
            return Collections.emptyList();
        }
        return portalAttributeValueSAASDTOs.stream().map(pa -> toPortalAttributeValue(pa)).collect(Collectors.toList());
    }

    public static List<PortalAttributeValueTenantDTO> toPortalAttributeValueDTOs(List<PortalAttributeValueTenant> portalAttributeValuesTenant) {
        if (portalAttributeValuesTenant == null) {
            return Collections.emptyList();
        }
        return portalAttributeValuesTenant.stream().map(pa -> toPortalAttributeValueDTO(pa)).collect(Collectors.toList());
    }

    public static List<PortalAttributeValueTenantDTO> toPortalAttributeValueDTOsFromSAAS(List<PortalAttributeValueSAAS> portalAttributeValuesSAAS) {
        if (portalAttributeValuesSAAS == null) {
            return Collections.emptyList();
        }
        return portalAttributeValuesSAAS.stream().map(pa -> toPortalAttributeValueDTO(pa)).collect(Collectors.toList());
    }
}
