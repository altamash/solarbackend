package com.solar.api.saas.mapper.attribute.systemAttribute;

import com.solar.api.saas.model.attribute.SystemAttribute;

import java.util.List;
import java.util.stream.Collectors;

public class SystemAttributeMapper {

    /**
     * @param sysAttrDTO
     * @return
     */
    public static SystemAttribute toSystemAttribute(SystemAttributeDTO sysAttrDTO) {
        return SystemAttribute.builder()
                .id(sysAttrDTO.getId())
                .attribute(sysAttrDTO.getAttribute())
                .attributeDescription(sysAttrDTO.getAttributeDescription())
                .attributeKey(sysAttrDTO.getAttributeKey())
                .attributeType(sysAttrDTO.getAttributeType())
                .parentAttribute(sysAttrDTO.getParentAttribute())
                .attributeValue(sysAttrDTO.getAttributeValue())
                .attributeValueLob(sysAttrDTO.getAttributeValueLob())
                .referenceObject(sysAttrDTO.getReferenceObject())
                .build();
    }

    public static SystemAttributeDTO toSystemAttributeDTO(SystemAttribute sysAttr) {
        if (sysAttr == null) {
            return null;
        }
        return SystemAttributeDTO.builder()
                .id(sysAttr.getId())
                .attribute(sysAttr.getAttribute())
                .attributeDescription(sysAttr.getAttributeDescription())
                .attributeKey(sysAttr.getAttributeKey())
                .attributeType(sysAttr.getAttributeType())
                .parentAttribute(sysAttr.getParentAttribute())
                .attributeValue(sysAttr.getAttributeValue())
                .attributeValueLob(sysAttr.getAttributeValueLob())
                .referenceObject(sysAttr.getReferenceObject())
                .build();
    }

    public static SystemAttribute toUpdatedSystemAttribute(SystemAttribute systemAttribute,
                                                           SystemAttribute systemAttributeUpdate) {
        systemAttribute.setAttribute(systemAttributeUpdate.getAttribute() == null ? systemAttribute.getAttribute() :
                systemAttributeUpdate.getAttribute());
        systemAttribute.setAttributeDescription(systemAttributeUpdate.getAttributeDescription() == null ?
                systemAttribute.getAttributeDescription() : systemAttributeUpdate.getAttributeDescription());
        systemAttribute.setAttributeKey(systemAttributeUpdate.getAttributeKey() == null ?
                systemAttribute.getAttributeKey() : systemAttributeUpdate.getAttributeKey());
        systemAttribute.setAttributeValue(systemAttributeUpdate.getAttributeValue() == null ?
                systemAttribute.getAttributeValue() : systemAttributeUpdate.getAttributeValue());
        systemAttribute.setAttributeValueLob(systemAttributeUpdate.getAttributeValueLob() == null ?
                systemAttribute.getAttributeValueLob() : systemAttributeUpdate.getAttributeValueLob());
        systemAttribute.setParentAttribute(systemAttributeUpdate.getParentAttribute() == null ?
                systemAttribute.getParentAttribute() : systemAttributeUpdate.getParentAttribute());
        systemAttribute.setAttributeType(systemAttributeUpdate.getAttributeType() == null ?
                systemAttribute.getAttributeType() : systemAttributeUpdate.getAttributeType());
        systemAttribute.setReferenceObject(systemAttributeUpdate.getReferenceObject() == null ?
                systemAttribute.getReferenceObject() : systemAttributeUpdate.getReferenceObject());
        return systemAttribute;
    }

    public static List<SystemAttribute> toSystemAttributes(List<SystemAttributeDTO> systemAttributeDTOS) {
        return systemAttributeDTOS.stream().map(sa -> toSystemAttribute(sa)).collect(Collectors.toList());
    }

    public static List<SystemAttributeDTO> toSystemAttributeDTOs(List<SystemAttribute> systemAttributes) {
        return systemAttributes.stream().map(sa -> toSystemAttributeDTO(sa)).collect(Collectors.toList());
    }
}
