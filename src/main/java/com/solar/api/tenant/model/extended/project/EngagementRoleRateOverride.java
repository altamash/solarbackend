package com.solar.api.tenant.model.extended.project;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "engagement_role_rate_override")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementRoleRateOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectEngagementId;
    private Long resourceId;
    private Long roleId;
    private Long prRateId;
    private boolean disabledIndicator;
    private Long overrideRate;
    private Long overrideOvertimeRate;
    private String state;


    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
