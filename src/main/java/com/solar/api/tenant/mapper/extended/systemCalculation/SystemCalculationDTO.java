package com.solar.api.tenant.mapper.extended.systemCalculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemCalculationDTO {

    private Long id;
    private Long acctId;
    private String refType;
    private Long refId;
    private Long siteId;
    private String calcType;
    private String calcValue;
    private String date;
    private String ext1;
    private String ext2;
    private String ext3;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
