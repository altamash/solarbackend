package com.solar.api.tenant.model.extended.project;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_accruals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialAccrual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private Long refId; //projectId
    private Long subRefId; //Optional -> taskId
    private Long accrualCategoryId; //resourceId, partnerId
    private String accrualCategory;
    private String accrualDatetime;
    private String accrualAdjustment;
    private String accrualPeriod;
    private double accruedAmount;
    private String rate;
    private String type;
    private String postingDate;
    private String status;
    private Long orgId;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
