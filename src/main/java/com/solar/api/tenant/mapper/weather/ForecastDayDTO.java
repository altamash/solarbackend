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
public class ForecastDayDTO {

    private String date;
    private long date_epoch;
    private DayDTO day;
    private AstroDTO astro;
    private List<HourDTO> hour;

}
