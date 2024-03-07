package com.solar.api.tenant.mapper.weather;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.cutomer.CustomerDetailDTO;
import com.solar.api.tenant.mapper.tiles.weatherTile.*;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.weather.WeatherData;
import com.solar.api.tenant.model.weather.WeatherDataDaily;
import com.solar.api.tenant.model.weather.WeatherDataHistory;
import com.solar.api.tenant.service.weather.WeatherServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class WeatherMapper {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    int counter = 0;

    @Autowired
    private WeatherServiceImpl weatherService;

        public List<WeatherData> toWeatherData(WeatherDTO dto) {
        List<WeatherData> weatherData = new ArrayList<>();

        for(ForecastDayDTO forecastDayDTO: dto.getForecast().getForecastday()){

            WeatherData weatherData1 = convertIntoWeatherData(
                    null, null, null, null,
                    forecastDayDTO.getAstro().getMoonPhase() ,
                    null, null, forecastDayDTO.getAstro().getMoonrise(), forecastDayDTO.getAstro().getMoonset(), null,
                    null, null, forecastDayDTO.getAstro().getSunrise(), forecastDayDTO.getAstro().getSunset(), (int) forecastDayDTO.getDay().getMaxtemp_c(),
                    (int) forecastDayDTO.getDay().getMintemp_c(), getZoneName(forecastDayDTO.getDate(), dto.getLocation().getTz_id()), forecastDayDTO.getDate(),
                    dto.getGardenId() , dto.getCurrent().getCloud(), null, null, null, String.valueOf(forecastDayDTO.getDay().getTotalprecip_mm()),
                    null, null, null, forecastDayDTO.getDay().getAvghumidity(), null, (int )forecastDayDTO.getDay().getAvgtemp_c(), (int) forecastDayDTO.getDay().getAvgtemp_c(),
                    null, null, null, String.valueOf(forecastDayDTO.getDay().getUv()), null, null,
                    (int) forecastDayDTO.getDay().getMaxwind_kph(), null, forecastDayDTO.getDay().getCondition().getText(), forecastDayDTO.getAstro().getSunrise(),
                    forecastDayDTO.getAstro().getSunset(),null, null
            );
            weatherData.add(weatherData1);
        }

        return weatherData;
    }

//    public List<WeatherData> toWeatherData(WeatherDTO dto) {
////        List<DayPartDTO> daypart = dto.getDaypart();
//        List<WeatherData> weatherData = new ArrayList<>();
//        for (int i = 0; i < 14; i++) {
//
//            if (i % 2 == 0 && i != 0) {
//                counter++;
//            }
//
//            WeatherData weatherData1 = convertIntoWeatherData(
//                    i % 2 == 0 ? (dto.getCalendarDayTemperatureMax().get(counter) != null && !dto.getCalendarDayTemperatureMax().isEmpty() ? dto.getCalendarDayTemperatureMax().get(counter) : null) : (dto.getCalendarDayTemperatureMax().get(counter) != null && !dto.getCalendarDayTemperatureMax().isEmpty() ? dto.getCalendarDayTemperatureMax().get(counter) : null),
//                    i % 2 == 0 ? (dto.getCalendarDayTemperatureMin().get(counter) != null && !dto.getCalendarDayTemperatureMin().isEmpty() ? dto.getCalendarDayTemperatureMin().get(counter) : null) : (dto.getCalendarDayTemperatureMin().get(counter) != null && !dto.getCalendarDayTemperatureMin().isEmpty() ? dto.getCalendarDayTemperatureMin().get(counter) : null),
//                    i % 2 == 0 ? (dto.getDayOfWeek().get(counter) != null && !dto.getDayOfWeek().isEmpty() ? dto.getDayOfWeek().get(counter) : null ) : (dto.getDayOfWeek().get(counter) != null && !dto.getDayOfWeek().isEmpty() ? dto.getDayOfWeek().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getExpirationTimeUtc().get(counter) != null && !dto.getExpirationTimeUtc().isEmpty() ? dto.getExpirationTimeUtc().get(counter).toString() : null) : (dto.getExpirationTimeUtc().get(counter) != null && !dto.getExpirationTimeUtc().isEmpty() ? dto.getExpirationTimeUtc().get(counter).toString() : null),
//                    i % 2 == 0 ? (dto.getMoonPhase().get(counter) != null && !dto.getMoonPhase().isEmpty() ? dto.getMoonPhase().get(counter) : null ) : (dto.getMoonPhase().get(counter) != null && !dto.getMoonPhase().isEmpty() ? dto.getMoonPhase().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getMoonPhaseCode().get(counter) != null && !dto.getMoonPhaseCode().isEmpty() ? dto.getMoonPhaseCode().get(counter) : null ) : (dto.getMoonPhaseCode().get(counter) != null && !dto.getMoonPhaseCode().isEmpty() ? dto.getMoonPhaseCode().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getMoonPhaseDay().get(counter) != null && !dto.getMoonPhaseDay().isEmpty() ? dto.getMoonPhaseDay().get(counter) : null ) : (dto.getMoonPhaseDay().get(counter) != null && !dto.getMoonPhaseDay().isEmpty() ? dto.getMoonPhaseDay().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getMoonriseTimeUtc().get(counter) != null && !dto.getMoonriseTimeUtc().isEmpty() ? dto.getMoonriseTimeUtc().get(counter).toString() : null) : (dto.getMoonriseTimeUtc().get(counter) != null && !dto.getMoonriseTimeUtc().isEmpty() ? dto.getMoonriseTimeUtc().get(counter).toString() : null),
//                    i % 2 == 0 ? (dto.getMoonsetTimeUtc().get(counter) != null && !dto.getMoonsetTimeUtc().isEmpty() ? dto.getMoonsetTimeUtc().get(counter).toString() : null) : (dto.getMoonsetTimeUtc().get(counter) != null && !dto.getMoonsetTimeUtc().isEmpty() ? dto.getMoonsetTimeUtc().get(counter).toString() : null),
//                    i % 2 == 0 ? (dto.getNarrative().get(counter) != null && !dto.getNarrative().isEmpty()? dto.getNarrative().get(counter) : null ) : (dto.getNarrative().get(counter) != null && !dto.getNarrative().isEmpty()? dto.getNarrative().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getQpf().get(counter) != null && !dto.getQpf().isEmpty() ? dto.getQpf().get(counter) : null ) : (dto.getQpf().get(counter) != null && !dto.getQpf().isEmpty() ? dto.getQpf().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getQpfSnow().get(counter) != null && !dto.getQpfSnow().isEmpty() ? dto.getQpfSnow().get(counter) : null ): (dto.getQpfSnow().get(counter) != null && !dto.getQpfSnow().isEmpty() ? dto.getQpfSnow().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getSunriseTimeUtc().get(counter) != null && !dto.getSunriseTimeUtc().isEmpty() ? dto.getSunriseTimeUtc().get(counter).toString() : null) : (dto.getSunriseTimeUtc().get(counter) != null && !dto.getSunriseTimeUtc().isEmpty() ? dto.getSunriseTimeUtc().get(counter).toString() : null),
//                    i % 2 == 0 ? (dto.getSunsetTimeUtc().get(counter) != null && !dto.getSunsetTimeUtc().isEmpty() ? dto.getSunsetTimeUtc().get(counter).toString() : null) : (dto.getSunsetTimeUtc().get(counter) != null && !dto.getSunsetTimeUtc().isEmpty() ? dto.getSunsetTimeUtc().get(counter).toString() : null),
//                    i % 2 == 0 ? (dto.getTemperatureMax().get(counter) != null && !dto.getTemperatureMax().isEmpty() ? dto.getTemperatureMax().get(counter) : null ) : (dto.getTemperatureMax().get(counter) != null && !dto.getTemperatureMax().isEmpty() ? dto.getTemperatureMax().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getTemperatureMin().get(counter) != null && !dto.getTemperatureMin().isEmpty() ? dto.getTemperatureMin().get(counter) : null ) : (dto.getTemperatureMin().get(counter) != null && !dto.getTemperatureMin().isEmpty() ? dto.getTemperatureMin().get(counter) : null ),
//                    i % 2 == 0 ? (dto.getValidTimeUtc().get(counter) != null && !dto.getValidTimeUtc().isEmpty() ? dto.getValidTimeUtc().get(counter).toString() : null) : (dto.getValidTimeUtc().get(counter) != null && !dto.getValidTimeUtc().isEmpty() ? dto.getValidTimeUtc().get(counter).toString() : null),
//                    dto.getValidTimeLocal().get(counter) != null && !dto.getValidTimeLocal().isEmpty() ? dto.getValidTimeLocal().get(counter).toString() : null,
//                    dto.getGardenId(),
//                    daypart.get(0).getCloudCover().get(i) == null ? null : daypart.get(0).getCloudCover().get(i),
//                    daypart.get(0).getDayOrNight().get(i) == null ? null : daypart.get(0).getDayOrNight().get(i),
//                    daypart.get(0).getDaypartName().get(i) == null ? null : daypart.get(0).getDaypartName().get(i),
//                    daypart.get(0).getNarrative().get(i) == null ? null : daypart.get(0).getNarrative().get(i),
//                    daypart.get(0).getPrecipChance().get(i) == null ? null : daypart.get(0).getPrecipChance().get(i),
//                    daypart.get(0).getPrecipType().get(i) == null ? null : daypart.get(0).getPrecipType().get(i),
//                    daypart.get(0).getQpf().get(i) == null ? null : daypart.get(0).getQpf().get(i),
//                    daypart.get(0).getQpfSnow().get(i) == null ? null : daypart.get(0).getQpfSnow().get(i),
//                    daypart.get(0).getRelativeHumidity().get(i) == null ? null : daypart.get(0).getRelativeHumidity().get(i),
//                    daypart.get(0).getSnowRange().get(i) == null ? null : daypart.get(0).getSnowRange().get(i),
//                    daypart.get(0).getTemperature().get(i) == null ? null : daypart.get(0).getTemperature().get(i),
//                    daypart.get(0).getTemperatureHeatIndex().get(i) == null ? null : daypart.get(0).getTemperatureHeatIndex().get(i),
//                    daypart.get(0).getTemperatureWindChill().get(i) == null ? null : daypart.get(0).getTemperatureWindChill().get(i),
//                    daypart.get(0).getThunderCategory().get(i) == null ? null : daypart.get(0).getThunderCategory().get(i),
//                    daypart.get(0).getThunderIndex().get(i) == null ? null : daypart.get(0).getThunderIndex().get(i),
//                    daypart.get(0).getUvDescription().get(i) == null ? null : daypart.get(0).getUvDescription().get(i),
//                    daypart.get(0).getWindDirection().get(i) == null ? null : daypart.get(0).getWindDirection().get(i),
//                    daypart.get(0).getWindPhrase().get(i) == null ? null : daypart.get(0).getWindPhrase().get(i),
//                    daypart.get(0).getWindSpeed().get(i) == null ? null : daypart.get(0).getWindSpeed().get(i),
//                    daypart.get(0).getWxPhraseLong().get(i) == null ? null : daypart.get(0).getWxPhraseLong().get(i),
//                    daypart.get(0).getWxPhraseShort().get(i) == null ? null : daypart.get(0).getWxPhraseShort().get(i),
//                    dto.getSunriseTimeLocal().get(counter),
//                    dto.getSunsetTimeLocal().get(counter),
//                    dto.getTimeZone(),
//                    dto.getTimeZoneCode());
//            weatherData.add(weatherData1);
//        }
//        counter = 0;
//
//                     }
//        return weatherData;
//    }

    private WeatherData convertIntoWeatherData(Integer calenderMaxTemperature, Integer calenderMinTemperature, String dayOfWeek, String expirationTimeUtc,
                                               String moonPhase, String moonPhaseCode, Integer moonPhaseDay, String moonriseTimeUtc, String moonsetTimeUtc,
                                               String narrative, Double qpf, Double qpfSnow, String sunriseTimeUtc, String sunsetTimeUtc, Integer temperatureMax,
                                               Integer temperatureMin, String validTimeUtc, String validTimeLocal, String gardenId, Integer cloudCover, String dayOrNight,
                                               String daypartName, String narrative2, String precipChance, String precipType, Double qpf2, Double qpfSnow2,
                                               Double relativeHumidity, String snowRange, Integer temperature, Integer temperatureHeatIndex, Integer temperatureWindChill,
                                               String thunderCategory, Integer thunderIndex, String uvDescription, Integer windDirection, String windPhrase,
                                               Integer windSpeed, String wxPhraseLong, String wxPhraseShort, String sunriseTimeLocal, String sunsetTimeLocal, String timeZone, String timeZoneCode) {
        try {
            return WeatherData.builder()
                    .calendarDayTemperatureMax(calenderMaxTemperature)
                    .calendarDayTemperatureMin(calenderMinTemperature)
                    .dayOfWeek(dayOfWeek)
                    .expirationTimeUtc(expirationTimeUtc)
                    .moonPhase(moonPhase)
                    .moonPhaseCode(moonPhaseCode)
                    .moonPhaseDay(moonPhaseDay)
                    .moonriseTimeUtc(moonriseTimeUtc)
                    .moonsetTimeUtc(moonsetTimeUtc)
                    .narrative(narrative)
                    .qpf(qpf)
                    .qpfSnow(qpfSnow)
                    .sunriseTimeUtc(sunriseTimeUtc)
                    .sunsetTimeUtc(sunsetTimeUtc)
                    .temperatureMax(temperatureMax)
                    .temperatureMin(temperatureMin)
                    .validTimeUtc(validTimeUtc)
                    .validTimeLocal(validTimeLocal)
                    .gardenId(gardenId)
                    .cloudCover(cloudCover)
                    .dayOrNight(String.valueOf(dayOrNight))
                    .daypartName(daypartName)
                    .narrative2(narrative2)
                    .precipChance(precipChance)
                    .precipType(precipType)
                    .qpf2(qpf2)
                    .qpfSnow2(qpfSnow2)
                    .relativeHumidity(relativeHumidity)
                    .snowRange(String.valueOf(snowRange))
                    .temperature(temperature)
                    .temperatureHeatIndex(temperatureHeatIndex)
                    .temperatureWindChill(temperatureWindChill)
                    .thunderCategory(String.valueOf(thunderCategory))
                    .thunderIndex(thunderIndex)
                    .uvDescription(String.valueOf(uvDescription))
                    .windDirection(windDirection)
                    .windPhrase(windPhrase)
                    .windSpeed(windSpeed)
                    .wxPhraseLong(wxPhraseLong)
                    .wxPhraseShort(wxPhraseShort)
                    .sunriseTimeLocal(sunriseTimeLocal)
                    .sunsetTimeLocal(sunsetTimeLocal)
                    .timeZone(timeZone)
                    .timeZoneCode(timeZoneCode)
                    .build();
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }


    public static WeatherDataHistory toWeatherDataHistory(WeatherData weatherData) {

        return WeatherDataHistory.builder()
                .id(weatherData.getId())
                .calendarDayTemperatureMax(weatherData.getCalendarDayTemperatureMax())
                .calendarDayTemperatureMin(weatherData.getCalendarDayTemperatureMin())
                .dayOfWeek(weatherData.getDayOfWeek())
                .expirationTimeUtc(weatherData.getExpirationTimeUtc())
                .moonPhase(weatherData.getMoonPhase())
                .moonPhaseCode(weatherData.getMoonPhaseCode())
                .moonPhaseDay(weatherData.getMoonPhaseDay())
                .moonriseTimeUtc(weatherData.getMoonriseTimeUtc())
                .moonsetTimeUtc(weatherData.getMoonsetTimeUtc())
                .narrative(weatherData.getNarrative())
                .qpf(weatherData.getQpf())
                .qpfSnow(weatherData.getQpfSnow())
                .sunriseTimeUtc(weatherData.getSunriseTimeUtc())
                .sunsetTimeUtc(weatherData.getSunsetTimeUtc())
                .temperatureMax(weatherData.getTemperatureMax())
                .temperatureMin(weatherData.getTemperatureMin())
                .validTimeUtc(weatherData.getValidTimeUtc())
                .validTimeLocal(weatherData.getValidTimeLocal())
                .cloudCover(weatherData.getCloudCover())
                .dayOrNight(weatherData.getDayOrNight())
                .daypartName(weatherData.getDaypartName())
                .narrative2(weatherData.getNarrative2())
                .precipChance(weatherData.getPrecipChance())
                .precipType(weatherData.getPrecipType())
                .qpf2(weatherData.getQpf2())
                .qpfSnow2(weatherData.getQpfSnow2())
                .relativeHumidity(weatherData.getRelativeHumidity())
                .snowRange(weatherData.getSnowRange())
                .temperature(weatherData.getTemperature())
                .temperatureHeatIndex(weatherData.getTemperatureHeatIndex())
                .temperatureWindChill(weatherData.getTemperatureWindChill())
                .thunderCategory(weatherData.getThunderCategory())
                .thunderIndex(weatherData.getThunderIndex())
                .uvDescription(weatherData.getUvDescription())
                .windDirection(weatherData.getWindDirection())
                .windPhrase(weatherData.getWindPhrase())
                .windSpeed(weatherData.getWindSpeed())
                .wxPhraseLong(weatherData.getWxPhraseLong())
                .wxPhraseShort(weatherData.getWxPhraseShort())
                .gardenId(weatherData.getGardenId())
                .sunriseTimeLocal(weatherData.getSunriseTimeLocal())
                .sunsetTimeLocal(weatherData.getSunsetTimeLocal())
                .timeZone(weatherData.getTimeZone())
                .timeZoneCode(weatherData.getTimeZoneCode())
                .build();
    }

    public static List<WeatherDataHistory> toWeatherDataHistorys(List<WeatherData> weatherData) {
        return weatherData.stream().map(bc -> toWeatherDataHistory(bc)).collect(Collectors.toList());
    }

    public static WeatherTemplateTile toWeatherTemplateTile(List<WeatherReportTile> weatherReportTile, String startDate, String endDate, String impact) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Utility.WEATHER_DATA_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcDateTimeString = dateFormat.format(currentDate);
        List<AlertDetails> alertDetailsList = mapWeatherDataToAlertDetail(weatherReportTile);
        List<AlertDetails> sortedList = alertDetailsList.stream()
                .sorted(Comparator.comparing(x -> {
                    try {
                        return dateFormat.parse(x.getDate());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .collect(Collectors.toList());
        return WeatherTemplateTile.builder()
                .desc("Following is the weather forecast for the upcoming week and its impact on production.")
                .name(weatherReportTile.get(0).getGardenName())
                .size(weatherReportTile.get(0).getGardenSize())
                .type("Low performance")
                .impact("Low power production expected")
                .category("Site weather impact alert")
                .duration(startDate + "-" + endDate)
                .loc(weatherReportTile.get(0).getPhysicalLocation())
                .platform(weatherReportTile.get(0).getMonPlatform())
                .performance("Performance")
                .report_date(utcDateTimeString)
                .alert_impact(impact)
                .alert_details(sortedList)
                .build();
    }

    public static List<AlertDetails> mapWeatherDataToAlertDetail(List<WeatherReportTile> weatherReportTileList) {
        return weatherReportTileList.stream().map(weatherReportTile -> toWeatherAlertDetails(weatherReportTile)).collect(Collectors.toList());
    }

    private static AlertDetails toWeatherAlertDetails(WeatherReportTile weatherReportTile) {
        return AlertDetails.builder()
                .date(weatherReportTile.getDay())
                .time("12:00 AM- 12:00 PM") // Extract time from 12am to 12pm
                .weather(weatherReportTile.getWeatherCondition())
                .production(weatherReportTile.getEstProduction())
                .build();
    }
    public List<WeatherDataDaily> toWeatherDataDaily(WeatherDataDailyDTO weatherDataDailyDTO) {
        List<WeatherDataDaily> weatherDataDailyList = new ArrayList<>();
        for ( ForecastDayDTO  weatherDataDaily : weatherDataDailyDTO.getForecast().getForecastday()) {
            for (HourDTO hourDTO : weatherDataDaily.getHour()) {
                try {

                WeatherDataDaily weatherDataDaily1 = convertIntoWeatherDataDaily(
                        null,null,hourDTO.getCloud(),null,null,null,null,
                        null,(int) hourDTO.getPrecip_mm(),null,null,
                        null,null, hourDTO.getHumidity(),(int) hourDTO.getTemp_c(),(int) hourDTO.getDewpoint_c(),
                        null,(int) hourDTO.getHeatindex_c(),(int) hourDTO.getWindchill_c(),
                        null,(int) hourDTO.getUv(), hourDTO.getTime(),
                        null,(float) hourDTO.getVis_km(),null,null,(float) hourDTO.getGust_mph(),
                        (int) hourDTO.getWind_kph(), hourDTO.getCondition().getText(), hourDTO.getCondition().getText(),null,
                        weatherDataDailyDTO.getGardenId()
                        );
                weatherDataDailyList.add(weatherDataDaily1);
                } catch (Exception e) {
                    LOGGER.error("Error while converting into weather data daily in weatherMapper", e);
                }
            }
        }
        return weatherDataDailyList;
    }
//    public List<WeatherDataDaily> toWeatherDataDaily(WeatherDataDailyDTO weatherDataDailyDTO) {
//        List<WeatherDataDaily> weatherDataDailyList = new ArrayList<>();
//        for (int i = 0; i <= 47; i++) {
//            try {
//                WeatherDataDaily weatherDataDaily1 = convertIntoWeatherDataDaily(
//                        (weatherDataDailyDTO.getSourceType() != null && !weatherDataDailyDTO.getSourceType().isEmpty()) ? weatherDataDailyDTO.getSourceType() : null,
//                        weatherDataDailyDTO.getCreatedAt() != null ? weatherDataDailyDTO.getCreatedAt() : null,
//                        (weatherDataDailyDTO.getCloudCover().get(i) != null && !weatherDataDailyDTO.getCloudCover().isEmpty()) ? weatherDataDailyDTO.getCloudCover().get(i) : null,
//                        (weatherDataDailyDTO.getDayOfWeek().get(i) != null && !weatherDataDailyDTO.getDayOfWeek().isEmpty()) ? weatherDataDailyDTO.getDayOfWeek().get(i) : null,
//                        (weatherDataDailyDTO.getDayOrNight().get(i) != null && !weatherDataDailyDTO.getDayOrNight().isEmpty()) ? weatherDataDailyDTO.getDayOrNight().get(i) : null,
//                        (weatherDataDailyDTO.getExpirationTimeUtc().get(i) != null && !weatherDataDailyDTO.getExpirationTimeUtc().isEmpty()) ? weatherDataDailyDTO.getExpirationTimeUtc().get(i).toString() : null,
//                        (weatherDataDailyDTO.getIconCode().get(i) != null && !weatherDataDailyDTO.getIconCode().isEmpty()) ? weatherDataDailyDTO.getIconCode().get(i) : null,
//                        (weatherDataDailyDTO.getIconCodeExtend().get(i) != null && !weatherDataDailyDTO.getIconCodeExtend().isEmpty()) ? weatherDataDailyDTO.getIconCodeExtend().get(i) : null,
//                        (weatherDataDailyDTO.getPrecipChance().get(i) != null && !weatherDataDailyDTO.getPrecipChance().isEmpty()) ? weatherDataDailyDTO.getPrecipChance().get(i) : null,
//                        (weatherDataDailyDTO.getPrecipType().get(i) != null && !weatherDataDailyDTO.getPrecipType().isEmpty()) ? weatherDataDailyDTO.getPrecipType().get(i) : null,
//                        (weatherDataDailyDTO.getPressureMeanSeaLevel().get(i) != null && !weatherDataDailyDTO.getPressureMeanSeaLevel().isEmpty()) ? weatherDataDailyDTO.getPressureMeanSeaLevel().get(i) : null,
//                        (weatherDataDailyDTO.getQpf().get(i) != null && !weatherDataDailyDTO.getQpf().isEmpty()) ? weatherDataDailyDTO.getQpf().get(i) : null,
//                        (weatherDataDailyDTO.getQpfSnow().get(i) != null && !weatherDataDailyDTO.getQpfSnow().isEmpty()) ? weatherDataDailyDTO.getQpfSnow().get(i) : null,
//                        (weatherDataDailyDTO.getRelativeHumidity().get(i) != null && !weatherDataDailyDTO.getRelativeHumidity().isEmpty()) ? weatherDataDailyDTO.getRelativeHumidity().get(i) : null,
//                        (weatherDataDailyDTO.getTemperature().get(i) != null && !weatherDataDailyDTO.getTemperature().isEmpty()) ? weatherDataDailyDTO.getTemperature().get(i) : null,
//                        (weatherDataDailyDTO.getTemperatureDewPoint().get(i) != null && !weatherDataDailyDTO.getTemperatureDewPoint().isEmpty()) ? weatherDataDailyDTO.getTemperatureDewPoint().get(i) : null,
//                        (weatherDataDailyDTO.getTemperatureFeelsLike().get(i) != null && !weatherDataDailyDTO.getTemperatureFeelsLike().isEmpty()) ? weatherDataDailyDTO.getTemperatureFeelsLike().get(i) : null,
//                        (weatherDataDailyDTO.getTemperatureHeatIndex().get(i) != null && !weatherDataDailyDTO.getTemperatureHeatIndex().isEmpty()) ? weatherDataDailyDTO.getTemperatureHeatIndex().get(i) : null,
//                        (weatherDataDailyDTO.getTemperatureWindChill().get(i) != null && !weatherDataDailyDTO.getTemperatureWindChill().isEmpty()) ? weatherDataDailyDTO.getTemperatureWindChill().get(i) : null,
//                        (weatherDataDailyDTO.getUvDescription().get(i) != null && !weatherDataDailyDTO.getUvDescription().isEmpty()) ? weatherDataDailyDTO.getUvDescription().get(i) : null,
//                        (weatherDataDailyDTO.getUvIndex().get(i) != null && !weatherDataDailyDTO.getUvIndex().isEmpty()) ? weatherDataDailyDTO.getUvIndex().get(i) : null,
//                        (weatherDataDailyDTO.getValidTimeLocal().get(i) != null && !weatherDataDailyDTO.getValidTimeLocal().isEmpty()) ? weatherDataDailyDTO.getValidTimeLocal().get(i) : "",
//                        (weatherDataDailyDTO.getValidTimeUtc().get(i) != null && !weatherDataDailyDTO.getValidTimeUtc().isEmpty()) ? weatherDataDailyDTO.getValidTimeUtc().get(i) : null,
//                        (weatherDataDailyDTO.getVisibility().get(i) != null && !weatherDataDailyDTO.getVisibility().isEmpty()) ? weatherDataDailyDTO.getVisibility().get(i) : null,
//                        (weatherDataDailyDTO.getWindDirection().get(i) != null && !weatherDataDailyDTO.getWindDirection().isEmpty()) ? weatherDataDailyDTO.getWindDirection().get(i) : null,
//                        (weatherDataDailyDTO.getWindDirectionCardinal().get(i) != null && !weatherDataDailyDTO.getWindDirectionCardinal().isEmpty()) ? weatherDataDailyDTO.getWindDirectionCardinal().get(i) : null,
//                        (weatherDataDailyDTO.getWindGust().get(i) != null && !weatherDataDailyDTO.getWindGust().isEmpty()) ? weatherDataDailyDTO.getWindGust().get(i) : null,
//                        (weatherDataDailyDTO.getWindSpeed().get(i) != null && !weatherDataDailyDTO.getWindSpeed().isEmpty()) ? weatherDataDailyDTO.getWindSpeed().get(i) : null,
//                        (weatherDataDailyDTO.getWxPhraseLong().get(i) != null && !weatherDataDailyDTO.getWxPhraseLong().isEmpty()) ? weatherDataDailyDTO.getWxPhraseLong().get(i) : null,
//                        (weatherDataDailyDTO.getWxPhraseShort().get(i) != null && !weatherDataDailyDTO.getWxPhraseShort().isEmpty()) ? weatherDataDailyDTO.getWxPhraseShort().get(i) : null,
//                        (weatherDataDailyDTO.getWxSeverity().get(i) != null && !weatherDataDailyDTO.getWxSeverity().isEmpty()) ? weatherDataDailyDTO.getWxSeverity().get(i) : null,
//                        (weatherDataDailyDTO.getGardenId() != null && !weatherDataDailyDTO.getGardenId().isEmpty()) ? weatherDataDailyDTO.getGardenId() : null
//                );
//                weatherDataDailyList.add(weatherDataDaily1);
//            } catch (Exception e) {
//                LOGGER.error("Error while converting into weather data daily in weatherMapper", e);
//            }
//        }
//        return weatherDataDailyList;
//    }

    private WeatherDataDaily convertIntoWeatherDataDaily(String s, LocalDateTime localDateTime, Integer cloudCover, String dayOfWeek, String dayOrNight,
                                                         String expirationTime, Integer iconCode,
                                                         Integer iconCodeExtend, Integer precipChance, String precipType, Float pressureMeanSeaLevel, Double qpf, Double qpfSnow,
                                                         Integer relativeHumidity, Integer temperature, Integer temperatureDewPoint, Integer temperatureFeelsLike,
                                                         Integer temperatureHeatIndex, Integer temperatureWindChill,
                                                         String uvDescription, Integer uvIndex, String validTimeLocal, String validTimeUtc, Float visibility,
                                                         Integer windDirection, String windDirectionCardinal,
                                                         Float windGust, Integer windSpeed, String wxPhraseLong, String wxPhraseShort, Integer wxSeverity, String gardenId) {
        try {
            return WeatherDataDaily.builder()
                    .sourceType(s)
                    .createdAt(localDateTime)
                    .cloudCover(cloudCover)
                    .dayOfWeek(dayOfWeek)
                    .dayOrNight(dayOrNight)
                    .expirationTimeUtc(expirationTime)
                    .iconCode(iconCode)
                    .iconCodeExtend(iconCodeExtend)
                    .precipChance(precipChance)
                    .precipType(precipType)
                    .pressureMeanSeaLevel(pressureMeanSeaLevel)
                    .qpf(qpf)
                    .qpfSnow(qpfSnow)
                    .relativeHumidity(relativeHumidity)
                    .temperature(temperature)
                    .temperatureDewPoint(temperatureDewPoint)
                    .temperatureFeelsLike(temperatureFeelsLike)
                    .temperatureHeatIndex(temperatureHeatIndex)
                    .temperatureWindChill(temperatureWindChill)
                    .uvDescription(uvDescription)
                    .uvIndex(uvIndex)
                    .validTimeLocal(validTimeLocal)
                    .validTimeUtc(validTimeUtc)
                    .visibility(visibility)
                    .windDirection(windDirection)
                    .windDirectionCardinal(windDirectionCardinal)
                    .windGust(windGust)
                    .windSpeed(windSpeed)
                    .wxPhraseLong(wxPhraseLong)
                    .wxPhraseShort(wxPhraseShort)
                    .wxSeverity(wxSeverity)
                    .gardenId(gardenId).build();
        } catch (Exception e) {
            LOGGER.error("Error while converting into weather data daily in weatherMapper", e);
            return null;
        }
    }

    public static WeatherWidgetTile toWeatherWidgetTile(WeatherWidgetData weatherWidgetData, int temperatureValue, String tempSymbol) {
        try {
            return WeatherWidgetTile.builder()
                    .refId(weatherWidgetData.getRefId())
                    .sunriseTimeLocal(weatherWidgetData.getSunrise())
                    .sunsetTimeLocal(weatherWidgetData.getSunset())
                    .wxPhraseLong(weatherWidgetData.getWxphraselong())
                    .narrative(weatherWidgetData.getNarrative())
                    .precipChance(weatherWidgetData.getPrecipchance())
                    .temperature(String.valueOf(temperatureValue) + tempSymbol)
                    .uvDescription(weatherWidgetData.getUvdescription())
                    .address(weatherWidgetData.getAddress())
                    .state(weatherWidgetData.getState())
                    .geoLat(weatherWidgetData.getGeolat())
                    .geoLong(weatherWidgetData.getGeolong())
//                    .uri(weatherWidgetData.getUri())
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
    private String getZoneName(String dateTimeString , String tzId) {
//            String dateTime = String.valueOf(dateTimeString);
        LocalDate localDate = LocalDate.parse(dateTimeString);

        // Convert to ZonedDateTime with the desired time zone
        ZoneId timeZone = ZoneId.of(tzId);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(timeZone);

        // Format the result
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");
        String formattedDateTime = zonedDateTime.format(formatter);
        return formattedDateTime;
    }

}
