package com.solar.api.tenant.service.weather;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.tenant.model.weather.WeatherApiHistory;
import com.solar.api.tenant.repository.weather.WeatherApiHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherApiHistoryServiceImpl implements WeatherApiHistoryService {

    @Autowired
    private WeatherApiHistoryRepository weatherApiHistoryRepository;

    @Override
    public WeatherApiHistory addWeatherApiHistory(WeatherApiHistory history) {
        return weatherApiHistoryRepository.save(history);
    }

    @Override
    public BaseResponse getAllWeatherApiHistory() {
       return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(weatherApiHistoryRepository.findAll()).build();

    }


}
