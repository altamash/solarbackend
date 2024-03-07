package com.solar.api.saas.model.permission;

import com.solar.api.saas.model.permission.userLevel.PermissionUserLevel;
import com.solar.api.saas.model.tenant.role.TenantRole;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "permission_set")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionSet implements Comparable<PermissionSet> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;

    @ManyToMany(targetEntity = Permission.class)
    @JoinTable(name = "permission_set_permission",
            joinColumns = @JoinColumn(name = "permission_set_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "permissionSets")
    private Set<TenantRole> tenantRoles;

    @ManyToMany(targetEntity = PermissionUserLevel.class)
    @JoinTable(name = "permission_set_user_level",
            joinColumns = @JoinColumn(name = "permission_set_id"),
            inverseJoinColumns = @JoinColumn(name = "user_level_id"))
    private Set<PermissionUserLevel> userLevels = new HashSet<>();
    @Transient
    private Set<String> userLevelNames;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void addPermissions(List<Permission> permissions) {
        this.permissions.addAll(permissions);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    @Override
    public int compareTo(PermissionSet set) {
        return this.name.compareTo(set.getName());
    }
}
