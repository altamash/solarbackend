package com.solar.api.tenant.model.permission;

import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.UserType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission_set_available")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailablePermissionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long permissionSetId;

    @Column(unique = true)
    private String name;
    private String description;

    @ManyToMany(mappedBy = "permissionSets")
    private Set<Role> roles;

    @ManyToMany(mappedBy = "permissionSets")
    private Set<PermissionGroup> permissionGroups;

    @ManyToMany(targetEntity = UserType.class)
    @JoinTable(name = "permission_set_user_level",
            joinColumns = @JoinColumn(name = "permission_set_id"),
            inverseJoinColumns = @JoinColumn(name = "user_type_id"))
    private Set<UserType> userLevels = new HashSet<>();

    private boolean enabled;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
