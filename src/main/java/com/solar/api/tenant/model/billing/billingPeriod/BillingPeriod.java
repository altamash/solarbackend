package com.solar.api.tenant.model.billing.billingPeriod;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "billing_period")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subscriptionCode;
    private String periodType; // from PortalAttribute
    private Integer periodGap;
    private String billingFinancialYear;
    private Date startDate;
    private Date endDate;
    private String status;
    private String comments;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
