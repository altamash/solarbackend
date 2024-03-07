package com.solar.api.tenant.mapper.tiles.dashboardwidget;

public interface DashboardSubscriptionWidget {

    String getTotalSubscriptions();

    String getActiveSubscriptions();

    String getInactiveSubscriptions();

    String getTotalSystemSize();

    String getTotalActiveSystemSize();
}
