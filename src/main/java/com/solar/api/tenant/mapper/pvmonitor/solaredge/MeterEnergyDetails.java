package com.solar.api.tenant.mapper.pvmonitor.solaredge;

import lombok.Data;

import java.util.List;

@Data
public class MeterEnergyDetails {

    private String timeUnit;
    private String unit;
    private List<Meters> meters;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}

