package com.solar.api.tenant.mapper.tiles.weatherTile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AlertDetails {

    private String date;
    private String time;
    private String weather;
    private String production;
}
