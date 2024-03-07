package com.solar.api.tenant.service.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.model.process.JobManagerTenant;

public interface JobHandlerTenant {

    JobManagerTenant addJobManager(String status, ObjectNode requestMessage);

    void updateJobManager(JobManagerTenant jobManager);
}
