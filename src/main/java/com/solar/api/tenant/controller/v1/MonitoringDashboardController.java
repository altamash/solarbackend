package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.tenant.mapper.pvmonitor.*;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.MonitoringDashboardService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.MonitoringDashboardWidgetService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Optional;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("MonitoringDashboardController")
@RequestMapping(value = "/monitoringDashboard")
public class MonitoringDashboardController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("monitoringYearlyService")
    private MonitoringDashboardService monitoringDashboardYearlyService;

    @Autowired
    @Qualifier("monitoringQuarterlyService")
    private MonitoringDashboardService monitoringDashboardQuarterlyService;

    @Autowired
    @Qualifier("monitoringMonthlyService")
    private MonitoringDashboardService monitoringDashboardMonthlyService;

    @Autowired
    @Qualifier("monitoringWeeklyService")
    private MonitoringDashboardService monitoringDashboardWeeklyService;

    @Autowired
    @Qualifier("monitoringDailyService")
    private MonitoringDashboardService monitoringDashboardDailyService;

    @Autowired
    private MonitoringDashboardWidgetService monitoringDashboardWidgetService;
    @Autowired
    private Utility utility;

    @PostMapping("/getCumulativeGraphData")
    public ResponseEntity<?> getCumulativeGraphData(
            @RequestBody MonitorAPIAuthBody body,
            @RequestParam(value = "graphType", required = true) String graphType) {

        MonitoringDashboardService service;
        switch (graphType.toUpperCase()) {
            case Constants.MONITORING_DASHBOARD_CONSTANTS.YEARLY:
                service = monitoringDashboardYearlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.QUARTERLY:
               service = monitoringDashboardQuarterlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.MONTHLY:
                service = monitoringDashboardMonthlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.WEEKLY:
                service = monitoringDashboardWeeklyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.DAILY:
                service = monitoringDashboardDailyService;
                break;
            default:
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid graphType");
        }

        return service.getCumulativeGraphData(body);
    }

    @PostMapping("/getSubscriptionComparativeGraphData")
    public ResponseEntity<?> getSubscriptionComparativeGraphData(
            @RequestBody MonitorAPIAuthBody body,
            @RequestParam(value = "graphType", required = true) String graphType) {

        MonitoringDashboardService service;
        switch (graphType.toUpperCase()) {
            case Constants.MONITORING_DASHBOARD_CONSTANTS.YEARLY:
                service = monitoringDashboardYearlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.QUARTERLY:
                service = monitoringDashboardQuarterlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.MONTHLY:
                service = monitoringDashboardMonthlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.WEEKLY:
                service = monitoringDashboardWeeklyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.DAILY:
                service = monitoringDashboardDailyService;
                break;
            default:
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid graphType");
        }

        return service.getSubscriptionComparativeGraphData(body);
    }

    @PostMapping("/getCustomerComparativeGraphData")
    public ResponseEntity<?> getCustomerComparativeGraphData(
            @RequestBody MonitorAPIAuthBody body,
            @RequestParam(value = "graphType", required = true) String graphType) {

        MonitoringDashboardService service;
        switch (graphType.toUpperCase()) {
            case Constants.MONITORING_DASHBOARD_CONSTANTS.YEARLY:
                service = monitoringDashboardYearlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.QUARTERLY:
                service = monitoringDashboardQuarterlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.MONTHLY:
                service = monitoringDashboardMonthlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.WEEKLY:
                service = monitoringDashboardWeeklyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.DAILY:
                service = monitoringDashboardDailyService;
                break;
            default:
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid graphType");
        }

        return service.getCustomerComparativeGraphData(body);
    }

    // this api used on power monitoring dashboard for customer and subscriptions
    @PostMapping("/getWidgetData")
    public ResponseEntity<?> getWidgetData(@RequestBody MonitorAPIAuthBody body)
    {
        return monitoringDashboardWidgetService.getWidgetData(body);
    }

    // this api used on power monitoring sites dashboard
    @PostMapping("/getSitesWidgetData")
    public ResponseEntity<?> getSitesWidgetData(@RequestBody MonitorAPIAuthBody body)
    {
        return monitoringDashboardWidgetService.getSitesWidgetData(body);
    }

    //this api used on power monitoring sites dashboard widget click detail
    @PostMapping("/getSitesWidgetDataDetail")
    public ResponseEntity<?> getSitesWidgetDataDetail(@RequestBody MonitorAPIAuthBody body)
    {
        return monitoringDashboardWidgetService.getSitesWidgetDataDetail(body);
    }

    // this api used on power monitoring sites dashboard to validate count of sites
    @GetMapping("/validateSitesCount")
    public ResponseEntity<?> validateSitesCount(@RequestParam(value = "count", required = true) Long count){

        return monitoringDashboardWidgetService.validateSitesSelectionCountAllowed(count);
    }

    // this api used on power monitoring sites dashboard
    @PostMapping("/getSiteComparativeGraphData")
    public ResponseEntity<?> getSiteComparativeGraphData(
            @RequestBody MonitorAPIAuthBody body,
            @RequestParam(value = "graphType", required = true) String graphType) {

        MonitoringDashboardService service;
        switch (graphType.toUpperCase()) {
            case Constants.MONITORING_DASHBOARD_CONSTANTS.YEARLY:
                service = monitoringDashboardYearlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.QUARTERLY:
                service = monitoringDashboardQuarterlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.MONTHLY:
                service = monitoringDashboardMonthlyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.WEEKLY:
                service = monitoringDashboardWeeklyService;
                break;
            case Constants.MONITORING_DASHBOARD_CONSTANTS.DAILY:
                service = monitoringDashboardDailyService;
                break;
            default:
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid graphType");
        }
        return service.getSitesComparativeGraphData(body);
    }

    // this api used on power monitoring dashboard for customer and subscriptions
    @PostMapping("/getYieldWidgetData")
    public ResponseEntity<?> getYieldWidgetData(@RequestBody MonitorAPIAuthBody body)
    {
        return monitoringDashboardWidgetService.getYieldWidgetData(body);
    }
}
