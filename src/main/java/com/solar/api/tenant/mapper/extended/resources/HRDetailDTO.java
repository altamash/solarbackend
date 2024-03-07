package com.solar.api.tenant.mapper.extended.resources;

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
public class HRDetailDTO {

    private Long id;
    private Long hrHeadId; //PTE,FTE,OTHERS
    private Long measureCodeId; // columns
    private String value; //columns default value
    private String measure;
    private MeasureDefinitionTenantDTO measureDefinitionTenant;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String filterByInd;
    private Date lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;

}
