package com.solar.api.tenant.service.weather;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.model.weather.WeatherApiHistory;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface WeatherApiHistoryService {
    WeatherApiHistory addWeatherApiHistory(WeatherApiHistory history);

    BaseResponse getAllWeatherApiHistory();


}
