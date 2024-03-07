package com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.billingHistory;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ticks {

    private String color;
}
