package com.solar.api.saas.module.com.solar.scheduler.v2;

import lombok.Data;

@Data
public class TaskDefinition {

    private String cronExpression;
    private String jobId;
    private String jobName;
    private String data;
}