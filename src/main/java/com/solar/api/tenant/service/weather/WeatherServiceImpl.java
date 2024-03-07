package com.solar.api.tenant.service.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.service.alerts.AlertService;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.mapper.tiles.weatherTile.*;
import com.solar.api.tenant.mapper.tiles.weatherTile.GardenDetail;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherReportTile;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherTemplateTile;
import com.solar.api.tenant.mapper.tiles.weatherTile.WeatherWidgetTile;
import com.solar.api.tenant.mapper.weather.ForecastDayDTO;
import com.solar.api.tenant.mapper.weather.WeatherDTO;
import com.solar.api.tenant.mapper.weather.WeatherDataDailyDTO;
import com.solar.api.tenant.mapper.weather.WeatherMapper;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.weather.WeatherData;
import com.solar.api.tenant.model.weather.WeatherDataDaily;
import com.solar.api.tenant.model.weather.WeatherImpactDescription;
import com.solar.api.tenant.repository.TenantConfigRepository;
import com.solar.api.tenant.repository.weather.WeatherDataDailyRepository;
import com.solar.api.tenant.repository.weather.WeatherDataRepository;
import com.solar.api.tenant.repository.weather.WeatherImpactDescriptionRepository;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.solar.api.Constants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.Constants.ALERTS.*;

@Service
public class WeatherServiceImpl implements WeatherService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WeatherDataRepository weatherDataRepository;
    @Autowired
    private WeatherMapper weatherMapper;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    MasterTenantService masterTenantService;
    @Autowired
    TenantConfigService tenantConfigService;
    @Autowired
    private WeatherDataHistoryService weatherDataHistoryService;
    @Autowired
    private TenantConfigRepository tenantConfigRepository;
    @Autowired
    AlertService alertService;
    @Autowired
    Gson gson;
    @Autowired
    private WeatherDataDailyRepository weatherDataDailyRepository;
    @Autowired
    WeatherImpactDescriptionRepository weatherImpactDescriptionRepository;
    @Autowired
    private Utility utility;
    @Autowired
    private StorageService storageService;
    @Value("${app.storage.container}")
    private String devContainer;

    /**
     * Method to get weather data for seven days
     *
     * @param compKey
     * @return
     * @throws Exception
     */
    @Override
    public List<WeatherDTO> getSevenDayDailyWeatherData(Long compKey, List<String> gardenIds) throws Exception {
        LOGGER.info("STORING WEATHER DATA");
        Long days = 7L;
        List<WeatherDTO> weatherDataList = new ArrayList<>();
        List<GardenDetail> data = null;
        masterTenantService.setCurrentDb(compKey);
        Optional<TenantConfig> weatherApi = tenantConfigService.findByParameter(Constants.WEATHER_API.isWeatherApiEnabledParam);
        Optional<TenantConfig> weatherApiKey = tenantConfigService.findByParameter(Constants.WEATHER_API.WeatherApiKey);
        if (Constants.WEATHER_API.isWeatherApiEnabled.equals(weatherApi.get().getText())) {
            if (gardenIds != null && gardenIds.size() > 0) {
                data = weatherDataRepository.findLatAndLogByGardenIds(gardenIds);
            } else {
                data = weatherDataRepository.findLatAndLog();
            }
            for (GardenDetail gardenDetail : data) {
                String response = dataExchange.getWeatherData(gardenDetail, weatherApiKey, days);
                try {
                    WeatherDTO weatherData = objectMapper.readValue(response, WeatherDTO.class);
                    weatherData.setGardenId(gardenDetail.getRefId());
                    weatherDataList.add(weatherData);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Weather data Error: " + e.getMessage());
                }
            }
            saveWeatherData(weatherDataList, gardenIds);
        }
        return weatherDataList;
    }

    /**
     * Method to get temperature in F and C
     */

    @Override
    public WeatherWidgetTile weatherWidgetData(String variantId) {
        WeatherWidgetData weatherWidgetData = null;
        WeatherWidgetTile weatherWidgetTile = null;
        Integer temp = 0;
        try {
            Optional<TenantConfig> weatherTemperature = tenantConfigService.findByParameter(Constants.WEATHER_API.WeatherTemp);
            weatherWidgetData = weatherDataRepository.findWeatherWidgetDataV2(variantId);
            if(weatherWidgetData != null) {
                if (weatherTemperature.isPresent() && weatherTemperature.get().getText().equalsIgnoreCase("C")) {
                    temp = Utility.convertFahrenheitIntoCelsius(weatherWidgetData.getTemperature());
                } else {
                    temp = weatherWidgetData.getTemperature();
                }
                weatherWidgetTile = WeatherMapper.toWeatherWidgetTile(weatherWidgetData, temp, weatherTemperature.get().getText());
            }else {
                LOGGER.error("Weather data not found for variant " + variantId);
            }
        } catch (Exception e) {
            LOGGER.error("ERROR", e);
        }

        return weatherWidgetTile;
    }


    /**
     * Method to Save weather data
     *
     * @param weatherDTOList
     */
    @Transactional
    public void saveWeatherData(List<WeatherDTO> weatherDTOList, List<String> gardenIds) {
        try {
            if (gardenIds != null && gardenIds.size() > 0) {
                weatherDataRepository.deleteAllByGardenIds(gardenIds);
            } else {
                weatherDataRepository.deleteAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Error in deleting weather data: " + e.getMessage());
        }
        for (WeatherDTO weatherDTO : weatherDTOList) {
            List<WeatherData> weatherDataList = weatherMapper.toWeatherData(weatherDTO);
            weatherDataHistoryService.convertWeatherDataToWeatherDataHistory(weatherDataList);
            weatherDataRepository.saveAll(weatherDataList);
        }

    }

    /**
     * Method to get weather data for seven days for template
     *
     * @param
     * @return
     */

    @Override
    public List<WeatherReportTile> findAllGarInfoForWeatherApi() {
        LOGGER.info("findAllGarInfoForWeatherApi");
        List<WeatherReportTile> weatherReportTiles = new ArrayList<>();
        List<WeatherTemplateTile> weatherTemplateTileList = new ArrayList<>();
        final String startDate;
        final String endDate;
        try {
            LocalDate today = LocalDate.now();
            int daysToAdd = DayOfWeek.MONDAY.getValue() - today.getDayOfWeek().getValue();
            LocalDate monday = today.plusDays(daysToAdd);
            List<String> dateList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = monday.plusDays(i);
                dateList.add(currentDate.toString());
            }
            startDate = dateList.get(0);
            endDate = dateList.get(6);
            weatherReportTiles = weatherDataRepository.findAllGarInfoForWeatherApiV2(startDate, endDate);

            weatherReportTiles.forEach(tile -> {
                String value = Utility.getMeasureAsJson(tile.getMpJson(), Constants.RATE_CODES.S_GS);
                tile.setGardenSize(value.trim().isEmpty() ? "0 kWh" : value + " kWh");
                tile.setMpJson(null);
            });
            Map<String, List<WeatherReportTile>> groupedMap = weatherReportTiles.stream().collect(Collectors.groupingBy(WeatherReportTile::getGardenId));
            groupedMap.forEach((key, value) -> {
                long count = value.stream().filter(x -> "Partly Cloudy".equals(x.getWeatherCondition()) || "Sunny".equals(x.getWeatherCondition())).count();
                if (count < 7) {
                    long count1 = value.stream().filter(x -> "Clear".equalsIgnoreCase(x.getWeatherCondition()) || "Sunny".equalsIgnoreCase(x.getWeatherCondition()) || "Mostly Clear".equalsIgnoreCase(x.getWeatherCondition())).count();
                    String impact = count1 >= 5 ? "low" : (count1 >= 3 ? "medium" : "high");
                    WeatherTemplateTile weatherTemplateTile = weatherMapper.toWeatherTemplateTile(value, startDate, endDate, impact);
                    weatherTemplateTileList.add(weatherTemplateTile);
                }
            });

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        for (WeatherTemplateTile weatherTemplateTile : weatherTemplateTileList) {
            triggerEmail(weatherTemplateTile);
        }
        return weatherReportTiles;
    }

    private BaseResponse triggerEmail(WeatherTemplateTile weatherTemplateTile) {
        String emailTOs = "";
        Optional<TenantConfig> tenantConfig = tenantConfigRepository.findByParameter(WEATHER_TOEMAIL_TENANT_CONFIG_PARAM);
        emailTOs = "tos=" + tenantConfig.get().getText().replace(",", "&tos=");
        String weatherTemplateString = null;
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("Key", weatherTemplateTile);
        weatherTemplateString = gson.toJson(weatherTemplateTile);

        return alertService.superSendEmailTrigger(getTenant(), "Weather-Forecast", emailTOs, "",
                "", weatherTemplateString);
    }

    private TenantConfig getTenant() {
        return tenantConfigRepository.findByParameter(WEATHER_EMAIL).orElse(null);
    }

    @Override
    public void getHourlyData(Long compKey, List<String> gardenIds) {
        LOGGER.info("Weather api for hourly is called");
        Long days = 2L;
        List<WeatherDataDailyDTO> weatherDataDailyList = new ArrayList<>();
        Optional<TenantConfig> weatherApi = null;
        Optional<TenantConfig> weatherApiKey = null;
        List<GardenDetail> data = null;
        try {
            weatherApi = tenantConfigService.findByParameter(Constants.WEATHER_API.isWeatherApiEnabledParam);
        } catch (Exception e) {
            LOGGER.error("Error while finding the isWeatherApiEnabled in WeatherServiceImpl", e);
        }
        try {
            weatherApiKey = tenantConfigService.findByParameter(Constants.WEATHER_API.WeatherApiKey);
        } catch (Exception e) {
            LOGGER.error("Error while finding the isWeatherApiKey in WeatherServiceImpl", e);
        }
        if (Constants.WEATHER_API.isWeatherApiEnabled.equals(weatherApi.get().getText())) {
            masterTenantService.setCurrentDb(compKey);
            if (gardenIds != null) {
                data = weatherDataRepository.findLatAndLogByGardenIds(gardenIds);
            } else {
                data = weatherDataRepository.findLatAndLog();
            }
            for (GardenDetail gardenDetail : data) {
                String response = dataExchange.getHourlyData(gardenDetail, weatherApiKey, days);
                WeatherDataDailyDTO weatherData = null;
                try {
                    weatherData = objectMapper.readValue(response, WeatherDataDailyDTO.class);
                    weatherData.setGardenId(gardenDetail.getRefId());
                    weatherDataDailyList.add(weatherData);
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error while converting response in WeatherDataDailyDTO in weatherServiceImpl", e);
                }

            }
            saveWeatherDataForHourly(weatherDataDailyList);
        }
    }

    private void saveWeatherDataForHourly(List<WeatherDataDailyDTO> weatherDataDailyList) {
        for (WeatherDataDailyDTO weatherDataDailyDTO : weatherDataDailyList) {
            try {
//                weatherDataDailyDTO.setCreatedAt(LocalDateTime.now());
                List<WeatherDataDaily> weatherDataDailyList1 = weatherMapper.toWeatherDataDaily(weatherDataDailyDTO);
                List<String> gardenIds = weatherDataDailyList1.stream().map(WeatherDataDaily::getGardenId).collect(Collectors.toList());
                List<WeatherDataDaily> weatherDataDailyFromDb = weatherDataDailyRepository.findByValidTimeLocalAndGardenIdsIn(weatherDataDailyList1.stream().map(m -> m.getValidTimeLocal()).collect(Collectors.toList()), gardenIds);
                for (WeatherDataDaily weatherDataDaily : weatherDataDailyList1) {
                    Optional<WeatherDataDaily> weatherDataDailyOptional = weatherDataDailyFromDb.stream().filter(m -> m.getValidTimeLocal().equalsIgnoreCase(weatherDataDaily.getValidTimeLocal())
                            && m.getGardenId().equalsIgnoreCase(weatherDataDaily.getGardenId())).findFirst();
                    if (weatherDataDailyOptional.isPresent()) {
                        weatherDataDaily.setId(weatherDataDailyOptional.get().getId());
                    }
                }
                weatherDataDailyRepository.saveAll(weatherDataDailyList1);
            } catch (Exception e) {
                LOGGER.error("Error while saving data", e);
            }
        }
    }

    private String getFormattedOffset(String dateTimeString) {
        OffsetDateTime odt = OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(Utility.YEAR_MONTH_DAY_TIME_HOUR_OFFSET));
        return "UTC" + odt.getOffset().toString();
    }

    private String getShortZoneName(String dateTimeString) {
        OffsetDateTime odt = OffsetDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(Utility.YEAR_MONTH_DAY_TIME_HOUR_OFFSET));
        String offset = odt.getOffset().toString();

        Map<String, String> offsetToZoneMap = new HashMap<>();
        offsetToZoneMap.put("-10:00", "HST");
        offsetToZoneMap.put("-09:00", "HDT");
        offsetToZoneMap.put("-08:00", "AKDT");
        offsetToZoneMap.put("-07:00", "PDT");
        offsetToZoneMap.put("-06:00", "MDT");
        offsetToZoneMap.put("-05:00", "CDT");
        offsetToZoneMap.put("-04:00", "EDT");

        return offsetToZoneMap.getOrDefault(offset, "Unknown");
    }

    @Override
    public BaseResponse findWeatherDataByGardenIdsAndDays(Long compkey, String gardenIds, Long numberOfDays) {
        try {
            List<String> gardenIdList = Arrays.stream(gardenIds.split(",")).filter(s -> s != null && !s.equalsIgnoreCase("null") && !s.trim().equalsIgnoreCase("")).collect(Collectors.toList());
            List<WeatherData> weatherDataList = new ArrayList<>();
            try {
                Optional<TenantConfig> weatherApi = tenantConfigService.findByParameter(Constants.WEATHER_API.isWeatherApiEnabledParam);
                if (Constants.WEATHER_API.isWeatherApiEnabled.equals(weatherApi.get().getText())) {
                    if (numberOfDays <= 0) {
                        return BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message("numberOfDays can't be lesser than 1").data(null).build();
                    } else if (numberOfDays <= 2) {
                               getHourlyData(compkey,gardenIdList);
                    } else if (numberOfDays >= 3 && numberOfDays <= 7) {
                               getSevenDayDailyWeatherData(compkey,gardenIdList);
                    } else {
                        return BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message("numberOfDays can't be greater than 7").data(null).build();
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return BaseResponse.builder().code(HttpStatus.PRECONDITION_FAILED.value()).message("Parameters cannot be null").data(null).build();
            }
            return BaseResponse.builder().code(HttpStatus.OK.value()).message("Data added Successfully").data(weatherDataList).build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WeatherDataForWeatherWidget> findWeatherDataForOneAndTwoDays(Long compKey, String gardenIds, Long numberOfDays) {
        List<WeatherDataForWeatherWidget> weatherDataForOneAndTwoDays = new ArrayList<>();
        Optional<TenantConfig> weatherTemperature = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        if(numberOfDays == 1){
            startDate = LocalDate.now();
            endDate = LocalDate.now();
        }else if(numberOfDays == 2){
            startDate = LocalDate.now();
            endDate = startDate.plusDays(1);
        }
        try {
             weatherTemperature = tenantConfigService.findByParameter(Constants.WEATHER_API.WeatherTemp);
        } catch (Exception e) {
            LOGGER.error("Error while getting F OR C from tenant config", e);
        }
        weatherDataForOneAndTwoDays = weatherDataRepository.getWeatherDataForOneAndTwoDays(gardenIds, startDate, endDate);
        for (WeatherDataForWeatherWidget weatherDataForOneAndTwoDaysTile : weatherDataForOneAndTwoDays) {
            if (weatherTemperature.isPresent() && weatherTemperature.get().getText().equalsIgnoreCase("C")) {
                int convertIntoCelcius = Utility.convertFahrenheitIntoCelsius(weatherDataForOneAndTwoDaysTile.getTemperature());
                weatherDataForOneAndTwoDaysTile.setTemperature(convertIntoCelcius);
                weatherDataForOneAndTwoDaysTile.setTempDegree(weatherTemperature.get().getText());
            }else {
                weatherDataForOneAndTwoDaysTile.setTempDegree(weatherTemperature.get().getText());
            }
        }
        return weatherDataForOneAndTwoDays;
    }
    @Override
    public List<WeatherDataForWeatherWidget> findWeatherDataForFiveAndSevenDays(Long compKey, String gardenIds, Long numberOfDays) {
        List<WeatherDataForWeatherWidget> weatherDataForFiveAndSevenDays = new ArrayList<>();
        Optional<TenantConfig> weatherTemperature = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        if(numberOfDays == 5){
            startDate = LocalDate.now();
            endDate = startDate.plusDays(4);
        }else if(numberOfDays == 7){
            startDate = LocalDate.now();
            endDate = startDate.plusDays(6);
        }
        try {
            weatherTemperature = tenantConfigService.findByParameter(Constants.WEATHER_API.WeatherTemp);
        } catch (Exception e) {
            LOGGER.error("Error while getting F OR C from tenant config", e);
        }
        weatherDataForFiveAndSevenDays = weatherDataRepository.getWeatherDataForFiveAndSevenDaysV2(gardenIds , startDate, endDate);
        for (WeatherDataForWeatherWidget weatherDataForWeatherWidget : weatherDataForFiveAndSevenDays) {
            if(weatherTemperature.isPresent() && weatherTemperature.get().getText().equalsIgnoreCase("C")){
                int convertIntoCelcius = Utility.convertFahrenheitIntoCelsius(weatherDataForWeatherWidget.getTemperature());
                weatherDataForWeatherWidget.setTemperature(convertIntoCelcius);
                weatherDataForWeatherWidget.setTempDegree(weatherTemperature.get().getText());
            }else {
                weatherDataForWeatherWidget.setTempDegree(weatherTemperature.get().getText());
            }
        }
        return weatherDataForFiveAndSevenDays;
    }

    /**
     * Method to store weather icon uri in database and deleting from storage
     * @param multipartFiles
     * @param weatherCode
     * @return
     */
    @Override
    public ResponseEntity<?> saveWeatherIconsUri(MultipartFile multipartFiles, String weatherCode){
        String uri = "";
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
        String fileNameWithOutExt = FilenameUtils.removeExtension(multipartFiles.getOriginalFilename());
        String fileExt = FilenameUtils.getExtension(multipartFiles.getOriginalFilename());
        try {
            WeatherImpactDescription weatherImpactDescription = weatherImpactDescriptionRepository.findByweatherCode(weatherCode);
            if (weatherImpactDescription != null) {
                String path = "tenant/" + utility.getCompKey() + "/weatherIcons";
//                if (weatherImpactDescription.getUri() != null && !weatherImpactDescription.getUri().isEmpty()) {
//                    String fileName = weatherImpactDescription.getUri().substring(weatherImpactDescription.getUri().lastIndexOf("/") + 1);
//                    storageService.deleteBlob(devContainer , fileName , path);
//                }
                uri = storageService.storeInContainer(multipartFiles, devContainer, path,
                        fileNameWithOutExt + "_" + timeStamp + "." + fileExt, utility.getCompKey()
                        , false);
                uri = URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
//                weatherImpactDescription.setUri(uri);
                weatherImpactDescriptionRepository.save(weatherImpactDescription);
                return utility.buildSuccessResponse(HttpStatus.OK, "Icon save successfully", uri);
            } else {
                return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "Weather Code not found");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}