package com.solar.api.tenant.service.extended.sitemanagement;


import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.physicalLocation.SiteLocation;
import com.solar.api.tenant.repository.SiteRepository;
import com.solar.api.tenant.repository.sitemanagement.SiteLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.solar.api.tenant.model.extended.physicalLocation.Site;

import java.util.ArrayList;
import java.util.List;

@Service
public class SiteLocationServiceImpl implements SiteLocationService {
    @Autowired
    private SiteLocationRepository siteLocationRepository;
    @Autowired
    private SiteRepository siteRepository;

    @Override
    public List<SiteLocation> saveAll(List<SiteLocation> siteLocationList) {
        return siteLocationRepository.saveAll(siteLocationList);
    }

    @Override
    public void deleteAll(List<SiteLocation> siteLocationList) {
        siteLocationRepository.deleteAll(siteLocationList);
    }

    @Override
    public List<SiteLocation> findAllBySiteIdAndPhysicalLocationIds(Long siteId, List<Long> physicalLocationIds) {
        return siteLocationRepository.findAllBySiteIdAndPhysicalLocationIds(siteId, physicalLocationIds);
    }

    @Override
    public List<SiteLocation> findAllBySiteId(Long siteId) {
        return siteLocationRepository.findAllBySiteId(siteId);
    }

    @Override
    public List<SiteLocation> buildSiteLocationList(Site site, List<PhysicalLocation> physicalLocations) {
        List<SiteLocation> siteLocations = new ArrayList<>();
        physicalLocations.stream().forEach(pl -> {
            SiteLocation siteLocationExist = findBySiteIdAndPhysicalLocationId(site.getId(), pl.getId());
            if (siteLocationExist == null) {
                siteLocations.add(SiteLocation.builder().physicalLocation(pl).site(site)
                        .type(site.getSiteType()).category(site.getCategory())
                        .locationPrimary(pl.getIsPrimary() != null ? pl.getIsPrimary() : false).build());
            }
            else if (siteLocationExist != null) {
                    siteLocationExist.setLocationPrimary(pl.getIsPrimary()!= null? pl.getIsPrimary() : false);
                    siteRepository.save(site);
            }
        });
        return saveAll(siteLocations);
    }

    @Override
    public SiteLocation findBySiteIdAndPhysicalLocationId(Long siteId, Long physicalLocationId) {
        return siteLocationRepository.findBySiteIdAndPhysicalLocationId(siteId, physicalLocationId);
    }

    @Override
    public List<PhysicalLocation> findPhysicalLocationsBySiteId(Long siteId) {
        return siteLocationRepository.findAllPhysicalLocationsBySiteId(siteId);
    }

    @Override
    public List<Site> findAllSitesByPhysicalLocationId(Long locId) {
        return siteLocationRepository.findAllSitesByPhysicalLocationId(locId);
    }

    @Override
    public PhysicalLocation findPrimaryLocationBySiteId(Long siteId) {
        return siteLocationRepository.findPrimaryPhysicalLocationBySiteId(siteId, true);
    }
}
