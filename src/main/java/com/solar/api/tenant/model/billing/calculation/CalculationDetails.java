package com.solar.api.tenant.model.billing.calculation;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calculation_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20)
    private String source;
    private Long sourceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calc_id")
    private CalculationTracker calculationTracker; //FK
    @Column(length = 20)
    private String state;
    private int attemptCount;
    private String errorInd;
    private String errorMessage;
    private Boolean lockedInd;
    private Long invoiceId;
    @Column(length = 10000)
    private String prevInvHtmlView;
    @Column(length = 10)
    private String publishState;
    private Boolean reCalcInd;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}