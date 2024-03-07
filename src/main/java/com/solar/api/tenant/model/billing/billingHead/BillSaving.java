package com.solar.api.tenant.model.billing.billingHead;

import com.solar.api.tenant.model.subscription.CustomerSubscription;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "billing_savings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillSaving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_id")
    private CustomerSubscription subscription;
    private Long billId;
    private String savingCode;
    private Integer value;
    private Date date;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
