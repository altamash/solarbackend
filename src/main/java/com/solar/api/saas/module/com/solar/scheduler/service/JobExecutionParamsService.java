package com.solar.api.saas.module.com.solar.scheduler.service;

import com.solar.api.saas.model.JobExecutionParams;

import java.util.List;

public interface JobExecutionParamsService {

    JobExecutionParams save(JobExecutionParams jobExecutionParams);

    JobExecutionParams getByJobExecutionParamsId(Long id);

    List<JobExecutionParams> getByScheduledJobId(Long id);

    List<JobExecutionParams> getAll();

    void delete(Long id);

    JobExecutionParams getByScheduleJobIdAndKeyString(Long jobId, String keyString);
    void deleteAll(List<JobExecutionParams> jobExecutionParams);
}
