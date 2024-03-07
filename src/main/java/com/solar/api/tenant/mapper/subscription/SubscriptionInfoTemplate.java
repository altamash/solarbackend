package com.solar.api.tenant.mapper.subscription;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionInfoTemplate {

    private Long subscriptionId;
    private String subscriptionStatus;
    private String subscriptionTemplate;
    private Long acctId;
    private String premiseNumber;
    private Double totalBill;
    private String monthYear;
    private Long billingId; // hidden
    private Long invoiceId;
    private Date dueDate;
    private String billingStatus;
    private Integer pendingPaymentDays;
    private Date pendingPaymentDate;

    public SubscriptionInfoTemplate(Long subscriptionId,
                                    String subscriptionStatus,
                                    String subscriptionTemplate,
                                    Long acctId,
                                    String premiseNumber,
                                    Double totalBill,
                                    String monthYear,
                                    Long billingId,
                                    Long invoiceId,
                                    Date dueDate,
                                    String billingStatus) {
        this.subscriptionId = subscriptionId;
        this.subscriptionStatus = subscriptionStatus;
        this.subscriptionTemplate = subscriptionTemplate;
        this.acctId = acctId;
        this.premiseNumber = premiseNumber;
        this.totalBill = totalBill;
        this.monthYear = monthYear;
        this.billingId = billingId;
        this.invoiceId = invoiceId;
        this.dueDate = dueDate;
        this.billingStatus = billingStatus;
    }

}
