package com.solar.api.tenant.service.dashboardwidget.factory.widget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.configuration.SpringContextHolder;
import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.widget.WidgetDTO;
import com.solar.api.tenant.mapper.widget.WidgetMapper;
import com.solar.api.tenant.model.widget.DashboardWidget;
import com.solar.api.tenant.repository.widget.TenantWidgetRepository;
import com.solar.api.tenant.service.dashboardwidget.param.Dropdown;
import com.solar.api.tenant.service.dashboardwidget.param.Param;
import com.solar.api.tenant.service.dashboardwidget.param.Params;

import java.util.List;

public interface Widget {

    WidgetDTO getWidgetData(Long widgetId, Long compKey, Params params);

    default WidgetDTO getWidgetDTO(Long widgetId, Long compKey) {
        TenantWidgetRepository tenantWidgetRepository = SpringContextHolder.getApplicationContext().getBean(TenantWidgetRepository.class);
        DashboardWidget dashboardWidget = tenantWidgetRepository.findById(widgetId)
                .orElseThrow(() -> new NotFoundException(DashboardWidget.class, widgetId));
        WidgetDTO widgetDTO = WidgetMapper.toWidgetDTO(dashboardWidget);
        ObjectNode details = new ObjectMapper().createObjectNode();
        widgetDTO.getConfigJson().put("details", details);
        return widgetDTO;
    }

    default List<Dropdown> getDropdowns(Params params) {
        return params != null && params.getDropdownSelections() != null && !params.getDropdownSelections().isEmpty()
                ? params.getDropdownSelections() : null;
    }
    default List<Param> getParams(Params params) {
        return params != null && params.getParams() != null && !params.getParams().isEmpty()
                ? params.getParams() : null;
    }
}
