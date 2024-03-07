package com.solar.api.tenant.mapper.pvmonitor.solaredge;

import lombok.Data;

import java.util.List;

@Data
public class Meters {

    private String meterSerialNumber;
    private String connectedSolaredgeDeviceSN;
    private String model;
    private String meterType;
    private final List<MetersValues> values;

}