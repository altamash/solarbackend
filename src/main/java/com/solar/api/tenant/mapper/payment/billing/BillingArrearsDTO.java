package com.solar.api.tenant.mapper.payment.billing;

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
public class BillingArrearsDTO {

    private Long id;
    private Long acctId;
    private Long subsId;
    private Double accumulatedArrears;
    private Boolean billed;
    private Long billingHeadId;
    private Date processDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
