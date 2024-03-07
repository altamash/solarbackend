package com.solar.api.saas.mapper.preferences;

import com.solar.api.saas.model.preferences.UnitConversionRates;

public class UnitConversionRatesMapper {

    public static UnitConversionRates toUnitConversionRates(UnitConversionRatesDTO unitConversionRatesDTO) {
        return UnitConversionRates.builder()
                .id(unitConversionRatesDTO.getId())
                .baseUnit(unitConversionRatesDTO.getBaseUnit())
                .conversionUnit(unitConversionRatesDTO.getConversionUnit())
                .conversionRate(unitConversionRatesDTO.getConversionRate())
                .conversionFormula(unitConversionRatesDTO.getConversionFormula())
                .build();
    }

    public static UnitConversionRatesDTO toUnitConversionRatesDTO(UnitConversionRates unitConversionRates) {
        if (unitConversionRates == null) {
            return null;
        }
        return UnitConversionRatesDTO.builder()
                .id(unitConversionRates.getId())
                .baseUnit(unitConversionRates.getBaseUnit())
                .conversionUnit(unitConversionRates.getConversionUnit())
                .conversionRate(unitConversionRates.getConversionRate())
                .conversionFormula(unitConversionRates.getConversionFormula())
                .build();
    }
}
