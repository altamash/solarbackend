package com.solar.api.tenant.model.stage.monitoring.tigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Legend {

    @JsonProperty("type")
    private String type;
    @JsonProperty("show")
    private String show;
    @JsonProperty("top")
    private String top;
    @JsonProperty("selected")
    private Selected selected;
}
