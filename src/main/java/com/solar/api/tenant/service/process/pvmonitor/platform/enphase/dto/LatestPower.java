package com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LatestPower {
     private Double value;
     private Integer time;
     private String units;
     private Integer precision;

    @Override
    public String toString() {
        return "LatestPower{" +
                "value=" + value +
                ", time=" + time +
                ", units='" + units + '\'' +
                ", precision=" + precision +
                '}';
    }
}
