package com.solar.api.tenant.controller.v1;

import com.solar.api.saas.model.JobExecutionParams;
import com.solar.api.saas.module.com.solar.batch.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("SubscriptionControllerAdhoc")
@RequestMapping(value = "/subscription/adhoc")
@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
public class SubscriptionControllerAdhoc {


    @Autowired
    private BatchService batchService;

    @GetMapping("/addMonitorReadingsBatchAdhoc")
    public void addMonitorReadingsBatchAdhoc(@RequestParam(value = "subscriptionType") String subscriptionType,
                                             @RequestParam(value = "date") String date) {
        List<JobExecutionParams> jobExecutionParams = new ArrayList<>();

        /**
         * passing date as empty parameter to execute function on current date
         */
        if (StringUtils.hasText(subscriptionType) && date != null) {
            jobExecutionParams.add(JobExecutionParams.builder()
                    .keyString("subscriptionType")
                    .valueString(subscriptionType)
                    .identifying("Y")
                    .build());
            jobExecutionParams.add(JobExecutionParams.builder()
                    .keyString("date")
                    .valueString(date.equals("") ? null : date)
                    .identifying("Y")
                    .build());
            batchService.addMonitorReadings(jobExecutionParams);
        }
    }

    @GetMapping("/addMonitorReadingsBySubscriptionIds/{subscriptionIdsCsv}")
    public void addMonitorReadingsBySubscriptionIds(@PathVariable(value = "subscriptionIdsCsv", required = true)
                                                    String subscriptionIdsCsv) {
        List<Long> subscriptionRateMatrixIds = null;
        subscriptionRateMatrixIds =
                Arrays.stream(subscriptionIdsCsv.split(",")).map(id -> Long.parseLong(id.trim())).collect(Collectors.toList());
        batchService.addMonitorReadingsBySubscriptions(subscriptionRateMatrixIds);
    }

}
