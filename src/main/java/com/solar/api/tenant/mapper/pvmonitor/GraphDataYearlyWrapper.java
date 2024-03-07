package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GraphDataYearlyWrapper {

//    private Map<Long, List<MonitorAPIResponse>> yearlyGraphData;
    private Map<String, List<MonitorAPIResponse>> yearlyGraphData;
    private List<String> xAxis;
}