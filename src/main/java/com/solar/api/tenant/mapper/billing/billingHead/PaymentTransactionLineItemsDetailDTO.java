package com.solar.api.tenant.mapper.billing.billingHead;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@Builder
//@AllArgsConstructor
public class PaymentTransactionLineItemsDetailDTO {

    @JsonProperty("head_id")
    private Long headId;
    @JsonProperty("invoice_amount")
    private Double invoiceAmount;
    @JsonProperty("subscription_id")
    private Long subscriptionId;
    @JsonProperty("invoice_id")
    private Long invoiceId;
    @JsonProperty("outstanding_amount")
    private Double outstandingAmount;
    @JsonProperty("subscription_name")
    private String subscriptionName;
    //unreconciled cols
    @JsonProperty("garden")
    private String garden;
    @JsonProperty("payment_id")
    private Long paymentId;
    @JsonProperty("job_id")
    private String jobId;
    @JsonProperty("reference_id")
    private String ref;
    @JsonProperty("detail_amount")
    private Double amount;
    @JsonProperty("payment_mode")
    private String paymentMode;
    @JsonProperty("instrument_id")
    private String instrumentId;
    @JsonProperty("payment_Date")
    private Date paymentDate;
    @JsonProperty("payment_detail_Id")
    private Long paymentDetailId;
    @JsonProperty("notes")
    private String notes;
    @JsonProperty("batch")
    private String batch;
    @JsonProperty("status")
    private String status; //reconcile or reverse status
    private String action;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("source")
    private String source;
    @JsonProperty("payment_ref_id")
    private String sourceId;
    @JsonProperty("payment_mode_id")
    private Long paymentModeId;
    public PaymentTransactionLineItemsDetailDTO(Long headId, Double invoiceAmount, Long subscriptionId, Long invoiceId, Double outstandingAmount, String subscriptionName, String garden, Long paymentId, String jobId, String ref, Double amount,
                                                String paymentMode, String instrumentId, Date paymentDate,  Long paymentDetailId, String notes, String batch, String status, String action) {
        this.headId = headId;
        this.invoiceAmount = invoiceAmount;
        this.subscriptionId = subscriptionId;
        this.invoiceId = invoiceId;
        this.outstandingAmount = outstandingAmount;
        this.subscriptionName = subscriptionName;
        this.garden = garden;
        this.paymentId = paymentId;
        this.jobId = jobId;
        this.ref = ref;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.instrumentId = instrumentId;
        this.paymentDate = paymentDate;
        this.paymentDetailId = paymentDetailId;
        this.notes = notes;
        this.batch = batch;
        this.status = status;
        this.action = action;
    }

    public PaymentTransactionLineItemsDetailDTO(Long headId, Double invoiceAmount, Long subscriptionId, Long invoiceId, Double outstandingAmount, String subscriptionName,
                                                String garden, Long paymentId, String jobId, String ref, Double amount,
                                                String paymentMode, String instrumentId, Date paymentDate,  Long paymentDetailId, String notes,
                                                String batch, String status,String source, String sourceId) {
        this.headId = headId;
        this.invoiceAmount = invoiceAmount;
        this.subscriptionId = subscriptionId;
        this.invoiceId = invoiceId;
        this.outstandingAmount = outstandingAmount;
        this.subscriptionName = subscriptionName;
        this.garden = garden;
        this.paymentId = paymentId;
        this.jobId = jobId;
        this.ref = ref;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.instrumentId = instrumentId;
        this.paymentDate = paymentDate;
        this.paymentDetailId = paymentDetailId;
        this.notes = notes;
        this.batch = batch;
        this.status = status;
        this.source = source;
        this.sourceId = sourceId ;
    }

    public PaymentTransactionLineItemsDetailDTO(Long headId, Double invoiceAmount, Long subscriptionId, Long invoiceId, Double outstandingAmount, String subscriptionName,
                                                String garden, Long paymentId, String jobId, String ref, Double amount,
                                                String paymentMode, String instrumentId, Date paymentDate,  Long paymentDetailId, String notes,
                                                String batch, String status, Long paymentModeId) {
        this.headId = headId;
        this.invoiceAmount = invoiceAmount;
        this.subscriptionId = subscriptionId;
        this.invoiceId = invoiceId;
        this.outstandingAmount = outstandingAmount;
        this.subscriptionName = subscriptionName;
        this.garden = garden;
        this.paymentId = paymentId;
        this.jobId = jobId;
        this.ref = ref;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.instrumentId = instrumentId;
        this.paymentDate = paymentDate;
        this.paymentDetailId = paymentDetailId;
        this.notes = notes;
        this.batch = batch;
        this.status = status;
        this.paymentModeId = paymentModeId;
    }
    public PaymentTransactionLineItemsDetailDTO(Long headId, Double invoiceAmount, Long subscriptionId, Long invoiceId, Double outstandingAmount, String subscriptionName,
                                                String garden, Long paymentId, String jobId, String ref, Double amount,
                                                String paymentMode, String instrumentId, Date paymentDate,  Long paymentDetailId, String notes,
                                                String batch, String status) {
        this.headId = headId;
        this.invoiceAmount = invoiceAmount;
        this.subscriptionId = subscriptionId;
        this.invoiceId = invoiceId;
        this.outstandingAmount = outstandingAmount;
        this.subscriptionName = subscriptionName;
        this.garden = garden;
        this.paymentId = paymentId;
        this.jobId = jobId;
        this.ref = ref;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.instrumentId = instrumentId;
        this.paymentDate = paymentDate;
        this.paymentDetailId = paymentDetailId;
        this.notes = notes;
        this.batch = batch;
        this.status = status;
    }

    public PaymentTransactionLineItemsDetailDTO(Long headId, Double invoiceAmount, Long subscriptionId, Long invoiceId, Double outstandingAmount, String subscriptionName, String garden, Long paymentId, String jobId, String ref, Double amount, String paymentMode, String instrumentId, Date paymentDate,
                                                Long paymentDetailId, String notes, String batch, String customerName, String status, String action,String source, String sourceId, Long paymentModeId) {
        this.headId = headId;
        this.invoiceAmount = invoiceAmount;
        this.subscriptionId = subscriptionId;
        this.invoiceId = invoiceId;
        this.outstandingAmount = outstandingAmount;
        this.subscriptionName = subscriptionName;
        this.garden = garden;
        this.paymentId = paymentId;
        this.jobId = jobId;
        this.ref = ref;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.instrumentId = instrumentId;
        this.paymentDate = paymentDate;
        this.paymentDetailId = paymentDetailId;
        this.notes = notes;
        this.batch = batch;
        this.customerName = customerName;
        this.status = status;
        this.action = action;
        this.source = source;
        this.sourceId = sourceId ;
        this.paymentModeId = paymentModeId;
    }
}