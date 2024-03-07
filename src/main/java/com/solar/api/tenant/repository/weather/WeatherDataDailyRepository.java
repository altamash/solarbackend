package com.solar.api.tenant.repository.weather;

import com.solar.api.tenant.model.weather.WeatherDataDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherDataDailyRepository extends JpaRepository<WeatherDataDaily , Long> {

    @Query("select weatherDataDaily from WeatherDataDaily weatherDataDaily where weatherDataDaily.validTimeLocal in (:validTimeLocal) and weatherDataDaily.gardenId in (:gardenIds)")
    List<WeatherDataDaily>  findByValidTimeLocalAndGardenIdsIn(@Param("validTimeLocal") List<String> validTimeLocal,@Param("gardenIds") List<String> gardenIds);
}
