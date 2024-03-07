package com.solar.api.tenant.service.process.pvmonitor.platform.solaredge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPVDTO {

    private String x;
    private Double y;



}
