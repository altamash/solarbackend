package com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.billingHistory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dataset {

    private String label;
    private List<Double> data;
    private String backgroundColor;
    private Integer borderWidth;
    private Integer barThickness;
    private Integer borderRadius;
    private Boolean borderSkipped;
}
