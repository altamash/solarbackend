package com.solar.api.tenant.mapper.tiles.weatherTile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor

public class WeatherDataForWeatherWidget {

    private String time;
    private Integer temperature;
    private String forecast;
    private String tempDegree;

//    private String uri;


    public WeatherDataForWeatherWidget(String time, Integer temperature, String forecast) {
        this.time = time;
        this.temperature = temperature;
        this.forecast = forecast;
    }


}
