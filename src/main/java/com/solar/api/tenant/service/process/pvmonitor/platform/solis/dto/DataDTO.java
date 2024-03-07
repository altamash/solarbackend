package com.solar.api.tenant.service.process.pvmonitor.platform.solis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataDTO {

    private PageDTO page;
    private Double capacity;
}
