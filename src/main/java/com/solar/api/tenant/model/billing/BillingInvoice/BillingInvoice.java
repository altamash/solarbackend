package com.solar.api.tenant.model.billing.BillingInvoice;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "billing_invoice")
@Audited
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // MONTHLY (default), TRUEUP, SETTLEMENT, REBATE, OTHERS
    private String category; // Individual, Corporate
    private Date dateOfInvoice;
    private String invoiceUrl; // = publish_indicator ? seturl of pdf : null
    private Date dueDate;
    private Boolean publishIndicator;
    private Date publishDate;
    private String publishUrl; // = publish_indicator ? seturl of pdf : null
//    @OneToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "publish_info_id", referencedColumnName = "id")
//    @NotAudited
    private Long publishInfoId;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
