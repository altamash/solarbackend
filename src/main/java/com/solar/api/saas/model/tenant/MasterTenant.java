package com.solar.api.saas.model.tenant;

import com.solar.api.saas.model.permission.PermissionSet;
import com.solar.api.saas.model.tenant.role.TenantRole;
import com.solar.api.saas.model.tenant.type.TenantType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tenant_master")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterTenant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "tenant_id")
    private Long id;

    @Transient
    private String jwtToken;

    @Size(max = 50)
    @Column(name = "db_name", nullable = false)
    private String dbName;

    //    @Convert(converter = StringAttributeConverter.class)
//    @Size(max = 100)
    @Column(name = "url", nullable = false)
    private String url;

//    @Convert(converter = StringAttributeConverter.class)
//    private String urltemp;

    @Size(max = 50)
    @Column(name = "user_name", nullable = false)
    private String userName;

    private String email;

    //    @Convert(converter = StringAttributeConverter.class)
    @Size(max = 100)
    @Column(name = "pass_code", nullable = false)
    private String passCode;

    @Size(max = 100)
    @Column(name = "driver_class", nullable = false)
    private String driverClass;

    //    @Convert(converter = StringAttributeConverter.class)
    private String emailApiKey;

    @Size(max = 10)
    @Column(name = "status", nullable = false)
    private String status;

    @Size(max = 45)
    private String companyCode;
    private Long companyKey;
    private String companyName;
    private String companyLogo;
    @Column(columnDefinition = "integer default 1")
    private Integer tenantTier;

    @ManyToOne
    @JoinColumn(name = "tenant_type")
    private TenantType tenantType;
    @Transient
    private String type;

    @ManyToMany(targetEntity = TenantRole.class)
    @JoinTable(name = "tenant_roles",
            joinColumns = @JoinColumn(name = "tenant_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<TenantRole> tenantRoles;
    @Transient
    private Set<String> roles;

    @ManyToMany(targetEntity = PermissionSet.class)
    @JoinTable(name = "tenant_permission_set",
            joinColumns = @JoinColumn(name = "tenant_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_set_id"))
    private Set<PermissionSet> permissionSets;

    private Boolean legacyBilling;
    @Column(name = "login_url")
    private String loginUrl;

    private Boolean enabled;
    private Boolean valid;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
