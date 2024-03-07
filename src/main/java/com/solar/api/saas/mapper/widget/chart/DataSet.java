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
public class DataSet {

    private String label;
    private List<Double> data;
    private String borderColor;
    private String backgroundColor;
    private Integer borderWidth;
    private Double lineTension;
    private Boolean fill;
}
