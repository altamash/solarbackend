package com.solar.api.tenant.model.stage.monitoring.tigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesData {

    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private Object[] value;
}
