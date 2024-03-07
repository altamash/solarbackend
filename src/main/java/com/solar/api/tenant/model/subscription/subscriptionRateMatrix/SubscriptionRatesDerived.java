package com.solar.api.tenant.model.subscription.subscriptionRateMatrix;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "subscription_rates_derived")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRatesDerived {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subscriptionRateMatrixId;
    private String subscriptionCode;
    @Column(length = 20)
    private String refType;
    @Column(length = 10)
    private String calcGroup;
    private String refCode;
    private Double value;
    private String conditionExpr;
    private String notes;
    private Boolean enabled;
    private Date startDate;
    private Date endDate;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "SubscriptionRatesDerived{" +
                "id=" + id +
                ", subscriptionRateMatrixId=" + subscriptionRateMatrixId +
                ", subscriptionCode='" + subscriptionCode + '\'' +
                ", refType='" + refType + '\'' +
                ", calcGroup='" + calcGroup + '\'' +
                ", refCode='" + refCode + '\'' +
                ", value=" + value +
                ", conditionExpr='" + conditionExpr + '\'' +
                '}';
    }
}
