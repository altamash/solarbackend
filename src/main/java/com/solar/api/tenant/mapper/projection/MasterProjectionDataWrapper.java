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
public class MasterProjectionDataWrapper {
    private List<ProjectionDataWrapper> data;
    private List<String> xAxis;
    private List<String> yAxis;

}
