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
public class ServiceHeadDTO {

    private Long serviceId;
    private Long registerId;
    private String summary;
    private String description;
    private Date createDate;
    private String status;
    private Date updateDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
