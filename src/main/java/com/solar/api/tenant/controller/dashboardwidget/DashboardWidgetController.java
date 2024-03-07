package com.solar.api.tenant.controller.dashboardwidget;

import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.service.dashboardwidget.DashboardWidgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("DashboardWidgetController")
@RequestMapping(value = "/dashboardWidget")
public class DashboardWidgetController {

    private final DashboardWidgetService dashboardWidgetService;

    public DashboardWidgetController(DashboardWidgetService dashboardWidgetService) {
        this.dashboardWidgetService = dashboardWidgetService;
    }

    /**
     * @return BaseResponse DashboardSubscriptionWidgetTile
     * @author Sheharyar
     * Built for dashboard subscription widget
     */
    @GetMapping("/getDashboardSubscriptionWidgetData")
    public BaseResponse getDashboardSubscriptionWidgetData() {
        return dashboardWidgetService.getDashboardSubscriptionWidgetData();
    }

    /**
     * @return BaseResponse String message
     * @author Sheharyar
     * Built for dashboard welcome widget
     */
    @GetMapping("/getWelcomeWidgetData")
    public BaseResponse getWelcomeWidgetData(@RequestHeader("Comp-Key") Long compKey) {
        return dashboardWidgetService.getWelcomeWidgetData(compKey);
    }

    /**
     * @return ResponseEntity<WidgetSiteDataDTO> WidgetSiteDataDTO
     * @author Sheharyar
     * Built for dashboard Enviromental contribution widget
     */
    @GetMapping("/getEnviromentalWidgetData")
    public BaseResponse getEnviromentalWidgetData(@RequestParam(value = "monthYear", required = false) String monthYear) {
        return dashboardWidgetService.getEnviromentalWidgetData(monthYear);
    }

    /**
     * @param compKey
     * @param monthYear
     * @return
     * @author Ibtehaj
     * Built to fetch dashboard billing summary widget data
     */
    @GetMapping("/getBillingSummaryWidgetData")
    public BaseResponse getBillingSummaryWidgetData(@RequestHeader("Comp-Key") Long compKey,
                                                    @RequestParam(value = "monthYear", required = false) String monthYear) {
        return dashboardWidgetService.getBillingSummaryWidgetData(compKey, monthYear);
    }

    /**
     * @param compKey
     * @param subscriptionIds
     * @return
     * @author Ibtehaj
     * Built to fetch dashboard billing outstanding amount widget data
     */
    @GetMapping("/getOutstandingAmountBillingWidgetData")
    public BaseResponse getOutstandingAmountBillingWidgetData(@RequestHeader("Comp-Key") Long compKey,
                                                              @RequestParam(value = "subsIds", required = false) String subscriptionIds) {
        return dashboardWidgetService.getOutstandingAmountBillingWidgetData(compKey, subscriptionIds);
    }

    @GetMapping("/getBillingHistoryWidgetData")
    public BaseResponse getBillingHistoryWidgetData(@RequestHeader("Comp-Key") Long compKey,
                                                           @RequestParam(value = "isComparison", required = false, defaultValue = "false") boolean isComparison,
                                                           @RequestParam(value = "isQuarterly", required = false, defaultValue = "false") boolean isQuarterly,
                                                           @RequestParam("year") String year,
                                                           @RequestParam("subsIds") String subscriptionIds) {
        return dashboardWidgetService.getBillingHistoryWidgetData(compKey,isQuarterly, isComparison,year,subscriptionIds);
    }
}
