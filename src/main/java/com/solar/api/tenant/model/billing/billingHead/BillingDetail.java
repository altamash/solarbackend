package com.solar.api.tenant.model.billing.billingHead;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "billing_detail")
@Audited(withModifiedFlag = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "billing_head_id")
    private BillingHead billingHead;
    private String rateCode;
    private Double value;
    private Integer lineSeqNo;
    private String billingCode;
    private Date date;
    private Boolean addToBillAmount;
    private String notes;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
