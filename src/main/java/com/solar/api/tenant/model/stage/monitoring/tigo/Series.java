package com.solar.api.tenant.model.stage.monitoring.tigo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Series {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    private List<SeriesData> data;
}
