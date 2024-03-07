package com.solar.api.tenant.mapper.pvmonitor.solaredge;

import lombok.Data;

import java.util.List;

@Data
public class Sites {

    private int count;
    private final List<Site> site;
}

