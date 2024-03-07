package com.solar.api.tenant.model.subscription.subscriptionRateMatrix;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_rate_matrix_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRateMatrixDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "subscription_rate_matrix_head_id")
    private SubscriptionRateMatrixHead subscriptionRateMatrixHead;
    private Long subscriptionRateMatrixId;
    @Column(length = 10)
    private String subscriptionCode;
    @Column(length = 10)
    private String rateCode;
    private String defaultValue;
    private Integer level;
    private Integer sequenceNumber;
    private Boolean mandatory;
    private Boolean maintainBillHistory;
    private String flags;
    private Boolean allowOthersToEdit;
    private Boolean systemUsed; // If it is used anywhere in the calculation logic (administrative)
    private Boolean varyByCustomer; // If yes, present in contract mapping
    @Transient
    private MeasureDefinitionTenantDTO measureDefinition;
    private Long measureDefinitionId;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
