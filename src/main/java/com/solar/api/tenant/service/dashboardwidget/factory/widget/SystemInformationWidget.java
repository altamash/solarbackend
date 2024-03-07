package com.solar.api.tenant.service.dashboardwidget.factory.widget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.widget.WidgetDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.systeminformation.SelectOption;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.systeminformation.Data;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.systeminformation.WidgetDetails;
import com.solar.api.tenant.service.dashboardwidget.param.Dropdown;
import com.solar.api.tenant.service.dashboardwidget.param.Param;
import com.solar.api.tenant.service.dashboardwidget.param.Params;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.MonitoringDashboardWidgetService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SystemInformationWidget implements Widget {
    private final MonitoringDashboardWidgetService monitoringDashboardWidgetService;
    private final UserService userService;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final ExtDataStageDefinitionService extDataStageDefinitionService;

    @Override
    public WidgetDTO getWidgetData(Long widgetId, Long compKey, Params params) {
        WidgetDTO widgetDTO = getWidgetDTO(widgetId, compKey);
        WidgetDetails widgetDetails = new WidgetDetails();
        setDetailSummary(widgetDetails);
        List<CustomerSubscription> userSubscriptions = customerSubscriptionRepository.findCustomerSubscriptionByUserAccount(userService.getLoggedInUser());
        List<String> subscriptionIds = userSubscriptions.stream().map(m -> m.getExtSubsId()).collect(Collectors.toList());
        setDropdownItems(subscriptionIds, widgetDetails);
        List<Dropdown> dropdownSelections = getDropdowns(params);
        List<Param> paramSelections = getParams(params);
        String selectedValue = null;
        if (dropdownSelections != null) {
            selectedValue = dropdownSelections.get(0).getValue().get(0);
        } else if (paramSelections != null) {
            selectedValue = paramSelections.get(0).getValue();
        } else {
            selectedValue = widgetDetails.getDropdownItems().get(0).getValue();
        }
        widgetDetails.setDropdownSelectedValue(selectedValue);
        widgetDetails.setDropdownName("SystemInfoSubscription");
        setData(widgetDetails, selectedValue);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode details;
        try {
            String jsonString = mapper.writeValueAsString(widgetDetails);
            details = mapper.readValue(jsonString, ObjectNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        widgetDTO.getConfigJson().put("details", details);
        return widgetDTO;
    }


    private void setDetailSummary(WidgetDetails widgetDetails) {
        widgetDetails.setHeading("System Information");
        widgetDetails.setIsIcons(true);
        widgetDetails.setIsDropdown(true);
    }

    private void setDropdownItems(List<String> subscriptions, WidgetDetails widgetDetails) {
        List<ExtDataStageDefinition> billingSubscriptions = extDataStageDefinitionService.findAllBySubsIdIn(subscriptions);
        List<SelectOption> dropdownItems = new ArrayList<>();
        billingSubscriptions.forEach(subscription -> {
            dropdownItems.add(SelectOption.builder()
                    .label(subscription.getSubscriptionName())
                    .value(subscription.getSubsId())
                    .build());
        });
        widgetDetails.setDropdownItems(dropdownItems);
    }

    private void setData(WidgetDetails widgetDetails, String selectedSubscriptionId) {
        WidgetDataDTO widgetDataDTO = monitoringDashboardWidgetService.getSystemInformationBySubscriptionId(selectedSubscriptionId);
        List<Data> dataList = new ArrayList<>();
        setData(dataList, "Roof Top", widgetDataDTO.getRefType(), "#212121", "#02577a", "", "assets/icons/power-monitoring-dashboard-assets/ic_rooftop.svg", "2rem", "2rem");
        setData(dataList, "System Size", widgetDataDTO.getSystemSize(), "#212121", "#02577a", "kWh", "assets/icons/power-monitoring-dashboard-assets/ic_system_size.svg", "2rem", "2rem");
        setData(dataList, "Monitoring Platform", widgetDataDTO.getMp(), "#212121", "#02577a", "", "assets/icons/power-monitoring-dashboard-assets/ic_monitoring_platform.svg", "2rem", "2rem");
        setData(dataList, widgetDataDTO.getAddress(), widgetDataDTO.getState(), "#212121", "#02577a", null, null, null, "02577a", widgetDataDTO.getTimeZone(), "", "assets/icons/power-monitoring-dashboard-assets/ic_location.svg", "2rem", "2rem");
        setData(dataList, "Sunrise", widgetDataDTO.getSunrise(), "#212121", "#02577a", "Sunset", widgetDataDTO.getSunset(), "#212121", "#02577a", null, "", "assets/icons/power-monitoring-dashboard-assets/ic_sunset.svg", "2rem", "2rem");
        widgetDetails.setData(dataList);
    }

    private void setData(List<Data> dataList, String name, String value, String color, String valueColor, String unit, String icon, String height, String width) {
        dataList.add(Data.builder().name(name).value(value).color(color).valueColor(valueColor).unit(unit).icon(icon).height(height).width(width).build());
    }

    private void setData(List<Data> dataList, String name, String value, String color, String valueColor, String name2, String value2, String color2, String valueColor2, String timeZone, String unit, String icon, String height, String width) {
        dataList.add(Data.builder().name(name).value(value).color(color).valueColor(valueColor)
                .name2(name2).value2(value2).color2(color2).valueColor2(valueColor2).timeZone(timeZone)
                .unit(unit).icon(icon).height(height).width(width).build());
    }
}
