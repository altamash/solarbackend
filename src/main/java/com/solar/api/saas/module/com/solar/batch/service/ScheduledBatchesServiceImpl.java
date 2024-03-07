package com.solar.api.saas.module.com.solar.batch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.model.extended.BatchDefinition;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.saas.module.com.solar.scheduler.service.JobSchedulerService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Service
public class ScheduledBatchesServiceImpl implements ScheduledBatchesService{

    @Autowired
    JobSchedulerService jobSchedulerService;

    @Override
    public void AnalyticalCalculation(BatchDefinition batchDefinition, ApplicationContext applicationContext,
                                      JobLauncher jobLauncher, JobScheduler jobScheduler) throws JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
            NoSuchJobInstanceException {
        jobScheduler.setState("RUNNING");
        jobSchedulerService.saveOrUpdate(jobScheduler);
        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);
        ObjectNode requestMessage = new ObjectMapper().createObjectNode();
        requestMessage.put("Start Time", System.currentTimeMillis());
        requestMessage.put("Job Name", batchDefinition.getJobName());
//        JobManagerTenant jobManagerTenant = jobManagerTenantService.add(batchDefinition.getJobName(), requestMessage, EJobStatus.RUNNING.toString(), null, LOGGER);
        JobExecution jobExecution = jobLauncher.run((Job) applicationContext.getBean(batchDefinition.getBean()), parameters);
//        jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), jobExecution.getJobId(), LOGGER);
        jobScheduler.setState("COMPLETED");
        jobSchedulerService.saveOrUpdate(jobScheduler);
    }
}
