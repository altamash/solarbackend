package com.solar.api.saas.repository;

import com.solar.api.saas.model.preferences.UnitConversionRates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitConversionRatesRepository extends JpaRepository<UnitConversionRates, Long> {
}
