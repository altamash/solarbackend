package com.solar.api.tenant.mapper.pvmonitor.solaredge;

import lombok.Data;

@Data
public class MetersValues {

    private String date;
    private Double value;
    private Double peakValue;
    private Double grossYield;
    private Double currentValueRunning;

    @Override
    public String toString() {
        return "MetersValues{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}