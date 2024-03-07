package com.solar.api.tenant.service.weather;

import com.solar.api.tenant.mapper.weather.WeatherMapper;
import com.solar.api.tenant.model.weather.WeatherData;
import com.solar.api.tenant.model.weather.WeatherDataHistory;
import com.solar.api.tenant.repository.weather.WeatherDataHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherDataHistoryServiceImpl implements WeatherDataHistoryService {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private WeatherDataHistoryRepository weatherDataHistoryRepository;

    @Override
    public List<WeatherDataHistory> saveAllWeatherDataHistory(List<WeatherDataHistory> weatherDataHistoryList) {
        return weatherDataHistoryRepository.saveAll(weatherDataHistoryList);
    }

    @Override
    public List<WeatherDataHistory> convertWeatherDataToWeatherDataHistory(List<WeatherData> weatherDataList) {

        List<WeatherDataHistory> resultWeatherDataHistoryList = new ArrayList<>();
        try {
            List<WeatherDataHistory> weatherDataHistoryList = WeatherMapper.toWeatherDataHistorys(weatherDataList);
            List<String> gardenIds = weatherDataHistoryList.stream().map(WeatherDataHistory::getGardenId).distinct().collect(Collectors.toList());
            List<String> validTimeLocal = weatherDataHistoryList.stream().map(WeatherDataHistory::getValidTimeLocal).distinct().collect(Collectors.toList());
            List<WeatherDataHistory> dbResult = weatherDataHistoryRepository.findByGardenIdAndValidTimeLocal(gardenIds, validTimeLocal);
            if (dbResult.size() != weatherDataHistoryList.size()) {
                resultWeatherDataHistoryList = saveAllWeatherDataHistory(weatherDataHistoryList);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while converting weather data to weather data history", e);
        }
        return resultWeatherDataHistoryList;
    }

}
