package com.solar.api.tenant.model.contract;

import com.solar.api.tenant.model.attribute.PortalAttributeTenant;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.model.docuSign.SigningRequestTracker;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.userGroup.EntityRole;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@javax.persistence.Entity
@Table(name = "entity")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entityName;
    private String entityType;  //  Customer, Employee, Investor, Partner
    private String status;
    private Boolean isDocAttached;
    private Boolean isDeleted;
    //added for business info
    private String companyName;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private String website;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "organization_id")
    private Organization organization;
    @OneToMany(mappedBy = "entity", cascade = CascadeType.MERGE)
    private List<Contract> contracts;
    @OneToMany(mappedBy = "entity", cascade = CascadeType.MERGE)
    private List<DocuLibrary> docuLibraries;
    @OneToMany(mappedBy = "entity", cascade = CascadeType.MERGE)
    private List<UserLevelPrivilege> userLevelPrivileges;

    @OneToMany(mappedBy = "entity")
    private List<EntityRole> entityRoles;
//    @OneToOne(mappedBy = "entity")
//    private SigningRequestTracker signingRequestTracker;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private  Boolean isActive;
    private String registerType; //project/partners..
    private Long registerId;

    public Entity(Long id){
        this.id =id;
    }

}
