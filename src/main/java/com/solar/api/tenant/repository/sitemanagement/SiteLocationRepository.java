package com.solar.api.tenant.repository.sitemanagement;

import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.physicalLocation.Site;
import com.solar.api.tenant.model.extended.physicalLocation.SiteLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SiteLocationRepository extends JpaRepository<SiteLocation, Long> {

    @Query("select sl from SiteLocation sl where sl.site.id=:siteId and sl.physicalLocation.id in(:physicalLocationIds)")
    List<SiteLocation> findAllBySiteIdAndPhysicalLocationIds(@Param("siteId") Long siteId, @Param("physicalLocationIds") List<Long> physicalLocationIds);

    @Query("select sl from SiteLocation sl where sl.site.id=:siteId")
    List<SiteLocation> findAllBySiteId(@Param("siteId") Long siteId);

    @Query("select sl from SiteLocation sl where sl.site.id=:siteId and sl.physicalLocation.id = :physicalLocationId")
    SiteLocation findBySiteIdAndPhysicalLocationId(@Param("siteId") Long siteId, @Param("physicalLocationId") Long physicalLocationId);

    @Query("SELECT sl.physicalLocation FROM SiteLocation sl WHERE sl.site.id = :siteId")
    List<PhysicalLocation> findAllPhysicalLocationsBySiteId(@Param("siteId") Long siteId);

    @Query("SELECT sl.site FROM SiteLocation sl WHERE sl.physicalLocation.id = :locId")
    List<Site> findAllSitesByPhysicalLocationId(@Param("locId") Long locId);

    @Query("SELECT sl.physicalLocation FROM SiteLocation sl WHERE sl.site.id = :siteId and sl.locationPrimary=:isPrimary")
    PhysicalLocation findPrimaryPhysicalLocationBySiteId(@Param("siteId") Long siteId, @Param("isPrimary") Boolean isPrimary);

}


