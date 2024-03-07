package com.solar.api.saas.module.com.solar.scheduler.mapper;

import com.solar.api.saas.model.JobExecutionParams;

import java.util.List;
import java.util.stream.Collectors;

public class JobExecutionParamsMapper {

    public static JobExecutionParams toJobExecutionParams(JobExecutionParamsDTO jobExecutionParamsDTO) {
        if (jobExecutionParamsDTO == null) {
            return null;
        }
        return JobExecutionParams.builder()
                .id(jobExecutionParamsDTO.getId())
                .keyString(jobExecutionParamsDTO.getKeyString())
                .valueString(jobExecutionParamsDTO.getValueString())
                .identifying(jobExecutionParamsDTO.getIdentifying())
                .build();
    }

    public static JobExecutionParamsDTO toJobExecutionParamsDTO(JobExecutionParams jobExecutionParams) {
        if (jobExecutionParams == null) {
            return null;
        }
        return JobExecutionParamsDTO.builder()
                .id(jobExecutionParams.getId())
                .keyString(jobExecutionParams.getKeyString())
                .valueString(jobExecutionParams.getValueString())
                .identifying(jobExecutionParams.getIdentifying())
                .build();
    }

    public static JobExecutionParams toUpdatedJobExecutionParams(JobExecutionParams jobExecutionParams,
                                                                 JobExecutionParams jobExecutionParamsUpdate) {
        jobExecutionParams.setKeyString(jobExecutionParamsUpdate.getKeyString() == null ?
                jobExecutionParams.getKeyString() : jobExecutionParamsUpdate.getKeyString());
        jobExecutionParams.setValueString(jobExecutionParamsUpdate.getValueString() == null ? jobExecutionParams.getValueString() :
                jobExecutionParamsUpdate.getValueString());
        jobExecutionParams.setIdentifying(jobExecutionParamsUpdate.getIdentifying() == null ?
                jobExecutionParams.getIdentifying() : jobExecutionParamsUpdate.getIdentifying());
        return jobExecutionParams;
    }

    public static List<JobExecutionParams> toJobExecutionParams(List<JobExecutionParamsDTO> jobExecutionParamsDTOS) {
        return jobExecutionParamsDTOS.stream().map(a -> toJobExecutionParams(a)).collect(Collectors.toList());
    }

    public static List<JobExecutionParamsDTO> toJobExecutionParamsDTOs(List<JobExecutionParams> jobExecutionParamss) {
        return jobExecutionParamss.stream().map(a -> toJobExecutionParamsDTO(a)).collect(Collectors.toList());
    }
}
