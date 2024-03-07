package com.solar.api.tenant.repository.weather;

import com.solar.api.tenant.model.weather.WeatherData;
import com.solar.api.tenant.model.weather.WeatherDataHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeatherDataHistoryRepository extends JpaRepository<WeatherDataHistory, Long> {

    @Query("select COUNT(*) from WeatherDataHistory ")
    Long countRecords();

    @Query("select wdh from WeatherDataHistory wdh where wdh.gardenId in (:gardenIds) and wdh.validTimeLocal in (:validTimeLocal)")
            List<WeatherDataHistory> findByGardenIdAndValidTimeLocal(@Param("gardenIds") List<String> gardenIds, @Param("validTimeLocal") List<String> validTimeLocal);
}
