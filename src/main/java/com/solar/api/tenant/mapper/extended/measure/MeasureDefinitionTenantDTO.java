package com.solar.api.tenant.mapper.extended.measure;

import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class MeasureDefinitionTenantDTO {

    private Long id;
    private String measure;
    private String code;
    private String format;
    private String uom;
    private Boolean pct;
    private String attributeIdRef;
    private Long attributeIdRefId;
    private Boolean locked;
    private Boolean mandatory;
    private String relatedMeasure;
    private String alias;
    private String type;
    private String regModule;
    private Long regModuleId;
    private String validationRule;
    private String validationParams;
    private String actions;
    private String visibilityLevel;
    private String compEvents;
    private Boolean systemUsed;
    private String notes;
    private List<String> portalAttributeValues;
    private List<PortalAttributeValueTenantDTO> portalAttributeValueDTOs;
    private Boolean visible;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
