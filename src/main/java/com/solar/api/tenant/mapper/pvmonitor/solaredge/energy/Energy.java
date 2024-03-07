package com.solar.api.tenant.mapper.pvmonitor.solaredge.energy;

import com.solar.api.tenant.mapper.pvmonitor.solaredge.MetersValues;
import lombok.Data;

import java.util.List;

@Data
public class Energy {

    /*{
         "energy": {
             "timeUnit": "QUARTER_OF_AN_HOUR",
             "unit": "Wh",
             "measuredBy": "INVERTER",
             "values": [
                 {
                     "date": "2023-05-01 00:00:00",
                     "value": null
                 }
             ]
         }
    }*/
    private String timeUnit;
    private String unit;
    private String measuredBy;
    private final List<MetersValues> values;

}