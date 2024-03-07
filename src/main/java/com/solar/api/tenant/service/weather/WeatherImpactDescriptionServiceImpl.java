package com.solar.api.tenant.service.weather;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;

import com.solar.api.tenant.model.weather.WeatherImpactDescription;

import com.solar.api.tenant.repository.weather.WeatherImpactDescriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class WeatherImpactDescriptionServiceImpl implements WeatherImpactDescriptionService {

    @Autowired
    private WeatherImpactDescriptionRepository weatherImpactDescriptionRepository;

    @Override
    public WeatherImpactDescription addWeatherImpactDescription(WeatherImpactDescription description) {
        return weatherImpactDescriptionRepository.save(description);
    }

    @Override
    public BaseResponse getAllWeatherImpactDescription() {
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(weatherImpactDescriptionRepository.findAll()).build();

    }

}
