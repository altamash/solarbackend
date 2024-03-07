package com.solar.api.tenant.mapper.extended.service;

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
public class WorkOrderHeadDTO {

    private Long id;
    private Long serviceId;
    private Long registerId;
    private String summary;
    private String status;
    private Date createDateTime;
    private Long createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
