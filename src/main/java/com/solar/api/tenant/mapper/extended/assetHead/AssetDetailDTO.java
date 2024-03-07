package com.solar.api.tenant.mapper.extended.assetHead;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
// Client schema only
public class AssetDetailDTO {

    private Long id;
    private Long assetHeadId;
    private Long measureCodeId;
    private String value;
    private String filterByInd;
    private Date lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;
    private MeasureDefinitionTenantDTO measureDefinition;
    private String measure;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
