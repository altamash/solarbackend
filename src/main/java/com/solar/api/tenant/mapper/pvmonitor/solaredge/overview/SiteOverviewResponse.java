package com.solar.api.tenant.mapper.pvmonitor.solaredge.overview;

import lombok.Data;

@Data
public class SiteOverviewResponse {

  /*{
        "overview": {
            "lastUpdateTime": "2023-06-13 03:01:58",
                "lifeTimeData": {
                "energy": 9.9268944E7
            },
            "lastYearData": {
                "energy": 5155202.0
            },
            "lastMonthData": {
                "energy": 935266.0
            },
            "lastDayData": {
                "energy": 0.0
            },
            "currentPower": {
                "power": 0.0
            },
            "measuredBy": "INVERTER"
        }
    }*/
    private Overview overview;

    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}

