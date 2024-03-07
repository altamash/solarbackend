package com.solar.api.tenant.service.dashboardwidget;

import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.widget.WidgetDTO;
import com.solar.api.tenant.model.widget.DashboardWidget;

import java.util.List;

public interface DashboardWidgetWrapper {
    BaseResponse<Object> getWidgetsStructureListAndIds();

    BaseResponse<Object> getWidgetDataById(Long widgetId, Long companyKey, String param);

    BaseResponse<Object> addWidget(Long widgetId);
}
