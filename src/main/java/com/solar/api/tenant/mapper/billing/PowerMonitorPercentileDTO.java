package com.solar.api.tenant.mapper.billing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerMonitorPercentileDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date day;
    private Double yield;
    private Double percentile;

    public PowerMonitorPercentileDTO(Date day, Double yield, Double percentile) {
        this.day = day;
        this.yield = yield;
        this.percentile = percentile;
    }

    public PowerMonitorPercentileDTO(Date day, Double yield) {
        this.day = day;

        this.yield = yield != null ? yield : 0d;
    }
}
