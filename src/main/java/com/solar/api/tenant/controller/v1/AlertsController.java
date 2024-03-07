package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.module.com.solar.batch.service.MonitorPlatformUtilityService;
import com.solar.api.tenant.service.alerts.PerformanceAlert;
import com.solar.api.tenant.service.alerts.ProjectionUtils;
import com.solar.api.tenant.service.alerts.UnderPerformanceAlert;
import com.solar.api.saas.service.integration.BaseResponse;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController("AlertsController")
@RequestMapping(value = "/alert")
public class AlertsController {

    private final MonitorPlatformUtilityService monitorPlatformUtilityService;
    private final PerformanceAlert performanceAlert;
    private final UnderPerformanceAlert underPerformanceAlert;
    private final ProjectionUtils projectionUtils;

    public AlertsController(MonitorPlatformUtilityService monitorPlatformUtilityService, PerformanceAlert performanceAlert, UnderPerformanceAlert underPerformanceAlert, ProjectionUtils projectionUtils) {
        this.monitorPlatformUtilityService = monitorPlatformUtilityService;
        this.performanceAlert = performanceAlert;
        this.underPerformanceAlert = underPerformanceAlert;
        this.projectionUtils = projectionUtils;
    }

    @GetMapping("/performance")
    public List<BaseResponse> performance() {
        return performanceAlert.generate();
    }

    @GetMapping("/underPerformance")
    public List<BaseResponse> underPerformance(@RequestParam(required = false) @ApiParam("yyyy-MM") String yearMonth) {
        return underPerformanceAlert.generate(yearMonth);
    }

    @PostMapping("/disableProjection/status")
    public BaseResponse enableDisableProjection(@RequestParam("sub_id") String subscriptionIds,
                                                                                       @RequestParam(value = "status", required = false) Boolean status,
                                                                                       @RequestParam("variant_id") String variant_id,
                                                                                        @RequestHeader("Tenant-id") String tenantId) {
        return projectionUtils.enableDisableProjection(subscriptionIds, status, variant_id, tenantId);
    }
}
