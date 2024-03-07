package com.solar.api.tenant.mapper.extended.project;

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
public class ProjectDetailDTO {

    private Long id;
    private Long projectId;
    private String measure;
    private Long measureCodeId;
    private String value;
    private String filterByInd;
    private Date lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;
    private String category;
    private MeasureDefinitionTenantDTO measureDefinition;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
