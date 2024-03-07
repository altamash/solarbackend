package com.solar.api.tenant.mapper.billing.billingHead;

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
public class BillSavingDTO {

    private Long id;
    private Long subscriptionId;
    private Long billId;
    private String savingCode;
    private Integer value;
    private Date date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
