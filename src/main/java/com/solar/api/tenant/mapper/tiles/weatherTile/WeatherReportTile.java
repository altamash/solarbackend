package com.solar.api.tenant.mapper.tiles.weatherTile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class WeatherReportTile {
    private String gardenId;
    private String gardenName;
    private String monPlatform;
    private String mpJson;
    private String physicalLocation;
    private String gardenSize;
    private String description;
    private  String day;
    private  String weatherCondition;
    private String affectedTime;
    private String estProduction;

    public WeatherReportTile(String gardenId, String gardenName, String monPlatform, String mpJson, String physicalLocation, String description, String day, String weatherCondition,String affectedTime, String estProduction) {
        this.gardenId = gardenId;
        this.gardenName = gardenName;
        this.monPlatform = monPlatform;
        this.mpJson = mpJson;
        this.physicalLocation = physicalLocation;
        this.description = description;
        this.day = day;
        this.weatherCondition = weatherCondition;
        this.affectedTime= affectedTime;
        this.estProduction = estProduction;
    }


}
