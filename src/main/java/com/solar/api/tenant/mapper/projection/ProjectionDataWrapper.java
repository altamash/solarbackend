package com.solar.api.tenant.mapper.projection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectionDataWrapper {

//    private Map<Long, List<MonitorAPIResponse>> yearlyGraphData;
    private List<ProjectionTileDTO> data; //yearlyData old name
    private List<String> xAxis;
    private List<String> yAxis;
    private String year;

}
