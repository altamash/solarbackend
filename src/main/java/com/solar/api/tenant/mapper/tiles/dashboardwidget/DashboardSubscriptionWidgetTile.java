package com.solar.api.tenant.mapper.tiles.dashboardwidget;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardSubscriptionWidgetTile {
    private String totalSubscriptions;
    private String activeSubscriptions;
    private String inactiveSubscriptions;
    private String totalSystemSize;
    private String totalActiveSystemSize;

    public DashboardSubscriptionWidgetTile(String totalSubscriptions, String activeSubscriptions, String inactiveSubscriptions, String totalSystemSize, String totalActiveSystemSize) {
        this.totalSubscriptions = totalSubscriptions;
        this.activeSubscriptions = activeSubscriptions;
        this.inactiveSubscriptions = inactiveSubscriptions;
        this.totalSystemSize = totalSystemSize;
        this.totalActiveSystemSize = totalActiveSystemSize;
    }

//    public DashboardSubscriptionWidgetTile(Long totalSubscriptions, Long activeSubscriptions, Long inactiveSubscriptions, Long totalSystemSize, Long totalActiveSystemSize) {
//        this.totalSubscriptions = totalSubscriptions;
//        this.activeSubscriptions = activeSubscriptions;
//        this.inactiveSubscriptions = inactiveSubscriptions;
//        this.totalSystemSize = totalSystemSize;
//        this.totalActiveSystemSize = totalActiveSystemSize;
//    }
}
