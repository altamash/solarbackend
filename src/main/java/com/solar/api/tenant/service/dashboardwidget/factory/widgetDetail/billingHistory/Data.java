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
public class Data {
    private String name;
    private Double value;
    private String color;
    private String valueColor;
    private String unit;
    private String icon;
    private String height;
    private String width;
}
