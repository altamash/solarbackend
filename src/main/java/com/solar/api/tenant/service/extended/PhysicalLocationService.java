package com.solar.api.tenant.service.extended;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;

import java.util.List;
import java.util.Map;

public interface PhysicalLocationService {

    PhysicalLocation findById(Long id);

    List<PhysicalLocation> findAll();

    List<PhysicalLocation> findAllNotLinkedToSite();

    PhysicalLocation findPhysicalLocationById(Long id);

    PhysicalLocation saveOrUpdate(PhysicalLocation physicalLocation);

    List<PhysicalLocation> findAllByLocationType(String locationType);

    List<PhysicalLocation> findAllByLocationCategory(String locationCategory);

    List<PhysicalLocation> saveAll(List<PhysicalLocation> physicalLocations);

    ObjectNode deleteById(Long id);

    PhysicalLocationDTO findLocationByUserId(Long userId);

    List<PhysicalLocation> getPhysicalLocationById(List<Long> ids);

    List<PhysicalLocation> findAllByEntityIdIn(List<Long> entityIds);

    List<PhysicalLocation> getAllSiteUnselectedLocationByType(String locationType, String unitType, Long parentOrgId);

    List<PhysicalLocation> getAllLocationsByExternalRefId(Long refId);

    boolean isSiteLocationAssociationExist(Long locationId, Long siteId);

    PhysicalLocation removeLocationAssociationById(Long refId);

    List<PhysicalLocationDTO> getAllLocationsDTOByExternalRefId(Long orgId);

    Map getAllPhysicalLocationByEntityId(Long entityId);

    PhysicalLocation findByEntityId(Long entityId);

    List<PhysicalLocation> findAllByOrgId(Long orgId);

    List<PhysicalLocation> findAllPhysicalLocationsByIsDeleted(Boolean isDeleted);

    PhysicalLocationDTO findPhysicalLocationWithSitesById(Long locId);

    ObjectNode updateLocationStatus(Long locId, Boolean isActive);

    ObjectNode markLocationAsPrimary(Long acctId, Long locId);

    List<String> findZipCodesByEntityAndLocationStatus(String entityType, Boolean isActive, Boolean isDeleted, Boolean locationDeleted);

    List<PhysicalLocation> findSubOrgLocations(Long orgId);
}
