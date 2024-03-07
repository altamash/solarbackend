package com.solar.api.tenant.model.stage.monitoring.tigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Xaxis {

    @JsonProperty("type")
    private String type;
    @JsonProperty("minInterval")
    private Long minInterval;
    @JsonProperty("maxInterval")
    private Long maxInterval;
    @JsonProperty("axisLabel")
    private AxisLabel axisLabel;

    @JsonProperty("data")
    private List<String> data;

}
