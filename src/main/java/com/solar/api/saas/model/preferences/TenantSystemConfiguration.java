package com.solar.api.saas.model.preferences;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenant_system_configurations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSystemConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orgId;
    private String orgName;
    private String regDate;
    private String tenancyState;
    private String expiryDate;
    private String lastRenewalDate;
    private String operationYear;
    private String region;
    private String ccy;
    private String language;
    private String targetSchema;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
