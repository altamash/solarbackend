package com.solar.api.tenant.mapper.extended;

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
public class EnvironmentLogDTO {

    private Long id;
    private Long assetRefId;
    private Long siteRefId;
    private Long sensorId;
    private String sensorType;
    private Long reading;
    private Date datetime;
    private Long batchId;
    private String sourceSystem;
    private String notes;
    private String ext1;
    private String ext2;
    private String ext3;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
