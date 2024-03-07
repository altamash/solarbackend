package com.solar.api.tenant.service.weather;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherDataForWeatherWidget;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherReportTile;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherWidgetTile;
import com.solar.api.tenant.mapper.weather.WeatherDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface WeatherService {

    //List<WeatherDTO> getSevenDayDailyWeatherData(Long compKey) throws Exception;

    List<WeatherDTO> getSevenDayDailyWeatherData(Long compKey, List<String> gardenIds) throws Exception;

    List<WeatherReportTile> findAllGarInfoForWeatherApi();

    void getHourlyData(Long compKey, List<String> gardenIds);

    BaseResponse findWeatherDataByGardenIdsAndDays(Long compkey, String gardenIds, Long numberOfDays);

    List<WeatherDataForWeatherWidget> findWeatherDataForOneAndTwoDays(Long compKey, String gardenIds, Long numberOfDays);

    WeatherWidgetTile weatherWidgetData(String gardenIds);

    List<WeatherDataForWeatherWidget> findWeatherDataForFiveAndSevenDays(Long compKey, String gardenIds, Long numberOfDays);

    ResponseEntity<?> saveWeatherIconsUri(MultipartFile multipartFiles, String weatherCode) throws URISyntaxException, IOException, StorageException;
}
