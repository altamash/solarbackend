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
public class GraphDataDTO {

    private Map<String, List<MonitorAPIResponse>> graphData;
    private List<String> xAxis;

    private String yAxis;

}