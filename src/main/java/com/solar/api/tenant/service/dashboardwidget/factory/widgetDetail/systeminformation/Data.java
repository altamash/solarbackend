package com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.systeminformation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Data {
    private String name;
    private String value;
    private String color;
    private String valueColor;
    private String name2;
    private String value2;
    private String color2;
    private String valueColor2;
    private String timeZone;
    private String unit;
    private String icon;
    private String height;
    private String width;
}
