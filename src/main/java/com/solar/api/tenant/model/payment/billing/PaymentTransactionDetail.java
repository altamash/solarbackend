package com.solar.api.tenant.model.payment.billing;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "payment_transaction_detail")
@Audited(withModifiedFlag = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payDetId;
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private PaymentTransactionHead paymentTransactionHead;
    private Date tranDate;
    private Long lineSeqNo;
    private Double amt;
    private Double origAmt;
    private String status; //invoice status
    private String source;
    private String sourceId;
    private String instrumentNum; //
    private String issuer; //payment mode
    private String issuerId;
    private String issuerReconStatus; // payment status
    private Date reconExpectedDate;
    private Date reconDate; //payment date
    private String notes;
    private String batchNo;
    private String referenceId;


    @Transient
    private Long invoiceRefId;
    @Transient
    private String paymentCode;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
