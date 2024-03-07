package com.solar.api.tenant.mapper.pvmonitor.solaredge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SiteEquipmentResponse {

    private SiteEquipment reporters;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}

