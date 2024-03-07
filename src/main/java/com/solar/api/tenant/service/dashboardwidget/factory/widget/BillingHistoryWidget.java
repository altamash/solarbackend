package com.solar.api.tenant.service.dashboardwidget.factory.widget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.BillingSummaryWidgetTemplate;
import com.solar.api.tenant.mapper.widget.WidgetDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.repository.CustomerSubscriptionRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.dashboardwidget.DashboardWidgetService;
import com.solar.api.tenant.service.dashboardwidget.factory.widgetDetail.billingHistory.*;
import com.solar.api.tenant.service.dashboardwidget.param.Dropdown;
import com.solar.api.tenant.service.dashboardwidget.param.Params;
import com.solar.api.tenant.service.process.pvmonitor.ExtDataStageDefinitionService;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class BillingHistoryWidget implements Widget {

    private final DashboardWidgetService dashboardWidgetService;
    private final UserService userService;
    private final CustomerSubscriptionRepository customerSubscriptionRepository;
    private final ExtDataStageDefinitionService extDataStageDefinitionService;

    public BillingHistoryWidget(DashboardWidgetService dashboardWidgetService, UserService userService,
                                CustomerSubscriptionRepository customerSubscriptionRepository, ExtDataStageDefinitionService extDataStageDefinitionService) {
        this.dashboardWidgetService = dashboardWidgetService;
        this.userService = userService;
        this.customerSubscriptionRepository = customerSubscriptionRepository;
        this.extDataStageDefinitionService = extDataStageDefinitionService;
    }

    @Override
    public WidgetDTO getWidgetData(Long widgetId, Long compKey, Params params) {
        WidgetDTO widgetDTO = getWidgetDTO(widgetId, compKey);
        BillingHistoryWidgetDetails widgetDetails = new BillingHistoryWidgetDetails();
        setDetailSummary(widgetDetails);
        List<CustomerSubscription> userSubscriptions = customerSubscriptionRepository.findCustomerSubscriptionByUserAccount(userService.getLoggedInUser());
        List<String> subscriptionIds = userSubscriptions.stream().map(m -> m.getExtSubsId()).collect(Collectors.toList());
        setMultiSelectOptions(subscriptionIds, widgetDetails);
        List<String> selectedSubscriptionIds = getSelectedSubscriptions(params, subscriptionIds, widgetDetails);
        setBasicData(widgetDetails, selectedSubscriptionIds);
        setBasicOptions(widgetDetails);
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

    private void setDetailSummary(BillingHistoryWidgetDetails widgetDetails) {
        widgetDetails.setHeading("Billing History");
        widgetDetails.setHeight("380px");
        widgetDetails.setIsDropdown(false);
        widgetDetails.setIsMultiSelect(true);
        widgetDetails.setMultiSelectName("Site");
    }

    private void setMultiSelectOptions(List<String> subscriptions, BillingHistoryWidgetDetails widgetDetails) {
        List<ExtDataStageDefinition> billingSubscriptions = extDataStageDefinitionService.findAllBySubsIdIn(subscriptions);
        List<YearMonth> yearMonths = Utility.getLastYearMonthDuring(12);
        List<SelectOption> multiSelectOptions = new ArrayList<>();
        billingSubscriptions.forEach(subscription -> {
            multiSelectOptions.add(SelectOption.builder()
                            .label(subscription.getSubscriptionName())
                            .value(subscription.getSubsId())
                    .build());
        });
        widgetDetails.setMultiSelectOptions(multiSelectOptions);
    }

    private List<String> getSelectedSubscriptions(Params params, List<String> subscriptionIds, BillingHistoryWidgetDetails widgetDetails) {
        List<Dropdown> dropdownSelections = getDropdowns(params);
        List<String> selectedSubscriptions = dropdownSelections != null ? dropdownSelections.stream()
                .filter(v -> "Site".equals(v.getName()))
                .map(m -> m.getValue())
                .findFirst().get() : subscriptionIds;
        ArrayNode selectedSubscriptionsJson = new ObjectMapper().createArrayNode();
        selectedSubscriptions.forEach(subscription -> selectedSubscriptionsJson.add(subscription));
        widgetDetails.setMultiSelectedValue(selectedSubscriptions);
        return selectedSubscriptions;
    }

    private void setBasicData(BillingHistoryWidgetDetails widgetDetails, List<String> selectedSubscriptionIds) {
        List<BillingSummaryWidgetTemplate> wrapperTile = dashboardWidgetService.getBillingHistory(selectedSubscriptionIds);
        List<YearMonth> yearMonths = Utility.getLastYearMonthDuring(12);
        List<String> months = new ArrayList<>();
        yearMonths.forEach(yearMonth -> {
            months.add(yearMonth.format(DateTimeFormatter.ofPattern(Utility.MONTH_FORMAT, Locale.ENGLISH)));
        });
        widgetDetails.setBasicData(BasicData.builder()
                .labels(months)
                .datasets(List.of(Dataset.builder()
                                    .label("Total Invoiced Amount")
                                    .data(wrapperTile.stream().map(m -> (m).getTotalInvoicedAmount()).collect(Collectors.toList()))
                                    .backgroundColor("#A45FB8")
                                    .borderWidth(0)
                                    .barThickness(12)
                                    .borderRadius(10)
                                    .borderSkipped(false)
                                    .build(),
                                Dataset.builder()
                                    .label("Total Paid Amount")
                                    .data(wrapperTile.stream().map(m -> (m).getTotalPaidAmount()).collect(Collectors.toList()))
                                    .backgroundColor("#068CFF")
                                    .borderWidth(0)
                                    .barThickness(12)
                                    .borderRadius(10)
                                    .borderSkipped(false)
                                    .build())).build());
    }

    private void setBasicOptions(BillingHistoryWidgetDetails widgetDetails) {
        widgetDetails.setBasicOptions(BasicOptions.builder()
                        .legend(Legend.builder()
                                .labels(Labels.builder()
                                        .color("#212121")
                                        .build())
                                .position("bottom")
                                .build())
                        .scales(Scales.builder()
                                .y(ScaleY.builder()
                                        .beginAtZero(true)
                                        .ticks(Ticks.builder()
                                                .color("#212121")
                                                .build())
                                        .grid(Grid.builder()
                                                .color("#919191")
                                                .drawBorder(false)
                                                .build())
                                        .build())
                                .x(ScaleX.builder()
                                        .ticks(Ticks.builder()
                                                .color("#000")
                                                .build())
                                        .grid(Grid.builder()
                                                .color("#919191")
                                                .drawBorder(false)
                                                .build())
                                        .build())
                                .build())
                .build());
    }
}
