package com.solar.api.tenant.mapper.controlPanel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ControlPanelTransactionalDataDTO {

    private Long id;
    private String inverterStatus;
    private String dailyProduction;
    private String inverterHealth;
    private String variantHealth;
    private String errors;
    private String faults;
    private String alerts;
    private String currentTemp;
    private String humidity;
    private String percipitation;
    private Long inverterId;
    private Long variantId;
    private Long locId;
}
