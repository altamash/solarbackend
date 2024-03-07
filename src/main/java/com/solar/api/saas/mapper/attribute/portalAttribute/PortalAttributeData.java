package com.solar.api.saas.mapper.attribute.portalAttribute;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PortalAttributeData {
    PortalAttributeSAASDTO portalAttribute;
    List<PortalAttributeValueSAASDTO> portalAttributeValues;
}
