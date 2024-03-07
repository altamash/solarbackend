package com.solar.api.saas.service.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.model.JobManager;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

public interface JobHandler {

    JobManager addJobManager(String status, ObjectNode requestMessage);

    void updateJobManager(JobManager jobManager);

    Long getLastJobInstanceId(String jobName);

    JobParameters getJobParameters(String jobName);

    JobExecution getJobExecutionContext(String name);

    Long getLastJobInstanceIdByJobName(String jobName);

    JobParameters getJobParametersByJobName(String jobName);


}
