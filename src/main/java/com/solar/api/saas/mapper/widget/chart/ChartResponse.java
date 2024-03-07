package com.solar.api.saas.mapper.widget.chart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartResponse {

    private List<String> labels;
    private List<DataSet> dataSets;
    private String retUri;
    private String type;
}
