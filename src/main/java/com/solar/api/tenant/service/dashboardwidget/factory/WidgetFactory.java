package com.solar.api.tenant.service.dashboardwidget.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solar.api.tenant.mapper.widget.WidgetDTO;
import com.solar.api.tenant.service.dashboardwidget.factory.widget.*;
import com.solar.api.tenant.service.dashboardwidget.param.Params;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
@RequiredArgsConstructor
@Component
public class WidgetFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WidgetFactory.class);
    private final HelloWidget helloWidget;
    private final BillingSummaryWidget billingSummaryWidget;
    private final EcoFactorsWidget ecoFactorsWidget;
    private final BillingInformationWidget billingInformationWidget;
    private final BillingHistoryWidget billingHistoryWidget;
    private final YieldWidget yieldWidget;
    private final SystemInformationWidget systemInformationWidget;

    public WidgetDTO getWidget(Long widgetId, Long compKey, String param) {
        Integer option = widgetId == null ? null : Math.toIntExact(widgetId);
        Params paramsObj = null;
        try {
            paramsObj = param == null ? null : new ObjectMapper().readValue(param, Params.class);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        switch (option) {
            case 1001:
                return helloWidget.getWidgetData(widgetId, compKey, null);
            case 1002:
                return billingSummaryWidget.getWidgetData(widgetId, compKey, paramsObj);
            case 1003:
                return yieldWidget.getWidgetData(widgetId, compKey, paramsObj);
            case 1004:
                return ecoFactorsWidget.getWidgetData(widgetId, compKey, paramsObj);
            case 1005:
                return billingInformationWidget.getWidgetData(widgetId, compKey, paramsObj);
            case 1006:
                return systemInformationWidget.getWidgetData(widgetId, compKey, paramsObj);
            case 1007:
                return billingHistoryWidget.getWidgetData(widgetId, compKey, paramsObj);
        }
        return null;
    }
}
