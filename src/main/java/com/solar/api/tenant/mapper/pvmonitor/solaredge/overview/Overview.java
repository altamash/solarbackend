package com.solar.api.tenant.mapper.pvmonitor.solaredge.overview;

import lombok.Data;

import java.util.List;

@Data
public class Overview {

  /*{
        "lastUpdateTime":"2023-06-13 03:01:58",
        "lifeTimeData":{
            "energy":9.9268944E7
        },
        "lastYearData":{
            "energy":5155202.0
        },
        "lastMonthData":{
            "energy":935266.0
        },
        "lastDayData":{
            "energy":0.0
        },
        "currentPower":{
            "power":0.0
        },
        "measuredBy":"INVERTER"
    }*/
    private String lastUpdateTime;
    private Energy lifeTimeData;
    private Energy lastYearData;
    private Energy lastMonthData;
    private Energy lastDayData;
    private CurrentPower currentPower;
    private String measuredBy;

    private Double peakValue;
}