package com.solar.api.tenant.service.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.tenant.mapper.process.job.JobManagerTenantMapper;
import com.solar.api.tenant.mapper.process.job.PagedJobManagerTenantDTO;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.repository.JobManagerTenantRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.process.job.JobManagerTenantMapper.toUpdatedJobManager;

@Service
//@Transactional("tenantTransactionManager")
public class JobManagerTenantServiceImpl implements JobManagerTenantService {

    @Autowired
    private JobManagerTenantRepository repository;

    @Override
    public JobManagerTenant saveOrUpdate(JobManagerTenant jobManagerTenant) {
        if (jobManagerTenant.getExecutionDatetime() != null && jobManagerTenant.getEndDatetime() != null) {
            jobManagerTenant.setDuration(jobManagerTenant.getEndDatetime().getTime() - jobManagerTenant.getExecutionDatetime().getTime());
        }
        return repository.save(jobManagerTenant);
    }

    @Override
    public List<JobManagerTenant> findByBatchId(Long id) {
        return repository.findByBatchId(id);
    }

    @Override
    public JobManagerTenant findByJobNameOrderByIdDesc(String jobName) {
        return repository.findByJobNameOrderByIdDesc(jobName);
    }

    @Override
    public JobManagerTenant toUpdateMapper(JobManagerTenant jobManagerTenant) {
        JobManagerTenant jobManagerTenantExist = null;
        if (jobManagerTenant.getId() != null) {
            jobManagerTenantExist = findById(jobManagerTenant.getId());
            if (jobManagerTenantExist == null) {
                throw new NotFoundException(JobManagerTenant.class, jobManagerTenant.getId());
            }
            saveOrUpdate(toUpdatedJobManager(jobManagerTenantExist, jobManagerTenant));
        }
        return jobManagerTenantExist;
    }

    @Override
    public JobManagerTenant add(String jobName, ObjectNode requestMessage, String status, Long batchId, Logger LOGGER) {
        JobManagerTenant jobManagerTenant = JobManagerTenant.builder()
                .status(status)
                .build();
        jobManagerTenant.setJobName(jobName);
        jobManagerTenant.setBatchId(batchId);
        jobManagerTenant.setRequestMessage(requestMessage != null ? requestMessage.toPrettyString() : null);
        jobManagerTenant.setExecutionDatetime(new Date());
        jobManagerTenant = saveOrUpdate(jobManagerTenant);
        Long jobId = jobManagerTenant.getId();
        LOGGER.info("Job " + jobManagerTenant.getJobName() + " with id ['" + jobId + "'] started");
        return jobManagerTenant;
    }

    @Override
    public JobManagerTenant findByJobName(String jobName) {
        return repository.findByJobName(jobName);
    }

    @Override
    public List<JobManagerTenant> findByJobNameAndStatus(String jobName, String status) {
        return repository.findByJobNameAndStatus(jobName, status);
    }

    @Override
    public Long findIdOfLastJobByJobNameAndStatus(String jobName, String status) {
        return repository.findIdOfLastJobByJobNameAndStatus(jobName, status);
    }

    @Override
    public void update(JobManagerTenant jobManagerTenant, String status, Long batchId, Logger LOGGER) {
        jobManagerTenant.setStatus(status);
        jobManagerTenant.setBatchId(batchId);
        jobManagerTenant.setEndDatetime(new Date());
        saveOrUpdate(jobManagerTenant);
        LOGGER.info("Job " + jobManagerTenant.getJobName() + " with id ['" + jobManagerTenant.getId() + "'] completed");
    }

    @Override
    public void setCompleted(JobManagerTenant jobManager, Logger LOGGER) {
        update(jobManager, EJobStatus.COMPLETED.toString(), null, LOGGER);
    }

    @Override
    public JobManagerTenant findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(JobManagerTenant.class, id));
    }

    @Override
    public JobManagerTenant findByIdNoThrow(Long id) {
        return repository.findById(id).orElse(null);
    }

    /*@Override
    public void addException(Exception e, JobManager jobManager) {
        jobManager.setErrors(true);
        jobManager.setErrorCount(jobManager.getErrorCount() + 1);
        StringBuilder builder = new StringBuilder();
        if (jobManager.getLog() != null) {
            builder.append(new String(jobManager.getLog()));
            builder.append("\n");
        }
        builder.append(e.getMessage());
        jobManager.setLog(builder.toString().getBytes());
        repository.save(jobManager);
    }*/

    @Override
    public PagedJobManagerTenantDTO findAll(int pageNumber, Integer pageSize, String sort) {
        Sort sortBy;
        if ("-1".equals(sort)) {
            sortBy = Sort.by(Sort.Direction.DESC, "-1".equals(sort) ? "createdAt" : sort);
        } else {
            List<String> sortColumns = Arrays.stream(sort.split(",")).collect(Collectors.toList());
            sortBy = Sort.by(sortColumns.get(0));
            for (int i = 1; i < sortColumns.size(); i++) {
                sortBy = sortBy.and(Sort.by(sortColumns.get(i)));
            }
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize == null ? SaasSchema.PAGE_SIZE : pageSize, sortBy);
        Page<JobManagerTenant> jobs = repository.findAll(pageable);
        return PagedJobManagerTenantDTO.builder()
                .totalItems(jobs.getTotalElements())
                .jobs(JobManagerTenantMapper.toJobManagerDTOs(jobs.getContent()))
                .build();
    }

    @Override
    public void delete(Long id) {
        JobManagerTenant jobManagerTenant = findById(id);
        repository.delete(jobManagerTenant);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
