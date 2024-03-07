package com.solar.api.tenant.mapper.extended.register;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.resources.MeasureBlockHeadDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterDetailDTO {

    private Long id;
    private String measureCode;
    private Long measureId;
    //    private RegisterHeadDTO registerHead;
    private Long registerHeadId;
    private MeasureDefinitionTenantDTO measureDefinition;
    private MeasureBlockHeadDTO measureBlockHead;
    private String defaultValue;
    private String level;
    private String category;
    private Integer sequenceNumber;
    private Boolean multiEntry;
    private Boolean mandatory;
    private String filterByInd;
    private String variableByDetail;
    private String flags;
    private Long measureBlockId;
    private String blockName;
    private Boolean measureUnique;
    private Boolean lockedTest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
