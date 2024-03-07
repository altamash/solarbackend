package com.solar.api.saas.module.com.solar.scheduler.v2;

import com.solar.api.saas.module.com.solar.utility.BatchEngineUtilityService;
import com.solar.api.saas.module.com.solar.utility.BatchEngineUtilityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Service
public class SyncSchedulerV2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncSchedulerV2.class);

    @Autowired
    private TaskScheduler taskScheduler;

    Map<String, ScheduledFuture<?>> jobsMap = new HashMap<>();

    public void scheduleATask(String jobId, Runnable tasklet, String cronExpression) {
        LOGGER.info("Entering scheduleATask with job id: {} and tasklet: {} and cron expression {}", jobId, tasklet.getClass() ,cronExpression);
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(tasklet, new CronTrigger(cronExpression,
                TimeZone.getTimeZone(TimeZone.getDefault().getID())));
        jobsMap.put(jobId, scheduledTask);
        LOGGER.info("Exiting scheduleATask with jobs scheduled: {}", jobsMap.size());
    }

    public void removeScheduledTask(String jobId) {
        ScheduledFuture<?> scheduledTask = jobsMap.get(jobId);
        if (scheduledTask != null) {
            scheduledTask.isCancelled();
            scheduledTask.cancel(true);
//            jobsMap.remove(jobId);
        }
    }

    public void removeAllScheduledTask() {
        jobsMap.forEach((key, value) -> {
            ScheduledFuture<?> scheduledTask = jobsMap.get(key);
            scheduledTask.cancel(true);
            jobsMap.remove(key);
        });
    }
}
