package com.solar.api.tenant.model.stage.monitoring.tigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AxisLabel {

    @JsonProperty("minInterval")
    private Long minInterval;
    @JsonProperty("maxInterval")
    private Long maxInterval;
}
