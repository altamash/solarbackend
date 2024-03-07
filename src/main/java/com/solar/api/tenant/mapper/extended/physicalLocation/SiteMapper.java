package com.solar.api.tenant.mapper.extended.physicalLocation;

import com.solar.api.tenant.model.extended.physicalLocation.Site;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SiteMapper {

    /**
     * To Create Default site object
     * <p>
     * On Organization Creation
     *
     * @param orgId
     * @return
     */
    public static Site toSite(Long orgId) {
        if (orgId == null) {
            return null;
        }
        return Site.builder()
                .siteType("Construction")
                .siteName("Business Unit")
                .subType("Default Sub type")
                .active("Active")
                .refCode("Org")
                .build();
    }

    public static Site toSite(SiteDTO siteDTO) {
        if (siteDTO == null) {
            return null;
        }
        return Site.builder()
                .id(siteDTO.getId())
                .siteType(siteDTO.getSiteType())
                .siteName(siteDTO.getSiteName())
                .subType(siteDTO.getSubType())
                .active(siteDTO.getActive())
                .category(siteDTO.getCategory())
                .physicalLocations(siteDTO.getPhysicalLocationDTOs() != null ? PhysicalLocationMapper.toPhysicalLocations(siteDTO.getPhysicalLocationDTOs()) : Collections.emptyList())
                //.siteLocations(siteDTO.getSiteLocations() != null ? toSiteLocations(siteDTO.getSiteLocations()) : Collections.emptyList())
                .build();
    }

    public static SiteDTO toSiteDTO(Site site) {
        if (site == null) {
            return null;
        }
        return SiteDTO.builder()
                .id(site.getId())
                .siteType(site.getSiteType())
                .siteName(site.getSiteName())
                .subType(site.getSubType())
                .active(site.getActive())
                .category(site.getCategory())
                .physicalLocationDTOs(site.getPhysicalLocations() != null ? PhysicalLocationMapper.toPhysicalLocationDTOs(site.getPhysicalLocations()) : Collections.emptyList())
                //.siteLocations(site.getSiteLocations() != null ? toSiteLocationDTOs(site.getSiteLocations()) : Collections.emptyList())
                .build();
    }

    public static Site toUpdatedSite(Site site, Site siteUpdate) {
        site.setSiteName(siteUpdate.getSiteName() == null ? site.getSiteName() : siteUpdate.getSiteName());
        site.setSiteType(siteUpdate.getSiteType() == null ? site.getSiteType() : siteUpdate.getSiteType());
        site.setSubType(siteUpdate.getSubType() == null ? site.getSubType() : siteUpdate.getSubType());
        site.setActive(siteUpdate.getActive() == null ? site.getActive() : siteUpdate.getActive());
        site.setCategory(siteUpdate.getCategory() == null ? site.getCategory(): siteUpdate.getCategory());
        //site.setSiteLocations(siteUpdate.getSiteLocations() != null ? site.getSiteLocations() : siteUpdate.getSiteLocations());
        return site;
    }

    public static List<Site> toSites(List<SiteDTO> siteDTOS) {
        return siteDTOS.stream().map(a -> toSite(a)).collect(Collectors.toList());
    }

    public static List<SiteDTO> toSiteDTOs(List<Site> sites) {
        return sites.stream().map(a -> toSiteDTO(a)).collect(Collectors.toList());
    }
}
