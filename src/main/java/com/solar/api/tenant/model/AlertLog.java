package com.solar.api.tenant.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "tenant_config_id")
    private TenantConfig tenantConfig;
    private String status;
    @Column(length = 100)
    private String subject;
    private String recipients;
    @Column(length = 4000)
    private String valuesJson;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
