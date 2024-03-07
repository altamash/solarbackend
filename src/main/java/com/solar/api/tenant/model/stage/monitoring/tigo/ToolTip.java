package com.solar.api.tenant.model.stage.monitoring.tigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolTip {

    @JsonProperty("trigger")
    private String trigger;
    @JsonProperty("transitionDuration")
    private int transitionDuration;
    @JsonProperty("axisPointer")
    private AxisPointer axisPointer;
}
