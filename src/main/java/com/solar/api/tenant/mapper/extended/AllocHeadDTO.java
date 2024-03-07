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
public class AllocHeadDTO {

    private Long allocId;
    private Long orderId;
    private Long assetId;
    private Long locationId;
    private Long qty;
    private String status;
    private String description;
    private Date dateTime;
    private Long submittedBy;
    private Long approverId;
    private Date approveDateTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
