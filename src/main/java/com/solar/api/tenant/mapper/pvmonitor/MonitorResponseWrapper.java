package com.solar.api.tenant.mapper.pvmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorResponseWrapper {
    private MonitorAPIResponse widgetData;
    private GraphDataWrapper graphData;
    private GraphDataMonthlyWrapper monthlyGraphData;
}
