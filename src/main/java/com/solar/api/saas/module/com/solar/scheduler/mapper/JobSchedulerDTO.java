package com.solar.api.saas.module.com.solar.scheduler.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobSchedulerDTO {

    private Long id;
    private Long jobInstanceId;
    private String jobName;
    private String cronExpression;        //<<"0 0 12 5 1/1 ? *">>
    private Date scheduled;    //<<2021-01-05 Tue 12:00:00>>
    private Date startedAt;
    private Date endedAt;
    private Date lastExecutionTime;    //<<2021-01-05 Tue 12:00:00>>
    private Date nextExecutionTime;    //<<2021-01-05 Tue 12:00:00>>
    private Long duration;
    private Long attemptCount;
    private String logUrl;
    private String state;
    private String message;
    private String status;
}
