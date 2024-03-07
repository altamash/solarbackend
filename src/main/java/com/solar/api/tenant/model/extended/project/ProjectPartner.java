package com.solar.api.tenant.model.extended.project;

import com.solar.api.tenant.mapper.extended.partner.PartnerHeadDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "project_partners")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private Long partnerId;
    private String associationType;
    private String revenueCap;
    private String actualRevenue;
    private String actualRevenueUsed;
    private String status;
    private Date estimatedStartDate;
    private Date estimatedEndDate;

    @Transient
    private PartnerHeadDTO partnerHead;

    @CreationTimestamp
    @NotAudited
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
