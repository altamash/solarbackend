package com.solar.api.tenant.service.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.model.process.JobManagerTenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JobHandlerTenantImpl implements JobHandlerTenant {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private JobManagerTenantService jobManagerTenantService;

    @Override
    public JobManagerTenant addJobManager(String status, ObjectNode requestMessage) {
        JobManagerTenant jobManagerTenant = JobManagerTenant.builder()
                .status(status)
                .executionDatetime(new Date())
                .requestMessage(requestMessage.toPrettyString())
                .build();
        jobManagerTenant = jobManagerTenantService.saveOrUpdate(jobManagerTenant);
        LOGGER.info("Job: ['" + jobManagerTenant.getId() + "'] status: " + status);
        return jobManagerTenant;
    }

    @Override
    public void updateJobManager(JobManagerTenant jobManagerTenant) {
        jobManagerTenant.setStatus("COMPLETED");
        jobManagerTenant.setEndDatetime(new Date());
        jobManagerTenantService.saveOrUpdate(jobManagerTenant);
        LOGGER.info("Job ['" + jobManagerTenant.getId() + "'] completed");
    }
}
