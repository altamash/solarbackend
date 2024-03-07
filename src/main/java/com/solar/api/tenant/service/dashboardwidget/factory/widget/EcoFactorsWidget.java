package com.solar.api.tenant.service.dashboardwidget.factory.widget;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.EnviromentalWidgetTile;
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
public class EcoFactorsWidget implements Widget {

    private final DashboardWidgetService dashboardWidgetService;

    public EcoFactorsWidget(DashboardWidgetService dashboardWidgetService) {
        this.dashboardWidgetService = dashboardWidgetService;
    }

    @Override
    public WidgetDTO getWidgetData(Long widgetId, Long compKey, Params params) {
        WidgetDTO widgetDTO = getWidgetDTO(widgetId, compKey);
        ObjectNode details = (ObjectNode) widgetDTO.getConfigJson().get("details");
        details.put("heading", "Eco Factors");
        details.put("isIcons", true);
        details.put("isDropdown", true);
        details.put("dropdownName", "EcoFactorsMonthYear");
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
        BaseResponse response = dashboardWidgetService.getEnviromentalWidgetData(selectedValue);
        EnviromentalWidgetTile widgetTile = ((EnviromentalWidgetTile) response.getData());
        ArrayNode data = new ObjectMapper().createArrayNode();
        setData(data, "TONs of CO2 Reduction", widgetTile.getCo2Reduction(), "number", "#212121", "#2AAA5D", "assets/icons/power-monitoring-dashboard-assets/ic_carbon_dioxide.svg", "2rem", "2rem");
        setData(data, "Trees Planted", widgetTile.getTreesPlanted(), "number", "#212121", "#2AAA5D", "assets/icons/power-monitoring-dashboard-assets/ic_trees_planted.svg", "2rem", "2rem");
        setData(data, "Oil Barrels Saved", widgetTile.getBarrels(), "number", "#212121", "#2AAA5D", "assets/icons/power-monitoring-dashboard-assets/ic_oil_barrels.svg", "2rem", "2rem");
        setData(data, "Miles Driven on Sunshine", widgetTile.getMilesCover(), "number", "#212121", "#2AAA5D", "assets/icons/power-monitoring-dashboard-assets/ic_car.svg", "2rem", "2rem");
        setData(data, "Smart Phones Charged", widgetTile.getPhoneCharges(), "number", "#212121", "#2AAA5D", "assets/icons/power-monitoring-dashboard-assets/ic_smartphone.svg", "2rem", "2rem");
        setData(data, "Electric Cars Charged", widgetTile.getCarCharges(), "number", "#212121", "#2AAA5D", "assets/icons/power-monitoring-dashboard-assets/ic_electric_car_charging.svg", "2rem", "2rem");
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

    private void setData(ArrayNode data, String name, String value, String unit, String color, String valueColor, String icon, String height, String width) {
        ObjectNode InfoJson = new ObjectMapper().createObjectNode();
        InfoJson.put("name", name);
        InfoJson.put("value", value);
        InfoJson.put("unit", unit);
        InfoJson.put("color", color);
        InfoJson.put("valueColor", valueColor);
        InfoJson.put("icon", icon);
        InfoJson.put("height", height);
        InfoJson.put("width", width);
        data.add(InfoJson);
    }
}
