package com.solar.api.tenant.mapper.contract;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyOperatingTerritoryDTO {

    private Long id;
    private String name;
    private String description;
    private OrganizationDTO organizationDTO;
    private String geoLat;
    private String geoLong;

    public CompanyOperatingTerritoryDTO(Long id, String name, String description,String geoLat, String geoLong) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.geoLat=geoLat;
        this.geoLong=geoLong;
    }
}