package com.solar.api.tenant.service.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.process.job.PagedJobManagerTenantDTO;
import com.solar.api.tenant.model.process.JobManagerTenant;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public interface JobManagerTenantService {

    JobManagerTenant saveOrUpdate(JobManagerTenant jobManagerTenant);

    List<JobManagerTenant> findByBatchId(Long id);

    JobManagerTenant findByJobNameOrderByIdDesc(String jobName);

    JobManagerTenant toUpdateMapper(JobManagerTenant jobManagerTenant);

    JobManagerTenant add(String jobName, ObjectNode requestMessage, String status, Long batchId, Logger LOGGER);

    JobManagerTenant findByJobName(String jobName);

    List<JobManagerTenant> findByJobNameAndStatus(String jobName, String status);

    Long findIdOfLastJobByJobNameAndStatus(String jobName, String status);

    void update(JobManagerTenant JobManagerTenant, String status, Long batchId, Logger LOGGER);

    void setCompleted(JobManagerTenant jobManager, Logger LOGGER);

    JobManagerTenant findById(Long id);

    JobManagerTenant findByIdNoThrow(Long id);

    PagedJobManagerTenantDTO findAll(int pageNumber, Integer pageSize, String sort);

    void delete(Long id);

    void deleteAll();
}
