package com.solar.api.tenant.mapper.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherDataDailyDTO {

//    private String sourceType;
//    private LocalDateTime createdAt;
//    private List<Integer> cloudCover;
//    private List<String> dayOfWeek;
//    private List<String> dayOrNight;
//    private List<String> expirationTimeUtc;
//    private List<Integer> iconCode;
//    private List<Integer> iconCodeExtend;
//    private List<Integer> precipChance;
//    private List<String> precipType;
//    private List<Float> pressureMeanSeaLevel;
//    private List<Double> qpf;
//    private List<Double> qpfSnow;
//    private List<Integer> relativeHumidity;
//    private List<Integer> temperature;
//    private List<Integer> temperatureDewPoint;
//    private List<Integer> temperatureFeelsLike;
//    private List<Integer> temperatureHeatIndex;
//    private List<Integer> temperatureWindChill;
//    private List<String> uvDescription;
//    private List<Integer> uvIndex;
//    private List<String> validTimeLocal;
//    private List<String> validTimeUtc;
//    private List<Float> visibility;
//    private List<Integer> windDirection;
//    private List<String> windDirectionCardinal;
//    private List<Float> windGust;
//    private List<Integer> windSpeed;
//    private List<String> wxPhraseLong;
//    private List<String> wxPhraseShort;
//    private List<Integer> wxSeverity;
//    private String gardenId;
    private LocationDTO location;
    private CurrentDTO current;
    private Forecast forecast;
    private String gardenId;
}
