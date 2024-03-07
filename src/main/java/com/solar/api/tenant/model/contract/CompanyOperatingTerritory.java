package com.solar.api.tenant.model.contract;


import lombok.*;

import javax.persistence.*;
import javax.persistence.Entity;


@Entity
@Table(name = "company_operating_territory")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyOperatingTerritory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "geo_lat")
    private String geoLat;

    @Column(name = "geo_long")
    private String geoLong;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
}
