package com.solar.api.tenant.mapper.portalAttribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortalAttributeTenantDTO {

    private Long id;
    private String name;
    private String parent;
    private String associateTo;
    private String attributeType;
    private Boolean locked;
    private Long wfId;
    private List<PortalAttributeValueTenantDTO> portalAttributeValuesTenant;
    private List<PortalAttributeTenantDTO> children;
}
