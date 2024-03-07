package com.solar.api.tenant.service.process.pvmonitor;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class APIDynamics {

    private List urlParams;
    private Map queryParams;
    private Map headers;
    private Object body;
}
