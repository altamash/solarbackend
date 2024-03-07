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
public class TigoResponseV2 {

//    @JsonProperty("title")
//    private Title title;
//    @JsonProperty("tooltip")
//    private ToolTip tooltip;
    @JsonProperty("series")
    private List<Series> seriesList;
//    @JsonProperty("timezone")
//    private String timezone;
//    @JsonProperty("legend")
//    private Legend legend;
//    @JsonProperty("yAxis")
//    private List<Yaxis> yAxis;
//    @JsonProperty("xAxis")
//    private Xaxis xAxis;
//    @JsonProperty("grid")
//    private Grid grid;
}
