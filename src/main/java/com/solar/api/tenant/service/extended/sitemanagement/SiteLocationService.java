package com.solar.api.tenant.service.extended.sitemanagement;

import com.solar.api.tenant.model.extended.physicalLocation.Site;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.physicalLocation.SiteLocation;

import java.util.List;

public interface SiteLocationService {
    List<SiteLocation> saveAll(List<SiteLocation> siteLocationList);

    void deleteAll(List<SiteLocation> siteLocationList);

    List<SiteLocation> findAllBySiteIdAndPhysicalLocationIds(Long siteId, List<Long> physicalLocationIds);

    List<SiteLocation> findAllBySiteId(Long siteId);

    SiteLocation findBySiteIdAndPhysicalLocationId(Long siteId, Long physicalLocationId);

    List<SiteLocation> buildSiteLocationList(Site site, List<PhysicalLocation> physicalLocations);

    List<PhysicalLocation> findPhysicalLocationsBySiteId(Long siteId);

    List<Site> findAllSitesByPhysicalLocationId(Long locId);

    PhysicalLocation findPrimaryLocationBySiteId(Long siteId);

}
