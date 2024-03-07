package com.solar.api.tenant.model.extended.project;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "engagement_rate_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementRateGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    private String rateType;
    private String ratePeriod;
    private String rate;
    private String fixedAmount;
    private String overtimePeriod;
    private String overtimeRate;
    private String overtimeFixedAmount;

    private Long resourceId;
    private Long projectId;
    private Long taskId;
    private String rateCategory;
    private String description;
    private Long termLengthInDays;
    private String calculationFactor;
    private String category;
    private String notes;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
