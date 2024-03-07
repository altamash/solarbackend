package com.solar.api.tenant.mapper.extended.partner;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PartnerDetailDTO {

    private Long id;
    private Long partnerId;
    private Long measureCodeId;
    private String value;
    private MeasureDefinitionTenantDTO measureDefinition;
    private String measure;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
