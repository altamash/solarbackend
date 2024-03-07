package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeekScheduleConfigDTO {
    private Long id;
    private String weekName;
    private String description;
    private Long mon;
    private Long tue;
    private Long wed;
    private Long thu;
    private Long fri;
    private Long sat;
    private Long sun;
    private Long projectId;

}
