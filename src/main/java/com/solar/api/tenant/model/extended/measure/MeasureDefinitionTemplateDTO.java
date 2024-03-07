package com.solar.api.tenant.model.extended.measure;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MeasureDefinitionTemplateDTO {

    private String measureNames;
    private String measureIds;
    private String formats;
}
