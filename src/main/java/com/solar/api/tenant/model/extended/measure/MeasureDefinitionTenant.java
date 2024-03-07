package com.solar.api.tenant.model.extended.measure;

import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "measure_definition",
        uniqueConstraints = @UniqueConstraint(columnNames = {"code", "regModuleId"}, name =
                "uniqueCodeRegModIdConstraint"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasureDefinitionTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String measure;
    @Column(length = 50, unique = true)
    private String code;
    private String format;
    private String uom;
    private Boolean pct;
    private String attributeIdRef;
    private Long attributeIdRefId;
    private Boolean locked;
    private Boolean mandatory; // not used
    private String relatedMeasure;
    private String alias;
    private String type;
    private String regModule;
    private Long regModuleId;
    private String validationRule;
    private String validationParams;
    private String actions;
    private String visibilityLevel;
    private String compEvents;
    private Boolean systemUsed; // Cannot be overridden
    private String notes;
    private Boolean visible;

    //@OneToOne(mappedBy = "measureDefinition")
    //private RegisterDetail registerDetail;
    @Transient
//    private String portalAttributeValues;
    private List<PortalAttributeValueTenantDTO> portalAttributeValues;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
