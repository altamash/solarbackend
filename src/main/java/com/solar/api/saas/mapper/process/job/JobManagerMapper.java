package com.solar.api.saas.mapper.process.job;

import com.solar.api.helper.Utility;
import com.solar.api.saas.model.JobManager;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class JobManagerMapper {

    public static JobManager toJobManager(JobManagerDTO jobManagerDTO) {
        return JobManager.builder()
                .id(jobManagerDTO.getId())
                .jobName(jobManagerDTO.getJobName())
                .jobComponent(jobManagerDTO.getJobComponent())
                .componentId(jobManagerDTO.getComponentId())
                .batchId(jobManagerDTO.getBatchId())
                .requestDatetime(jobManagerDTO.getRequestDatetime())
                .executionDatetime(jobManagerDTO.getExecutionDatetime())
                .endDatetime(jobManagerDTO.getEndDatetime())
                .status(jobManagerDTO.getStatus())
                .errors(jobManagerDTO.getErrors())
                .requestMessage(jobManagerDTO.getRequestMessage())
                .log(jobManagerDTO.getLog() == null ? null : jobManagerDTO.getLog().getBytes())
                .build();
    }

    public static JobManagerDTO toJobManagerDTO(JobManager jobManager) {
        if (jobManager == null) {
            return null;
        }
        Long duration = null;
        if (jobManager.getDuration() != null) {
            duration = jobManager.getDuration();
        } else if (jobManager.getEndDatetime() != null && jobManager.getExecutionDatetime() != null) {
            duration = jobManager.getEndDatetime().getTime() - jobManager.getExecutionDatetime().getTime();
        }

        return JobManagerDTO.builder()
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
                .log(jobManager.getLog() == null ? null : new String(jobManager.getLog()))
                .createdAt(jobManager.getCreatedAt())
                .updatedAt(jobManager.getUpdatedAt())
                .build();
    }

    public static JobManager toUpdatedJobManager(JobManager jobManager, JobManager jobManagerUpdate) {
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
        jobManager.setLog(jobManagerUpdate.getLog() == null ? jobManager.getLog() : jobManagerUpdate.getLog());

        return jobManager;
    }

    public static List<JobManager> toJobManagers(List<JobManagerDTO> jobManagerDTOS) {
        return jobManagerDTOS.stream().map(j -> toJobManager(j)).collect(Collectors.toList());
    }

    public static List<JobManagerDTO> toJobManagerDTOs(List<JobManager> jobManagers) {
        return jobManagers.stream().map(j -> toJobManagerDTO(j)).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Long duration = 405000l;
        if (duration != null) {
            Duration d = Duration.ofMillis(duration);
            long daysPart = d.toDays();
            long hoursPart = d.minusDays(daysPart).toHours();
            long minutesPart = d.minusDays(daysPart).minusHours(hoursPart).toMinutes();
            long secondsPart = d.minusDays(daysPart).minusHours(hoursPart).minusMinutes(minutesPart).getSeconds();
            long millisPart =
                    d.minusDays(daysPart).minusHours(hoursPart).minusMinutes(minutesPart).minusSeconds(secondsPart).toMillis();
            System.out.println(daysPart + ":" + hoursPart + ":" + minutesPart + ":" + secondsPart + ":" + millisPart);
            System.out.println(DurationFormatUtils.formatDuration(duration, "dd:HH:mm:ss:S", true));
        }
    }

}
