package com.solar.api.tenant.service.acquisition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.StorageException;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Acquisition.AcquisitionUtils;
import com.solar.api.helper.Utility;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.WorkflowHookMaster;
import com.solar.api.saas.model.contact.Contacts;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.repository.workflow.WorkflowHookMasterRepository;
import com.solar.api.saas.service.ContactsService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.process.upload.mapper.Customer;
import com.solar.api.tenant.mapper.DocumentSigningTemplateDTO;
import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.ca.CaUserTemplatePaginationDTO;
import com.solar.api.tenant.mapper.ca.CustomerAcquisitionFilterDTO;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.integration.docuSign.DataExchangeDocuSign;
import com.solar.api.saas.service.integration.docuSign.dto.Action;
import com.solar.api.saas.service.integration.docuSign.dto.FieldData;
import com.solar.api.saas.service.integration.docuSign.dto.Template;
import com.solar.api.saas.service.integration.docuSign.dto.request.TemplateData;
import com.solar.api.saas.service.integration.docuSign.dto.response.CreateTemplateResponse;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.ca.CaUtilityDTO;
import com.solar.api.tenant.mapper.contract.OrganizationDTO;
import com.solar.api.tenant.mapper.contract.OrganizationMapper;
import com.solar.api.tenant.mapper.cutomer.CustomerDetailDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.tiles.docuLibrary.DocuLibraryTile;
import com.solar.api.tenant.mapper.tiles.signingRequestTracker.SigningReqTrackerTile;
import com.solar.api.tenant.mapper.user.SalesRepMapper;
import com.solar.api.tenant.mapper.user.SalesRepresentativeDTO;
import com.solar.api.tenant.mapper.user.UserMappingDTO;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.contract.*;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.ca.CaReferralInfo;
import com.solar.api.tenant.model.ca.CaSoftCreditCheck;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.docuSign.DocumentSigningTemplate;
import com.solar.api.tenant.model.docuSign.SigningRequestTracker;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.contractStatus.EContractStatus;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userMapping.UserMapping;
import com.solar.api.tenant.model.user.userType.EAuthenticationType;
import com.solar.api.tenant.model.user.userType.ECustomerDetailStates;
import com.solar.api.tenant.model.user.userType.ESourceType;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.workflow.WorkflowHookMap;
import com.solar.api.tenant.repository.CustomerDetailRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.repository.UserGroup.EntityRoleRepository;
import com.solar.api.tenant.repository.acquisition.AcquisitionRepo;
import com.solar.api.tenant.repository.contract.EntityDetailRepository;
import com.solar.api.tenant.repository.docuSign.DocumentSigningTemplateRepository;
import com.solar.api.tenant.repository.docuSign.SigningRequestTrackerRepository;
import com.solar.api.tenant.repository.workflow.WorkflowHookMapRepository;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.contract.EntityDetailService;
import com.solar.api.tenant.service.ca.CaReferralInfoService;
import com.solar.api.tenant.service.ca.CaSoftCreditCheckService;
import com.solar.api.tenant.service.ca.CaUtilityService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.contract.OrganizationService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.extended.LocationMappingService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.solarAmps.SolarAmpsService;
import com.solar.api.tenant.service.userMapping.UserMappingService;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.ca.CaReferralInfoMapper.toCaReferralInfoDTO;
import static com.solar.api.tenant.mapper.ca.CaSoftCreditCheckMapper.toCaSoftCreditCheck;
import static com.solar.api.tenant.mapper.ca.CaUtilityMapper.toCaUtilityDTO;
import static com.solar.api.tenant.mapper.contract.EntityMapper.toEntity;
import static com.solar.api.tenant.mapper.contract.EntityMapper.userDTOtoEntity;
import static com.solar.api.tenant.mapper.cutomer.CustomerDetailMapper.toCustomerDetail;
import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.toPhysicalLocationDTOs;
import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.toPhysicalLocations;
import static com.solar.api.tenant.mapper.user.UserMapper.toUser;
import static com.solar.api.tenant.mapper.user.UserMapper.toUserDTO;

@Service
public class AcquisitionServiceImpl implements AcquisitionService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerDetailRepository customerDetailRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private AcquisitionRepo acquisitionRepo;
    @Autowired
    private EntityService entityService;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;
    @Autowired
    private CaUtilityService caUtilityService;
    @Autowired
    private CaReferralInfoService caReferralInfoService;
    @Autowired
    private LocationMappingService locationMappingService;
    @Autowired
    private EntityDetailRepository entityDetailRepository;
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private CaSoftCreditCheckService caSoftCreditCheckService;
    @Autowired
    private PhysicalLocationService physicalLocationService;
    @Autowired
    private DocuLibraryService docuLibraryService;
    @Autowired
    private EntityRoleRepository entityRoleRepository;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private CustomerSubscriptionMappingRepository customerSubscriptionMappingRepo;
    @Autowired
    private PhysicalLocationRepository physicalLocationRepository;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private DataExchangeDocuSign dataExchangeDocuSign;
    @Autowired
    private UserService userService;
    @Autowired
    private EntityDetailService entityDetailService;
    @Autowired
    private DocumentSigningTemplateRepository signingTemplateRepository;
    @Autowired
    private SigningRequestTrackerRepository signingRequestTrackerRepository;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private Utility utility;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private WorkflowHookMasterRepository workFlowHookMasterRepository;
    @Autowired
    private WorkflowHookMapRepository workflowHookMapRepository;
    @Autowired
    private CompanyPreferenceService companyPreferenceService;
    @Autowired
    private ContactsService contactsService;
    @Value("${app.storage.blobService}")
    private String blobService;
    @Autowired
    private SolarAmpsService solarAmpsService;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private UserMappingService userMappingService;
    @Autowired
    private DocumentSigningTemplateService documentSigningTemplateService;
    @Autowired
    private StorageService storageService;
    @Value("${app.profile}")
    private String appProfile;

    @Autowired
    private TenantConfigService tenantConfigService;


    @Override
    public UserDTO getContracts(Long entityId) {
        try{
            List<SigningReqTrackerTile> signingRequestTrackers = acquisitionRepo.getByEntityId(entityId);
            List<DocuLibraryTile> docuLibraryTileList = acquisitionRepo.findByCodeRefIdAndCodeRefType(entityId);
            Entity entity = entityService.findById(entityId);
            UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.UserLevelPrivilegeByEntityId(entity.getId());
            if (userLevelPrivilege == null) {
                throw new NotFoundException(UserLevelPrivilege.class, entity.getId());
            }
            User user = findById(userLevelPrivilege.getUser().getAcctId());
            UserDTO userDto = toUserDTO(user);
            userDto.setEntityId(entity.getId());
            List<Object> combinedResults = new ArrayList<>();
            combinedResults.addAll(signingRequestTrackers);
            combinedResults.addAll(docuLibraryTileList);
            userDto.setSigningReqTrackerTiles(combinedResults);
            return userDto;
        }catch(Exception e){
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public List<CaUtility> saveOrUpdateCaUtility(UserDTO userdto, List<MultipartFile> utilityMultipartFiles) {
        Long acctId = userdto.getAcctId();
        User user = findById(acctId);
        ArrayList<PhysicalLocation> physicalLocation = new ArrayList<>();
        List<CaUtility> caUtilityList = new ArrayList<>();
        List<CaUtilityDTO> caUtilityDTOs = userdto.getCaUtility();
        for (CaUtilityDTO caUtilityDTO : caUtilityDTOs) {
            if (caUtilityDTO.getPassCode() != null &&  ! caUtilityDTO.getPassCode().isEmpty()) {
                String encodedString = Base64.getEncoder().encodeToString(caUtilityDTO.getPassCode().getBytes());
                caUtilityDTO.setPassCode(encodedString);
            }
            CaUtility caUtility = caUtilityService.save(caUtilityDTO, user, utilityMultipartFiles);
            if (caUtilityDTO.getPhysicalLocations() != null) {
                physicalLocation.addAll(updateLocationToDefaultTypeAndCategory(caUtilityDTO.getPhysicalLocations(), Constants.LOCATION_CATEGORY_CONSTANTS.UTILITY, Constants.LOCATION_TYPE.SITE));
                locationMappingService.saveAll(getAcquisitionLocationMappings(physicalLocation, user, caUtility, AppConstants.PRIMARY_INDEX_LOCATION_FALSE));
            }
            caUtilityList.add(caUtility);
        }
        return caUtilityList;
    }

    @Override
    public List<PhysicalLocationDTO> getAllPhysicalLocation(Long acctId ) {
        return acquisitionRepo.findBySourceId(acctId);
    }

    @Override
    public BaseResponse saveOrUpdateCaUser(UserDTO userDTO, Boolean sendForSigning, List<Long> ids) {
        Long id = userDTO.getAcctId();
        User user = findById(id);
        if (sendForSigning != null && sendForSigning) {
            BaseResponse response = sendForSigningWithIds(user, userDTO, ids);
            return response;
        }
        return null;
    }

    private List<PhysicalLocation> updateLocationToDefaultTypeAndCategory(List<PhysicalLocationDTO> physicalLocList, String category, String locDefaultType) {
        Organization organization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId("ACTIVE", true, null);
        OrganizationDTO organizationDTO = OrganizationMapper.toOrganizationDTO(organization);
        Long orgId = organization.getId();
        physicalLocList.forEach(physicalLoc -> {
            //physicalLoc.setLocationType(locDefaultType);
            physicalLoc.setCategory(category);
            physicalLoc.setOrgId(orgId);
            physicalLoc.setOrganizationDTO(organizationDTO);
            physicalLoc.setIsDeleted(false);
            physicalLoc.setStatus(AppConstants.STATUS_ACTIVE);
        });
        return toPhysicalLocations(physicalLocList);
    }

    public User findById(Long id) {
        User user = null;
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new NotFoundException(User.class, id);
        }
        List<CustomerSubscription> customerSubscriptions =
                subscriptionRepository.findCustomerSubscriptionByUserAccount(userOptional.get());
        customerSubscriptions.forEach(customerSubscription -> {
            List<CustomerSubscriptionMapping> customerSubscriptionMappings =
                    customerSubscriptionMappingRepo.findCustomerSubscriptionMappingBySubscription(customerSubscription);

        });
        user = userOptional.get();
        user.setCustomerSubscriptions(customerSubscriptions);
        return user;
    }

    public List<LocationMapping> getAcquisitionLocationMappings(List<PhysicalLocation> physicalLocationsDTO, User user, CaUtility utility, String primaryInd) {
        List<LocationMapping> locationMappings = new ArrayList<>();
        physicalLocationsDTO.forEach(loc -> {
            String primaryIndDB = primaryInd;
            Long sourceId = null;
            String sourceType = null;
            Long locId = null;
            Long locMappingId = null;
            if (loc.getId() == null)
                throw new NotFoundException("Physical location not saved in DB");
            if (loc.getCategory().equalsIgnoreCase(Constants.LOCATION_CATEGORY_CONSTANTS.UTILITY)) {
                sourceId = utility.getId();
            }
            if (!loc.getCategory().equalsIgnoreCase(Constants.LOCATION_CATEGORY_CONSTANTS.UTILITY)) {
                sourceId = user.getAcctId();
            }

            sourceType = loc.getCategory();
            locId = loc.getId();
            //if record is for update
            LocationMapping locationMappingDB = locationMappingService.findBySourceIdAndSourceTypeAndLocationId(sourceId, sourceType, locId);
            if (locationMappingDB != null) {
                locMappingId = locationMappingDB.getId();
                primaryIndDB = locationMappingDB.getPrimaryInd();
            }
            locationMappings.add(LocationMapping.builder()
                    .id(locMappingId)
                    .locationId(locId)
                    .sourceType(sourceType)
                    .sourceId(sourceId)
                    .primaryInd(primaryIndDB).build());
        });
        return locationMappings;
    }

    public BaseResponse sendForSigningWithIds(User user, UserDTO userDTO, List<Long> ids) {
        CustomerDetail customerDetail = new CustomerDetail();
        Boolean check = false;
        String accessToken =
                dataExchangeDocuSign.getAccessTokenViaRefreshToken().getAccessToken();
        Entity entity = entityService.findEntityByUserId(user.getAcctId());
        System.out.println("1 **************************************************************************************");
        System.out.println("Customer Acquisition:  getCustomerType> " + userDTO.getCustomerType() + " getOrganization> " + entity.getOrganization().getOrganizationName());
        System.out.println("**************************************************************************************");
//        List<DocumentSigningTemplate> signingTemplates = signingTemplateRepository
//                .findByFunctionalityAndCustomerTypeAndOrganizationAndEnabled("Customer Acquisition"
//                        , userDTO.getCustomerType(), entity.getOrganization(), true); // find by+ customer_type +
        List<DocumentSigningTemplate> signingTemplates = signingTemplateRepository.findAllById(ids);

        if (!signingTemplates.isEmpty()) {
            // for loop on signing templates
            for (DocumentSigningTemplate template : signingTemplates) {
//                DocumentSigningTemplate template = signingTemplates.get(0);
                String[] templateIdActionId = template.getExtTemplateId().split("_");
                TemplateData request = getTemplateData(userDTO, templateIdActionId[2]);
                // TODO multiple create documents save call;
                CreateTemplateResponse response =
                        dataExchangeDocuSign.createDocument(templateIdActionId[0], request, accessToken);
                if (response != null && response.getCode() != null && response.getCode() == 400) {
                    LOGGER.error(response.getMessage());
                    return BaseResponse.builder()
                            .message("E- Sign is unavailable at the moment, please contact system administrator.")
                            .code(HttpStatus.BAD_REQUEST.value())
                            .build();
                } else if (response != null) {
                    try {
                        signingRequestTrackerRepository.save(SigningRequestTracker.builder()
                                .documentSigningTemplate(template)
                                .extTemplateId(template.getExtTemplateId())
                                .extRequestId(response.getRequests().getRequestId())
                                .entity(entity)
                                .requestDateTime(new Date(response.getRequests().getSignSubmittedTime()))
                                .requestMessage(new ObjectMapper().writeValueAsString(templateIdActionId))
                                .status(EContractStatus.SIGNED_PENDING.getName())
                                .expiryDate(new Date(response.getRequests().getExpireBy()))
                                .build());
                    } catch (JsonProcessingException e) {
                        check = true;
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
            try{
                customerDetail = customerDetailRepository.findByEntityId(entity.getId());
                if(!check && !customerDetail.getStates().equalsIgnoreCase(ECustomerDetailStates.CUSTOMER.getName())){
                    customerDetail.setStates(ECustomerDetailStates.CONTRACT_PENDING.getName());
                    customerDetailService.save(customerDetail);
                }
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            LOGGER.error("Document template not found for functionality: Customer Acquisition, customer type: " +
                    userDTO.getCustomerType() + " and organization:" + entity.getOrganization().getOrganizationName());
        }
        return null;
    }

    private TemplateData getTemplateData(UserDTO userDTO, String actionId) {
        MasterTenant tenant = masterTenantService.findByDbName(DBContextHolder.getTenantName());
        return TemplateData.builder()
                .templates(Template.builder()
                        .fieldData(FieldData.builder()
                                .build())
                        .actions(Arrays.asList(Action.builder()
                                .actionId(actionId)
                                .actionType("SIGN")
                                .recipientName(userDTO.getFirstName() + " " + userDTO.getLastName())
                                .role(EUserType.PROSPECT.getName())
                                .recipientEmail(userDTO.getEmailAddress())
//                                .recipientPhonenumber(userDTO.getPhone())
                                .recipientCountrycode(null)
                                .privateNotes("")
                                .verifyRecipient(true)
                                .verificationType("EMAIL")
                                .build()))
                        .notes(String.valueOf(tenant.getCompanyKey()))
                        .build())
                .build();
    }

    public UserDTO saveAndUpdateReferralInfo(CaReferralInfoDTO caReferralInfoDTO, UserDTO userDTO) {
        if (userDTO != null && caReferralInfoDTO != null) {
            caReferralInfoService.saveV2(caReferralInfoDTO, userDTO);
        }
        return userDTO;
    }

    public UserDTO saveAndUpdateSoftCreditCheck(CaSoftCreditCheckDTO caSoftCreditCheckDTO, UserDTO userDTO) {
        if (userDTO != null && caSoftCreditCheckDTO != null) {
            CaSoftCreditCheck caSoftCreditCheck = caSoftCreditCheckService.saveV2(caSoftCreditCheckDTO, userDTO);
            userDTO.setIsChecked(caSoftCreditCheck.getIsChecked() ? "1" : "0");
        }
        return userDTO;
    }

    @Override
    public ResponseEntity<?> getAllSalesRepresentatives() {
        Pair<Role, Set<User>> roleAndUsers = getRoleAndUsers(ERole.SALES_REPRESENTATIVE.name());
        Pair<Role, Set<User>> roleManagerAndUsers = getRoleAndUsers(ERole.ACQUISITION_MANAGER.name());
        Set<User> allUsers = new HashSet<>(roleAndUsers.getValue());
        allUsers.addAll(roleManagerAndUsers.getValue());

        List<SalesRepresentativeDTO> salesRepresentativeDTOList = fetchSalesRepresentatives(roleAndUsers.getKey(), allUsers);
        if (!salesRepresentativeDTOList.isEmpty()) {
            return utility.buildSuccessResponse(HttpStatus.OK, "data returned successfully", salesRepresentativeDTOList);
        }
        return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "no data found");
    }


    @Override
    public ResponseEntity<?> assignLeads(List<Long> entityIdList, Long salesRepRoleId, Long acctId) {
        Role role = roleService.findById(salesRepRoleId);
        Organization organization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId("ACTIVE", true, null);
        if (role != null) {
            User user = userService.findById(acctId);
            if (user != null) {
                for (Long entityId : entityIdList) {
                    Entity entity = entityService.findById(entityId);
                    if (entity != null) {
                        userLevelPrivilegeService.save(UserLevelPrivilege.builder().user(user).organization(organization).entity(entity).role(role).build());
                    } else {
                        return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "no entity found");
                    }
                }
            } else {
                return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "no user found");
            }
        } else {
            return utility.buildErrorResponse(HttpStatus.NOT_FOUND, "no role found");
        }
        return utility.buildSuccessResponse(HttpStatus.OK, "Sales agent assigned successfully", null);
    }

    private List<SalesRepresentativeDTO> setProfileImage(Set<User> users, List<SalesRepresentativeDTO> salesRepresentativeDTOList) {
        Map<Long, SalesRepresentativeDTO> salesRepMap = salesRepresentativeDTOList.stream()
                .collect(Collectors.toMap(SalesRepresentativeDTO::getAcctId, Function.identity()));
        for (User user : users) {
            Long acctId = user.getAcctId();
            SalesRepresentativeDTO salesRepDTO = salesRepMap.get(acctId);
            if (salesRepDTO != null) {
                UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(acctId);
                if (userLevelPrivilege != null) {
                    Entity entity = entityService.findById(userLevelPrivilege.getEntity().getId());
                    if (entity != null) {
                        EntityDetail entityDetail = entityDetailRepository.findByEntityId(entity.getId());
                        if (entityDetail != null && entityDetail.getUri() != null) {
                            salesRepDTO.setProfileUrl(entityDetail.getUri());
                        }
                    }
                }
            }
        }
        return salesRepresentativeDTOList;
    }

    @Override
    public BaseResponse getAllCAUsers(String leadType, String statuses, String zipCodes,
                                      String agentIds, String startDate, String endDate, String searchedWords,
                                      Integer pageNumber, Integer pageSize) {
        CaUserTemplatePaginationDTO result = new CaUserTemplatePaginationDTO();
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Boolean isSoftCreditCheckIconVisibility  = false;
        List<String> statusList = parseList(statuses);
        List<String> zipCodeList = parseList(zipCodes);
        List<Long> agentAcctIdList = parseLongList(agentIds);
        Optional<TenantConfig> tenantConfigOptional = null;
        try {
            tenantConfigOptional = tenantConfigService.findByParameter(Constants.TENANT_CONFIG.VISIBLITY_OF_SOFT_CREDIT_ICON);
        } catch (Exception e) {
        }
        if(tenantConfigOptional.isPresent()) {
             isSoftCreditCheckIconVisibility = tenantConfigOptional.get().getText()
                    .equalsIgnoreCase("1") ? true : false;
        }
        try {
            if (searchedWords == null) {
                processWithoutSearchWords(leadType, startDate, endDate, statusList, zipCodeList, agentAcctIdList, pageRequest, result, isSoftCreditCheckIconVisibility);
            } else {
                processWithSearchWords(searchedWords, pageRequest, result , leadType, isSoftCreditCheckIconVisibility);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return buildErrorResponse(e);
        }
        return buildSuccessResponse(result);
    }

    private CaUserTemplatePaginationDTO processWithoutSearchWords(String leadType, String startDate, String endDate,
                                           List<String> statusList, List<String> zipCodeList, List<Long> agentAcctIdList,
                                           PageRequest pageRequest, CaUserTemplatePaginationDTO result, Boolean isSoftCreditCheckIconVisibility) {
        Page<CaUserTemplateDTO> page ;
        List<Long> custEntityIds = agentAcctIdList.isEmpty() ? userLevelPrivilegeService.getEntityIdListByScope() : userLevelPrivilegeService.getCustomerEntityIdsByAgentAcctId(agentAcctIdList);
        page = userRepository.findAllCaUsersByEntityIdsAndLeadType(getLeadTypeState(leadType), leadType, custEntityIds, startDate, endDate, statusList, statusList.size(), zipCodeList, zipCodeList.size(),pageRequest);
        List<CaUserTemplateDTO> dataList = page.getContent();
        dataList.stream().forEach(data -> {
            UserLevelPrivilege agentAccount = userLevelPrivilegeService.findSalesAgentByCustomerEntityId(data.getEntityId());
            data.setIsSoftCreditCheckIconVisibility(isSoftCreditCheckIconVisibility);
            if (agentAccount != null) {
                UserLevelPrivilege agentLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(agentAccount.getUser().getAcctId());
                data.setAgentName(agentLevelPrivilege != null ? agentLevelPrivilege.getUser().getFirstName() + " " + agentLevelPrivilege.getUser().getLastName() : "Unassigned");
                EntityDetail entityDetail = agentLevelPrivilege != null ? entityDetailService.findByEntityId(agentLevelPrivilege.getEntity().getId()) : null;
                if (entityDetail != null) {
                    data.setAgentImage(entityDetail.getUri() != null ? entityDetail.getUri() : null);
                }
            }else{
                data.setAgentName("Unassigned");
            }

        });
        result.setCaUserTemplateDTOList(dataList);
        result.setTotalPages(page.getTotalPages());
        result.setTotalElements(page.getTotalElements());
        return result;
    }

    private CaUserTemplatePaginationDTO processWithSearchWords(String searchedWords, PageRequest pageRequest, CaUserTemplatePaginationDTO result , String leadType, Boolean isSoftCreditCheckIconVisibility) {
        Page<CaUserTemplateDTO> page ;
        page = userRepository.findAllBySearchWord(searchedWords, getLeadTypeState(leadType), leadType ,pageRequest);
        List<CaUserTemplateDTO> dataList = page.getContent();
        dataList.stream().forEach(data -> {
            data.setIsSoftCreditCheckIconVisibility(isSoftCreditCheckIconVisibility);
            UserLevelPrivilege agentLevelPrivilege = userLevelPrivilegeService.findSalesAgentByCustomerEntityId(data.getEntityId());
            data.setAgentName(agentLevelPrivilege != null ? agentLevelPrivilege.getEntity().getEntityName() : "Unassigned");
            EntityDetail entityDetail = agentLevelPrivilege != null ? entityDetailService.findByEntityId(agentLevelPrivilege.getEntity().getId()) : null;
            if (entityDetail != null) {
                data.setAgentImage(entityDetail.getUri() != null ? entityDetail.getUri() : null);
            }
        });
        result.setCaUserTemplateDTOList(dataList);
        result.setTotalPages(page.getTotalPages());
        result.setTotalElements(page.getTotalElements());
        return result;
    }

    private BaseResponse buildErrorResponse(Exception e) {
        return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
    }

    private BaseResponse buildSuccessResponse(CaUserTemplatePaginationDTO result) {
      return  BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }
    private List<String> parseList(String commaSeparatedValues) {
        if (commaSeparatedValues == null || commaSeparatedValues.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(commaSeparatedValues.split(","))
                .filter(s -> s != null && !s.isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private List<Long> parseLongList(String commaSeparatedValues) {
        if (commaSeparatedValues == null || commaSeparatedValues.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(commaSeparatedValues.split(","))
                .filter(s -> s != null && !s.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
    private List<String> getLeadTypeState(String leadType) {
        Map<String, Supplier<List<String>>> leadTypeStateMap = Map.of(
                Constants.CA_TAB.LEAD, () -> Collections.singletonList(ECustomerDetailStates.LEAD.getName()),
                Constants.CA_TAB.NEW_REQUEST, () -> Arrays.asList(
                        ECustomerDetailStates.APPROVAL_PENDING.getName(),
                        ECustomerDetailStates.REQUEST_PENDING.getName()
                ),
                Constants.CA_TAB.PROSPECT, () -> Arrays.asList(
                        ECustomerDetailStates.PROSPECT.getName(),
                        ECustomerDetailStates.CONTRACT_PENDING.getName()),
                Constants.CA_TAB.COMPLETED, () -> Arrays.asList(
                        ECustomerDetailStates.CUSTOMER.getName(),
                        ECustomerDetailStates.RESOLVED.getName(),
                        ECustomerDetailStates.DEFERRED.getName(),
                        ECustomerDetailStates.CLOSED.getName())
        );

            Supplier<List<String>> stateSupplier = leadTypeStateMap.get(leadType);
            if (stateSupplier == null) {
                throw new NotFoundException("Invalid State: " + leadType);
            }
        return stateSupplier.get();
    }


    @Override
    public BaseResponse loadCaFilterData() {
        CustomerAcquisitionFilterDTO dataFilterDTO = new CustomerAcquisitionFilterDTO();
        try {
            dataFilterDTO.setStatus(customerDetailRepository.findAllUniqueStates());
            Role roleSales = roleService.findByName(ERole.SALES_REPRESENTATIVE.name());
            Role roleManager = roleService.findByName(ERole.ACQUISITION_MANAGER.name());
            User loggedInUser = userService.getLoggedInUser();
            Boolean isSalesRep = loggedInUser.getRoles().stream().anyMatch(r -> r.getName().equals(roleSales.getName()));
            Set<User> users = new HashSet<>();
            if (isSalesRep) {
                users.add(loggedInUser);
            }
            if (roleSales != null) {
                users.addAll(roleSales.getUsers());
            }
            if (roleManager != null) {
                users.addAll(roleManager.getUsers());
            }
            List<SalesRepresentativeDTO> salesRepresentativeDTOList = fetchSalesRepresentatives(roleSales, users);
            if (!salesRepresentativeDTOList.isEmpty()) {
                dataFilterDTO.setSalesRepresentativeDTOList(salesRepresentativeDTOList);
            }
            dataFilterDTO.setZipCodes(physicalLocationService.findZipCodesByEntityAndLocationStatus(ECustomerDetailStates.CUSTOMER.getName(), true, false, false));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(dataFilterDTO).build();
    }
    @Override
    public String decodeBase64String(String encodedString) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
            String decodedString = new String(decodedBytes);
            return decodedString;
        } catch (Exception e) {
            return "Invalid input : " + e.getMessage();
        }
    }
    public ResponseEntity<Object> saveRegisterInterestUser(UserDTO userDTO, String template) {
        String oid = null;
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;
        if ((userDTO.getEmailAddress() == null || userDTO.getEmailAddress().isEmpty()) ||
                (userDTO.getFirstName() == null || userDTO.getFirstName().isEmpty()) ||
                (userDTO.getLastName() == null || userDTO.getLastName().isEmpty()) ||
                (userDTO.getCustomerType() == null || userDTO.getCustomerType().isEmpty())) {
            throw new AlreadyExistsException("You must provide these all <<< EmailAddress|FirstName|LastName|CustomerType >>> ");
        } else {
            Entity entityEmailCheck = null;
            try {
                entityEmailCheck = entityService.findByEmailAddressAndEntityType(userDTO.getEmailAddress(), userDTO.getEntityType());
            } catch (Exception ex) {
                ex.getMessage();
            }
            if (userDTO.getEmailAddress() != null && entityEmailCheck != null) {
                throw new AlreadyExistsException("Email " + userDTO.getEmailAddress() + " is already taken");
            }
            try {
                Organization organization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId("ACTIVE", true, null);
                if (organization == null)
                    throw new NotFoundException(Organization.class, "PrimaryIndicator,Status", "true, active");
                Set<Role> roles = new HashSet<>();
                roles.add(roleService.findByName(ERole.ROLE_PROSPECT.toString()));
                userDTO.setAuthentication(EAuthenticationType.NA.getName());// for Ca user we will have this na when those are lead are prospect once it become customer then it will be standard
                user = toUser(userDTO);
                if (userDTO.getPassword() != null) {
                    user.setPassword(encoder.encode(userDTO.getPassword()));
                }
                user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
                user.setRoles(roles);
                user.setStatus(EUserStatus.INACTIVE.getStatus());

                User dbUser = findByEmailAddress(userDTO.getEmailAddress());
                if (dbUser != null) {
                    throw new AlreadyExistsException("Email " + userDTO.getEmailAddress() + " is already taken");
                }
                user.setUserName(user.getEmailAddress());
                User finalUser = userRepository.save(user);

                if (finalUser.getAcctId() == null) {
                    throw new NotFoundException("User not saved in DB");
                }
//                else {
//                    finalUser.setUserName("CA_NONE_" + finalUser.getAcctId());
                //  finalUser.setUserName(finalUser.getEmailAddress());
                // userRepository.save(finalUser);
//                }
                Entity entity = toEntity(userDTOtoEntity(userDTO, user, "Customer", EUserStatus.ACTIVE.getStatus(), true));
                entity.setContactPersonPhone(userDTO.getPhone());
                entity.setOrganization(organization);
                Entity entity1 = entityService.save(entity);
                String customerType = userDTO.getCustomerType();
                CustomerDetail customerDetail = toCustomerDetail(CustomerDetailDTO.builder().customerType(userDTO.getCustomerType())
                        .isContractSign(false).isActive(true).isCustomer(false).hasLogin(false).mobileAllowed(false).signUpDate(new Date())
                        .priorityIndicator(false).states(ECustomerDetailStates.LEAD.toString()).entityId(entity.getId()).status(EUserStatus.INACTIVE.getStatus()).build());
                customerDetail.setSelfInitiative(true);
                customerDetail.setLeadSource(ESourceType.SELF_SIGNUP.getName());
                customerDetail = customerDetailService.save(customerDetail);
                userLevelPrivilegeService.save(UserLevelPrivilege.builder()
                        .user(user)
                        .createdAt(userDTO.getCreatedAt())
                        .updatedAt(userDTO.getUpdatedAt())
                        .entity(entity)
                        .organization(organization)
                        .build());
                try {
                    com.solar.api.tenant.model.BaseResponse data = dataExchange.saveOrUpdateAcquisitionProject(AcquisitionUtils.addEntityIdInTemplate(template, entity1.getId()));
                    // Parse the JSON string into a JsonNode
                    JsonNode rootNode = objectMapper.readTree(data.getMessage().toString());
                    // Extract the _id field
                    JsonNode idNode = rootNode.get("_id");
                    if (idNode != null) {
                        oid = idNode.get("$oid").asText();
                        // Now, 'oid' contains the value of the _id field}
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("Error while hitting mongo api", e);
                }
                try{
                UserMapping userMapping = userMappingService.save(new UserMappingDTO().builder()
                        .entityId(entity1.getId())
                        .ref_id(oid)
                        .module("Acquistion")
                        .build());
                }catch (Exception e){
                    LOGGER.error("Error while save in user mapping table" , e);
                }
                //send email to user
                MasterTenant masterTenant = masterTenantService.findByCompanyKey(userDTO.getCompKey());
                WorkflowHookMaster workFlowHookMaster = workFlowHookMasterRepository.findByHookConstant(Constants.HOOK_CONSTANT.REGISTER_YOUR_INTEREST);
                if (workFlowHookMaster != null) {
                    List<WorkflowHookMap> workflowHookMaps = workflowHookMapRepository.findListByHookId(workFlowHookMaster.getId());
                    CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(userDTO.getCompKey());
                    if (workflowHookMaps.size() > 0) {
                        Optional<WorkflowHookMap> mapUser = workflowHookMaps.stream().filter(s -> s.getEmailTemplate().getMsgTmplName().equals(Constants.MESSAGE_TEMPLATE.REGISTER_YOUR_INTEREST_USER)).findFirst();
                        Optional<WorkflowHookMap> mapSales = workflowHookMaps.stream().filter(s -> s.getEmailTemplate().getMsgTmplName().equals(Constants.MESSAGE_TEMPLATE.REGISTER_YOUR_INTEREST_SALES)).findFirst();
                        Long sourceId = Constants.MESSAGE_TEMPLATE.INTEREST_SALES_TEAM_EMAIL_SOURCE_ID;
                        List<String> CSVs = contactsService.findBySourceId(sourceId).stream().map(Contacts::getEmail).collect(Collectors.toList());

                        Map<String, String> placeholderValues = new HashMap<>();
                        placeholderValues.put("google_play", blobService + Constants.MESSAGE_TEMPLATE.GOOGLE_PLAY);
                        placeholderValues.put("facebook", blobService + Constants.MESSAGE_TEMPLATE.FACEBOOK);
                        placeholderValues.put("apple", blobService + Constants.MESSAGE_TEMPLATE.APPLE);
                        placeholderValues.put("linkedin", blobService + Constants.MESSAGE_TEMPLATE.LINKEDIN);
                        placeholderValues.put("youtube", blobService + Constants.MESSAGE_TEMPLATE.YOUTUBE);
                        placeholderValues.put("twitter", blobService + Constants.MESSAGE_TEMPLATE.TWITTER);
                        placeholderValues.put("solar_amps", blobService + Constants.MESSAGE_TEMPLATE.SOLAR_AMPS);
                        placeholderValues.put("company_key", String.valueOf(userDTO.getCompKey()));
                        placeholderValues.put("company_logo", userDTO.getCompanyLogo() != null ? userDTO.getCompanyLogo() : "");
                        placeholderValues.put("company_name", masterTenant.getCompanyName() != null ? masterTenant.getCompanyName() : "");
                        placeholderValues.put("first_name", userDTO.getFirstName());
                        placeholderValues.put("last_name", userDTO.getLastName());
                        if (finalUser.getUserType() != null) {
                            placeholderValues.put("user_type", finalUser.getUserType().getName().name() != null ? finalUser.getUserType().getName().name() : "");
                        }
                        placeholderValues.put("user_phone_number", userDTO.getPhone());
                        placeholderValues.put("user_dob", String.valueOf(userDTO.getDataOfBirth()));
                        JSONObject json = new JSONObject();
                        json.put("acctId", String.valueOf(user.getAcctId()));
                        json.put("entityId", String.valueOf(entity.getId()));
                        json.put("userType", user.getUserType().getName().name());
                        json.put("initiateLead", "true");
                        json.put("emailAddress", userDTO.getEmailAddress() != null ? userDTO.getEmailAddress() : "");

                        String queryParamBase64 = Base64.getEncoder().encodeToString(json.toString().getBytes());

                        placeholderValues.put("token", queryParamBase64);
                        placeholderValues.put("login_url", masterTenant.getLoginUrl() != null ? masterTenant.getLoginUrl() : "");
                        if (companyPreference != null) {
                            placeholderValues.put("website_url", companyPreference.getWebsiteURL() != null ? companyPreference.getWebsiteURL() : "");
                            placeholderValues.put("company_email", companyPreference.getEmailAddress() != null ? companyPreference.getEmailAddress() : "");
                            placeholderValues.put("company_phone", companyPreference.getEmergencySupportNumber() != null ? String.valueOf(companyPreference.getEmergencySupportNumber()) : "");
                            placeholderValues.put("linkedin_url", companyPreference.getLinkedInURL() != null ? companyPreference.getLinkedInURL() : "");
                            placeholderValues.put("youtube_url", companyPreference.getYoutubeURL() != null ? companyPreference.getYoutubeURL() : "");
                            placeholderValues.put("twitter_url", companyPreference.getTwitterURL() != null ? companyPreference.getTwitterURL() : "");
                        } else {
                            placeholderValues.put("website_url", "");
                            placeholderValues.put("company_email", "");
                            placeholderValues.put("company_phone", "");
                            placeholderValues.put("linkedin_url", "");
                            placeholderValues.put("youtube_url", "");
                            placeholderValues.put("twitter_url", "");
                        }
                        if (CSVs.size() > 0) {
                            String htmlCodeUserMsg = solarAmpsService.getMessage(mapUser.get().getEmailTemplate().getTemplateHTMLCode(), placeholderValues);
                            solarAmpsService.sendEmail(placeholderValues, userDTO.getEmailAddress(), null, null,
                                    mapUser.get().getEmailTemplate().getSubject(), null, mapUser.get().getEmailTemplate().getParentTmplId(), htmlCodeUserMsg);

                            String htmlCodeSalesMsg = solarAmpsService.getMessage(mapSales.get().getEmailTemplate().getTemplateHTMLCode(), placeholderValues);
                            solarAmpsService.sendEmail(placeholderValues, CSVs, null, null,
                                    mapSales.get().getEmailTemplate().getSubject(), null, mapSales.get().getEmailTemplate().getParentTmplId(), htmlCodeSalesMsg);
                        } else {
                            return new ResponseEntity<>(APIResponse.builder().message("Email sent Failed - No Sales team recipient found")
                                    .code(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.getMessage();
                return new ResponseEntity<>(APIResponse.builder().message("Exception Occurred.")
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(APIResponse.builder().message("Email sent successfully.")
                .code(HttpStatus.OK.value()).build(), HttpStatus.OK);
    }
    @Override
    public User findByEmailAddress(String email) {
        return userRepository.findByEmailAddress(email);
    }

    @Override
    public ResponseEntity<Object> saveRegisterInterest(UserDTO userDTO, String template) {
        JsonNode measures = AcquisitionUtils.getMeasuresArray(template);
        userDTO.setCustomerType(AcquisitionUtils.getSubCategory(template).replaceAll("^\"|\"$", ""));
        userDTO.setEntityType(EUserType.CUSTOMER.getName());
        if (userDTO.getCustomerType().equalsIgnoreCase("Individual")) {
            for (JsonNode measure : measures) {
                if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.F_NAME)) {
                    userDTO.setFirstName(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.L_NAME)) {
                    userDTO.setLastName(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.PHONE_NUMBER)) {
                    userDTO.setPhone(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.ZIP_CODE)) {
                    userDTO.setZipCode(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.EMAIL)) {
                    userDTO.setEmailAddress(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                }
            }
        } else if (userDTO.getCustomerType().equalsIgnoreCase("Commercial")) {
            for (JsonNode measure : measures) {
                if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.F_NAME)) {
                    userDTO.setFirstName(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.L_NAME)) {
                    userDTO.setLastName(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.PHONE_NUMBER)) {
                    userDTO.setPhone(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.ZIP_CODE)) {
                    userDTO.setZipCode(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.CONTACT_PERSON_EMAIL)) {
                    userDTO.setEmailAddress(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.WEBSITE)) {
                    userDTO.setBusinessWebsite(!measure.get("default_value").isNull() && !measure.get("default_value").toString().isEmpty() ? measure.get("default_value").toString().replaceAll("^\"|\"$", "") : null);
                }
            }
        }

        ResponseEntity<Object> user = null;
        if (userDTO.getAcctId() == null) {
            user = saveRegisterInterestUser(userDTO, template);
        }
        return user;
    }
    @Override
    public String getUploadedDocumentUrl(MultipartFile file, String notes, Long entityId, DocumentSigningTemplateDTO templateDTO) {
        try {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
            Entity entity = entityService.findById(entityId);
            User user = null;
            String url = null;
            if (entity != null) {
                CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
                if (customerDetail != null) {
                    String customerType = customerDetail.getCustomerType();
                    Contract contract = null;
                    Organization organization = null;
                    String format = "pdf";
                    String functionality = Constants.CONTRACT_TEMPLATE_CONSTANTS.CUSTOMER_ACQUISITION;
                    Optional<UserLevelPrivilege> userLevelPrivilegeOptional = entity.getUserLevelPrivileges().stream().filter(prv -> prv.getEntity().getId() == entity.getId()).findFirst();
                    if (userLevelPrivilegeOptional.isPresent()) {
                        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeOptional.get();
                        contract = userLevelPrivilege.getContract();
                        organization = userLevelPrivilege.getOrganization();
                        user = userLevelPrivilege.getUser();
                    }
                    Organization organization1 = organizationService.findById(templateDTO.getOrganizationId()); // get organization by organization id
                    DocumentSigningTemplate template = documentSigningTemplateService.findByFunctionalityAndCustomerTypeAndOrganizationAndEnabled(templateDTO.getFunctionality(), templateDTO.getCustomerType(), organization1, true).stream().findFirst().get();
                    StringBuilder templateNameBuilder = documentSigningTemplateService.getDocSigningTemplateNameV2(entity, organization, contract, file, template, format, timeStamp);
                    url = storageService.uploadByteArray(file.getBytes(), appProfile, "tenant/docuSign/" + notes, templateNameBuilder.toString());
//                    List<DocuLibrary> docuLibraryList = docuLibraryService.findByCodeRefIdAndCodeRefType(String.valueOf(entity.getId()), "SIGNREQ");
//                    if (docuLibraryList != null && docuLibraryList.size() > 0) {
//                        Optional<DocuLibrary> docuLibraryOptional = docuLibraryList.stream().findFirst();
//                        if (docuLibraryOptional.isPresent()) {
//                            DocuLibrary docuLibrary = docuLibraryOptional.get();
//                            docuLibrary.setDocuName(templateDTO.getTemplateName());
//                            docuLibrary.setUri(url);
//                            docuLibraryService.saveOrUpdate(docuLibrary);
//                        }
//                    } else {
                        docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                                .organization(organization)
                                .entity(entity)
                                .docuType("application/pdf")
                                .codeRefType("SIGNREQ")
                                .status("COMPLETED")
                                .codeRefId(String.valueOf(entity.getId()))
                                .docuName(file.getOriginalFilename())
                                .visibilityKey(true)
                                .uri(url)
                                .referenceTime(timeStamp)
                                .build());
                    }
//                }
                customerDetail.setCustomer(true);
//                customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
                user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
                Set roles = new HashSet<>();
                roles.add(roleService.findByName(ERole.ROLE_CUSTOMER.toString()));
                user.setRoles(roles);
                user.setAuthentication(EAuthenticationType.STANDARD.getName());
                customerDetail.setStatus(EUserStatus.ACTIVE.getStatus());
                customerDetail.setContractSign(true);
                saveUser(user);
                customerDetailService.save(customerDetail);
                entity.setIsDocAttached(true);
                entityService.save(entity);
                return url;
            }
        } catch (IOException | URISyntaxException | StorageException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }
    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    private List<SalesRepresentativeDTO> fetchSalesRepresentatives(Role role, Set<User> users) {
        List<SalesRepresentativeDTO> salesRepresentativeDTOList = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            salesRepresentativeDTOList = SalesRepMapper.toSalesRepresentativeDTOList(users, role);
            salesRepresentativeDTOList = setProfileImage(users, salesRepresentativeDTOList);
        }
        return salesRepresentativeDTOList;
    }

    private Pair<Role, Set<User>> getRoleAndUsers(String roleName) {
        Role role = roleService.findByName(roleName);
        Set<User> users = new HashSet<>();
        if (role != null) {
            List<User> dbUsers = userService.findByRoleName(role.getName());
            if(dbUsers != null && !dbUsers.isEmpty()) {
                users.addAll(dbUsers);
            }
        }
        return Pair.of(role, users);
    }


    @Override
    public ResponseEntity<Object> onSubmit(UserDTO userDTO) {
        try {
            User user = null;
            Entity entity = null;
            CustomerDetail customerDetail = null;
            user = userService.findById(userDTO.getAcctId());
            user.setStatus(EUserStatus.ACTIVE.getStatus());
            userRepository.save(user);
            customerDetail = customerDetailRepository.findByEntityId(userDTO.getEntityId());
            customerDetail.setStatus(EUserStatus.ACTIVE.getStatus());
            entity = entityService.findById(userDTO.getEntityId());
            entity.setStatus(EUserStatus.ACTIVE.getStatus());
            entityService.save(entity);
            if (user != null) {
                Set<Role> userRoles = user.getRoles();
                boolean hasCustomerRole = userRoles.stream().anyMatch(role -> "ROLE_CUSTOMER".equals(role.getName()));
                boolean hasCustomerState = customerDetail.getStates().equalsIgnoreCase("CUSTOMER");
                if (hasCustomerRole && hasCustomerState) {
                    return new ResponseEntity<>(APIResponse.builder().message(Constants.MESSAGE.ALREADY_HAS_ROLE_CUSTOMER)
                            .code(HttpStatus.OK.value()).build(), HttpStatus.OK);
                } else if (hasCustomerRole && !hasCustomerState) {
                    customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
                    customerDetailService.save(customerDetail);
                    return new ResponseEntity<>(APIResponse.builder().message(Constants.MESSAGE.PROSPECT_CONVERTED_MESSAGE)
                            .code(HttpStatus.OK.value()).build(), HttpStatus.OK);
                } else {
                    Role customerRole = roleService.findByName(ERole.ROLE_CUSTOMER.toString());
                    user.getRoles().add(customerRole);
                    customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
                    customerDetailService.save(customerDetail);
                }
            }
            return new ResponseEntity<>(APIResponse.builder().message(Constants.MESSAGE.PROSPECT_CONVERTED_MESSAGE)
                    .code(HttpStatus.OK.value()).build(), HttpStatus.OK);

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(APIResponse.builder().message(e.getMessage())
                    .code(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public List<CaUserTemplateDTO> getAllCaUsersByCorrespondenceCount(String statuses, String zipCodes, String agentIds, String startDate, String endDate, String searchedWord) {
        List<CaUserTemplateDTO> caUserTemplateDTOList = null;
        try {
            List<String> statusList = parseList(statuses);
            List<String> zipCodeList = parseList(zipCodes);
            List<Long> agentAcctIdList = parseLongList(agentIds);
            List<Long> custEntityIds = agentAcctIdList.isEmpty() ? userLevelPrivilegeService.getEntityIdListByScope() : userLevelPrivilegeService.getCustomerEntityIdsByAgentAcctId(agentAcctIdList);
            caUserTemplateDTOList = acquisitionRepo.findAllCaUsersByCorrespondenceCount(custEntityIds, startDate, endDate, statusList, statusList.size(), zipCodeList, zipCodeList.size());
        }catch (Exception e) {
            LOGGER.error("Error while fetching ca users by correspondence count", e);
        }
        return caUserTemplateDTOList;
    }
}
