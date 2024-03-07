package com.solar.api.tenant.service.process.pvmonitor.platform.solaredge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponseL2;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAPIYieldRatioResponse {

    // For graph
    private Map<String, Map<Date, MonitorAPIResponseL2>> inverterValuesOverTime;
//    private Map<String, Map<Date, MonitorAPIResponseL2>> inverterGridpowerOverTime;
//    private Map<String, Map<Date, MonitorAPIResponseL2>> inverterRelayPowerOverTime
    //  GET_REALTIME_INFO response from Solax
    private MonitorAPIResponseL2 result;
    //  GET_SITE_TOTAL_POWER response from Solax
    private List<MonitorAPIResponseL2> object;
    private String msg;
    private List<DetailDataDTO> data;

    //For GoodWe
//    private String sn;
//    private Double eday;
//    private Double emonth;
//    private Double etotal;
////    private double capacity;
//    private Double pac;
//    private Double y; //yield value for year
}
