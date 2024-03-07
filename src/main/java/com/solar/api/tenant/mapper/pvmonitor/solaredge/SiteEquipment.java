package com.solar.api.tenant.mapper.pvmonitor.solaredge;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SiteEquipment {

    private int count;
    private final List<Equipment> list;

}