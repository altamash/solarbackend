package com.solar.api.tenant.model.ca;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ca_utility")
public class CaUtility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "utility_provider_id")
    private Long utilityProviderId;

    @Column(name = "premise")
    private String premise;

    @Column(name = "average_monthly_bill")
    private Long averageMonthlyBill;

    @JoinColumn(name = "customer_entity_id")
    @ManyToOne
    private com.solar.api.tenant.model.contract.Entity entity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "is_checked")
    private Boolean isChecked;

    @Transient
    private Long entityId;

    @Transient
    private String action;
    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Column(name = "utility_portal_username")
    private String utilityPortalUsername;

    @Column(name = "pass_code")
    private String passCode;

    @Column(name = "portal_access_allowed")
    private String portalAccessAllowed;

}
