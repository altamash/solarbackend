package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.AppConstants;
import com.solar.api.saas.module.com.solar.batch.configuration.components.Listener.JobExecutionListener;
import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;
import com.solar.api.saas.module.com.solar.scheduler.service.JobSchedulerService;
import com.solar.api.saas.module.com.solar.utility.BatchEngineUtilityService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class JobListener extends JobExecutionListener {

    @Autowired
    JobSchedulerService jobSchedulerService;
    @Autowired
    BatchEngineUtilityService batchEngineUtilityService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
//        super.beforeJob(jobExecution);

        JobInstance jobInstance = jobExecution.getJobInstance();
        JobParameters jobParameters = jobExecution.getJobParameters();
        if (jobParameters.getString("jobScheduleId") != null) {
            JobScheduler jobScheduler = jobSchedulerService.findById(Long.valueOf(Objects.requireNonNull(jobParameters.getString("jobScheduleId"))));
            jobScheduler.setJobInstanceId(jobInstance.getInstanceId());
            jobSchedulerService.saveOrUpdate(jobScheduler);
        }
//            if (!jobExecution.getId().equals(lastJobExecution.getId())) {
//                if (lastJobExecution.isRunning()) {
////                    JobManagerTenant jobManagerTenant = jobManagerTenantService.findById(Long.valueOf(Objects.requireNonNull(jobParameters.getString("jobId"))));
//                    JobManagerTenant jobManagerTenant = jobManagerTenantService.findByBatchId(jobInstance.getInstanceId());
////                    jobManagerTenant.setBatchId(jobInstance.getInstanceId());
//                    jobManagerTenant.setEndDatetime(new Date());
//                    jobManagerTenant.setDuration(jobManagerTenant.getEndDatetime().getTime() - jobManagerTenant.getExecutionDatetime().getTime());
//                    jobManagerTenant.setStatus("STOPPED");
//                    jobManagerTenantService.toUpdateMapper(jobManagerTenant);
//                    jobExecution.stop();
//                }
//            }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
//        super.afterJob(jobExecution);

        JobParameters jobParameters = jobExecution.getJobParameters();
//        if (jobParameters.equals("jobName") || Objects.requireNonNull(jobParameters.getString("jobName")).equals(AppConstants.SCHEDULED_INVOICING)) {
//            batchEngineUtilityService.scheduleJobWithDifferential(jobExecution);
//        }
        if (jobParameters.getString("jobScheduleId") != null) {
            JobScheduler jobScheduler = jobSchedulerService.findById(Long.valueOf(Objects.requireNonNull(jobParameters.getString("jobScheduleId"))));
            jobScheduler.setState(AppConstants.SCHEDULED);
            jobSchedulerService.saveOrUpdate(jobScheduler);
        }
    }
}
