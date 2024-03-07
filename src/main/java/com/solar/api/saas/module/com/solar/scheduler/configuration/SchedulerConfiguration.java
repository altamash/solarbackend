package com.solar.api.saas.module.com.solar.scheduler.configuration;

import com.solar.api.saas.module.com.solar.scheduler.service.JobSchedulerService;
import com.solar.api.saas.module.com.solar.scheduler.v2.jobs.SolrenViewTaskConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class SchedulerConfiguration {

    @Autowired
    private SolrenViewTaskConfig solrenViewTaskConfig;
    @Autowired
    JobSchedulerService jobSchedulerService;
    @Autowired
    private TaskScheduler taskScheduler;

    Map<String, ScheduledFuture<?>> jobsMap = new HashMap<>();

//    @EventListener(ApplicationReadyEvent.class)
//    public void runAfterStartUp() {
//        List<JobScheduler> jobSchedulers = jobSchedulerService.findAll();
//        jobSchedulers.forEach(js -> {
//            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(solrenViewTaskConfig, new CronTrigger(js.getCronExpression(),
//                    TimeZone.getTimeZone(TimeZone.getDefault().getID())));
//            jobsMap.put(js.getTaskId(), scheduledTask);
//        });
//        System.out.println(jobsMap.size() + " jobs rescheduled");
//    }
}
