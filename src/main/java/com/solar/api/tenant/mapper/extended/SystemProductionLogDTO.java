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
public class SystemProductionLogDTO {

    private Long id;
    private Long siteid;
    private Long assetCompRef;
    private Integer peakPower;
    private String status;
    private Date recDateTime;
    private Long assetId;
    private String premiseNo;
    private String assetDesc;
    private String sourceSystem;
    private String externalAcctId;
    private String ext2;
    private String ext3;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
