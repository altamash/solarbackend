package com.solar.api.tenant.mapper.tiles.weatherTile;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherWidgetTile {
    private String refId;
    private String sunriseTimeLocal;
    private String sunsetTimeLocal;
    private String wxPhraseLong;
    private String narrative;
    private Integer precipChance;
    private String temperature;
    private String uvDescription;
    private String address;
    private String state;
    private String geoLat;
    private String geoLong;
//    private String uri;

}


