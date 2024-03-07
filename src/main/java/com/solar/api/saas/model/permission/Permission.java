package com.solar.api.saas.model.permission;

import com.solar.api.saas.model.permission.component.ComponentLibrary;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission", indexes = @Index(name = "name_index", columnList = "name"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "component_library_id")
    private ComponentLibrary componentLibrary;

    @ManyToMany(mappedBy = "permissions")
    private Set<PermissionSet> permissionSets = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addPermissionSet(PermissionSet permissionSet) {
        this.permissionSets.add(permissionSet);
    }

    public void removePermissionSet(PermissionSet permissionSet) {
        this.permissionSets.remove(permissionSet);
    }
}
