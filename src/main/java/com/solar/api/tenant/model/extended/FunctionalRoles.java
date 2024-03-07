package com.solar.api.tenant.model.extended;

import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroup;
import com.solar.api.tenant.model.billing.billingHead.BillingDetail;
import com.solar.api.tenant.model.userGroup.EntityRole;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "functional_roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionalRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private Long defaultPrivilegeLevel;
    private String defaultHierarchyId; //keeping sequence no 001.001
    private String defaultHierarchySeqCode; //keeping sequence no 001.001
    private String category;
    private String subCategory;
    private String hierarchyType; //project,partner,
    private String status; //project,partner,

    @OneToMany(mappedBy = "functionalRoles")
    private List<EntityRole> entityRoles;
    @OneToMany(mappedBy = "functionalRoles")
    private List<DefaultUserGroup> defaultUserGroups;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
