package com.solar.api.tenant.service.dashboardwidget.factory.widget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.BillingSummaryWidgetTile;
import com.solar.api.tenant.mapper.widget.WidgetDTO;
import com.solar.api.tenant.service.dashboardwidget.DashboardWidgetService;
import com.solar.api.tenant.service.dashboardwidget.param.Dropdown;
import com.solar.api.tenant.service.dashboardwidget.param.Param;
import com.solar.api.tenant.service.dashboardwidget.param.Params;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
public class BillingSummaryWidget implements Widget {

    private final DashboardWidgetService dashboardWidgetService;

    public BillingSummaryWidget(DashboardWidgetService dashboardWidgetService) {
        this.dashboardWidgetService = dashboardWidgetService;
    }

    @Override
    public WidgetDTO getWidgetData(Long widgetId, Long compKey, Params params) {
        WidgetDTO widgetDTO = getWidgetDTO(widgetId, compKey);
        ObjectNode details = (ObjectNode) widgetDTO.getConfigJson().get("details");
        details.put("heading", "Payment Information");
        details.put("isDropdown", true);
        details.put("dropdownName", "BillingSummaryMonthYear");
        List<Dropdown> dropdownSelections = getDropdowns(params);
        List<Param> paramSelections = getParams(params);
        String selectedValue = null;
        if (dropdownSelections != null) {
            selectedValue = dropdownSelections.get(0).getValue().get(0);
        } else if (paramSelections != null) {
            selectedValue = paramSelections.get(0).getValue();
        } else {
            selectedValue = YearMonth.now().minusMonths(1).format(DateTimeFormatter.ofPattern(Utility.MONTH_YEAR_FORMAT, Locale.ENGLISH));
        }
        details.put("dropdownSelectedValue", selectedValue);
        setDropdownItems(details);
        BaseResponse response = dashboardWidgetService.getBillingSummaryWidgetData(compKey, selectedValue);
        BillingSummaryWidgetTile widgetTile = ((BillingSummaryWidgetTile) response.getData());
        ArrayNode data = new ObjectMapper().createArrayNode();
        setData(data, "Pending", widgetTile.getTotalPendingAmount(), "$", "#D38E49", "#FDF7EC");
        setData(data, "Calculated", widgetTile.getTotalCalculatedAmount(), "$", "#919191", "#fff");
        setData(data, "Invoiced", widgetTile.getTotalInvoicedAmount(), "$", "#068CFF", "#E9F3F9");
        setData(data, "Paid", widgetTile.getTotalPaidAmount(), "$", "#2AAA5D", "#EFFFF3");
        details.put("data", data);
        return widgetDTO;
    }

    private void setDropdownItems(ObjectNode details) {
        List<YearMonth> yearMonths = Utility.getLastYearMonthDuring(12);
        ArrayNode dropdownItems = new ObjectMapper().createArrayNode();
        yearMonths.forEach(yearMonth -> {
            ObjectNode dropdownItem = new ObjectMapper().createObjectNode();
            dropdownItem.put("label", yearMonth.format(DateTimeFormatter.ofPattern(Utility.CAPITAL_MONTH_YEAR_FORMAT, Locale.ENGLISH)));
            dropdownItem.put("value", yearMonth.format(DateTimeFormatter.ofPattern(Utility.MONTH_YEAR_FORMAT, Locale.ENGLISH)));
            dropdownItems.add(dropdownItem);
        });
        details.put("dropdownItems", dropdownItems);
    }

    private void setData(ArrayNode data, String name, Double value, String unit, String color, String background) {
        ObjectNode InfoJson = new ObjectMapper().createObjectNode();
        InfoJson.put("name", name);
        InfoJson.put("value", value == null ? 0 : value);
        InfoJson.put("unit", unit);
        InfoJson.put("color", color);
        InfoJson.put("background", background);
        data.add(InfoJson);
    }
}
