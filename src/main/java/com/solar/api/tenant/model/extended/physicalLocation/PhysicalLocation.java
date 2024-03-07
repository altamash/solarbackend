package com.solar.api.tenant.model.extended.physicalLocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solar.api.tenant.mapper.contract.OrganizationDTO;
import com.solar.api.tenant.model.contract.Organization;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "physical_locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String locationName;
    private String locationType;
    private String otherDetails;
    private String add1;
    private String add2;
    private String add3;
    private String contactPerson;
    private String phone;
    private String category; //ext;
    private String email;
    //private String externalRefId;
    private Long externalRefId;
    private String geoLat;
    private String geoLong;
    private String googleCoordinates;
    private String terrainType;
    private String status;
    private String active;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    @Column(columnDefinition = "boolean default false")
    private Boolean correspondenceAddress; //use it for unique measure value
    private Long entityId;
    @Column(columnDefinition = "boolean default false")
    private Boolean primaryIndex; //use it for primaryIndex
    @Transient
    private String action;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private String zipCode;
    private Boolean isPrimary;
    private Boolean isDeleted;
    @ManyToOne
    @JoinColumn(name="org_id")
    @JsonIgnore
    Organization organization;
    @Transient
    private Long acctId;
}
