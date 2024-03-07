package com.solar.api.tenant.model.extended.physicalLocation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.solar.api.tenant.model.contract.Organization;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sites")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String siteName;
    private String siteType;
    private String subType;
    private String active;
    private String refCode;
    @JsonIgnore
    @OneToMany(mappedBy = "site", cascade = CascadeType.MERGE)
    private List<SiteLocation> siteLocations;
    @Transient
    private List<LocationMapping> locationMappings;
    @Transient
    private List<PhysicalLocation> physicalLocations;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ref_id")
    private Organization organization;
    private Boolean isDeleted;
    private String category;
}
