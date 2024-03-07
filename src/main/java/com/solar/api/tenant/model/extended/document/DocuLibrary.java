package com.solar.api.tenant.model.extended.document;

import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Organization;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "docu_library")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocuLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long docuId;
    private String docuType;
    private String codeRefType;
    private String codeRefId;
    private String docuName;
    private String notes;
    private String tags;
    private String size;
    private String format;
    private String uri;
    private String status;
    private Boolean visibilityKey;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "entity_id")
    private com.solar.api.tenant.model.contract.Entity entity;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    private Long compKey;
    private Long resourceInterval;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name="digitally_signed")
    private String digitallySigned;

    @Column(name="sign_ref_no")
    private String signRefNo;
    @Column(name="reference_time")
    private String referenceTime;

}
