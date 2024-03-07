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
public class ServiceDetailDTO {

    private Long id;
    private Long serviceId;
    private String measureCode;
    private String value;
    private Date lastUpdateOn;
    private Long lastUpdateBy;
    private String validationRule;
    private String validationParams;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
