package com.solar.api.saas.module.com.solar.scheduler.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobExecutionParamsDTO {

    private Long id;
    private Long scheduledJobId;
    private String keyString;
    private String valueString;
    private String identifying;
}
