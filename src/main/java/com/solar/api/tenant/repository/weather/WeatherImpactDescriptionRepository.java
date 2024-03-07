package com.solar.api.tenant.repository.weather;

import com.solar.api.tenant.model.weather.WeatherImpactDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository


public interface WeatherImpactDescriptionRepository extends JpaRepository<WeatherImpactDescription, Long> {

    WeatherImpactDescription findByweatherCode(String weatherCode);
}
