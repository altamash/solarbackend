package com.solar.api.tenant.mapper.pvmonitor.solaredge.energy;

import lombok.Data;

@Data
public class SiteEnergyResponse {

    private Energy energy;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}

