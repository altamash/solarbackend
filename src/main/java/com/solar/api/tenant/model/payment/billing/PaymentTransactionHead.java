package com.solar.api.tenant.model.payment.billing;

import com.solar.api.tenant.model.billing.BillingInvoice.BillingInvoice;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payment_transaction_head")
@Audited(withModifiedFlag = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;//
    private String paymentCode;//BILLING enum
    private Long custAccountId;//
    @OneToOne
    @JoinColumn(name = "invoice_ref_id", referencedColumnName = "id")
    private BillingInvoice invoice;// INV ID
    private Double net;
    private String ccy; // preferences
    private String destination;
    private String destId;
    private Long beneficiaryId;
    private Long subsId;//
    private String description;
    private String notes;
    private String jobId;


    @OneToMany(mappedBy = "paymentTransactionHead", cascade = CascadeType.MERGE)
    private List<PaymentTransactionDetail> paymentTransactionDetails;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
