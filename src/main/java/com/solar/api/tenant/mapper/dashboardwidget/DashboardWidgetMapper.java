package com.solar.api.tenant.mapper.dashboardwidget;


import com.solar.api.tenant.mapper.tiles.dashboardwidget.DashboardSubscriptionWidget;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.DashboardSubscriptionWidgetTile;

import org.springframework.stereotype.Component;


@Component
public class DashboardWidgetMapper {
    public static DashboardSubscriptionWidgetTile convertIntoTiles(DashboardSubscriptionWidget dashboardSubscriptionWidget) {
        return new DashboardSubscriptionWidgetTile(
                dashboardSubscriptionWidget.getTotalSubscriptions(),
                dashboardSubscriptionWidget.getActiveSubscriptions(),
                dashboardSubscriptionWidget.getInactiveSubscriptions(),
                dashboardSubscriptionWidget.getTotalSystemSize(),
                dashboardSubscriptionWidget.getTotalActiveSystemSize()
        );
    }

    //    public MonitorAPIAuthBody convertIntoMonitorAPIAuthBody(List<EnviromentalWidgetTile> enviromentalWidgetTile) {
//        return MonitorAPIAuthBody.builder().
//                variantId(enviromentalWidgetTile.getVariantId())
//        .build();
//    }
//    public MonitorAPIAuthBody convertIntoMonitorAPIAuthBodyList(List<EnviromentalWidgetTile> enviromentalWidgetTiles) {
//        MonitorAPIAuthBody monitorAPIAuthBody = new MonitorAPIAuthBody();
//        List<String> variantIds = new ArrayList<>();
//
//        for (EnviromentalWidgetTile widgetTile : enviromentalWidgetTiles) {
//            variantIds.add(widgetTile.getVariantId());
//            // Add other mappings as needed
//        }
//
//        monitorAPIAuthBody.setVariantIds(variantIds);
//        // Add other mappings as needed
//
//        return monitorAPIAuthBody;
//    }
}
