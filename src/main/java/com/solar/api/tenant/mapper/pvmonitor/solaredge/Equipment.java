package com.solar.api.tenant.mapper.pvmonitor.solaredge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Equipment {

    private String name;
    private String model;
    private String manufacturer;
    private String serialNumber;

}