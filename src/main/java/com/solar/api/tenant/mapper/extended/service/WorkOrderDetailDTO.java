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
public class WorkOrderDetailDTO {

    private Long id;
    private Long workOrderId;
    private String measure;
    private String value;
    private Long createdBy;
    private Date datetime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
