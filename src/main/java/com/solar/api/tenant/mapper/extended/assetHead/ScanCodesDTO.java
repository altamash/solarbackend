package com.solar.api.tenant.mapper.extended.assetHead;

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
public class ScanCodesDTO {

    private Long scanId;
    private String regCode;
    private Long ref;
    private String scanCode;
    private String codeType;
    private String standardCodeFormat;
    private String status;
    private Boolean temporary;
    private Date startDate;
    private Date expiry;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
