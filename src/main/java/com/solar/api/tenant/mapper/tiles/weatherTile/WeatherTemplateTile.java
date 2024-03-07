package com.solar.api.tenant.mapper.tiles.weatherTile;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class WeatherTemplateTile {

         private String desc;
         private String name;
         private String size;
         private String type; // Weather alert
         private String impact;
         private String category; // Weather alert
         private String duration;
         private String loc;
         private String platform;
         private String performance;
         private String report_date;
         private String alert_impact; //low
         private List<AlertDetails> alert_details;
}
