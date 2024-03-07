package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;

public interface ScheduledBatchesService {

    void AnalyticalCalculation(BatchDefinition batchDefinition, ApplicationContext applicationContext,
                               JobLauncher jobLauncher, JobScheduler jobScheduler) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobInstanceException;
}
