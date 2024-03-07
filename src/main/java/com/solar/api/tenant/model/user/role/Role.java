package com.solar.api.tenant.model.user.role;

import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.userType.UserType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @ManyToMany(targetEntity = PermissionGroup.class)
    @JoinTable(name = "role_permission_group",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_group_id"))
    private Set<PermissionGroup> permissionGroups;
    @Transient
    private Set<PermissionGroup> remainingPermissionGroups;
    @ManyToMany(targetEntity = AvailablePermissionSet.class)
    @JoinTable(name = "role_permission_set",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_set_id"))
    private Set<AvailablePermissionSet> permissionSets;

    /*@ManyToMany(targetEntity = UserType.class)
    @JoinTable(name = "role_user_level",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_type_id"))
    private Set<UserType> userLevels = new HashSet<>();*/
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_level", referencedColumnName = "id")
    private UserType userLevel;
    @Transient
    private String userLevelName;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addPermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroups.add(permissionGroup);
    }

    public void removePermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroups.remove(permissionGroup);
    }

    public void addPermissionSet(AvailablePermissionSet permissionSet) {
        this.permissionSets.add(permissionSet);
    }

    public void removePermissionSet(AvailablePermissionSet permissionSet) {
        this.permissionSets.remove(permissionSet);
    }
}
