package com.solar.api.tenant.model.permission;

import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.UserType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "permission_group")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String description;

    @ManyToMany(targetEntity = AvailablePermissionSet.class)
    @JoinTable(name = "permission_group_permission_set",
            joinColumns = @JoinColumn(name = "permission_group_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_set_id"))
    private Set<AvailablePermissionSet> permissionSets;
    @Transient
    private Set<AvailablePermissionSet> remainingPermissionSets;

    @ManyToMany(mappedBy = "permissionGroups")
    private Set<User> users;

    @ManyToMany(mappedBy = "permissionGroups")
    private Set<Role> roles;

    /*@ManyToMany(targetEntity = UserType.class)
    @JoinTable(name = "permission_group_user_level",
            joinColumns = @JoinColumn(name = "permission_group_id"),
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

    public void addPermissionSet(AvailablePermissionSet permissionSet) {
        this.permissionSets.add(permissionSet);
    }

    public void removePermissionSet(AvailablePermissionSet permissionSet) {
        this.permissionSets.remove(permissionSet);
    }

}
