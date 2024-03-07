package com.solar.api.tenant.mapper.billingCredits;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingCreditsTempDTO {

    private Long id;
    private Integer importTypeId;
    private Long jobId;
    private String creditCodeType;
    private String gardenId;
    private Double mPA;
    private Double tariffRate;
    private String creditCodeVal;
    private Double creditValue;
    private String creditForDate;
    private String subscriptionCode;
    private Integer lineSeqNo;
    private String calendarMonth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
