package com.solar.api.tenant.mapper.pvmonitor.solaredge;

import lombok.Data;

import java.util.List;

@Data
public class MeterEnergyDetailResponse {

    private MeterEnergyDetails meterEnergyDetails;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}

