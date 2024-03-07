package com.solar.api.tenant.model.contract;

import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.physicalLocation.Site;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@javax.persistence.Entity
@Table(name = "organization")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String organizationName;
    private String businessDescription;
    private String organizationType; //master or BusinessUnit
    private Long parentOrgId; //master org id for BusinessUnit
    private String logoImage;
    private Boolean isDocAttached;
    private Boolean primaryIndicator;
    private String status;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ref_id")
    private List<Site> sites;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "organization_id")
    private List<Entity> entities;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "organization_id")
    private List<DocuLibrary> docuLibraries;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "organization_id")
    private List<UserLevelPrivilege> userLevelPrivileges;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Long subType; // business unit type(i.e distribution centre)
    private Long organizationSubType;// business unit type(i.e distribution centre)

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_preference_id", referencedColumnName = "id")
    private CompanyPreference companyPreference;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "contact_person_id", referencedColumnName = "id")
    private Entity contactPerson;
    private String unitCategory;
    private String unitCategoryType;

}
