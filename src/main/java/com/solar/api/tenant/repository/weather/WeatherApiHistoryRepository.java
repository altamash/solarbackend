package com.solar.api.tenant.repository.weather;

import com.solar.api.tenant.model.weather.WeatherApiHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WeatherApiHistoryRepository extends JpaRepository<WeatherApiHistory, Long> {

}
