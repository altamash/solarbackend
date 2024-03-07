package com.solar.api.saas.module.com.solar.scheduler.mapper;

import com.solar.api.saas.module.com.solar.scheduler.model.JobScheduler;

import java.util.List;
import java.util.stream.Collectors;

public class JobSchedulerMapper {

    public static JobScheduler toJobScheduler(JobSchedulerDTO jobSchedulerDTO) {
        if (jobSchedulerDTO == null) {
            return null;
        }
        return JobScheduler.builder()
                .id(jobSchedulerDTO.getId())
                .jobInstanceId(jobSchedulerDTO.getJobInstanceId())
                .jobName(jobSchedulerDTO.getJobName())
                .cronExpression(jobSchedulerDTO.getCronExpression())
                .scheduled(jobSchedulerDTO.getScheduled())
                .startedAt(jobSchedulerDTO.getStartedAt())
                .endedAt(jobSchedulerDTO.getEndedAt())
                .lastExecutionTime(jobSchedulerDTO.getLastExecutionTime())
                .nextExecutionTime(jobSchedulerDTO.getNextExecutionTime())
                .duration(jobSchedulerDTO.getDuration())
                .attemptCount(jobSchedulerDTO.getAttemptCount())
                .logUrl(jobSchedulerDTO.getLogUrl())
                .status(jobSchedulerDTO.getStatus())
                .message(jobSchedulerDTO.getMessage())
                .state(jobSchedulerDTO.getState())
                .build();
    }

    public static JobSchedulerDTO toJobSchedulerDTO(JobScheduler jobScheduler) {
        if (jobScheduler == null) {
            return null;
        }
        return JobSchedulerDTO.builder()
                .id(jobScheduler.getId())
                .jobInstanceId(jobScheduler.getJobInstanceId())
                .jobName(jobScheduler.getJobName())
                .cronExpression(jobScheduler.getCronExpression())
                .scheduled(jobScheduler.getScheduled())
                .startedAt(jobScheduler.getStartedAt())
                .endedAt(jobScheduler.getEndedAt())
                .lastExecutionTime(jobScheduler.getLastExecutionTime())
                .nextExecutionTime(jobScheduler.getNextExecutionTime())
                .duration(jobScheduler.getDuration())
                .attemptCount(jobScheduler.getAttemptCount())
                .logUrl(jobScheduler.getLogUrl())
                .status(jobScheduler.getStatus())
                .message(jobScheduler.getMessage())
                .state(jobScheduler.getState())
                .build();
    }

    public static JobScheduler toUpdatedJobScheduler(JobScheduler jobScheduler, JobScheduler jobSchedulerUpdate) {

        jobScheduler.setJobInstanceId(jobSchedulerUpdate.getJobInstanceId() == null ?
                jobScheduler.getJobInstanceId() : jobSchedulerUpdate.getJobInstanceId());
        jobScheduler.setJobName(jobSchedulerUpdate.getJobName() == null ? jobScheduler.getJobName() :
                jobSchedulerUpdate.getJobName());
        jobScheduler.setCronExpression(jobSchedulerUpdate.getCronExpression() == null ?
                jobScheduler.getCronExpression() : jobSchedulerUpdate.getCronExpression());
        jobScheduler.setScheduled(jobSchedulerUpdate.getScheduled() == null ? jobScheduler.getScheduled() :
                jobSchedulerUpdate.getScheduled());
        jobScheduler.setStartedAt(jobSchedulerUpdate.getStartedAt() == null ? jobScheduler.getStartedAt() :
                jobSchedulerUpdate.getStartedAt());
        jobScheduler.setEndedAt(jobSchedulerUpdate.getEndedAt() == null ? jobScheduler.getEndedAt() :
                jobSchedulerUpdate.getEndedAt());
        jobScheduler.setLastExecutionTime(jobSchedulerUpdate.getLastExecutionTime() == null ?
                jobScheduler.getLastExecutionTime() : jobSchedulerUpdate.getLastExecutionTime());
        jobScheduler.setNextExecutionTime(jobSchedulerUpdate.getNextExecutionTime() == null ?
                jobScheduler.getNextExecutionTime() : jobSchedulerUpdate.getNextExecutionTime());
        jobScheduler.setDuration(jobSchedulerUpdate.getDuration() == null ? jobScheduler.getDuration() :
                jobSchedulerUpdate.getDuration());
        jobScheduler.setDuration(jobSchedulerUpdate.getAttemptCount() == null ? jobScheduler.getAttemptCount() :
                jobSchedulerUpdate.getAttemptCount());
        jobScheduler.setLogUrl(jobSchedulerUpdate.getLogUrl() == null ? jobScheduler.getLogUrl() :
                jobSchedulerUpdate.getLogUrl());
        jobScheduler.setStartedAt(jobSchedulerUpdate.getStartedAt() == null ? jobScheduler.getStartedAt() :
                jobSchedulerUpdate.getStartedAt());
        jobScheduler.setStatus(jobSchedulerUpdate.getStatus() == null ? jobScheduler.getStatus() :
                jobSchedulerUpdate.getStatus());
        jobScheduler.setMessage(jobSchedulerUpdate.getMessage() == null ? jobScheduler.getMessage() :
                jobSchedulerUpdate.getMessage());
        jobScheduler.setState(jobSchedulerUpdate.getState() == null ? jobScheduler.getState() :
                jobSchedulerUpdate.getState());
        return jobScheduler;
    }

    public static List<JobScheduler> toJobSchedulers(List<JobSchedulerDTO> jobSchedulerDTOS) {
        return jobSchedulerDTOS.stream().map(js -> toJobScheduler(js)).collect(Collectors.toList());
    }

    public static List<JobSchedulerDTO> toJobSchedulerDTOs(List<JobScheduler> jobSchedulers) {
        return jobSchedulers.stream().map(js -> toJobSchedulerDTO(js)).collect(Collectors.toList());
    }
}
