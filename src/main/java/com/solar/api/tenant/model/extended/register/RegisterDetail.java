package com.solar.api.tenant.model.extended.register;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.resources.MeasureBlockHead;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "register_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @Column(nullable = false)
    private Long measureBlockId;

    //@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE, mappedBy = "registerDetail")
    //@JoinColumn(name="measure_code", referencedColumnName = "id" ,nullable=false)
    private String measureCode;
    private Long measureCodeId;
/*
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "measure_code_id", referencedColumnName = "id")
    private MeasureDefinition measureDefinition;
*/

    @Transient
    private MeasureDefinitionTenantDTO measureDefinitionTenant;
    @Transient
    private MeasureBlockHead measureBlockHead;
    private String defaultValue;
    private String level;
    private String category;
    private Integer sequenceNumber;
    private Boolean multiEntry;
    private Boolean mandatory;
    private String filterByInd;
    private String variableByDetail;
    private String flags;

    @ManyToOne
    @JoinColumn(name = "register_id", referencedColumnName = "id")
    private RegisterHead registerHead;

    @Transient
    private Long registerHeadId;
    @Transient
    private Long measureId;
    @Transient
    private String blockName;

    @Column(columnDefinition = "boolean default false")
    private Boolean measureUnique; //use it for unique measure value

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
