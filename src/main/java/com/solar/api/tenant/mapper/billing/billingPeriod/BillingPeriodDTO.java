package com.solar.api.tenant.mapper.billing.billingPeriod;

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
public class BillingPeriodDTO {

    private Long id;
    private String subscriptionCode;
    private String periodType; // from PortalAttribute
    private Integer periodGap;
    private String billingFinancialYear;
    private Date startDate;
    private Date endDate;
    private String status;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
