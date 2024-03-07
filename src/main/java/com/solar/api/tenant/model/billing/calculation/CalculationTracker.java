package com.solar.api.tenant.model.billing.calculation;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "calculation_tracker")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String calcRefType;
    private String refId;
    @Column(length = 20)
    private String billingPeriod;
    private Date periodStartDate;
    private Date periodEndDate;
    @Column(length = 20)
    private String state;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}