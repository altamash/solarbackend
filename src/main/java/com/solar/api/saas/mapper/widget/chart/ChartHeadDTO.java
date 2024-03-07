package com.solar.api.saas.mapper.widget.chart;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChartHeadDTO {

    private Long chartId;
    private String chartCode;
    private String chartName;
    private String chartType; // From portal attribute
    private String maxXLabels;
    private Integer maxXPoints;
    private Integer minLabelWidth;
    private Boolean showLegend;
    private Boolean enabled;
    private String orientation;
    private String javaMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
