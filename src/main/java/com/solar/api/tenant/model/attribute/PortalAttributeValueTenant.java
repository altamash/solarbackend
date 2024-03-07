package com.solar.api.tenant.model.attribute;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "portal_attribute_value")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalAttributeValueTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "attribute_id")
    private PortalAttributeTenant attribute;
    @Transient
    private String attributeName;
    @Column(length = 125)
    private String attributeValue;
    private Integer sequenceNumber;
    @Column(length = 45)
    private String parentReferenceValue;
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
