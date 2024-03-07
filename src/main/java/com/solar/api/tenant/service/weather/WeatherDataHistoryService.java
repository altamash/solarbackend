package com.solar.api.tenant.service.weather;

import com.solar.api.tenant.model.weather.WeatherData;
import com.solar.api.tenant.model.weather.WeatherDataHistory;

import java.util.List;

public interface WeatherDataHistoryService {
    List<WeatherDataHistory> saveAllWeatherDataHistory(List<WeatherDataHistory> weatherDataHistoryList);
    List<WeatherDataHistory> convertWeatherDataToWeatherDataHistory(List<WeatherData> weatherDataList);
}
