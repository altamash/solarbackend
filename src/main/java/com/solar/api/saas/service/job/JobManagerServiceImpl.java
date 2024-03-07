package com.solar.api.saas.service.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.mapper.process.job.JobManagerMapper;
import com.solar.api.saas.mapper.process.job.PagedJobManagerDTO;
import com.solar.api.saas.model.JobManager;
import com.solar.api.saas.model.SaasSchema;
import com.solar.api.saas.repository.JobManagerRepository;
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
import java.util.stream.Collectors;

@Service
//@Transactional("masterTransactionManager")
public class JobManagerServiceImpl implements JobManagerService {

    @Autowired
    private JobManagerRepository repository;

    @Override
    public JobManager saveOrUpdate(JobManager jobManager) {
        if (jobManager.getExecutionDatetime() != null && jobManager.getEndDatetime() != null) {
            jobManager.setDuration(jobManager.getEndDatetime().getTime() - jobManager.getExecutionDatetime().getTime());
        }
        return repository.save(jobManager);
    }

    @Override
    public JobManager add(String jobName, ObjectNode requestMessage, String status, Long batchId, Logger LOGGER) {
        JobManager jobManager = JobManager.builder()
                .status(status)
                .build();
        jobManager.setJobName(jobName);
        jobManager.setBatchId(batchId);
        jobManager.setRequestMessage(requestMessage != null ? requestMessage.toPrettyString() : null);
        jobManager.setExecutionDatetime(new Date());
        jobManager = saveOrUpdate(jobManager);
        Long jobId = jobManager.getId();
        LOGGER.info("Job " + jobManager.getJobName() + " with id ['" + jobId + "'] started");
        return jobManager;
    }

    @Override
    public JobManager findByJobName(String jobName) {
        return repository.findByJobName(jobName);
    }

    @Override
    public void update(JobManager jobManager, String status, Long batchId, Logger LOGGER) {
        jobManager.setStatus(status);
        jobManager.setBatchId(batchId);
        jobManager.setEndDatetime(new Date());
        saveOrUpdate(jobManager);
        LOGGER.info("Job " + jobManager.getJobName() + " with id ['" + jobManager.getId() + "'] completed");
    }

    @Override
    public JobManager findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(JobManager.class, id));
    }

    @Override
    public PagedJobManagerDTO findAll(int pageNumber, Integer pageSize, String sort) {
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
        Page<JobManager> jobs = repository.findAll(pageable);
        return PagedJobManagerDTO.builder()
                .totalItems(jobs.getTotalElements())
                .jobs(JobManagerMapper.toJobManagerDTOs(jobs.getContent()))
                .build();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
