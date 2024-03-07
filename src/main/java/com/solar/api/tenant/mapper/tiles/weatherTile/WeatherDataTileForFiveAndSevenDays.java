package com.solar.api.tenant.mapper.tiles.weatherTile;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class WeatherDataTileForFiveAndSevenDays     {
    private String time;
    private Integer temperature;
    private String forecast;
    private  String dayOrNight;
    private  String sunriseTimeLocal;
    private  String sunsetTimeLocal;

    public WeatherDataTileForFiveAndSevenDays(String time, Integer temperature, String forecast, String dayOrNight, String sunriseTimeLocal, String sunsetTimeLocal) {
        this.time = time;
        this.temperature = temperature;
        this.forecast = forecast;
        this.dayOrNight = dayOrNight;
        this.sunriseTimeLocal = sunriseTimeLocal;
        this.sunsetTimeLocal = sunsetTimeLocal;
    }
}
