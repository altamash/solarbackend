package com.solar.api.tenant.mapper.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DayPartDTO {

    private List<Integer> cloudCover;
    private List<String> dayOrNight;
    private List<String> daypartName;
    private List<String> narrative;
    private List<String> precipChance;
    private List<String> precipType;
    private List<Double> qpf;
    private List<Double> qpfSnow;
    private List<Double> relativeHumidity;
    private List<String> snowRange;
    private List<Integer> temperature;
    private List<Integer> temperatureHeatIndex;
    private List<Integer> temperatureWindChill;
    private List<String> thunderCategory;
    private List<Integer> thunderIndex;
    private List<String> uvDescription;
    private List<Integer> uvIndex;
    private List<Integer> windDirection;
    private List<String> windPhrase;
    private List<Integer> windSpeed;
    private List<String> wxPhraseLong;
    private List<String> wxPhraseShort;
}
