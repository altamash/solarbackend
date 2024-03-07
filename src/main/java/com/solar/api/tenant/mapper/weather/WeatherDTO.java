package com.solar.api.tenant.mapper.weather;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.joda.time.DateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherDTO {

    private LocationDTO location;
    private CurrentDTO current;
    private Forecast forecast;
    private String gardenId;
    private String timeZone;
    private String timeZoneCode;
}
