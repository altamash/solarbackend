package com.solar.api.tenant.model.attribute;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "portal_attribute")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalAttributeTenant {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(length = 45)
    private String parent;
    @Column(length = 45)
    private String associateTo;
    private String attributeType;
    private Boolean locked; // Cannot be overridden
    private Long wfId;
    @OneToMany(mappedBy = "attribute", cascade = CascadeType.MERGE)
    private List<PortalAttributeValueTenant> portalAttributeValuesTenant;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
