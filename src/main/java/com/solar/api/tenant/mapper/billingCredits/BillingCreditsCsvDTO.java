package com.solar.api.tenant.mapper.billingCredits;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingCreditsCsvDTO {

    private Long id;
    private String paymentType;
    private String debtorNumber;
    private String premiseNumber;
    private String subscriberAllocationHistorySubscriberName;
    private String monthlyProductionAllocationinkWh;
    private String tariffRate;
    private String billCredit;
    private String gardenID;
    private String namePlateCapacitykWDC;
    private String calendarMonth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
