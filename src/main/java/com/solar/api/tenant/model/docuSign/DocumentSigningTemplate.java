package com.solar.api.tenant.model.docuSign;

import com.solar.api.tenant.model.contract.Contract;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "doc_signing_template",
        uniqueConstraints = @UniqueConstraint(columnNames = {"functionality", "org_id", "entity_id", "contract_id"},
                name = "uniqueFunctionalityOrgIdEntityIdContractIdConstraint"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSigningTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // composite key : functionality + org id + entity id + contract id
//    @Column(length = 50)
//    private String signTempId;
    @Column(length = 50)
    private String templateName;
    private Boolean enabled = true;
    @Column(length = 20)
    private String functionality; // portal attribute, SIGNFN
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "org_id", referencedColumnName = "id")
    private Organization organization;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "entity_id", referencedColumnName = "id")
    private com.solar.api.tenant.model.contract.Entity entity;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private Contract contract;
    @Column(length = 20)
    private String customerType; // portal attribute, DSGN_TPL
    @Transient
    private DocuLibrary docuLibrary;
    @Column(length = 100)
    private String extTemplateId; // Zoho Signs template ID once the document is uploaded.
    @OneToMany(mappedBy = "documentSigningTemplate", cascade = CascadeType.MERGE)
    private List<SigningRequestTracker> signingRequestTrackers;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
