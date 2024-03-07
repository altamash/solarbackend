package com.solar.api.tenant.service.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.StorageException;
import com.solar.api.AppConstants;
import com.solar.api.configuration.JwtTokenUtil;
import com.solar.api.exception.ForbiddenException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.saas.mapper.companyPreference.CompanyPreferenceDTO;
import com.solar.api.saas.mapper.companyPreference.CompanyPreferenceMapper;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.service.StorageService;
import com.solar.api.tenant.mapper.contract.*;
import com.solar.api.tenant.mapper.extended.document.DocuMapper;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper;
import com.solar.api.tenant.mapper.extended.physicalLocation.SiteMapper;
import com.solar.api.tenant.mapper.organization.OrganizationResponseDTO;
import com.solar.api.tenant.mapper.organization.UnitDTO;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementGroupBy;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementPaginationTile;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementTile;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementTileMapper;
import com.solar.api.tenant.mapper.tiles.customersupportmanagement.CustomerSupportFiltersMapper;
import com.solar.api.tenant.mapper.tiles.customersupportmanagement.CustomerSupportFiltersTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.LinkedSiteTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.LinkedSitesTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter.LinkedSiteGroupBy;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter.LinkedSitePaginationTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter.LinkedSitesFiltersTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementPaginationTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementTemplate;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementTileMapper;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.*;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.filter.PhysicalLocationOMFilterDTO;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.filter.PhysicalLocationOMFilterMapper;
import com.solar.api.tenant.mapper.user.userGroup.DefaultUserGroupResponseDTO;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.contract.CompanyOperatingTerritory;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementPaginationTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.employee.EmployeeManagementTile;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.contract.OrganizationDetail;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.physicalLocation.Site;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.UserTemplate;
import com.solar.api.tenant.repository.CompanyPreferenceRepository;
import com.solar.api.tenant.repository.SiteRepository;
import com.solar.api.tenant.repository.contract.CompanyOperatingTerritoryRepository;
import com.solar.api.tenant.repository.contract.OrganizationRepository;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.contract.lookup.AttachmentFactory;
import com.solar.api.tenant.service.contract.lookup.ERefCode;
import com.solar.api.tenant.service.contract.validation.IContractValidation;
import com.solar.api.tenant.service.contract.validation.ValidationFactory;
import com.solar.api.tenant.service.extended.LocationMappingService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementTileMapper.*;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static String ERROR_MSG_UPGRADE_ACCT = "Please upgrade your account";
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Value("${app.profile}")
    private String appProfile;

    @Value("${app.storage.container}")
    private String storageContainer;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private DocuLibraryService docuLibraryService;

    @Autowired
    private ValidationFactory validationFactory;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AttachmentFactory attachmentFactory;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    private OrganizationDetailService organizationDetailService;

    @Autowired
    private PhysicalLocationService physicalLocationService;

    @Autowired
    private LocationMappingService locationMappingService;

    @Autowired
    private CompanyOperatingTerritoryRepository companyOperatingTerritoryRepository;
    @Autowired
    private CompanyPreferenceRepository companyPreferenceRepository;

    @Autowired
    private EntityService entityService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private OrganizationManagementTileMapper organizationManagementTileMapper;


    @Override
    public Organization add(String authorization, Organization organization, String refCode, List<MultipartFile> multipartFiles)
            throws URISyntaxException, IOException, StorageException {
        IContractValidation validation = validationFactory.get(jwtTokenUtil.getMasterTenant(authorization).getTenantTier());
        if (!validation.isOrganizationAllowed()) {
            throw new ForbiddenException(ERROR_MSG_UPGRADE_ACCT, 0L);
        }
        organization = organizationRepository.save(organization);
        if (!CollectionUtils.isEmpty(multipartFiles)) {
            attachmentFactory.doPostAttachment(refCode, multipartFiles, "tenant/", AppConstants.CONTRACT_FILE_PATH,
                    ERefCode.ORGANIZATION.getRefCode(), String.valueOf(organization.getId()), organization);
        }
        return organization;
    }

    @Override
    public Organization update(String authorization, Organization organization) {
        Organization organizationData = null;
        if (organization.getId() != null) {
            organizationData = organizationRepository.findById(organization.getId()).orElseThrow(() ->
                    new NotFoundException(Organization.class, organization.getId()));
            organizationData = organizationRepository.save(organizationData);
        }
        return organizationData;
    }

    @Override
    public Organization findById(Long id) {
        return organizationRepository.findById(id).orElseThrow(() -> new NotFoundException(Organization.class, id));
    }

    @Override
    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }

    @Override
    public Organization findByStatusAndPrimaryIndicator(String status, Boolean ind) {
        return organizationRepository.findByStatusAndPrimaryIndicator(status, ind);
    }

    @Transactional
    @Override
    public OrganizationDetailDTO saveUpdateOrgUnit(String organizationDetailDTO, Long compId) throws URISyntaxException, IOException, StorageException {

        Organization organization = null;
        OrganizationDetailDTO organizationDetailDTO1 = null;
        //String codeRefType = "ORG_MNG";

        try {

            boolean isUpdate = false;
            organizationDetailDTO1 = new ObjectMapper().readValue(organizationDetailDTO, OrganizationDetailDTO.class);
            organization = OrganizationMapper.toOrganization(organizationDetailDTO1);
            if (organizationDetailDTO1.getId() != null && !organizationDetailDTO1.getId().equals(""))
                isUpdate = true;

//            if (!isUpdate)
            organization = saveUpdateOrgUnit(organization);

            if (organization.getId() != null) {

                Long siteId = null;
                if (!isUpdate) {

                    saveOrgDetail(organization.getId(), organizationDetailDTO1.getEntityRoleId());
                    siteId = saveSite(organization.getId());
                }
                if (organizationDetailDTO1.getPhysicalLocationDto() != null)
                    savePhysicalLocationAndMapper(organization.getId(), organizationDetailDTO1.getPhysicalLocationDto(), isUpdate);

                if (siteId == null || siteId.equals(""))
                    siteId = siteRepository.findByRefId(organization.getId()).getId();

                //location association
                if (organizationDetailDTO1.getPhysicalLocationDTOList() != null && organizationDetailDTO1.getPhysicalLocationDTOList().size() > 0) {
                    associatePhysicalLocationWithOrgUnit(siteId, organizationDetailDTO1.getPhysicalLocationDTOList());
                }

                if (organizationDetailDTO1.getLinkedContracts() != null && organizationDetailDTO1.getLinkedContracts().size() > 0) {
                    addLinkedContracts(organization, organizationDetailDTO1);
                }
                //save doc
                // docuLibraryService.saveDocumentForOrg(multiPartFileDto, String.valueOf(organization.getId()), compId, codeRefType);
                organizationDetailDTO1.setId(organization.getId());
                organizationDetailDTO1.setPhysicalLocationDTOList(null);
                organizationDetailDTO1.setLinkedContracts(null);
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SolarApiException(e.getMessage());
        }
        return organizationDetailDTO1;
    }

    private Organization saveUpdateOrgUnit(Organization orgunit) {

        Organization saveOrg = orgunit;
        try {

            saveOrg = organizationRepository.save(orgunit);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("error,saving organization unit");
        }
        return saveOrg;
    }

    private void saveOrgDetail(Long orgId, Long unitManagerId) {

        try {
            organizationDetailService.save(OrganizationDetailMapper.toOrganizationDetail(orgId, unitManagerId));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("error,saving organization detail");
        }
    }

    /**
     * For internal Usage only
     * <p>
     * This will only create default site object
     * On Organization creation
     *
     * @param orgId
     * @return
     */
    private Long saveSite(Long orgId) {
        Site site = null;
        try {
            site = SiteMapper.toSite(orgId);
            site = siteRepository.save(site);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("error, saving site");
        }
        return site.getId();
    }

    private void savePhysicalLocationAndMapper(Long orgId, PhysicalLocationDTO locationDTO, boolean isupdate) {

        try {
            PhysicalLocation physicalLocation = PhysicalLocationMapper.toPhysicalLocation(locationDTO);
            physicalLocation.setCorrespondenceAddress(false);
            physicalLocation.setLocationType("Business Unit");
            physicalLocation = physicalLocationService.saveOrUpdate(physicalLocation);
            if (!isupdate)
                locationMappingService.save(PhysicalLocationMapper.getLocationMappings(physicalLocation, orgId));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("error, saving new physical location");
        }
    }

    private void associatePhysicalLocationWithOrgUnit(Long siteId, List<PhysicalLocationDTO> physicalLocationDTOList) {

        try {
            physicalLocationDTOList.forEach(locationDTO -> {
                locationDTO.setExternalRefId(siteId);
                physicalLocationService.saveOrUpdate(PhysicalLocationMapper.toPhysicalLocation(locationDTO));
            });

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException("error, physical location association");
        }
    }


    @Override
    public List<OrganizationResponseDTO> findAllOrgUnits() {
        List<Organization> organizations = organizationRepository.findAll();
        List<OrganizationResponseDTO> response = new ArrayList<>();

        List<Organization> mainOrgs = organizations.stream().filter(o -> o.getOrganizationType() != null && o.getOrganizationType().equalsIgnoreCase("master")).collect(Collectors.toList());
        List<Organization> subOrgs = organizations.stream().filter(o -> o.getOrganizationType() != null && !o.getOrganizationType().equalsIgnoreCase("master")).collect(Collectors.toList());

        mainOrgs.forEach(o -> {
            OrganizationResponseDTO organizationResponseDTO = new OrganizationResponseDTO();
            organizationResponseDTO.setId(o.getId());
            organizationResponseDTO.setType(o.getOrganizationType());
            organizationResponseDTO.setName(o.getOrganizationName());
            organizationResponseDTO.setStatus(o.getStatus());
            organizationResponseDTO.setSubType(o.getOrganizationSubType());
            organizationResponseDTO.setUnits(setUnits(subOrgs, o.getId()));
            organizationResponseDTO.setNoOfUnits(organizationResponseDTO.getUnits().size());

            response.add(organizationResponseDTO);
        });

        return response;
    }

    @Override
    public OrgDetailDTO findOrgDetails(Long id, Boolean isMaster) {

        OrgDetailDTO data;
        if (isMaster) {
            data = organizationRepository.findMasterOrgDetails(id);
        } else {
            data = organizationRepository.findSubOrgDetails(id);
        }

        if (data == null) {
            return null;
        }

        return data;
    }

    @Override
    public OrganizationDetailDTO findOrganizationDetails(Long id, Boolean isMaster) {
        OrganizationDetailDTO resultDto = new OrganizationDetailDTO();
        OrgDetailDTO data;
        if (isMaster) {
            data = organizationRepository.findMasterOrgDetails(id);
        } else {
            data = organizationRepository.findSubOrgDetails(id);
        }

        if (data == null) {
            return null;
        } else {

            resultDto.setId(data.getUnitId());
            resultDto.setUnitName(data.getUnitName());
            resultDto.setUnitTypeId(data.getUnitTypeId());
            resultDto.setEntityRoleId(data.getEntityRoleId());
            resultDto.setEntityName(data.getUnitManager());
            resultDto.setParentName(data.getParentName());
            resultDto.setParentId(data.getParentId());
            resultDto.setDetails(data.getDetails());
            PhysicalLocationDTO physicalLocationDto = new PhysicalLocationDTO();
            physicalLocationDto.setId(data.getLocId());
            physicalLocationDto.setAdd1(data.getAdd1());
            physicalLocationDto.setAdd2(data.getAdd2());
            physicalLocationDto.setZipCode(data.getZipCode());
            physicalLocationDto.setGeoLat(data.getGeoLat());
            physicalLocationDto.setGeoLong(data.getGeoLong());
            physicalLocationDto.setExt1(data.getExt1());
            physicalLocationDto.setExt2(data.getExt2());
            physicalLocationDto.setContactPerson(data.getContactPerson());
            physicalLocationDto.setEmail(data.getEmail());

            resultDto.setPhysicalLocationDto(physicalLocationDto);
            List<PhysicalLocationDTO> physicalLocationDTOList = physicalLocationService.getAllLocationsDTOByExternalRefId(id);
            resultDto.setPhysicalLocationDTOList(physicalLocationDTOList);
        }
        return resultDto;
    }

    @Override
    public Organization disableOrgUnit(Long id) {
        Optional<Organization> org = organizationRepository.findById(id);

        if (!org.isPresent()) {
            throw new NotFoundException("No User Group Found");
        }

        org.get().setStatus("INACTIVE");

        return organizationRepository.save(org.get());
    }

    private List<UnitDTO> setUnits(List<Organization> subOrgs, Long id) {
        List<UnitDTO> response = new ArrayList<>();

        UnitDTO unitDTO = null;
        for (int i = 0; i < subOrgs.size(); i++) {
            if (subOrgs.get(i).getParentOrgId() != null && subOrgs.get(i).getParentOrgId().equals(id)) {
                unitDTO = new UnitDTO();
                unitDTO.setId(subOrgs.get(i).getId());
                unitDTO.setName(subOrgs.get(i).getOrganizationName());
                unitDTO.setType(subOrgs.get(i).getOrganizationSubType());
                unitDTO.setStatus(subOrgs.get(i).getStatus());
                unitDTO.setNoOfLocations(setNoOfLocation(subOrgs.get(i).getId()));
                response.add(unitDTO);
            }
        }


        return response;
    }

    private Integer setNoOfLocation(Long refId) {
        return siteRepository.findSiteCountByRefId(refId);
    }

    //linked contracts are basically gardens
    private List<OrganizationDetail> addLinkedContracts(Organization organization, OrganizationDetailDTO organizationDetailDTO) {
        OrganizationDetail organizationDetail = organizationDetailService.findByOrgIdAndRefId(organization.getId(), organizationDetailDTO.getEntityRoleId());
        List<OrganizationDetail> organizationDetails = new ArrayList<>();
        if (organizationDetail != null) {
            try {
                if (organizationDetailDTO.getLinkedContracts() != null) {
                    for (LinkedContractDTO linkedContractDTO : organizationDetailDTO.getLinkedContracts()) {

//                        organizationDetails.add(OrganizationDetail.builder()
//                                .id(linkedContractDTO.getId() != null ? linkedContractDTO.getId() : null)
//                                .orgId(organizationDetail.getOrgId())
//                                .productId(linkedContractDTO.getProductId())
//                                .variantId(linkedContractDTO.getVariantId())
//                                .gardenName(linkedContractDTO.getGardenName())
//                                .gardenDescription(linkedContractDTO.getGardenDescription())
//                                .isDeleted(Boolean.FALSE)
//                                .build());
                    }
                }
                organizationDetailService.addAll(organizationDetails);
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
                throw new SolarApiException(e.getMessage());
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                throw new SolarApiException(e.getMessage());
            } catch (StorageException e) {
                LOGGER.error(e.getMessage(), e);
                throw new SolarApiException(e.getMessage());
            }
            return organizationDetails;
        }
        return null;
    }

    @Override
    public OrganizationDetailDTO updateUnit(String organizationDetailDto, String multiPartFileDto, Long compId) throws URISyntaxException, IOException, StorageException {

        Organization organizationData = null;
        OrganizationDetailDTO organizationDetailDTO1 = null;

        try {
            organizationDetailDTO1 = new ObjectMapper().readValue(organizationDetailDto, OrganizationDetailDTO.class);
            Long orgDetailId = organizationDetailDTO1.getId();
            if (organizationDetailDTO1.getId() != null) {
                organizationData = organizationRepository.findById(organizationDetailDTO1.getId()).orElseThrow(() ->
                        new NotFoundException(Organization.class, orgDetailId));
                if (organizationData.getId() != null) {
                    organizationData.setOrganizationName(organizationDetailDTO1.getUnitName());
                    organizationData.setOrganizationType(organizationDetailDTO1.getUnitSubType());
                    organizationData.setBusinessDescription(organizationDetailDTO1.getDetails());
                    organizationData.setParentOrgId(organizationDetailDTO1.getParentId());
                    organizationData.setStatus(organizationDetailDTO1.getStatus());
                    organizationData = organizationRepository.save(organizationData);

                    Site site = siteRepository.findByRefId(organizationData.getId());
                    final Long siteId = site.getId();
                    if (organizationDetailDTO1.getPhysicalLocationDTOList().size() > 0) {
                        List<PhysicalLocationDTO> physicalLocationDTOList = organizationDetailDTO1.getPhysicalLocationDTOList();
                        physicalLocationDTOList.forEach(locationDTO -> {
                            locationDTO.setExternalRefId(siteId);
                            locationDTO.setCorrespondenceAddress(false);
                            physicalLocationService.saveOrUpdate(PhysicalLocationMapper.toPhysicalLocation(locationDTO));
                        });
                    }

                    try {
                        if (organizationDetailDTO1.getLinkedContracts().size() > 0) {

                            List<OrganizationDetail> organizationDetails = new ArrayList<>();
                            for (LinkedContractDTO linkedContractDTO : organizationDetailDTO1.getLinkedContracts()) {

//                                organizationDetails.add(OrganizationDetail.builder().orgId(organizationData.getId())
//                                        .productId(linkedContractDTO.getProductId())
//                                        .variantId(linkedContractDTO.getVariantId())
//                                        .gardenName(linkedContractDTO.getGardenName())
//                                        .gardenDescription(linkedContractDTO.getGardenDescription())
//                                        .build());
                            }
                            organizationDetailService.addAll(organizationDetails);
                        }
                        String codeRefType = "ORG_MNG";
                        docuLibraryService.saveDocumentForOrg(multiPartFileDto, String.valueOf(organizationData.getId()), compId, codeRefType);

                    } catch (URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    } catch (StorageException e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new SolarApiException("update");
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SolarApiException(e.getMessage());
        }

        return organizationDetailDTO1;
    }

    public OrgDTO findOrganizationDetailWithLocation(Long id, Boolean isMaster) {
        final Long siteId = siteRepository.findByRefId(id).getId();

        return OrgDTO.builder().orgDetailDTO(findOrgDetails(id, isMaster))
                .physicalLocationDTOList(PhysicalLocationMapper.toPhysicalLocationDTOs(physicalLocationService.getAllLocationsByExternalRefId(siteId)))
                .build();
    }

    @Override
    public Organization enableOrgUnit(Long id) {
        Optional<Organization> org = organizationRepository.findById(id);
        if (!org.isPresent()) {
            throw new NotFoundException("No User Group Found");

        }
        org.get().setStatus("ACTIVE");
        return organizationRepository.save(org.get());
    }

    @Override
    public Organization findByStatusAndPrimaryIndicatorAndParentOrgId(String status, Boolean ind, Long parentOrgId) {
        return organizationRepository.findByStatusAndPrimaryIndicatorAndParentOrgId(status, ind, parentOrgId);

    }

    //TODO:    officeCount,linkedAssets;associatedCustomers.
    @Override
    public BaseResponse getAllOrganizationList(Long orgId, Integer size, int pageNumber, String searchedWord) {

        Pageable pageable = PageRequest.of(pageNumber, size);
        OrganizationManagementPaginationTile result = new OrganizationManagementPaginationTile();
        try {
            if (Objects.isNull(orgId)) {
                Page<OrganizationManagementTile> orgData = organizationRepository.getAllOrganizationMainLists(pageable);
                result.setTotalPages(orgData.getTotalPages());
                result.setTotalElements(orgData.getTotalElements());
                List<OrganizationManagementTile> data = orgData.getContent();
                result.setData(data);
            } else {
                Page<OrganizationManagementTemplate> orgData = organizationRepository.getAllOrganizationInnerLists(orgId, searchedWord, pageable);
                result.setTotalPages(orgData.getTotalPages());
                result.setTotalElements(orgData.getTotalElements());
                List<OrganizationManagementTile> data = toOrganizationManagementTiles(orgData.getContent());
                result.setData(data);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    //TODO officeCount,linkedAssets,associatedCustomers
    @Override
    public BaseResponse getOrganizationDetailsV2(Long orgId, Boolean isMaster) {
        OrganizationDTO result = null;
        try {
            if (isMaster) {
                result = organizationRepository.getMasterOrganizationDetails(orgId);
                result.setCompanyOperatingTerritoryDTOs(companyOperatingTerritoryRepository.findByOrganizationId(orgId));
            } else {
                result = toOrganizationDTO(organizationRepository.getAllBusinessUnitDetails(orgId));
                result.setPhysicalLocationDTOList(PhysicalLocationMapper.toPhysicalLocationDTOsOM(physicalLocationService.findSubOrgLocations(orgId)));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }


    @Override
    public BaseResponse getAllOrganizationCustomerList(Long orgId, String searchWord, Boolean isMaster, Integer size, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, size);
        CustomerManagementPaginationTile result = new CustomerManagementPaginationTile();
        try {
            Page<UserTemplate> userTemplates = isMaster ? organizationRepository.getAllOrgCustomerLists(orgId, searchWord, pageable) : organizationRepository.getAllSubOrgsCustomerList(orgId, searchWord, pageable);
            result.setTotalPages(userTemplates.getTotalPages());
            result.setTotalElements(userTemplates.getTotalElements());
            List<CustomerManagementTile> data = CustomerManagementTileMapper.toCustomerManagementTiles(userTemplates.getContent());
            result.setData(data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public BaseResponse getAllOfficeList(Long orgId, Boolean isMaster, String groupBy, String groupByName, String locationCategory, String locationType, String businessUnit, String searchWords, Integer size, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, size);
        PhysicalLocationOMPaginationTile result = new PhysicalLocationOMPaginationTile();
        try {
            PhysicalLocationOMGroupBy groupByType = PhysicalLocationOMGroupBy.get(groupBy);
            Page<PhysicalLocationOMTemplate> locationOMTemplates = getPhysicalLocationOMGroupByResult(orgId, groupBy, groupByName, groupByType, isMaster, locationCategory, locationType, businessUnit, searchWords, pageable);
            result.setTotalPages(locationOMTemplates.getTotalPages());
            result.setTotalElements(locationOMTemplates.getTotalElements());
            List<PhysicalLocationOMTile> data = (!groupBy.equalsIgnoreCase(groupByType.NONE.getType()) && groupByName == null) ? PhysicalLocationOMTileMapper.toPhysicalLocationOMTilesGroupBy(locationOMTemplates.getContent()) : PhysicalLocationOMTileMapper.toPhysicalLocationOMTiles(locationOMTemplates.getContent());
            result.setGroupBy(groupBy);
            result.setData(data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    private Page<PhysicalLocationOMTemplate> getPhysicalLocationOMGroupByResult(Long orgId, String groupBy, String groupByName, PhysicalLocationOMGroupBy groupByType, Boolean isMaster, String locationCategory, String locationType, String businessUnit, String searchWords, Pageable pageable) {
        Page<PhysicalLocationOMTemplate> locationOMTemplates = null;

        if (!groupBy.equalsIgnoreCase(CustomerManagementGroupBy.NONE.getType()) && groupByName != null) {
            locationOMTemplates = organizationRepository.getAllOrgOfficeList(orgId, groupBy, groupByName, true, isMaster, locationCategory, locationType, businessUnit, searchWords, pageable);
        } else {
            switch (groupByType) {
                case NONE:
                    locationOMTemplates = organizationRepository.getAllOrgOfficeList(orgId, null, null, false, isMaster, locationCategory, locationType, businessUnit, searchWords, pageable);
                    break;
                case CATEGORY:
                case BUSINESS_UNIT:
                case TYPE:
                    locationOMTemplates = isMaster ? organizationRepository.getAllMasterOrgOfficeGroupByList(orgId, false, groupByType.getType(), pageable) : organizationRepository.getAllSubOrgOfficeGroupByList(orgId, false, groupByType.getType(), pageable);
                    break;
            }
        }
        return locationOMTemplates;
    }

    @Override
    public BaseResponse addOrUpdateOrganizationV2(Long compKey, Boolean isMaster, String reqType, MultipartFile image, String organizationDTOString) {

        try {
            OrganizationDTO organizationDTO = objectMapper.readValue(organizationDTOString, OrganizationDTO.class);
            if ((isMaster && reqType.equalsIgnoreCase(AppConstants.OrganizationManagement.SAVE))
                    || (isMaster && organizationDTO.getId() == null)) {
                return BaseResponse.builder().code(HttpStatus.BAD_REQUEST.value())
                        .message(AppConstants.OrganizationManagement.MASTER_ORG_UPDATE_VALIDATION_MESSAGE).build();
            }        // Validation for organization name length
            if (organizationDTO.getOrganizationName().length() > 50) {
                return BaseResponse.builder().message("Organization name cannot be greater than 50 characters").code(HttpStatus.BAD_REQUEST.value()).build();
            }

            // Check for unique organization name, excluding the current ID in case of updates
            boolean isNameExists = organizationRepository.isOrganizationNameExists(organizationDTO.getOrganizationName(), organizationDTO.getId());
            if (isNameExists) {
                return BaseResponse.builder().message("Organization name already exists").code(HttpStatus.BAD_REQUEST.value()).build();
            }
            if (reqType.equalsIgnoreCase(AppConstants.OrganizationManagement.UPDATE) && isMaster) { //Update case for Master Organizaitons
                saveOrUpdateMasterOrg(compKey, image, organizationDTO);
            } else if (!isMaster && organizationDTO.getParentOrgId() != null)//Save or Update Case for Sub Organizations
            {
                Organization masterOrg = organizationRepository.findById(organizationDTO.getParentOrgId())
                        .orElseThrow(() -> new EntityNotFoundException(AppConstants.OrganizationManagement.MASTER_ORG_NOT_FOUND));
                saveOrUpdateSubOrg(compKey, image, organizationDTO);
            } else {
                return BaseResponse.builder().message(AppConstants.OrganizationManagement.SUB_ORG_PARENT_VALIDATION_MESSAGE).code(HttpStatus.PRECONDITION_FAILED.value()).build();
            }

            if (reqType.equalsIgnoreCase("save")) {
                return BaseResponse.builder().message(isMaster ? AppConstants.OrganizationManagement.ORG_SAVE_SUCCESS_MESSAGE : AppConstants.OrganizationManagement.BUSINESS_UNIT_SAVE_SUCCESS_MESSAGE).code(HttpStatus.OK.value()).build();
            } else {
                return BaseResponse.builder().message(isMaster ? AppConstants.OrganizationManagement.ORG_UPDATE_SUCCESS_MESSAGE : AppConstants.OrganizationManagement.BUSINESS_UNIT_UPDATE_SUCCESS_MESSAGE).code(HttpStatus.OK.value()).build();
            }
        } catch (EntityNotFoundException e) {
            LOGGER.error("Organization not found: {}", e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.MASTER_ORG_NOT_FOUND).code(HttpStatus.NOT_FOUND.value()).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.ORG_UPDATE_ERROR_MESSAGE + e.getMessage()).code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
    }

    //TODO implement primary indicator check when adding multiple master org
    private Organization saveOrUpdateMasterOrg(Long compKey, MultipartFile image, OrganizationDTO organizationDTO) {
        String uri = null;
        //Update image
        if (image != null) {
            uri = checkIfImageAlreadyExists(image, compKey);
            if (uri != null) {
                organizationDTO.setLogoImage(uri);
            }
        }
        Organization organization = OrganizationMapper.toOrganization(organizationDTO);

        Organization existingOrganization = organizationRepository.findById(organization.getId()).get();
        // Save or  Update CompanyPreference
        if (organizationDTO.getCompanyPreference() != null) {
            organization.setCompanyPreference(saveOrUpdateCompanyPreference(organizationDTO, uri));
        }
        // Save or Update CompanyOperatingTerritories
        if (organizationDTO.getCompanyOperatingTerritoryDTOs() != null) {
            saveOrUpdateCompanyOperatingTerritories(organizationDTO, organization);
        }
        // Save or Update Entity (Contact Person)
        if (organizationDTO.getContactPerson() != null && organizationDTO.getContactPerson().getId() != null &&
                (existingOrganization.getContactPerson() == null || (existingOrganization.getContactPerson() != null &&
                        !organizationDTO.getContactPerson().getId().equals(existingOrganization.getContactPerson().getId())))) {
            Entity contactPerson = entityService.findById(organizationDTO.getContactPerson().getId());
            if (contactPerson != null) {
                organization.setContactPerson(contactPerson);
            }
        }

        // Update or save Organization
        organization = organizationRepository.save(OrganizationMapper.toUpdateOrganization(existingOrganization, organization));
        return organization;
    }

    private Organization saveOrUpdateSubOrg(Long compKey, MultipartFile image, OrganizationDTO organizationDTO) {
        String uri = null;
        Organization existingOrganization = null;
        //Update image
        if (image != null) {
            uri = checkIfImageAlreadyExists(image, compKey);
            if (uri != null) {
                organizationDTO.setLogoImage(uri);
            }
        }
        Organization organization = OrganizationMapper.toOrganization(organizationDTO);
        if (organization.getId() != null) {
            existingOrganization = organizationRepository.findById(organization.getId()).get();
        }
        // Save or Update Entity (Contact Person)
        if (organizationDTO.getContactPerson() != null && organizationDTO.getContactPerson().getId() != null) {
            Entity contactPerson = entityService.findById(organizationDTO.getContactPerson().getId());
            if (contactPerson != null) {
                if (existingOrganization == null ||
                        (existingOrganization != null && (existingOrganization.getContactPerson() == null ||
                                !organizationDTO.getContactPerson().getId().equals(existingOrganization.getContactPerson().getId())))) {
                    organization.setContactPerson(contactPerson);
                }
            }
        }
        // Update or save Organization
        organization = organizationRepository.save(existingOrganization != null ? OrganizationMapper.toUpdateOrganization(existingOrganization, organization) : organization);

        //Add or update locations
        if (organizationDTO.getPhysicalLocationDTOList() != null) {
            saveOrUpdateLocationMappings(organization.getId(), organizationDTO.getPhysicalLocationDTOList());
        }
        return organization;
    }

    private CompanyPreference saveOrUpdateCompanyPreference(OrganizationDTO organizationDTO, String uri) {
        CompanyPreference result = null;
        CompanyPreference companyPreferenceUpdate = CompanyPreferenceMapper.toCompanyPreference(organizationDTO.getCompanyPreference());
        companyPreferenceUpdate.setLogo(uri == null ? companyPreferenceUpdate.getLogo() : uri);
        if (companyPreferenceUpdate.getId() != null) {
            CompanyPreference existingCompanyPreference = companyPreferenceRepository.findById(companyPreferenceUpdate.getId())
                    .orElseThrow(() -> new NotFoundException(AppConstants.OrganizationManagement.COMPANY_PREF_NOT_FOUND + companyPreferenceUpdate.getId()));
            result = companyPreferenceRepository.save(CompanyPreferenceMapper.toUpdateCompanyPreference(existingCompanyPreference, companyPreferenceUpdate));
        } else {
            result = companyPreferenceRepository.save(companyPreferenceUpdate);
        }
        return result;
    }

    private List<CompanyOperatingTerritory> saveOrUpdateCompanyOperatingTerritories(OrganizationDTO organizationDTO, Organization organization) {
        List<CompanyOperatingTerritory> result = new ArrayList<>();
        List<CompanyOperatingTerritory> newTerritories = CompanyOperationTerritoryMapper.toCompanyOperatingTerritoryList(organizationDTO.getCompanyOperatingTerritoryDTOs());
        List<CompanyOperatingTerritory> existingTerritories = companyOperatingTerritoryRepository.findAllByOrganization(organization);

        // Identify IDs to delete
        Set<Long> newTerritoryIds = newTerritories.stream()
                .map(CompanyOperatingTerritory::getId)
                .collect(Collectors.toSet());

        List<Long> idsToDelete = existingTerritories.stream()
                .map(CompanyOperatingTerritory::getId)
                .filter(id -> id != null && !newTerritoryIds.contains(id))
                .collect(Collectors.toList());

        if (!idsToDelete.isEmpty()) {
            companyOperatingTerritoryRepository.deleteAllById(idsToDelete);
        }

        // Update existing and add new territories
        List<CompanyOperatingTerritory> territoriesToSave = existingTerritories.stream()
                .filter(t -> newTerritoryIds.contains(t.getId()))
                .peek(t -> {
                    CompanyOperatingTerritory newTerritory = newTerritories.stream()
                            .filter(nt -> nt.getId().equals(t.getId()))
                            .findFirst()
                            .orElse(null);
                    if (newTerritory != null) {
                        CompanyOperationTerritoryMapper.toUpdateCompanyOperatingTerritory(t, newTerritory);
                    }
                })
                .collect(Collectors.toList());

        newTerritories.stream()
                .filter(nt -> nt.getId() == null)
                .peek(nt -> nt.setOrganization(organization))
                .forEach(territoriesToSave::add);

        if (!territoriesToSave.isEmpty()) {
            result = companyOperatingTerritoryRepository.saveAll(territoriesToSave);
        }
        return result;
    }

    private void saveOrUpdateLocationMappings(Long orgId, List<PhysicalLocationDTO> physicalLocationDTOs) {
        List<LocationMapping> result = new ArrayList<>();
        List<LocationMapping> existingMappings = locationMappingService.findAllBySourceIdAndSourceType(orgId, AppConstants.OrganizationManagement.ORGANIZATION);

        // Convert DTOs to LocationMapping entities
        List<LocationMapping> newMappings = physicalLocationDTOs.stream()
                .map(dto -> toLocationMapping(orgId, dto))
                .collect(Collectors.toList());

        // Identify IDs to delete (existing but not in new list)
        Set<Long> newLocationIds = newMappings.stream()
                .map(LocationMapping::getLocationId)
                .collect(Collectors.toSet());

        List<Long> idsToDelete = existingMappings.stream()
                .filter(mapping -> !newLocationIds.contains(mapping.getLocationId()))
                .map(LocationMapping::getId)
                .collect(Collectors.toList());

        if (!idsToDelete.isEmpty()) {//Performing the delete operation
            locationMappingService.deleteAllByIds(idsToDelete);
        }

        // Filter out existing mappings that are still present
        List<LocationMapping> mappingsToSave = newMappings.stream()
                .filter(mapping -> !existingMappings.stream()
                        .map(LocationMapping::getLocationId)
                        .collect(Collectors.toSet())
                        .contains(mapping.getLocationId()))
                .collect(Collectors.toList());

        if (!mappingsToSave.isEmpty()) {
            locationMappingService.saveAll(mappingsToSave);
        }
    }

    private LocationMapping toLocationMapping(Long orgId, PhysicalLocationDTO dto) {
        LocationMapping mapping = new LocationMapping();
        // Set fields from dto to mapping
        mapping.setLocationId(dto.getId());
        mapping.setSourceId(orgId);
        mapping.setSourceType(AppConstants.OrganizationManagement.ORGANIZATION);
        // Set other fields as required
        return mapping;
    }

    @Override
    public BaseResponse getAllOrganizationEmployeesList(Long orgId, String searchWord, Boolean isMaster, Integer size, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, size);
        EmployeeManagementPaginationTile result = new EmployeeManagementPaginationTile();
        try {
            Page<EmployeeManagementTile> employeeManagementTiles = isMaster ? organizationRepository.getAllOrgEmployeeLists(orgId, searchWord, pageable) : organizationRepository.getAllSubOrgEmployeeLists(orgId, searchWord, pageable);
            result.setTotalPages(employeeManagementTiles.getTotalPages());
            result.setTotalElements(employeeManagementTiles.getTotalElements());
            List<EmployeeManagementTile> data = employeeManagementTiles.getContent();
            result.setData(data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    private String checkIfImageAlreadyExists(MultipartFile image, Long compKey) {
        String uri = null;
        try {
            uri = storageService.storeInContainer(image, appProfile, "tenant/" + compKey
                    + AppConstants.PATHS.ORG_LOGO, image.getOriginalFilename(), compKey, false);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (StorageException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uri;
    }

    @Override
    public BaseResponse addOrUpdateConfigurations(Long compKey, Long orgId, String reqType, String companyPreferenceDTOString) {
        try {
            CompanyPreferenceDTO companyPreferenceDTO = objectMapper.readValue(companyPreferenceDTOString, CompanyPreferenceDTO.class);
            Organization organization = organizationRepository.findById(orgId)
                    .orElseThrow(() -> new EntityNotFoundException(AppConstants.OrganizationManagement.ORG_NOT_FOUND));

            if (organization.getParentOrgId() != null) {
                return BaseResponse.builder().code(HttpStatus.BAD_REQUEST.value()).message(AppConstants.OrganizationManagement.CONFIGURATION_VALIDATION_MESSAGE).build();
            }

            if (companyPreferenceDTO.getId() == null && organization.getCompanyPreference() != null) {
                companyPreferenceDTO.setId(organization.getCompanyPreference().getId());
            }

            if (AppConstants.OrganizationManagement.SAVE.equalsIgnoreCase(reqType) || companyPreferenceDTO.getId() == null) {
                saveCompanyPreference(organization, companyPreferenceDTO);
                return BaseResponse.builder().message(AppConstants.OrganizationManagement.CONFIGURATION_SAVE_SUCCESS_MESSAGE).code(HttpStatus.OK.value()).build();
            }

            if (AppConstants.OrganizationManagement.UPDATE.equalsIgnoreCase(reqType) && companyPreferenceDTO.getId() != null) {
                updateCompanyPreference(companyPreferenceDTO);
                return BaseResponse.builder().message(AppConstants.OrganizationManagement.CONFIGURATION_UPDATE_SUCCESS_MESSAGE).code(HttpStatus.OK.value()).build();
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing JSON: {}", e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.JSON_PARSE_ERROR + e.getMessage()).code(HttpStatus.BAD_REQUEST.value()).build();
        } catch (EntityNotFoundException e) {
            LOGGER.error("Organization not found: {}", e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.ORG_NOT_FOUND).code(HttpStatus.NOT_FOUND.value()).build();
        } catch (Exception e) {
            LOGGER.error("Error updating configurations: {}", e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.CONFIGURATION_UPDATE_ERROR_MESSAGE + e.getMessage()).code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
        return BaseResponse.builder().message(AppConstants.OrganizationManagement.INVALID_REQ_TYPE).code(HttpStatus.BAD_REQUEST.value()).build();
    }


    private void saveCompanyPreference(Organization organization, CompanyPreferenceDTO companyPreferenceDTO) {
        CompanyPreference savedCompanyPreference = companyPreferenceRepository.save(CompanyPreferenceMapper.toCompanyPreference(companyPreferenceDTO));
        organization.setCompanyPreference(savedCompanyPreference);
        organizationRepository.save(organization);
    }

    private void updateCompanyPreference(CompanyPreferenceDTO companyPreferenceDTO) {
        CompanyPreference updateCompanyPreference = CompanyPreferenceMapper.toCompanyPreference(companyPreferenceDTO);
        CompanyPreference existingCompanyPreference = companyPreferenceRepository.findById(companyPreferenceDTO.getId()).get();
        companyPreferenceRepository.save(CompanyPreferenceMapper.toUpdateCompanyPreference(existingCompanyPreference, updateCompanyPreference));
    }

    @Override
    public BaseResponse addLandingPageImages(Long compKey, Long orgId, Long companyPreferenceId, String reqType, List<MultipartFile> files) {
        try {
            CompanyPreference companyPreference = companyPreferenceRepository.findById(companyPreferenceId)
                    .orElseThrow(() -> new EntityNotFoundException(AppConstants.OrganizationManagement.CONFIGURATION_NOT_FOUND));

            switch (reqType.toUpperCase()) {
                case AppConstants.OrganizationManagement.MOBILE:
                    processLandingPageImages(compKey, orgId, companyPreference.getMobileLandingImagesUrl(), AppConstants.OrganizationManagement.MOBILE_LANDING_PAGE_IMAGES, files);
                    break;
                case AppConstants.OrganizationManagement.LANDING:
                    processLandingPageImages(compKey, orgId, companyPreference.getLandingImagesUrl(), AppConstants.OrganizationManagement.LANDING_PAGE_IMAGES, files);
                    break;
                default:
                    return BaseResponse.builder().message(AppConstants.OrganizationManagement.INVALID_REQ_TYPE).code(HttpStatus.BAD_REQUEST.value()).build();
            }
        } catch (EntityNotFoundException e) {
            LOGGER.error("Configuration not found: {}", e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.CONFIGURATION_NOT_FOUND).code(HttpStatus.NOT_FOUND.value()).build();
        } catch (Exception e) {
            LOGGER.error("Error processing images: {}", e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.IMG_PARSE_ERROR + e.getMessage()).code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
        return BaseResponse.builder().message(AppConstants.OrganizationManagement.IMAGE_SUCCESS_MESSAGE).code(HttpStatus.OK.value()).build();
    }

    @Override
    public BaseResponse getConfigurations(Long orgId) {
        CompanyPreferenceDTO companyPreferenceDTO = null;
        try {
            User currentUser = userService.getLoggedInUser();
            Organization organization = organizationRepository.findById(orgId)
                    .orElseThrow(() -> new EntityNotFoundException(AppConstants.OrganizationManagement.ORG_NOT_FOUND));
            if (organization.getCompanyPreference() != null) {
                companyPreferenceDTO = CompanyPreferenceMapper.toCompanyPreferenceDTO(organization.getCompanyPreference());
                companyPreferenceDTO.setOrgId(organization.getId());
                companyPreferenceDTO.setLandingPageImages(DocuMapper.toDocuLibraryDTOs(docuLibraryService.findByCodeRefIdAndCodeRefType(String.valueOf(organization.getId()), AppConstants.OrganizationManagement.LANDING_PAGE_IMAGES)));
                companyPreferenceDTO.setMobileLandingPageImages(DocuMapper.toDocuLibraryDTOs(docuLibraryService.findByCodeRefIdAndCodeRefType(String.valueOf(organization.getId()), AppConstants.OrganizationManagement.MOBILE_LANDING_PAGE_IMAGES)));
            }
        } catch (EntityNotFoundException e) {
            LOGGER.error("Organization not found: {}", e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.ORG_NOT_FOUND).code(HttpStatus.NOT_FOUND.value()).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(companyPreferenceDTO).build();
    }

    private void processLandingPageImages(Long compKey, Long orgId, String path, String docuType, List<MultipartFile> multipartFiles) throws URISyntaxException, IOException, StorageException {
        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
            String documentName = multipartFile.getOriginalFilename().replaceAll("\\s", "_");

            String uri = storageService.storeInContainer(multipartFile, appProfile, path,
                    timeStamp + "-" + documentName, compKey
                    , false);
            Double fileSizeInKB = Double.valueOf(multipartFile.getSize() / 1024);
            Double fileSizeInMB = fileSizeInKB / 1024;
            docuLibraryList.add(DocuLibrary.builder()
                    .docuName(multipartFile.getName())
                    .codeRefType(docuType)
                    .codeRefId(String.valueOf(orgId))
                    .uri(uri)
                    .size(fileSizeInMB + "MB")
                    .docuType(multipartFile.getContentType())
                    .visibilityKey(true)
                    .referenceTime(timeStamp)
                    .build());
        }
        docuLibraryService.saveAll(docuLibraryList);
    }

    @Override
    @Transactional
    public BaseResponse toggleOrgUnitStatus(List<Long> orgIds, Boolean isActive, Boolean isMaster) {
        List<BaseResponse> responses = new ArrayList<>();

        for (Long orgId : orgIds) {
            organizationRepository.findById(orgId).ifPresentOrElse(organization -> {
                BaseResponse response;
                if (isActive) {
                    response = activateOrganization(organization, isMaster);
                } else {
                    response = deactivateOrganization(organization, isMaster, orgId);
                }
                responses.add(response);
            }, () -> {
                LOGGER.error("Organization not found for ID: {}", orgId);
                responses.add(responseNotFound());
            });
        }
        return mergeResponses(responses);
    }

    private BaseResponse activateOrganization(Organization organization, Boolean isMaster) {
        organization.setStatus(AppConstants.OrganizationManagement.ACTIVE);
        organizationRepository.save(organization);
        String message = isMaster ? AppConstants.OrganizationManagement.MASTER_ORG_ENABLE_SUCCESS_MESSAGE : AppConstants.OrganizationManagement.SUB_ORG_ENABLE_SUCCESS_MESSAGE;
        return buildBaseResponse(HttpStatus.OK.value(), message);
    }

    private BaseResponse deactivateOrganization(Organization organization, Boolean isMaster, Long orgId) {
        if (isMaster && organization.getParentOrgId() != null) {
            return handleMasterDeactivation(orgId);
        } else {
            return handleSubOrganizationDeactivation(orgId);
        }
    }

    private BaseResponse handleMasterDeactivation(Long orgId) {
        Long totalSubOrgCount = organizationRepository.totalSubOrgCount(orgId);
        if (totalSubOrgCount == 0) {
            return deactivateAndRespond(orgId, AppConstants.OrganizationManagement.MASTER_ORG_DISABLE_SUCCESS_MESSAGE);
        }
        return buildBaseResponse(HttpStatus.OK.value(), AppConstants.OrganizationManagement.MASTER_ORG_DISABLE_VALIDATION_MESSAGE);
    }

    private BaseResponse handleSubOrganizationDeactivation(Long orgId) {
        Long totalSubOrgLocCount = organizationRepository.totalSubOrgLocationCount(orgId);
        if (totalSubOrgLocCount == 0) {
            return deactivateAndRespond(orgId, AppConstants.OrganizationManagement.SUB_ORG_DISABLE_SUCCESS_MESSAGE);
        }
        return buildBaseResponse(HttpStatus.OK.value(), AppConstants.OrganizationManagement.SUB_ORG_DISABLE_VALIDATION_MESSAGE);
    }

    private BaseResponse deactivateAndRespond(Long orgId, String message) {
        Organization organization = organizationRepository.findById(orgId).orElseThrow();
        organization.setStatus(AppConstants.OrganizationManagement.INACTIVE);
        organizationRepository.save(organization);
        return buildBaseResponse(HttpStatus.OK.value(), message);
    }

    private BaseResponse buildBaseResponse(int code, String message) {
        return BaseResponse.builder().code(code).message(message).build();
    }

    private BaseResponse responseNotFound() {
        return BaseResponse.builder().message(AppConstants.OrganizationManagement.ORG_NOT_FOUND).code(HttpStatus.NOT_FOUND.value()).build();
    }

    @Override
    public BaseResponse getAvailableOffices(Long compKey, Long masterOrgId, Long subOrgId, String unitCategory, String unitType, String searchWords) {
        List<PhysicalLocationDTO> result = null;
        try {
            result = organizationRepository.getFilteredPhysicalLocations(masterOrgId, subOrgId, unitCategory, searchWords);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public BaseResponse getOrgOfficeFilters(Long orgId) {

        PhysicalLocationOMFilterDTO result = new PhysicalLocationOMFilterDTO();
        try {
            result = PhysicalLocationOMFilterMapper.toPhysicalLocationOMFilterDTO(organizationRepository.getOrgOfficeFilters(orgId));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    @Override
    public BaseResponse getAvailableEmployeesList(Long masterOrgId, Long orgId, String searchWord) {
        List<DefaultUserGroupResponseDTO> result = new ArrayList<>();
        try {
            result = organizationRepository.getAllAvailableEmployees(masterOrgId, orgId, searchWord);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    private BaseResponse mergeResponses(List<BaseResponse> responses) {
        return responses.stream()
                .reduce((response1, response2) -> response1.getCode() == 200 && response2.getCode() == 200 ? response1 : response2)
                .orElse(responseNotFound());
    }

    @Override
    public BaseResponse getLinkedSites(Long businessUnitId, String searchWord, String groupBy, String groupByName, String gardenType, String gardenOwner,
                                       String startDateRegistrationDate, String endDateRegistrationDate,String startDateGoLiveDate, String endDateGoLiveDate
                                        ,Integer pageNumber, Integer pageSize) {
        LinkedSitePaginationTile result = new LinkedSitePaginationTile();
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            LinkedSiteGroupBy groupByType = LinkedSiteGroupBy.get(groupBy);
            Page<LinkedSitesTemplate> linkedSitesTemplate = getLinkedSitesGroupByResult(businessUnitId, searchWord, groupBy, groupByName, groupByType, gardenType, gardenOwner,
                    startDateRegistrationDate, endDateRegistrationDate, startDateGoLiveDate, endDateGoLiveDate ,pageable);
            result.setTotalPages(linkedSitesTemplate.getTotalPages());
            result.setTotalElements(linkedSitesTemplate.getTotalElements());
            List<LinkedSiteTile> data = (!groupBy.equalsIgnoreCase(groupByType.NONE.getType()) && groupByName == null) ? OrganizationManagementTileMapper.toPhysicalLocationOMTilesGroupBy(linkedSitesTemplate.getContent()) : OrganizationManagementTileMapper.toLinkedListTiles(linkedSitesTemplate.getContent());
            result.setGroupBy(groupBy);
            result.setData(data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message(AppConstants.OrganizationManagement.ERROR + " " + e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    private Page<LinkedSitesTemplate> getLinkedSitesGroupByResult(Long businessUnitId, String searchWord, String groupBy, String groupByName, LinkedSiteGroupBy groupByType, String gardenType, String gardenOwner,
                                                                  String startDateRegistrationDate, String endDateRegistrationDate,String startDateGoLiveDate, String endDateGoLiveDate , Pageable pageable) {
        Page<LinkedSitesTemplate> linkedSitesTemplates = null;
        Boolean gardenOwnerIsPresent = gardenOwner != null ? true : false;
        Boolean gardenTypeIsPresent = gardenType != null ? true : false;
        List<String> gardenTypeList = gardenTypeIsPresent ? Arrays.asList(gardenType.split(",")) : Collections.emptyList();
        List<Long> gardenOwnerList = gardenOwnerIsPresent ? Arrays.asList(gardenOwner.split(",")).stream().map(Long::parseLong).collect(Collectors.toList()) : Collections.emptyList();
        if (!groupBy.equalsIgnoreCase(CustomerManagementGroupBy.NONE.getType()) && groupByName != null) {
            linkedSitesTemplates = organizationRepository.findLinkedSiteByGardenId(businessUnitId , searchWord, gardenType,gardenTypeList, gardenOwnerList,gardenOwnerIsPresent,
                    gardenTypeIsPresent, startDateRegistrationDate, endDateRegistrationDate, startDateGoLiveDate, endDateGoLiveDate ,groupBy, groupByName, pageable);
        } else {
            switch (groupByType) {
                case NONE:
                    linkedSitesTemplates = organizationRepository.findLinkedSiteByGardenId(businessUnitId, searchWord, gardenType,gardenTypeList, gardenOwnerList, gardenOwnerIsPresent, gardenTypeIsPresent, startDateRegistrationDate, endDateRegistrationDate, startDateGoLiveDate, endDateGoLiveDate ,null, null, pageable);
                    break;
                case GARDEN_TYPE:
                case GARDEN_OWNER:
                    linkedSitesTemplates = organizationRepository.findLinkedSiteGroupBy(groupByType.getType(), false, pageable);
                    break;
            }
        }
        return linkedSitesTemplates;
    }

    @Override
    public BaseResponse getFiltersData() {
        try {
            LinkedSitesFiltersTile linkedSitesFiltersTile = OrganizationManagementTileMapper.
                    toLinkedSitesFiltersTile(organizationRepository.findAllFiltersData());
            return BaseResponse.builder().data(linkedSitesFiltersTile).code(HttpStatus.OK.value()).message("Data Found Successfully").build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.UNPROCESSABLE_ENTITY.value()).message("Error while finding data").build();
        }
    }
}
