package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingHeadDTO {

    private Long id;
    private Long invoiceId;
    private String invoiceUrl;
    private Long userAccountId; // userAccountId;
    private Long subscriptionId; // subscriptionId;
    private String custProdId;
    private String billType;
    private Double amount;
    private Date generatedOn;
    private String billingMonthYear;
    private String billStatus;
    private Date invoiceDate;
    private Date dueDate;
    private Date defermentDate;
    private List<Long> billingDetailIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean invalidationAllowed;
    private Boolean billSkip;
}
