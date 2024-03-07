package com.solar.api.tenant.model.subscription;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "customer_subscription_mapping", indexes = @Index(columnList = "value", name = "value_index"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * RULES APPLY FROM THIS TABLE
 */
public class CustomerSubscriptionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_subscription_id")
    private CustomerSubscription subscription;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_rate_matrix_head_id")
    private SubscriptionRateMatrixHead subscriptionRateMatrixHead; // ?
    @Transient
    private MeasureDefinitionTenantDTO measureDefinition;
    private Long measureDefinitionId;
    @Column(length = 10, unique = true)
    private String rateCode;
    private String value;
    @Transient
    private String defaultValue;
    private Integer level;
    @Column(length = 20)
    private String status;
    private Date effectiveDate;
    private Date endDate;
    private Integer hourOfDay;
    @Transient
    private Long subscriptionId;
    @Transient
    private Long subscriptionRateMatrixId;
    @Transient
    private String externalId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
