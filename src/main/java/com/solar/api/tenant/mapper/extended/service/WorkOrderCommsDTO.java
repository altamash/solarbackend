package com.solar.api.tenant.mapper.extended.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderCommsDTO {

    private Long id;
    private Long workOrderId;
    private Integer seq;
    private String message;
    private String type;
    private String refCode;
    private Long refId;
    private String status;
    private String dependingOn;
    private String closingRemarks;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
