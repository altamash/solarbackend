package com.solar.api.tenant.mapper.billing.tiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.billing.calculation.CustomerDetailDTO;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminBillingDashboardTile {
    private String source;
    private Long billHeadId;
    private Long invoiceId;
    private String period;
    private String subscriptionId;
    private String billStatus;
    private Double invoicedAmount;
    private Double paidAmount;
    private Double remainingAmount;
    private CustomerDetailDTO customerDetailDTO;

    private String transactionId;
    private String customerId;
    private String subscriptionName;
    private Date dueDate;
    private Date paymentDate;
    private long statusCount;
    private long customerTypeCount;
    private String customerType;
    private String billingMonthYear;


    public AdminBillingDashboardTile(Long billHeadId, Long invoiceId, String period,
                                     String subscriptionId, String billStatus,
                                     Double invoicedAmount, Double paidAmount,
                                     String customerName, String customerType,
                                     String customerEmail, String customerPhone,
                                     String profileUrl, String transactionId,
                                     String customerId,
                                     Date dueDate, Date paymentDate) {
        this.billHeadId = billHeadId;
        this.invoiceId = invoiceId;
        this.period = period;
        this.subscriptionId = subscriptionId;
        this.billStatus = billStatus;
        this.invoicedAmount = invoicedAmount ;
        this.paidAmount = paidAmount ;
        this.remainingAmount = (invoicedAmount != null ? invoicedAmount : 0) - (paidAmount != null ? paidAmount : 0);
        this.customerDetailDTO = new CustomerDetailDTO(customerName, customerType, customerEmail, customerPhone, profileUrl);
        this.transactionId = transactionId;
        this.customerId = customerId;
        if (dueDate != null) {
            this.dueDate = dueDate;
        }
        if (paymentDate != null) {
            this.paymentDate = paymentDate;
        }

    }

    public AdminBillingDashboardTile(Long statusCount, String billStatus) {
        this.statusCount = statusCount;
        this.billStatus = billStatus;
    }

    public AdminBillingDashboardTile(Long customerTypeCount, String customerType, String billStatus) {
     this.customerTypeCount = customerTypeCount;
     this.customerType = customerType;
     this.billStatus = billStatus;
    }

    public AdminBillingDashboardTile(String billStatus, String billingMonthYear, Long statusCount) {
        this.statusCount = statusCount;
        this.billingMonthYear = billingMonthYear;
        this.billStatus = billStatus;
    }
}
