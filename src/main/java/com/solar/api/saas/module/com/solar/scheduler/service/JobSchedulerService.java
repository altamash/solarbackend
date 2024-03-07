package com.solar.api.saas.module.com.solar.scheduler.service;

import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Date;
import java.util.List;

public interface JobSchedulerService {

    JobScheduler findByJobNameAndCronExpression(String jobName, String cronExpression);

    JobScheduler saveOrUpdate(JobScheduler jobScheduler);

    JobScheduler createInstance(JobScheduler jobScheduler);

    JobScheduler findByJobInstanceId(Long id);

    JobScheduler findById(Long id);

    JobScheduler findByJobName(String jobName);

    List<JobScheduler> findAll();

    List<JobScheduler> findByStatus(String status);

    List<JobScheduler> findByState(String state);

    void cronTest(String second, String minute, String hour, String date, String month, String day);

    CronTask cronTaskFunction(Long jobScheduleId);

    void jobCheck(Long id);

    List<JobScheduler> analyzer(ScheduledTaskRegistrar scheduledTaskRegistrar, boolean viaStartup);

    void scheduleJob(ScheduledTaskRegistrar scheduledTaskRegistrar, JobScheduler jobScheduler);

    CronTask trigger(Long id);

    void stopScheduler();

    String removeScheduledJob(Long id);

    void delete(Long id);

    void markAsInactive(Long id);

    void emptyScheduledTaskList();

    JobScheduler findLatestJobByJobName(String jobName);

    Date getNextValidTime(String cronExpression);

    /**
     * JOB NOTIFICATION EMAIL
     *
     * @param jobName
     * @param jobId
     * @param stackTrace
     */
    void batchNotification(String jobName, Long jobId, String stackTrace, String subject);

}
