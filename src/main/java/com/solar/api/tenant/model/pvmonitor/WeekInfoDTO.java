package com.solar.api.tenant.model.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeekInfoDTO {
    int weekNumber;
    String startDay;
    String endDay;

    String monthName;
}
