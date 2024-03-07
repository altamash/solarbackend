package com.solar.api.tenant.mapper.process.job;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobManagerTenantDTO {

    private Long id;
    private String jobName;
    private String jobComponent;
    private Long componentId;
    private Long batchId;
    private Date requestDatetime;
    private Date executionDatetime;
    private Date endDatetime;
    private String duration;
    private String status;
    private Boolean errors;
    private String requestMessage;
    private String responseMessage;
    private String log;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
