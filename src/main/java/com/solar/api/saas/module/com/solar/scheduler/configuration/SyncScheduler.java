package com.solar.api.saas.module.com.solar.scheduler.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.saas.module.com.solar.scheduler.service.JobSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class SyncScheduler implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncScheduler.class);
    private static final Integer CANCEL_SCHEDULED_TASK_DELAY_THRESHOLD_IN_SECONDS = 5;

    private String jobName = "Job";

    @Autowired
    @Lazy
    private TaskScheduler taskScheduler;

    private Date nextExecutionTime = null;


    private ScheduledTaskRegistrar scheduledTaskRegistrar;
    private ScheduledFuture<?> scheduledFuture;

    private Integer scheduleInSeconds = 917;
    private Integer processingTimeInSeconds = 25;

    @Autowired
    private JobSchedulerService jobSchedulerService;

    Runnable runnable = () -> System.out.println("Thread started at " + new Date());

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(); //single threaded by default
    }

    @Override
    public synchronized void configureTasks(@NotNull @org.jetbrains.annotations.NotNull ScheduledTaskRegistrar scheduledTaskRegistrar) {

        if (this.scheduledTaskRegistrar == null) {
            this.scheduledTaskRegistrar = scheduledTaskRegistrar;
        }

        this.scheduledTaskRegistrar.setScheduler(taskScheduler);
        scheduledFuture = Objects.requireNonNull(this.scheduledTaskRegistrar.getScheduler())
                .schedule(runnable, triggerContext -> {
                    Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                    List<JobScheduler> jobSchedulers = null;
                    LOGGER.info("Analyzer triggered...");
                    LOGGER.info(">>>>>>>>>>>>>>> Before JobSchedulerService.analyzer() via SyncScheduler#configureTasks()");
                    jobSchedulers = jobSchedulerService.analyzer(this.scheduledTaskRegistrar, false);
                    LOGGER.info("<<<<<<<<<<<<<<< After JobSchedulerService.analyzer() via SyncScheduler#configureTasks()");
                    LOGGER.info("{} Job(s) synced", jobSchedulers.size());
                    if (lastActualExecutionTime != null) {
                        lastActualExecutionTime = new Date();
                    }
                    Calendar cal = Calendar.getInstance();
                    if (lastActualExecutionTime != null) {
                        cal.setTime(lastActualExecutionTime);
                    }
                    cal.add(Calendar.SECOND, scheduleInSeconds);
                    this.nextExecutionTime = cal.getTime();
                    LOGGER.info(
                            "{} sync : lastActualExecutionTime {}. Next schedule : {}. If the schedule has already " +
                                    "passed, it would kick-in right away",
                            this.jobName,
                            lastActualExecutionTime,
                            nextExecutionTime);
                    System.out.println(lastActualExecutionTime +
                            ". Next schedule : "
                            + nextExecutionTime +
                            ". If the schedule has already passed, it would kick-in right away");
                    return nextExecutionTime;
                });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartUp() {
        List<JobScheduler> jobSchedulers = jobSchedulerService.findByState(AppConstants.RUNNING);
        jobSchedulers.forEach(js -> {
            js.setState(AppConstants.QUEUED);
            jobSchedulerService.saveOrUpdate(js);
            jobSchedulerService.batchNotification(js.getJobName(),js.getId(),"","JOB QUEUED");
        });
        LOGGER.info(">>>>>>>>>>>>>>> Before JobSchedulerService.analyzer() via runAfterStartUp()");
        jobSchedulers = jobSchedulerService.analyzer(this.scheduledTaskRegistrar, true);
        LOGGER.info("<<<<<<<<<<<<<<< After JobSchedulerService.analyzer() via runAfterStartUp()");
        LOGGER.info("{} Job(s) synced", jobSchedulers.size());
    }

    public synchronized void updateSchedule(Integer syncScheduleInSeconds) {
        LOGGER.info("Sync schedule is updated in DB for {} from {} to {} seconds", jobName, scheduleInSeconds,
                syncScheduleInSeconds);
        this.scheduleInSeconds = syncScheduleInSeconds;

        long delayInSeconds = this.scheduledFuture.getDelay(TimeUnit.SECONDS);
        LOGGER.info("Current scheduledTask delay {}", delayInSeconds);

        if (delayInSeconds >= 0) {
            LOGGER.info("Sync run is already in process. New schedule will take effect after the current run");
        } else if (delayInSeconds >= CANCEL_SCHEDULED_TASK_DELAY_THRESHOLD_IN_SECONDS) {
            LOGGER.info(
                    "Next sync is less than {} seconds away. after the next run, schedule will automatically be " +
                            "adjusted.",
                    CANCEL_SCHEDULED_TASK_DELAY_THRESHOLD_IN_SECONDS);
        } else {
            LOGGER.info(
                    "Next sync is more than {} seconds away. scheduledFuture.delay() is {}. Hence cancelling the " +
                            "schedule and rescheduling.",
                    CANCEL_SCHEDULED_TASK_DELAY_THRESHOLD_IN_SECONDS, delayInSeconds);

            boolean cancel = this.scheduledFuture.cancel(false); //do not interrupt the current run if it kicked in.
            LOGGER.info(
                    "future.cancel() returned {}. isCancelled() : {} isDone : {}",
                    cancel,
                    scheduledFuture.isCancelled(),
                    scheduledFuture.isDone());
            LOGGER.info("Reconfiguring sync for {} with new schedule {}", jobName, syncScheduleInSeconds);
        }
    }

    public ObjectNode getExecutor() throws ExecutionException, InterruptedException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        ((ThreadPoolTaskScheduler) taskScheduler).getScheduledExecutor();
        scheduledFuture.get();
        return response;
    }

    public String removeJob(Long id) {
        return jobSchedulerService.removeScheduledJob(id);
    }

    public void trigger(int timeInSeconds) {
        updateSchedule(timeInSeconds);
    }

    public void abortSync() {
        scheduledFuture.cancel(false);
        scheduledFuture.isDone();
        scheduledTaskRegistrar.destroy();
    }

    public void destroy(int timeInSeconds) throws ExecutionException, InterruptedException {
        scheduledFuture.cancel(false);
        scheduledFuture.isDone();
        jobSchedulerService.emptyScheduledTaskList();
        scheduledTaskRegistrar.destroy();
        trigger(timeInSeconds);
    }

    public void threadTime(Integer timeInSec) throws InterruptedException {
        LOGGER.info("Sync kicked in..");
        TimeUnit.SECONDS.sleep(timeInSec);
        LOGGER.info("Sync completed in..");
    }
}
