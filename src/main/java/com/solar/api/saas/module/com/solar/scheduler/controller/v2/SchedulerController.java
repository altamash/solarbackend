package com.solar.api.saas.module.com.solar.scheduler.controller.v2;

import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.module.com.solar.scheduler.v2.SyncSchedulerV2;
import com.solar.api.saas.module.com.solar.utility.BatchEngineUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@PreAuthorize("checkAccess()")
@RestController("ScheduleControllerV2")
@RequestMapping("/v2")
public class SchedulerController {
    @Autowired
    private SyncSchedulerV2 syncSchedulerV2;
    @Autowired
    private BatchEngineUtilityService batchEngineUtilityService;


    @PostMapping(path = "/schedule")
    public BaseResponse<?> scheduleJob(@RequestParam(name = "jobId", required = true) Long jobId) {
        return batchEngineUtilityService.scheduleJob(jobId);
    }

    @GetMapping(path = "/remove/{jobId}")
    public void removeJob(@PathVariable String jobId) {
        batchEngineUtilityService.removeScheduledTask(jobId);
    }

    @GetMapping(path = "/removeAll")
    public void removeAll() {
        syncSchedulerV2.removeAllScheduledTask();
    }
}
