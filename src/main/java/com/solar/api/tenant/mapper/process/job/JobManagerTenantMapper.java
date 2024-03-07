package com.solar.api.tenant.mapper.process.job;

import com.solar.api.helper.Utility;
import com.solar.api.tenant.model.process.JobManagerTenant;

import java.util.List;
import java.util.stream.Collectors;

public class JobManagerTenantMapper {

    public static JobManagerTenant toJobManager(JobManagerTenantDTO jobManagerTenantDTO) {
        return JobManagerTenant.builder()
                .id(jobManagerTenantDTO.getId())
                .jobName(jobManagerTenantDTO.getJobName())
                .jobComponent(jobManagerTenantDTO.getJobComponent())
                .componentId(jobManagerTenantDTO.getComponentId())
                .batchId(jobManagerTenantDTO.getBatchId())
                .requestDatetime(jobManagerTenantDTO.getRequestDatetime())
                .executionDatetime(jobManagerTenantDTO.getExecutionDatetime())
                .endDatetime(jobManagerTenantDTO.getEndDatetime())
                .status(jobManagerTenantDTO.getStatus())
                .errors(jobManagerTenantDTO.getErrors())
                .responseMessage(jobManagerTenantDTO.getResponseMessage())
                .requestMessage(jobManagerTenantDTO.getRequestMessage())
                .log(jobManagerTenantDTO.getLog() == null ? null : jobManagerTenantDTO.getLog().getBytes())
                .build();
    }

    public static JobManagerTenantDTO toJobManagerDTO(JobManagerTenant jobManager) {
        if (jobManager == null) {
            return null;
        }
        Long duration = null;
        if (jobManager.getDuration() != null) {
            duration = jobManager.getDuration();
        } else if (jobManager.getEndDatetime() != null && jobManager.getExecutionDatetime() != null) {
            duration = jobManager.getEndDatetime().getTime() - jobManager.getExecutionDatetime().getTime();
        }

        return JobManagerTenantDTO.builder()
                .id(jobManager.getId())
                .jobName(jobManager.getJobName())
                .batchId(jobManager.getBatchId())
                .jobComponent(jobManager.getJobComponent())
                .componentId(jobManager.getComponentId())
                .requestDatetime(jobManager.getRequestDatetime())
                .executionDatetime(jobManager.getExecutionDatetime())
                .endDatetime(jobManager.getEndDatetime())
                .duration(duration != null ?
                        Utility.formatDuration(duration, Utility.DURATION_FORMAT_SHORT, true) : null)
                .status(jobManager.getStatus())
                .errors(jobManager.getErrors())
                .requestMessage(jobManager.getRequestMessage())
                .responseMessage(jobManager.getResponseMessage())
                .log(jobManager.getLog() == null ? null : new String(jobManager.getLog()))
                .createdAt(jobManager.getCreatedAt())
                .updatedAt(jobManager.getUpdatedAt())
                .build();
    }

    public static JobManagerTenant toUpdatedJobManager(JobManagerTenant jobManager, JobManagerTenant jobManagerUpdate) {
        jobManager.setJobName(jobManagerUpdate.getJobName() == null ? jobManager.getJobName() :
                jobManagerUpdate.getJobName());
        jobManager.setJobComponent(jobManagerUpdate.getJobComponent() == null ? jobManager.getJobComponent() :
                jobManagerUpdate.getJobComponent());
        jobManager.setComponentId(jobManagerUpdate.getComponentId() == null ? jobManager.getComponentId() :
                jobManagerUpdate.getComponentId());
        jobManager.setRequestDatetime(jobManagerUpdate.getRequestDatetime() == null ?
                jobManager.getRequestDatetime() : jobManagerUpdate.getRequestDatetime());
        jobManager.setExecutionDatetime(jobManagerUpdate.getExecutionDatetime() == null ?
                jobManager.getExecutionDatetime() : jobManagerUpdate.getExecutionDatetime());
        jobManager.setEndDatetime(jobManagerUpdate.getEndDatetime() == null ? jobManager.getEndDatetime() :
                jobManagerUpdate.getEndDatetime());
        jobManager.setStatus(jobManagerUpdate.getStatus() == null ? jobManager.getStatus() :
                jobManagerUpdate.getStatus());
        jobManager.setErrors(jobManagerUpdate.getErrors() == null ? jobManager.getErrors() :
                jobManagerUpdate.getErrors());
        jobManager.setRequestMessage(jobManagerUpdate.getRequestMessage() == null ? jobManager.getRequestMessage() :
                jobManagerUpdate.getRequestMessage());
        jobManager.setResponseMessage(jobManagerUpdate.getResponseMessage() == null ? jobManager.getResponseMessage() :
                jobManagerUpdate.getResponseMessage());
        jobManager.setLog(jobManagerUpdate.getLog() == null ? jobManager.getLog() : jobManagerUpdate.getLog());

        return jobManager;
    }

    public static List<JobManagerTenant> toJobManagers(List<JobManagerTenantDTO> jobManagerTenantDTOS) {
        return jobManagerTenantDTOS.stream().map(j -> toJobManager(j)).collect(Collectors.toList());
    }

    public static List<JobManagerTenantDTO> toJobManagerDTOs(List<JobManagerTenant> jobManagers) {
        return jobManagers.stream().map(j -> toJobManagerDTO(j)).collect(Collectors.toList());
    }

}
