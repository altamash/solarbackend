package com.solar.api.saas.service.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.mapper.process.job.PagedJobManagerDTO;
import com.solar.api.saas.model.JobManager;
import org.slf4j.Logger;

public interface JobManagerService {

    JobManager saveOrUpdate(JobManager jobManager);

    JobManager add(String jobName, ObjectNode requestMessage, String status, Long batchId, Logger LOGGER);

    JobManager findByJobName(String jobName);

    void update(JobManager jobManager, String status, Long batchId, Logger LOGGER);

    JobManager findById(Long id);

    PagedJobManagerDTO findAll(int pageNumber, Integer pageSize, String sort);

    void delete(Long id);

    void deleteAll();
}
