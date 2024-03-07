package com.solar.api.tenant.service.extended;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.saas.service.integration.Content;
import com.solar.api.saas.service.integration.LocationJson;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.SiteDTO;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.physicalLocation.Site;
import com.solar.api.tenant.model.extended.physicalLocation.SiteLocation;
import com.solar.api.tenant.repository.LocationMappingRepository;
import com.solar.api.tenant.repository.PhysicalLocationRepository;
import com.solar.api.tenant.repository.SiteRepository;
import com.solar.api.tenant.service.contract.OrganizationService;
import com.solar.api.tenant.service.extended.sitemanagement.SiteLocationService;
import com.solar.api.tenant.service.extended.sitemanagement.SiteLocationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.toPhysicalLocationDTO;
import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.toPhysicalLocationDTOs;
import static com.solar.api.tenant.mapper.extended.physicalLocation.SiteMapper.*;

@Service
public class SiteServiceImpl implements SiteService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private LocationMappingRepository locationMappingRepository;
    @Autowired
    private PhysicalLocationRepository physicalLocationRepository;
    @Autowired
    private SiteLocationService siteLocationService;
    @Autowired
    private OrganizationService organizationService;

    @Override
    public Site findById(Long id) {
        Site site = siteRepository.findById(id).orElseThrow(() -> new NotFoundException(Site.class, id));
        List<PhysicalLocation> phLocs = siteLocationService.findPhysicalLocationsBySiteId(id);
        PhysicalLocation primaryLocation = siteLocationService.findPrimaryLocationBySiteId(id);
        if (primaryLocation != null) {
            phLocs.stream().filter(loc -> loc.getId().equals(primaryLocation.getId())).findFirst().ifPresent(loc -> loc.setIsPrimary(true));
        }
        site.setPhysicalLocations(phLocs);
        return site;
    }

    @Override
    public List<Site> findAll() {
        return siteRepository.findAll();
    }

    //TODO: Update the process to fetch masterOrg
    @Override
    public ObjectNode saveOrUpdate(SiteDTO siteDTO) {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        try {
            siteDTO.setActive(AppConstants.ACTIVE_STATUS);
            Site site = toSite(siteDTO);
            List<Long> phIds = site.getPhysicalLocations().stream().filter(ph -> ph.getId() != null).map(PhysicalLocation::getId).collect(Collectors.toList());
            List<PhysicalLocation> physicalLocationsDb = site.getPhysicalLocations();
            Organization organization = siteDTO.getOrgId() != null ? organizationService.findById(siteDTO.getOrgId()) : organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId(Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE, true, null);
            site.setOrganization(organization);
            site.setIsDeleted(false);
            if (site.getId() != null && site.getId() != 0) { //Update Case
                Site siteExists = findById(site.getId());
                Site siteDb2 = toUpdatedSite(siteExists, site);
                if (physicalLocationsDb.size() != 0) {
                    Site siteDbUpdated = siteRepository.save(siteDb2);
                    removeUnusedSiteLocations(siteDbUpdated.getId(), phIds); //Removing deleted physical locations
                    siteLocationService.buildSiteLocationList(siteDbUpdated, physicalLocationsDb); //Saving new physical locations
                    return messageJson.put("message", "Site Updated Successfully!");
                }
            } else { //Save Case
                Site siteNameAlreadyExists = siteRepository.findBySiteName(site.getSiteName());
                if (siteNameAlreadyExists != null) {
                    return messageJson.put("message", "Site Already Exists!");
                }
                Site siteDb = siteRepository.save(site);
                siteLocationService.buildSiteLocationList(siteDb, physicalLocationsDb);
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return messageJson.put("error", ex.getMessage());
        }
        return messageJson.put("message", "Site Added Successfully!");
    }

    @Override
    public List<SiteDTO> getSiteAndPhysicalLocationDetail(List<Content> contents) {
        List<SiteDTO> siteDTOS = new ArrayList<>();
        for (Content content : contents) {
            SiteDTO siteDto = null;
            List<PhysicalLocation> physicalLocations = null;
            Site site = siteRepository.findById(content.getSite().siteId).orElseThrow(() -> new NotFoundException(Site.class, content.getSite().siteId));
            List<PhysicalLocation> phLocs = siteLocationService.findPhysicalLocationsBySiteId(site.getId());
            PhysicalLocation primaryLocation = siteLocationService.findPrimaryLocationBySiteId(site.getId());
            if (primaryLocation != null) {
                phLocs.stream().filter(loc -> loc.getId().equals(primaryLocation.getId())).findFirst().ifPresent(loc -> loc.setIsPrimary(true));

                //and if there is ploc true at physical loc tbl then set it false as its not primary with linked site
                phLocs.stream().forEach(ploc -> {
                    if (ploc.getId() != primaryLocation.getId()) {
                        ploc.setIsPrimary(false);
                    }
                });
            }
            siteDto = toSiteDTO(site);
            siteDto.setPhysicalLocationDTOs(toPhysicalLocationDTOs(phLocs));
            siteDTOS.add(siteDto);

//            List<Long> physicalLocationIds = content.getSite().getLocations().stream().map(LocationJson::getPhysical_loc_Id).collect(Collectors.toList());
//            if (physicalLocationIds != null) {
//                physicalLocations = physicalLocationRepository.findByIdIn(physicalLocationIds);
//                if (physicalLocations == null)
//                    throw new SolarApiException("physical locations not with ids:" + physicalLocationIds);
//            }
//            siteDto = toSiteDTO(site);
//            siteDto.setPhysicalLocationDTOs(toPhysicalLocationDTOs(physicalLocations));
//            siteDTOS.add(siteDto);
        }
        return siteDTOS;
    }

    @Override
    public List<SiteDTO> findAllBySiteType(String siteType) {
        List<SiteDTO> sites = siteRepository.findAllBySiteType(siteType);
        Map<Long, SiteDTO> map = new HashMap<>();

        for (SiteDTO site : sites) {
            if (map.size() > 0 && map.containsKey(site.getId())) {
                SiteDTO siteObj = map.get(site.getId());
                siteObj.getPhysicalLocationDTOs().add(toPhysicalLocationDTO(site.getPhysicalLocation()));
                site.setPhysicalLocation(null);
            } else {
                List<PhysicalLocationDTO> list = new ArrayList<>();
                list.add(toPhysicalLocationDTO(site.getPhysicalLocation()));
                site.setPhysicalLocationDTOs(list);
                site.setPhysicalLocation(null);
                map.put(site.getId(), site);
            }
        }
        return map.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Site> findAllWithPhysicalLocations() {
        List<Site> site = new ArrayList<>();
        try {
            List<Site> sitesDB = siteRepository.findAllSitesByIsDeleted(false);
            if (sitesDB.size() != 0) {
                sitesDB.forEach(st -> {
                    List<PhysicalLocation> phLocs = siteLocationService.findPhysicalLocationsBySiteId(st.getId());
                    PhysicalLocation primaryLocation = siteLocationService.findPrimaryLocationBySiteId(st.getId());
                    if (primaryLocation != null) {
                        phLocs.stream().filter(loc -> loc.getId().equals(primaryLocation.getId())).findFirst().ifPresent(loc -> loc.setIsPrimary(true));

                        //and if there is ploc true at physical loc tbl then set it false as its not primary with linked site
                        phLocs.stream().forEach(ploc -> {
                            if (ploc.getId() != primaryLocation.getId()) {
                                ploc.setIsPrimary(false);
                            }
                        });
                    }
                    st.setPhysicalLocations(phLocs);
                    site.add(st);
                });
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        Comparator<Site> comparator = Comparator.comparing(Site::getUpdatedAt);
        Collections.sort(site, comparator.reversed());
        return site;
    }

    @Override
    public Site getSiteByRefId(Long refid) {
//        return siteRepository.findByRefId(refid);
        return null;
    }

    private void removeUnusedSiteLocations(Long siteId, List<Long> physicalLocationIds) {
        List<SiteLocation> allSiteLocations = siteLocationService.findAllBySiteId(siteId);
        List<SiteLocation> existingSiteLocations = siteLocationService.findAllBySiteIdAndPhysicalLocationIds(siteId, physicalLocationIds);
        allSiteLocations.removeAll(existingSiteLocations); //Filtering out the locations that were removed during the update case
        if (allSiteLocations.size() > 0) {
            siteLocationService.deleteAll(allSiteLocations); //Deleting the removed locations from database;
        }
    }

    @Override
    public ObjectNode updateSiteStatus(Long siteId, Boolean isActive) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        Site site = findById(siteId);
        try {
            if (site != null) {
                return isActive ? activateSite(site, response) : deActivateSite(site, response);
            } else {
                return response.put("message", "Invalid site id.");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return response.put("error", e.getMessage());
        }

    }

    @Override
    public ObjectNode deleteById(Long id) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        Site site = findById(id);
        try {
            if (site != null && site.getActive().equalsIgnoreCase(Constants.PHYSICAL_LOCATIONS.STATUS_INACTIVE)) {
                if (checkSiteLinking(site)) {
                    return response.put("message", "Cannot delete site due to active locations.");
                }
                site.setIsDeleted(true);
                siteRepository.save(site);
                return response.put("message", "Site has been deleted successfully.");
            } else {
                return site == null ? response.put("message", "Invalid site id.") :
                        response.put("message", "Active site can not be deleted.");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return response.put("error", e.getMessage());
        }
    }

    private ObjectNode activateSite(Site site, ObjectNode response) {
        site.setActive(Constants.PHYSICAL_LOCATIONS.STATUS_ACTIVE);
        siteRepository.save(site);
        return response.put("message", "Site marked as active.");
    }


    private ObjectNode deActivateSite(Site site, ObjectNode response) {

        if (checkSiteLinking(site)) {
            return response.put("message", "Cannot mark as inactive due to active locations.");
        }
        site.setActive(Constants.PHYSICAL_LOCATIONS.STATUS_INACTIVE);
        siteRepository.save(site);
        return response.put("message", "Site marked as in-active.");
    }

    //TODO: Need to add checks for other modules
    private Boolean checkSiteLinking(Site site) {
        Boolean result = false;
        List<PhysicalLocation> physicalLocations = siteLocationService.findPhysicalLocationsBySiteId(site.getId());
        if (physicalLocations.size() > 0) {
            result = true;
        }
        return result;
    }

    @Override
    public List<SiteDTO> findAllBySiteByCategoryAndIsDeleted(String category, Boolean isDeleted) {
        Organization organization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId("ACTIVE", true, null);
        List<Site> sites = siteRepository.findSitesByCategoryAndOrganizationAndIsDeleted(category, organization, isDeleted);
        Map<Long, SiteDTO> map = new HashMap<>();
        for (Site site : sites) {
            if (map.size() > 0 && map.containsKey(site.getId())) {
                SiteDTO siteObj = map.get(site.getId());
                siteObj.getPhysicalLocationDTOs().addAll(toPhysicalLocationDTOs(siteLocationService.findPhysicalLocationsBySiteId(site.getId())));
            } else {
                List<PhysicalLocationDTO> list = new ArrayList<>();
                list.addAll(toPhysicalLocationDTOs(siteLocationService.findPhysicalLocationsBySiteId(site.getId())));
                site.setSiteLocations(null);
                SiteDTO siteDTO = toSiteDTO(site);
                siteDTO.setPhysicalLocationDTOs(list);
                map.put(site.getId(), siteDTO);
            }
        }
        return map.values().stream().collect(Collectors.toList());
    }
}