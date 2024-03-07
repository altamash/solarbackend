package com.solar.api.tenant.mapper.extended.physicalLocation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import lombok.*;

import javax.persistence.Transient;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SiteDTO {

    private Long id; //required (Update)
    private String siteName; //required
    private String siteType; //required
    private String subType;
    private String active;
    @Transient
    private List<LocationMappingDTO> locationMappings;
    private List<PhysicalLocationDTO> physicalLocationDTOs; //required
    private PhysicalLocation physicalLocation;
    private Long orgId; //required
    private String category; //required
    private Boolean isDeleted;

    public SiteDTO(Long id, String siteName, String siteType, String subType, String active, PhysicalLocation physicalLocation) {
        this.id = id;
        this.siteName = siteName;
        this.siteType = siteType;
        this.subType = subType;
        this.active = active;
        this.physicalLocation = physicalLocation;
    }
}
