package com.solar.api.tenant.model.user.userType;

import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "user_type")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EUserType name;

    @ManyToMany(mappedBy = "userLevels")
    private Set<AvailablePermissionSet> permissionSets;

    /*@ManyToMany(mappedBy = "userLevels")
    private Set<PermissionGroup> permissionGroups;

    @ManyToMany(mappedBy = "userLevels")
    private Set<Role> roles;*/

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public UserType(Long id) {
        this.id = id;
    }
}
