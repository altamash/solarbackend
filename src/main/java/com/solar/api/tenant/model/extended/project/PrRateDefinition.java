package com.solar.api.tenant.model.extended.project;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pr_rate_definitions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrRateDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String uniqueCode;
    private String rateCategory;
    private String description;
    private String rateType;
    private String ratePeriod;
    private String termLengthInDays;
    private String rate;
    private String fixedAmount;
    private String overtimePeriod;
    private String overtimeRate;
    private String overtimeFixedAmount;
    private String calculationFactor;
    private String calculationFrequency;
    private String category;
    private String notes;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
