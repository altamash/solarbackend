package com.solar.api.saas.model.attribute;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "portal_attribute")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalAttributeSAAS implements Serializable {

    private static final long serialVersionUID = 6993009308481768666L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(length = 45)
    private String parent;
    @Column(length = 45)
    private String associateTo;
    private String attributeType;
    private Boolean locked; // Cannot be overridden
    @Lob
    private byte[] icon;
    private Long wfId;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "attribute", cascade = CascadeType.ALL)
    private List<PortalAttributeValueSAAS> portalAttributeValuesSAAS;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
