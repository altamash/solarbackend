package com.solar.api.tenant.mapper.extended.order;

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
public class ClaimFileDTO {

    private Long id;
    private String claimType;
    private Long partnerId;
    private Long assetId;
    private Long locationId;
    private Long filer;
    private Date claimDateTime;
    private String status;
    private Double approvedAmt;
    private String approvalType;
    private Long approver;
    private Date approveDateTime;
    private String reasonDetails;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
