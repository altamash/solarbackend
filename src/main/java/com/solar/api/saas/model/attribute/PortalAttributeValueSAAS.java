package com.solar.api.saas.model.attribute;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "portal_attribute_value")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalAttributeValueSAAS implements Serializable {

    private static final long serialVersionUID = -320201016664966345L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "attribute_id")
    private PortalAttributeSAAS attribute;
    @Transient
    private String attributeName;
    @Column(length = 125)
    private String attributeValue;
    private Integer sequenceNumber;
    @Column(length = 45)
    private String parentReferenceValue;
    private String description;
    @Column(length = 255)
    private String systemValue;
    @Lob
    private byte[] icon;
    private Long resourceInterval;
    private String status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
