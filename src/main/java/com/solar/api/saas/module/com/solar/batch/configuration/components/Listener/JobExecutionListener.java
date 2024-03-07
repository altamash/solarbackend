package com.solar.api.saas.module.com.solar.batch.configuration.components.Listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class JobExecutionListener implements org.springframework.batch.core.JobExecutionListener {

    @Autowired
    private JobExplorer jobExplorer;

    @Override
    public void beforeJob(JobExecution jobExecution) {

        int runningJobsCount = jobExplorer.findRunningJobExecutions(jobExecution.getJobInstance().getJobName()).size();
        if (runningJobsCount > 1) {
            throw new RuntimeException("There are already active running instances of this job, Please cancel those " +
                    "executions first.");
        }

    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            //job success
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            //job failure
        }

    }
}
