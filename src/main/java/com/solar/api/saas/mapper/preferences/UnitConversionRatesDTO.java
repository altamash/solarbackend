package com.solar.api.saas.mapper.preferences;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitConversionRatesDTO {

    private Long id;
    private String baseUnit;
    private String conversionUnit;
    private String conversionRate;
    private String conversionFormula;
}
