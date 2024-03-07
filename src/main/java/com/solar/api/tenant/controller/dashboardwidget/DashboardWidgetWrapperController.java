package com.solar.api.tenant.controller.dashboardwidget;

import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.tenant.service.dashboardwidget.DashboardWidgetWrapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("DashboardWidgetWrapperController")
@RequestMapping(value = "/dashboard/widget")
public class DashboardWidgetWrapperController {

    private final DashboardWidgetWrapper dashboardWidgetWrapper;

    public DashboardWidgetWrapperController(DashboardWidgetWrapper dashboardWidgetWrapper) {
        this.dashboardWidgetWrapper = dashboardWidgetWrapper;
    }

    // addWidget
    @PostMapping("/addOrUpdate/{widgetId}")
    public BaseResponse<Object> addOrUpdateWidget(@PathVariable Long widgetId) {
        return dashboardWidgetWrapper.addWidget(widgetId);
    }

    @GetMapping(value = "/structureList", produces = {MediaType.APPLICATION_JSON_VALUE})
    public BaseResponse<Object> getWidgetsStructureListAndIds() {
        return dashboardWidgetWrapper.getWidgetsStructureListAndIds();
    }

    @GetMapping("/data/{widgetId}")
    public BaseResponse<Object> getWidgetDataById(@RequestHeader("Comp-Key") Long compKey,
                                                  @PathVariable("widgetId") Long widgetId,
                                                  @RequestParam(value = "param", required = false) String param) {
        return dashboardWidgetWrapper.getWidgetDataById(widgetId, compKey, param);
    }
}
