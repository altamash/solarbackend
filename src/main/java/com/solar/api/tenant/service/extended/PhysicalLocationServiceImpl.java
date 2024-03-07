package com.solar.api.tenant.service.extended;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper;
import com.solar.api.tenant.mapper.extended.physicalLocation.SiteDTO;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.physicalLocation.Site;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.repository.PhysicalLocationRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.OrganizationService;
import com.solar.api.tenant.service.extended.sitemanagement.SiteLocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.toPhysicalLocationDTO;
import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.toPhysicalLocationDTOs;
import static com.solar.api.tenant.mapper.extended.physicalLocation.SiteMapper.toSiteDTOs;

@Service
public class PhysicalLocationServiceImpl implements PhysicalLocationService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private PhysicalLocationRepository physicalLocationRepository;
    @Autowired
    private LocationMappingService locationMappingService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SiteLocationService siteLocationService;

    @Override
    public PhysicalLocation findById(Long id) {
        return physicalLocationRepository.findById(id).orElseThrow(() -> new NotFoundException(PhysicalLocation.class, id));
    }

    @Override
    public List<PhysicalLocation> findAll() {
        return physicalLocationRepository.findAll();
    }

    @Override
    public List<PhysicalLocation> findAllNotLinkedToSite() {
        return physicalLocationRepository.findAll().stream().filter(ph -> ph.getExternalRefId() == null).collect(Collectors.toList());
    }

    @Override
    public PhysicalLocation findPhysicalLocationById(Long id) {
        return physicalLocationRepository.findById(id).orElseThrow(() -> new NotFoundException(PhysicalLocation.class, id));
    }

    //TODO: Update the process to fetch masterOrg
    @Override
    public PhysicalLocation saveOrUpdate(PhysicalLocation physicalLocation) {
        PhysicalLocation result = null;
        try {
            if (physicalLocation.getId() != null && physicalLocation.getId() != 0) {
                PhysicalLocation physicalLocationDb = findPhysicalLocationById(physicalLocation.getId());
                physicalLocationDb = PhysicalLocationMapper.toUpdatedPhysicalLocation(physicalLocationDb, physicalLocation);
                return physicalLocationRepository.save(physicalLocationDb);
            }
            Organization masterOrganization = (physicalLocation.getOrganization() != null && physicalLocation.getOrganization().getId() != null) ? organizationService.findById(physicalLocation.getOrganization().getId()) :
                    organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId(Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE, true, null);
            if (masterOrganization != null) {
                physicalLocation.setOrganization(masterOrganization);
            }

            physicalLocation.setStatus(Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE);
            physicalLocation.setIsDeleted(false);
            result = physicalLocationRepository.save(physicalLocation);
            if (physicalLocation.getAcctId() != null) {
                String primaryIndAddress = AppConstants.PRIMARY_INDEX_LOCATION_FALSE;
                User user = userService.findById(physicalLocation.getAcctId());
                List<LocationMapping> locationMappingDB = locationMappingService.findAllBySourceId(physicalLocation.getAcctId());
                if (locationMappingDB.isEmpty()) {
                    primaryIndAddress = AppConstants.PRIMARY_INDEX_LOCATION_TRUE;
                }
                locationMappingService.saveAll(userService.getAcquisitionLocationMappings(Arrays.asList(result), user, null, primaryIndAddress));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<PhysicalLocation> findAllByLocationType(String locationType) {
        return physicalLocationRepository.findAllByLocationType(locationType);
    }
    @Override
    public List<PhysicalLocation> findAllByLocationCategory(String locationCategory) {
        return physicalLocationRepository.findAllByCategory(locationCategory);
    }

    @Override
    public List<PhysicalLocation> saveAll(List<PhysicalLocation> physicalLocations) {
        return physicalLocationRepository.saveAll(physicalLocations);
    }

    @Override
    public ObjectNode deleteById(Long id) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        PhysicalLocation physicalLocation = findById(id);
        try {
            if (physicalLocation != null && physicalLocation.getStatus().equalsIgnoreCase(Constants.PHYSICAL_LOCATIONS.STATUS_INACTIVE)) {
                if (checkLocationLinking(physicalLocation)) {
                    return response.put("message", "Cannot delete location due to active sites.");
                }
                physicalLocation.setIsDeleted(true);
                physicalLocationRepository.save(physicalLocation);
                return response.put("message", "Physical location has been deleted successfully.");
            } else {
                return physicalLocation == null ? response.put("message", "Invalid location id.") :
                        response.put("message", "Active location can not be deleted.");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return response.put("error", e.getMessage());
        }
    }

    @Override
    public PhysicalLocationDTO findLocationByUserId(Long userId) {
        String primaryInd = "Y";
        LocationMapping locationMapping = locationMappingService.findBySourceIdAndSourceTypeAndPrimaryInd(userId, Constants.LOCATION_CATEGORY_CONSTANTS.USER, primaryInd);
        if (locationMapping == null) {
            throw new NotFoundException(LocationMapping.class, userId);
        }
        Optional<PhysicalLocation> physicalLocation = physicalLocationRepository.findById(locationMapping.getLocationId());
        if (physicalLocation.isPresent())
            return toPhysicalLocationDTO(physicalLocation.get());
        else
            return null;
    }

    @Override
    public List<PhysicalLocation> getPhysicalLocationById(List<Long> ids) {
        return physicalLocationRepository.findByIdIn(ids);
    }

    @Override
    public List<PhysicalLocation> findAllByEntityIdIn(List<Long> entityIds) {
        return physicalLocationRepository.findAllByEntityIdIn(entityIds);
    }

    @Override
    public List<PhysicalLocation> getAllSiteUnselectedLocationByType(String locationType, String unitType, Long parentOrgId) {
        List<PhysicalLocation> physicalLocations = null;
        try {
            if (parentOrgId != null && unitType != null)
                physicalLocations = physicalLocationRepository.findAllByLocationTypeAndExternalRefId(locationType, unitType, parentOrgId);
            else
                physicalLocations = physicalLocationRepository.findAllByLocationType(locationType);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SolarApiException("location not found");
        }

        return physicalLocations;
    }

    @Override
    public List<PhysicalLocation> getAllLocationsByExternalRefId(Long refId) {
        return physicalLocationRepository.findAllByExternalRefId(refId);
    }


    @Override
    public boolean isSiteLocationAssociationExist(Long locationId, Long siteId) {
        return physicalLocationRepository.findSiteLocationAssociationExist(locationId, siteId);
    }

    @Override
    public PhysicalLocation removeLocationAssociationById(Long refId) {

        PhysicalLocation location = findById(refId);
        location.setExternalRefId(0l);

        location = saveOrUpdate(location);

        return location;
    }

    @Override
    public List<PhysicalLocationDTO> getAllLocationsDTOByExternalRefId(Long orgId) {
        return physicalLocationRepository.getAllLocationsDTOByExternalRefId(orgId);
    }

    /**
     * Description : this method takes entity id and looking into user tbl and return list of physical location
     * created_by : sana
     * create_date : 12-22-2022
     *
     * @param entityId : its the primary id of entity table.
     * @return physical location list for particular user
     */
    @Override
    public Map getAllPhysicalLocationByEntityId(Long entityId) {
        Map map = new HashMap();
        List<Long> physicalLocIds = null;
        try {
            User user = userService.findUserByEntityId(entityId);
            map.put("data", null);
            if (user != null) {
                List<LocationMapping> locationMapping = locationMappingService.findBySourceId(user.getAcctId());
                if (locationMapping != null) {
                    physicalLocIds = locationMapping.stream().map(LocationMapping::getLocationId).collect(Collectors.toList());
                    List<PhysicalLocation> physicalLocationList = physicalLocationRepository.findByIdInAndStatusAndIsDeleted(physicalLocIds, Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE, false);
                    map.put("data", toPhysicalLocationDTOs(physicalLocationList));
                    map.put("code", HttpStatus.OK);
                    map.put("message", "user Physical Locations");
                } else {
                    map.put("code", HttpStatus.NOT_FOUND);
                    map.put("message", "can n't find locations for user");
                }

            } else {
                map.put("code", HttpStatus.NOT_FOUND);
                map.put("message", "can n't find user with following entity id = " + entityId);
            }
        } catch (Exception ex) {
            map.put("code", HttpStatus.NOT_FOUND);
            map.put("message", ex.getMessage());
            map.put("data", null);
        }
        return map;
    }

    @Override
    public PhysicalLocation findByEntityId(Long entityId) {
        return physicalLocationRepository.findByEntityId(entityId);
    }

    @Override
    public List<PhysicalLocation> findAllByOrgId(Long orgId) {
        List<PhysicalLocation> physicalLocations = new ArrayList<>();
        try {
            physicalLocations = physicalLocationRepository.findAllByOrgIdAndStatus(orgId, Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return physicalLocations;
    }

    @Override
    public List<PhysicalLocation> findAllPhysicalLocationsByIsDeleted(Boolean isDeleted) {
        return physicalLocationRepository.findAllPhysicalLocationsByIsDeleted(isDeleted);
    }

    @Override
    public PhysicalLocationDTO findPhysicalLocationWithSitesById(Long locId) {
        PhysicalLocationDTO physicalLocationDTO = toPhysicalLocationDTO(findById(locId));
        List<SiteDTO> siteDTOs = toSiteDTOs(siteLocationService.findAllSitesByPhysicalLocationId(locId));
        physicalLocationDTO.setSiteDTOs(siteDTOs);
        return physicalLocationDTO;
    }

    @Override
    public ObjectNode updateLocationStatus(Long locId, Boolean isActive) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        PhysicalLocation physicalLocation = findById(locId);
        try {
            if (physicalLocation != null) {
                return isActive ? activateLocation(physicalLocation, response) : deActivateLocation(physicalLocation, response);
            } else {
                return response.put("message", "Invalid location id.");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return response.put("error", e.getMessage());
        }

    }

    private ObjectNode activateLocation(PhysicalLocation physicalLocation, ObjectNode response) {
        physicalLocation.setStatus(Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE);
        physicalLocationRepository.save(physicalLocation);
        return response.put("message", "Location marked as active.");
    }


    private ObjectNode deActivateLocation(PhysicalLocation physicalLocation, ObjectNode response) {

        if (checkLocationLinking(physicalLocation)) {
            return response.put("message", "Cannot mark as inactive due to active sites.");
        }
        physicalLocation.setStatus(Constants.PHYSICAL_LOCATIONS.STATUS_INACTIVE);
        physicalLocationRepository.save(physicalLocation);
        return response.put("message", "Location marked as in-active.");
    }

    //TODO: Need to add checks for other modules
    private Boolean checkLocationLinking(PhysicalLocation physicalLocation) {
        Boolean result = false;
        List<Site> sites = siteLocationService.findAllSitesByPhysicalLocationId(physicalLocation.getId());
        if (sites.size() > 0) {
            result = true;
        }
        return result;
    }

    @Override
    public ObjectNode markLocationAsPrimary(Long acctId, Long locId) {
       ObjectNode response = new ObjectMapper().createObjectNode();
        List<LocationMapping> locationMappings = locationMappingService.findAllBySourceId(acctId);
        locationMappings.stream().forEach(locationMapping -> {
            if(locationMapping.getLocationId().longValue() == locId.longValue()) {
                locationMapping.setPrimaryInd("Y");
            }else{
                locationMapping.setPrimaryInd("N");
            }
        });
        locationMappingService.saveAll(locationMappings);
     return response.put("message", "Location marked as primary.");
    }
    @Override
    public List<String> findZipCodesByEntityAndLocationStatus(String entityType, Boolean isActive, Boolean isDeleted, Boolean locationDeleted)
    {
        return physicalLocationRepository.findZipCodesByEntityAndLocationStatus(entityType,isActive,isDeleted,locationDeleted);
    }
    @Override
    public List<PhysicalLocation> findSubOrgLocations(Long orgId)
    {
        return physicalLocationRepository.findSubOrgLocations(orgId);
    }
}
