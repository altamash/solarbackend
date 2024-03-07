package com.solar.api.tenant.service.weather;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.model.weather.WeatherApiHistory;
import com.solar.api.tenant.model.weather.WeatherImpactDescription;

public interface WeatherImpactDescriptionService {
    WeatherImpactDescription addWeatherImpactDescription(WeatherImpactDescription description);
    BaseResponse getAllWeatherImpactDescription();
}
