package com.solar.api.tenant.controller.v1.weather;

import com.microsoft.azure.storage.StorageException;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.extended.document.DocuLibraryDTO;
import com.solar.api.tenant.mapper.tiles.weatherTile.*;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherDataForWeatherWidget;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherWidgetTile;
import com.solar.api.tenant.model.weather.WeatherImpactDescription;
import com.solar.api.tenant.service.weather.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;


@CrossOrigin
@RestController("WeatherController")
@RequestMapping(value = "/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;
    @Autowired
    @Lazy
    private BatchService batchService;

    /**
     * getSevenDayDailyWeatherData
     *
     * @param compKey
     * @throws Exception
     */
    @GetMapping("/getSevenDayDailyWeatherData")
    public void getSevenDayDailyWeatherData(@RequestHeader("Comp-Key") Long compKey) throws Exception {
        weatherService.getSevenDayDailyWeatherData(compKey,null);
    }

    /**
     * Get weather data
     *
     * @param
     */
    @GetMapping("/weather-report")
    public void getWeatherReport() {
        weatherService.findAllGarInfoForWeatherApi();
    }

    @GetMapping("/hourlyData")
    public void getHourlyData(@RequestHeader("Comp-Key") Long compKey) {
        weatherService.getHourlyData(compKey , null);
    }

    @GetMapping("/weather-data")
    public BaseResponse getWeatherData(@RequestHeader("Comp-Key") Long compKey,
                                       @RequestParam("gardenIds") String gardenIds,
                                       @RequestParam("numberOfDays") Long numberOfDays) {

        return weatherService.findWeatherDataByGardenIdsAndDays(compKey, gardenIds, numberOfDays);
    }

    @GetMapping("/getWeatherDataForOneAndTwoDays")
    public List<WeatherDataForWeatherWidget> getWeatherDataForOneAndTwoDays(@RequestHeader("Comp-Key") Long compKey,
                                                                            @RequestParam("gardenIds") String gardenIds,
                                                                            @RequestParam("numberOfDays") Long numberOfDays){
        return weatherService.findWeatherDataForOneAndTwoDays(compKey,gardenIds,numberOfDays);
    }

    @GetMapping("/weather-widget-data")
    public WeatherWidgetTile getWeatherWidgetData(@RequestParam("gardenIds") String gardenIds) {
        return weatherService.weatherWidgetData(gardenIds);
    }

    @GetMapping("/getWeatherDataForFiveAndSevenDays")
    public List<WeatherDataForWeatherWidget> getWeatherDataForFiveAndSevenDays(@RequestHeader("Comp-Key") Long compKey,
                                                                               @RequestParam("gardenIds") String gardenIds,
                                                                               @RequestParam("numberOfDays") Long numberOfDays){
        return weatherService.findWeatherDataForFiveAndSevenDays(compKey,gardenIds,numberOfDays);
    }
    @PostMapping("/weatherIconsUri")
    public ResponseEntity<?> addWeatherIconsUri(@RequestParam(value = "multipartFile") MultipartFile multipartFiles,
                                                                       @RequestParam(value = "weatherCode") String weatherCode)
            throws StorageException, IOException, URISyntaxException {
        return weatherService.saveWeatherIconsUri(multipartFiles , weatherCode);

    }
}
