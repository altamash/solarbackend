package com.solar.api.tenant.model.extended.project;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "engagement_role_maps")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EngagementRoleMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long engagementRoleId;
    private Long prRateGroupId;
    private Long prRateId;
    private Long overrideRate;
    private Long overrideOtRate;
    private String sequence;
    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
