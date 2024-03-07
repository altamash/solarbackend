package com.solar.api.tenant.model.stage.monitoring.tigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grid {

    @JsonProperty("containLabel")
    private String containLabel;
    @JsonProperty("top")
    private String top;
    @JsonProperty("left")
    private String left;
    @JsonProperty("right")
    private String right;
}
