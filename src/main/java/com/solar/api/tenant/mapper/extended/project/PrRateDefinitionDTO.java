package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrRateDefinitionDTO {

    private Long id;
    private String name;
    private String uniqueCode;
    private String rateCategory;
    private String description;
    private String rateType;
    private String ratePeriod;
    private String termLengthInDays;
    private String rate;
    private String fixedAmount;
    private String overtimePeriod;
    private String overtimeRate;
    private String overtimeFixedAmount;
    private String calculationFactor;
    private String calculationFrequency;
    private String category;
    private String notes;
}
