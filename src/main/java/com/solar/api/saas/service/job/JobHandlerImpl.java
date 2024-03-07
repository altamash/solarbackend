package com.solar.api.saas.service.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.saas.model.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class JobHandlerImpl implements JobHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private JobManagerService jobManagerService;
    @Autowired
    private JobExplorer jobExplorer;

    @Override
    public JobExecution getJobExecutionContext(String name) {
        return jobExplorer.getLastJobExecution(Objects.requireNonNull(jobExplorer.getLastJobInstance(name)));
    }

    @Override
    public Long getLastJobInstanceIdByJobName(String jobName) {
        JobInstance jobInstance = jobExplorer.getLastJobInstance(jobName);
        if (jobInstance != null) {
            return jobInstance.getInstanceId();
        }
        return Long.valueOf(0);
    }

    @Override
    public JobParameters getJobParametersByJobName(String jobName) {
        JobInstance jobInstance = jobExplorer.getLastJobInstance(jobName);
        if(jobInstance != null){
            return Objects.requireNonNull(jobExplorer.getLastJobExecution(jobInstance)).getJobParameters();
        }
        return null;
    }

    @Override
    public Long getLastJobInstanceId(String jobName) {
        JobInstance jobInstance = jobExplorer.getLastJobInstance(AppConstants.BILLING_CREDITS_IMPORT_JOB);
        if (jobInstance != null) {
            return jobInstance.getInstanceId();
        }
        return Long.valueOf(0);
    }

    @Override
    public JobParameters getJobParameters(String jobName) {
        JobInstance jobInstance = jobExplorer.getLastJobInstance(AppConstants.BILLING_CREDITS_IMPORT_JOB);
        if(jobInstance != null){
            return Objects.requireNonNull(jobExplorer.getLastJobExecution(jobInstance)).getJobParameters();
        }
        return null;
    }

    @Override
    public JobManager addJobManager(String status, ObjectNode requestMessage) {
        JobManager jobManager = JobManager.builder()
                .status(status)
                .executionDatetime(new Date())
                .requestMessage(requestMessage.toPrettyString())
                .build();
        jobManager = jobManagerService.saveOrUpdate(jobManager);
        LOGGER.info("Job: ['" + jobManager.getId() + "'] status: " + status);
        return jobManager;
    }

    @Override
    public void updateJobManager(JobManager jobManager) {
        jobManager.setStatus("COMPLETED");
        jobManager.setEndDatetime(new Date());
        jobManagerService.saveOrUpdate(jobManager);
        LOGGER.info("Job ['" + jobManager.getId() + "'] completed");
    }
}
