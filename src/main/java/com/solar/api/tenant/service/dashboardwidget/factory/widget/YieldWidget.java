package com.solar.api.tenant.service.dashboardwidget.factory.widget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.widget.WidgetDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.yield.Data;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.yield.SelectOption;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.yield.WidgetDetails;
import com.solar.api.tenant.service.dashboardwidget.param.Dropdown;
import com.solar.api.tenant.service.dashboardwidget.param.Param;
import com.solar.api.tenant.service.dashboardwidget.param.Params;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.MonitoringDashboardWidgetService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.YieldWidgetDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class YieldWidget implements Widget {
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
        widgetDetails.setDropdownName("YieldSubscription");
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
        widgetDetails.setHeading("Yield");
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
        YieldWidgetDataDTO yieldWidgetDataDTO = monitoringDashboardWidgetService.getYieldWidgetDataBySubscriptionId(selectedSubscriptionId);
        List<Data> dataList = new ArrayList<>();
        setData(dataList,"Daily Yield",yieldWidgetDataDTO.getDailyYield(),"#212121","#2AAA5D","kWh","assets/icons/dashboard-icons/daily-yield.svg","2.5rem","2.5rem");
        setData(dataList,"Monthly Yield",yieldWidgetDataDTO.getMonthlyYield(),"#212121","#2AAA5D","kWh","assets/icons/dashboard-icons/monthly-yield.svg","2.5rem","2.5rem");
        setData(dataList,"Annual Yield",yieldWidgetDataDTO.getYearlyYield(),"#212121","#2AAA5D","kWh","assets/icons/dashboard-icons/annual-yield.svg","2.5rem","2.5rem");
        setData(dataList,"Total Yield",yieldWidgetDataDTO.getLifeTimeYield(),"#212121","#2AAA5D","kWh","assets/icons/dashboard-icons/total-yield.svg","2.5rem","2.5rem");
        widgetDetails.setData(dataList);
    }

    private void setData(List<Data> dataList, String name, Double value, String color, String valueColor, String unit, String icon, String height, String width) {
        dataList.add(Data.builder().name(name).value(String.valueOf(value)).color(color).valueColor(valueColor).unit(unit).icon(icon).height(height).width(width).build());
    }

}
