package com.solar.api.tenant.mapper.payment.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PaymentInfoTemplate {

    private Long payDetId;
    private Long customerSubscriptionId;
    private String customerName;
    private Long premiseNo;
    private Double invoiceAmount;
    private Double paidAmount;
    private Long invoiceId;
    private String billStatus;
    private String billingMonthYear;
    private Long billingId;
    private String paymentAlias;
    private String paymentSource;
    private String accountNumber;
    private String achStatus;
    //for process payment
    @JsonProperty("outstanding_amount")
    private Double outstandingAmount;
    @JsonProperty("payment_mode")
    private String paymentMode;
    @JsonProperty("detail_amount")
    private Double paymentAmount;
    @JsonProperty("payment_Date")
    private Date paymentDate;     // trans or payment date
    private String notes;
    @JsonProperty("reference_id")
    private String referenceId;
    @JsonProperty("head_id")
    private Long headId;
    @JsonProperty("batch")
    private String batchNo;
    @JsonProperty("job_id")
    private String jobId;
    private String status;
    @JsonProperty("payment_id")
    private Long paymentId;
    @JsonProperty("payment_detail_Id")
    private Long paymentDetailId;
    @JsonProperty("invoice_id")
    private Long invoice_Id;
    @JsonProperty("subscription_id")
    private Long subscription_id;
    @JsonProperty("subscription_name")
    private String subscriptionName;
    @JsonProperty("garden")
    private String garden;
    @JsonProperty("customer_name")
    private String customer_Name;
    @JsonProperty("premise")
    private String premise;
    @JsonProperty("invoice_amount")
    private Double invoice_amount;
    @JsonProperty("isAttachment")
    private Boolean isAttachment;
    @JsonProperty("payment_mode_id")
    private Long paymentModeId;
    public PaymentInfoTemplate() { }

    public PaymentInfoTemplate(Long headId,
                               Long invoice_Id,
                               Double outstandingAmount,
                               Long paymentId,
                               Double paymentAmount,
                               Date paymentDate,
                               Long paymentDetailId,
                               String status
                                ) {
        this.outstandingAmount = outstandingAmount;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.headId = headId;
        this.invoice_Id = invoice_Id;
        this.status = status;
        this.paymentId = paymentId;
        this.paymentDetailId = paymentDetailId;
    }

    public PaymentInfoTemplate(Long headId,String status,Double paymentAmount,Double invoice_amount,Long invoice_Id, String notes,
                               Long subscription_id, Double outstandingAmount,  Date paymentDate,String paymentMode,
                               String referenceId,Long paymentDetailId,Long paymentModeId ) {
        this.outstandingAmount = outstandingAmount;
        this.paymentMode = paymentMode;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.notes = notes;
        this.referenceId = referenceId;
        this.headId = headId;
        this.status = status;
        this.paymentDetailId = paymentDetailId;
        this.invoice_Id = invoice_Id;
        this.subscription_id = subscription_id;
        this.invoice_amount = invoice_amount;
        this.paymentModeId = paymentModeId;
    }
}
