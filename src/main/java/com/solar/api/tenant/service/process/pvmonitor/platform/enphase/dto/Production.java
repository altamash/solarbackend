package com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponseL2;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Production {
    private Integer production;

    @Override
    public String toString() {
        return "Production{" +
                "production=" + production +
                '}';
    }
}
