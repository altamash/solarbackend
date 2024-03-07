package com.solar.api.saas.module.com.solar.scheduler.service;

import com.solar.api.saas.model.JobExecutionParams;
import com.solar.api.saas.module.com.solar.scheduler.repository.JobExecutionParamsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile({"dev", "batch", "stage", "preprod", "prod", "newprod"})
@Component
public class JobExecutionParamsServiceImpl implements JobExecutionParamsService {

    @Autowired
    JobExecutionParamsRepository jobExecutionParamsRepository;

    @Override
    public JobExecutionParams save(JobExecutionParams jobExecutionParams) {
        return jobExecutionParamsRepository.save(jobExecutionParams);
    }

    @Override
    public JobExecutionParams getByJobExecutionParamsId(Long id) {
        return jobExecutionParamsRepository.getOne(id);
    }

    @Override
    public List<JobExecutionParams> getByScheduledJobId(Long id) {
        return jobExecutionParamsRepository.findByScheduledJobId(id);
    }

    @Override
    public List<JobExecutionParams> getAll() {
        return jobExecutionParamsRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        jobExecutionParamsRepository.deleteById(id);
    }

    @Override
    public JobExecutionParams getByScheduleJobIdAndKeyString(Long jobId, String keyString) {
        return jobExecutionParamsRepository.getByScheduleJobIdAndKeyString(jobId, keyString);
    }

    @Override
    public void deleteAll(List<JobExecutionParams> jobExecutionParams) {
        jobExecutionParamsRepository.deleteAll(jobExecutionParams);
    }
}
