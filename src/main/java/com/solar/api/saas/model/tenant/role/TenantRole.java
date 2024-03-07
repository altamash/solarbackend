package com.solar.api.saas.model.tenant.role;

import com.solar.api.saas.model.permission.PermissionSet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tenant_role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ETenantRole name;

    @ManyToMany(targetEntity = PermissionSet.class)
    @JoinTable(name = "tenant_role_permission_set",
            joinColumns = @JoinColumn(name = "tenant_role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_set_id"))
    private Set<PermissionSet> permissionSets;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
