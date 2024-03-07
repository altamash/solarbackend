package com.solar.api.tenant.model.stage.monitoring.tigo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TigoResponseDTO {

    @JsonProperty("legend")
    private Legend legend;
    @JsonProperty("tooltip")
    private ToolTip tooltip;
    @JsonProperty("yAxis")
    private Yaxis yAxis;
    @JsonProperty("xAxis")
    private Xaxis xAxis;
    @JsonProperty("grid")
    private Grid grid;
    @JsonProperty("series")
    private List<Series> seriesList;
    @JsonProperty("timezone")
    private String timezone;
}
