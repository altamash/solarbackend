package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.azure.storage.StorageException;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.solar.api.AppConstants;
import com.solar.api.Constants;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.InvalidValueException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.helper.Acquisition.AcquisitionUtils;
import com.solar.api.helper.Message;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.WorkflowHookMaster;
import com.solar.api.saas.model.contact.Contacts;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.workflow.WorkflowHookMasterRepository;
import com.solar.api.saas.service.ContactsService;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.docuSign.DataExchangeDocuSign;
import com.solar.api.saas.service.integration.docuSign.dto.FieldData;
import com.solar.api.saas.service.integration.docuSign.dto.Template;
import com.solar.api.saas.service.integration.docuSign.dto.Action;
import com.solar.api.saas.service.integration.docuSign.dto.callback.Notifications;
import com.solar.api.saas.service.integration.docuSign.dto.callback.ZRequests;
import com.solar.api.saas.service.integration.docuSign.dto.callback.ZResponse;
import com.solar.api.saas.service.integration.docuSign.dto.request.TemplateData;
import com.solar.api.saas.service.integration.docuSign.dto.response.CreateTemplateResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.ca.CaUtilityDTO;
import com.solar.api.tenant.mapper.contract.OrganizationDTO;
import com.solar.api.tenant.mapper.contract.OrganizationMapper;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeWrapperDTO;
import com.solar.api.tenant.mapper.cutomer.CustomerDetailDTO;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeDetailDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeManagementDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeTenantDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.mapper.tiles.UtilityInformationTile;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementGroupBy;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementPaginationTile;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementTile;
import com.solar.api.tenant.mapper.tiles.customermanagement.CustomerManagementTileMapper;
import com.solar.api.tenant.mapper.user.*;
import com.solar.api.tenant.mapper.user.userType.UserTypeDTO;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoDTO;
import com.solar.api.tenant.mapper.workOrder.MongoCustomerDetailWoMasterDTO;
import com.solar.api.tenant.mapper.workOrder.UserSubscriptionTemplateWoDTO;
import com.solar.api.tenant.mapper.workOrder.UserDetailTemplateWoDTO;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.ca.CaReferralInfo;
import com.solar.api.tenant.model.ca.CaSoftCreditCheck;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.contract.*;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.docuSign.DocumentSigningTemplate;
import com.solar.api.tenant.model.docuSign.ExternalCallBackLog;
import com.solar.api.tenant.model.docuSign.SigningRequestTracker;
import com.solar.api.tenant.model.extended.FunctionalRoles;
import com.solar.api.tenant.model.extended.document.DocuLibrary;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.user.*;
import com.solar.api.tenant.model.user.contractStatus.EContractStatus;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userMapping.UserMapping;
import com.solar.api.tenant.model.user.userType.EAuthenticationType;
import com.solar.api.tenant.model.user.userType.ECustomerDetailStates;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;
import com.solar.api.tenant.model.userGroup.EntityRole;
import com.solar.api.tenant.model.workflow.WorkflowHookMap;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.repository.UserGroup.EntityRoleRepository;
import com.solar.api.tenant.repository.contract.EntityDetailRepository;
import com.solar.api.tenant.repository.contract.EntityRepository;
import com.solar.api.tenant.repository.contract.OrganizationDetailRepository;
import com.solar.api.tenant.repository.contract.UserLevelPrivilegeRepository;
import com.solar.api.tenant.repository.docuSign.DocumentSigningTemplateRepository;
import com.solar.api.tenant.repository.docuSign.ExternalCallBackLogRepository;
import com.solar.api.tenant.repository.docuSign.SigningRequestTrackerRepository;
import com.solar.api.tenant.repository.permission.AvailablePermissionSetRepository;
import com.solar.api.tenant.repository.project.EmployeeDetailRepository;
import com.solar.api.tenant.repository.workflow.WorkflowHookMapRepository;
import com.solar.api.tenant.service.ca.CaReferralInfoService;
import com.solar.api.tenant.service.ca.CaSoftCreditCheckService;
import com.solar.api.tenant.service.ca.CaUtilityService;
import com.solar.api.tenant.service.contract.*;
import com.solar.api.tenant.service.extended.LocationMappingService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.extended.project.EmployeeDetailService;
import com.solar.api.tenant.service.extended.project.EmployeeManagementService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import com.solar.api.tenant.service.override.portalAttribute.PortalAttributeOverrideService;
import com.solar.api.tenant.service.process.permission.PermissionGroupService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.ErrorDTO;
import com.solar.api.tenant.service.solarAmps.SolarAmpsService;
import com.solar.api.tenant.service.userGroup.DefaultUserGroupNewService;
import com.solar.api.tenant.service.userMapping.UserMappingService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpMethod;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.transaction.Transactional;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.ca.CaReferralInfoMapper.toCaReferralInfoDTO;
import static com.solar.api.tenant.mapper.ca.CaSoftCreditCheckMapper.toCaSoftCreditCheck;
import static com.solar.api.tenant.mapper.ca.CaUserTemplateDTOMapper.caUserTemplateDTO;
import static com.solar.api.tenant.mapper.ca.CaUtilityMapper.toCaUtilityDTO;
import static com.solar.api.tenant.mapper.contract.AccountMapper.accountDTOtoAccount;
import static com.solar.api.tenant.mapper.contract.AccountMapper.userToAccountDTO;
import static com.solar.api.tenant.mapper.contract.EntityMapper.toEntity;
import static com.solar.api.tenant.mapper.contract.EntityMapper.userDTOtoEntity;
import static com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper.*;
import static com.solar.api.tenant.mapper.user.UserMapper.*;
import static com.solar.api.tenant.mapper.user.userType.UserTypeMapper.portalAttributeValuesToUserTypeDTO;
import static com.solar.api.tenant.mapper.cutomer.CustomerDetailMapper.toCustomerDetail;


@Service
//@Transactional("tenantTransactionManager")
public class UserServiceImpl implements UserService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrganizationDetailRepository oragnizationDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private CustomerSubscriptionRepository subscriptionRepository;
    @Autowired
    private PaymentInfoRepository paymentInfoRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CustomerSubscriptionMappingRepository customerSubscriptionMappingRepo;
    @Autowired
    CustomerSubscriptionsListRepository subscriptionsListRepositoryCustom;
    @Autowired
    private SubscriptionRateMatrixHeadRepository subscriptionRateMatrixHeadRepository;
    @Autowired
    private UniqueResetLinkService uniqueResetLinkService;

    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private PermissionGroupService permissionGroupService;
    @Autowired
    private AvailablePermissionSetRepository availablePermissionSetRepository;
    @Autowired
    private Utility utility;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private StorageService storageService;
    @Autowired
    private DocuLibraryService docuLibraryService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private PhysicalLocationRepository physicalLocationRepository;
    @Autowired
    private EntityService entityService;
    @Autowired
    private LocationMappingService locationMappingService;
    @Autowired
    private PortalAttributeOverrideService portalAttributeOverrideService;
    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;
    @Autowired
    private CaUtilityService caUtilityService;
    @Autowired
    private CaReferralInfoService caReferralInfoService;
    @Autowired
    private PhysicalLocationService physicalLocationService;
    @Autowired
    private CaSoftCreditCheckService caSoftCreditCheckService;
    @Autowired
    private DataExchangeDocuSign dataExchangeDocuSign;
    @Autowired
    private DataExchange dataExchange;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private DocumentSigningTemplateRepository signingTemplateRepository;
    @Autowired
    private SigningRequestTrackerRepository signingRequestTrackerRepository;
    @Autowired
    private EntityDetailRepository entityDetailRepository;
    @Autowired
    private ExternalCallBackLogRepository externalCallBackLogRepository;
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private EmployeeDetailService employeeDetailService;
    @Autowired
    private DocumentSigningTemplateService documentSigningTemplateService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    EntityRepository entityRepository;

    @Value("${app.mongoBaseUrl}")
    private String MONGO_BASE_URL;

    @Value("${app.profile}")
    private String appProfile;

    @Value("${app.storage.container}")
    private String storageContainer;
    @Autowired
    private EntityRoleRepository entityRoleRepository;
    @Autowired
    private WorkflowHookMasterRepository workFlowHookMasterRepository;
    @Autowired
    private WorkflowHookMapRepository workflowHookMapRepository;
    @Autowired
    private SolarAmpsService solarAmpsService;
    @Value("${app.storage.blobService}")
    private String blobService;
    @Autowired
    private CompanyPreferenceService companyPreferenceService;
    @Autowired
    private ContactsService contactsService;

    @Autowired
    private DefaultUserGroupNewService defaultUserGroupNewService;
    @Autowired
    private UserMappingService userMappingService;
    @Autowired
    private EmployeeManagementService employeeManagementService;
    @Autowired
    private EntityDetailService entityDetailService;
    @Autowired
    private UserLevelPrivilegeRepository userLevelPrivilegeRepository;
    @Autowired
    private LocationMappingRepository locationMappingRepository;
    @Autowired
    CustomerDetailRepository customerDetailRepository;
    @Autowired
    EmployeeDetailRepository employeeDetailRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
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

    @Override
    public User findByIdNoThrow(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    @Override
    public User findByIdFetchRoles(Long id) {
        return userRepository.findByIdFetchRoles(id);
    }

    @Override
    public User findByIdFetchAddresses(Long id) {
        return userRepository.findByIdFetchAddresses(id);
    }

    @Override
    public User findByIdFetchAll(Long id) {
        return userRepository.findByIdFetchAll(id);
    }

    @Override
    public User findByEmailAddress(String email) {
        return userRepository.findByEmailAddress(email);
    }

    @Override
    public User findByEmailAddressFetchRoles(String email) {
        return userRepository.findByEmailAddressFetchRoles(email);
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public List<User> findAllByUserName(String userName) {
        return userRepository.findAllByUserName(userName);
    }

    @Override
    public User findByUserNameFetchRoles(String userName) {
        return userRepository.findByUserNameFetchRoles(userName);
    }

    @Override
    public User findByUserNameFetchPermissions(String userName) {
        return userRepository.findByUserNameFetchPermissions(userName);
    }

    @Override
    public List<User> findAllFetchRoles() {
        return userRepository.findAllFetchRoles();
    }

    @Override
    public List<User> findAllCustomersFromEntity() {
        return null;
    }

    @Override
    public List<User> findAllFetchPermissions() {
        return userRepository.findAllFetchPermissions();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findByText(String text) {
        if (StringUtils.isNumeric(text)) {
            return userRepository.findAll((Specification
                    .where(UserSpecification.textInAllColumns(text)).or(Specification.where(UserSpecification.withFieldValue("acctId", Long.parseLong(text)))))
                    .and(UserSpecification.withFieldValueNot("status", EUserStatus.INVALID.getStatus())));
        }
        return userRepository.findAll(Specification
                .where(UserSpecification.textInAllColumns(text))
                .and(UserSpecification.withFieldValueNot("status", EUserStatus.INVALID.getStatus())));
    }

    @Override
    public List<User> findByUserType(EUserType userType) {
        UserType type = userTypeService.findByName(userType);
        return userRepository.findByUserType(type);
    }

    @Override
    public List<User> findByAcctIdIn(List<Long> ids) {
        return userRepository.findByAcctIdIn(ids);
    }

    @Override
    public User saveOrUpdate(User user, Set<Address> addresses, List<CustomerSubscription> customerSubscriptions,
                             Set<PaymentInfo> paymentInfos) {
        user.setStatus(EUserStatus.ACTIVE.getStatus());
        Date date = new Date();
        user.setActiveDate(date);
        user.setRegisterDate(date);
        user = userRepository.save(user);
        User finalUser = user;
        addresses.forEach(a -> a.setUserAccount(finalUser));
        addresses = new HashSet<>(addressRepository.saveAll(addresses));
        customerSubscriptions.forEach(cc -> {
            cc.setUserAccount(finalUser);
            cc.getCustomerSubscriptionMappings().forEach(ccm -> {
                ccm.setValue(ccm.getValue() != null ? ccm.getValue() : ccm.getDefaultValue());
                ccm.setSubscription(cc);
                ccm.setSubscriptionRateMatrixHead(subscriptionRateMatrixHeadRepository.findById(ccm.getSubscriptionRateMatrixId()).orElse(null));
                ccm.setLevel(null);
                MeasureDefinitionTenantDTO billingDefinition =
                        measureDefinitionOverrideService.findMeasureDefinitionByCode(ccm.getRateCode());
                if (billingDefinition != null) {
                    ccm.setMeasureDefinition(billingDefinition);
                    ccm.setMeasureDefinitionId(billingDefinition.getId());
                }
                cc.setSubscriptionRateMatrixId(ccm.getSubscriptionRateMatrixId());
            });
            cc.setSubscriptionStatus(ESubscriptionStatus.INACTIVE.getStatus());
        });
        customerSubscriptions = subscriptionRepository.saveAll(customerSubscriptions);
        paymentInfos.forEach(cc -> cc.setPortalAccount(finalUser));
        paymentInfos = new HashSet<>(paymentInfoRepository.saveAll(paymentInfos));
        finalUser.setAddresses(addresses);
        finalUser.setCustomerSubscriptions(customerSubscriptions);
        finalUser.setPaymentInfos(paymentInfos);
        return userRepository.save(finalUser);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> saveAll(List<User> users) {
        return userRepository.saveAll(users);
    }

    @Override
    public User resetPassword(Long id, String newPass) {
        User userData = findById(id);
//        if(encodedPass != userData.getPassword()){
//            userData.setPassword(encodedPass);
//        } else{
//
//        }
        String encodedPass = encoder.encode(newPass);
        userData.setPassword(encodedPass);
        return userRepository.save(userData);
    }

    @Override
    public User setPassword(Long id, String newPass) {
        User userData = findById(id);
        userData.setPassword(newPass);
        return userRepository.save(userData);
    }

    @Override
    public User updateUser(Long id, String newPass) {
        User user = null;
        User userData = findById(id);
        Set<Role> roles = new HashSet<>();
        Role role = roleService.findByName(ERole.ROLE_NEW_CUSTOMER.toString());
        if (role == null) {
            //  response.put("error", "cannot activate your user");
            throw new InvalidValueException("this role doesn't exist in DB", role.getName());
        }
        roles.add(role);
        userData.setRoles(roles);
        userData.setStatus(EUserStatus.ACTIVE.getStatus());
        userData.setEmailVerified(true);
        userData.setPassword(newPass);
        userData.setAuthentication(EAuthenticationType.STANDARD.getName());
        Entity entity = entityService.findEntityByUserId(userData.getAcctId());
        if (entity != null) {
            CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
            user = userRepository.save(userData);
            if (user != null && customerDetail != null) {
                customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
                customerDetail.setStatus(EUserStatus.ACTIVE.getStatus());
                customerDetail.setActive(true);
                customerDetailService.save(customerDetail);
            }
        }
        return user;
    }

    @Override
    public ObjectNode verifyAdmin(VerificationDTO verificationDTO) {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        User userData = findById(verificationDTO.userId);
        if (userData != null) {

            Long userID = toUserDTO(userData).getAcctId();
            StringBuilder salt = new StringBuilder();
            Random rnd = new Random();

            while (salt.length() < 18) {

                int index = (int) (rnd.nextFloat() * AppConstants.SALT_CHARS.length());
                salt.append(AppConstants.SALT_CHARS.charAt(index));
            }

            String saltStr = salt.toString();

            UniqueResetLink uniqueResetLink = new UniqueResetLink();
            uniqueResetLink.setUserAccount(userID);
            uniqueResetLink.setAdminAccount(verificationDTO.getAdminId());
            uniqueResetLink.setUniqueText(saltStr);
            uniqueResetLink.setUsedIndicator(false);
            uniqueResetLinkService.save(uniqueResetLink);

            objectNode.put("message", "Admin Verified");
            objectNode.put("token", saltStr);
        } else {
            objectNode.put("warning", "UserId cannot be null");
        }
        return objectNode;
    }

    @Override
    public ObjectNode resetCustomerPassword(VerificationDTO verificationDTO) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        User user = getLoggedInUser();

        if (user.getUserType().getName().getName().equals(EUserType.CUSTOMER.getName())) {
            if (verificationDTO.getUserId().longValue() == user.getAcctId().longValue()) {
                user.setPassword(verificationDTO.getPassword());
                saveUser(user);
                response.put("message", "Password changed successfully!");
                return response;
            }
        }

        UniqueResetLink uniqueResetLinkData = uniqueResetLinkService.findByUniqueText(verificationDTO.getToken());
        if (!uniqueResetLinkData.getUsedIndicator()) {
            User userData = findById(uniqueResetLinkData.getUserAccount());
            userData.setPassword(verificationDTO.getPassword());
            saveUser(userData);
            uniqueResetLinkData.setUsedIndicator(true);
            uniqueResetLinkService.save(uniqueResetLinkData);
            response.put("message", "Password changed successfully!");
        } else {
            response.put("warning", "Invalid request!");
        }
        return response;
    }

    @Override
    public User update(User user) {
        User userDb = userRepository.findByUserName(user.getUserName());
        if (userDb != null && userDb.getAcctId().longValue() != user.getAcctId().longValue() && userDb.getUserName().equals(user.getUserName())) {
            throw new SolarApiException("UserName already exists.");
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new NotFoundException(User.class, id));
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> passwordVerification(Long id, String password) {
        Optional<User> userOb = userRepository.findById(id);
        return userOb;
    }

    @Override
    public List<TempPass> passwordGenerator() {
        return userRepository.passwordGenerator();
    }

    @Override
    public List<Long> findUserBySubsId(List<Long> subIds) {
        List<Long> userIds = userRepository.findUserBySubsId(subIds);
        return userIds;
    }

    @Override
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = findByUserName(username);
        return user;
    }

    @Override
    public Set<Role> addRole(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleService.findById(roleId);
        user.addRole(role);
        user = saveUser(user);
        return user.getRoles();
    }

    @Override
    public Set<Role> removeRole(Long userId, Long roleId) {
        User user = findById(userId);
        Role role = roleService.findById(roleId);
        user.removeRole(role);
        user = saveUser(user);
        return user.getRoles();
    }

    // User permissions
    @Override
    public Set<PermissionGroup> addPermissionGroup(Long userId, Long permissionGroupId) {
        User user = findById(userId);
        PermissionGroup permissionGroup = permissionGroupService.findById(permissionGroupId);
        user.addPermissionGroup(permissionGroup);
        user = saveUser(user);
        return user.getPermissionGroups();
    }

    @Override
    public Set<PermissionGroup> removePermissionGroup(Long userId, Long permissionGroupId) {
        User user = findById(userId);
        PermissionGroup permissionGroup = permissionGroupService.findById(permissionGroupId);
        user.removePermissionGroup(permissionGroup);
        user = saveUser(user);
        return user.getPermissionGroups();
    }

    @Override
    public Set<AvailablePermissionSet> addPermissionSet(Long userId, Long availablePermissionSetId) {
        User user = findById(userId);
        AvailablePermissionSet availablePermissionSet = availablePermissionSetRepository.findById(availablePermissionSetId)
                .orElseThrow(() -> new NotFoundException(AvailablePermissionSet.class, availablePermissionSetId));
        user.addPermissionSet(availablePermissionSet);
        user = saveUser(user);
        return user.getPermissionSets();
    }

    @Override
    public Set<AvailablePermissionSet> removePermissionSet(Long userId, Long availablePermissionSetId) {
        User user = findById(userId);

        AvailablePermissionSet availablePermissionSet = availablePermissionSetRepository.findById(availablePermissionSetId)
                .orElseThrow(() -> new NotFoundException(AvailablePermissionSet.class, availablePermissionSetId));
        user.removePermissionSet(availablePermissionSet);
        user = saveUser(user);
        return user.getPermissionSets();
    }

    @Override
    public User userSelfRegistration(UserDTO userDto, List<MultipartFile> multipartFiles, List<MultipartFile> businessMultipartFiles) {
        String profileFileName = Constants.NEW_USER_CONSTANTS.PROFILE;
        String businessLogoName = Constants.NEW_USER_CONSTANTS.BUSINESS_LOGO;
        List<PhysicalLocation> physicalLocation = new ArrayList<>();
        if (userDto.getUserName() != null) {
            User userNameExists = findByUserName(userDto.getUserName());
            if (userNameExists != null) {
                throw new AlreadyExistsException("UserName " + userDto.getUserName());
            }
            User EmailAddrExists = findByEmailAddress(userDto.getEmailAddress());
            if (EmailAddrExists != null) {
                throw new AlreadyExistsException("Email " + userDto.getEmailAddress());
            }
        }
        Organization organization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId("ACTIVE", true, null);
        if (organization == null)
            throw new NotFoundException(Organization.class, "PrimaryIndicator,Status", "true, active");
        String entityType = getEntityType(userDto.getCustomerType());
        User user = toUser(userDto);
        String DEFAULT_USERNAME = user.getFirstName() + user.getLastName() + "2020";
        if (user.getUserName() == null) {
            user.setUserName(DEFAULT_USERNAME);
        }
        if (userDto.getPassword() != null) {
            user.setPassword(encoder.encode(userDto.getPassword()));
        } else {
            user.setPassword("");
        }
        user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByName(ERole.ROLE_CUSTOMER.toString()));
        user.setRoles(roles);
        user.setStatus(EUserStatus.INACTIVE.getStatus());
        Date date = new Date();
        if (user.getActiveDate() == null) {
            user.setActiveDate(date);
        }
        if (user.getRegisterDate() == null) {
            user.setRegisterDate(date);
        }
        User finalUser = userRepository.save(user);
        if (finalUser.getAcctId() == null) throw new NotFoundException("User not saved in DB");
        Account account = accountDTOtoAccount(userToAccountDTO(finalUser, userDto.getIsAttachment()));
        account.setUser(finalUser);
        Entity entity = toEntity(userDTOtoEntity(userDto, user, entityType, EUserStatus.ACTIVE.getStatus(), true));
        entity.setOrganization(organization);
        //same address adding twice for different categories
        physicalLocation.addAll(updateLocationToDefaultTypeAndCategory(userDto.getPhysicalLocations(), Constants.LOCATION_CATEGORY_CONSTANTS.USER, Constants.LOCATION_TYPE.DEFAULT_ADDRESS));
        //this one added for an entity
        physicalLocation.addAll(updateLocationToDefaultTypeAndCategory(userDto.getPhysicalLocations(), Constants.LOCATION_CATEGORY_CONSTANTS.ENTITY, Constants.LOCATION_TYPE.DEFAULT_ADDRESS));
        accountService.save(account);
        entityService.save(entity);
        if (entity.getId() == null || account.getId() == null)
            throw new NotFoundException("Entity|Account not saved in DB");
        physicalLocationRepository.saveAll(physicalLocation);
        locationMappingService.saveAll(getLocationMappings(physicalLocation, finalUser, entity));
        if (userDto.getPhotoBase64() != null) {
            if (multipartFiles == null) multipartFiles = new ArrayList<>();
            multipartFiles.add(convertPhotoBase64ToMultipart(userDto.getPhotoBase64(), profileFileName));
        }
        if (userDto.getBusinessLogoBase64() != null) {
            if (businessMultipartFiles == null) businessMultipartFiles = new ArrayList<>();
            businessMultipartFiles.add(convertPhotoBase64ToMultipart(userDto.getBusinessLogoBase64(), businessLogoName));
        }
        doAttachmentToUser(multipartFiles, user, "");//upload user files
        doAttachmentToUser(businessMultipartFiles, user, AppConstants.REGISTER_NEW_USER_BUSINESS_INFO_PATH);//upload businessInfo files
        return finalUser;
    }

    @Override
    public List<UserTypeDTO> getAllUserType() {
        PortalAttributeTenantDTO portalAttributeTenantDTO = portalAttributeOverrideService.findByNameFetchPortalAttributeValues(Constants.NEW_USER_CONSTANTS.CUSTOMER_TYPE);
        return portalAttributeValuesToUserTypeDTO(portalAttributeTenantDTO.getPortalAttributeValuesTenant());
    }

    @Override
    public User internalUserRegistration(UserDTO userDto) {
        List<PhysicalLocation> physicalLocation = new ArrayList<>();
        if (userDto.getUserName() != null) {
            User userNameExists = findByUserName(userDto.getUserName());
            if (userNameExists != null) {
                throw new AlreadyExistsException("UserName " + userDto.getUserName());
            }
            User EmailAddrExists = findByEmailAddress(userDto.getEmailAddress());
            if (EmailAddrExists != null) {
                throw new AlreadyExistsException("Email " + userDto.getEmailAddress());
            }
        }
        User user = toUser(userDto);
        if (userDto.getPassword() != null) {
            user.setPassword(encoder.encode(userDto.getPassword()));
        } else {
            user.setPassword("");
        }
        if (userDto.getUserType() != null) {
            UserType userType = userTypeService.findByName(EUserType.get(userDto.getUserType()));
            user.setUserType(userType);
        }
        // Set Roles
        Set<String> strRoles = userDto.getRoles();
        Set<Role> roles = new HashSet<>();
        if (strRoles != null) {
            strRoles.forEach(role -> roles.add(roleService.findByName(role)));
        }
        user.setRoles(roles);
        if (userDto.getPhysicalLocations() != null || userDto.getPhysicalLocations().size() > 0)
            physicalLocation.addAll(updateLocationToDefaultTypeAndCategory(userDto.getPhysicalLocations(), Constants.LOCATION_CATEGORY_CONSTANTS.USER, Constants.LOCATION_TYPE.EMPLOYEE_ADDRESS));
        user.setStatus(EUserStatus.INACTIVE.getStatus());
        Date date = new Date();
        if (user.getActiveDate() == null) {
            user.setActiveDate(date);
        }
        if (user.getRegisterDate() == null) {
            user.setRegisterDate(date);
        }
        User finalUser = userRepository.save(user);
        physicalLocationRepository.saveAll(physicalLocation);
        locationMappingService.saveAll(getLocationMappings(physicalLocation, finalUser, null));
        return finalUser;
    }


    @Override
    public User saveOrUpdateCaUser(UserDTO userDTO, List<MultipartFile> utilityMultipartFiles, Boolean sendForSigning, List<Long> ids) {
        User user = null;
        if (userDTO.getAcctId() == null) {
            user = saveCaUser(userDTO);
        } else if (userDTO.getAcctId() != null) {
            user = updateCaUser(userDTO, sendForSigning);
        }
        if (user != null) {
            if (userDTO.getCaUtility() != null) {
                saveOrUpdateCaUtilityDTO(userDTO, user, utilityMultipartFiles);
            }
            if (user != null && userDTO.getCaReferralInfo() != null) {
                CaReferralInfoDTO caReferralInfoDTO = userDTO.getCaReferralInfo();
                caReferralInfoService.save(caReferralInfoDTO, user);
                if (sendForSigning != null && sendForSigning) {
                    sendForSigning(user, userDTO);
                }
            }
            if (user != null && userDTO.getCaSoftCreditCheck() != null) {
                CaSoftCreditCheckDTO caSoftCreditCheckDTO = userDTO.getCaSoftCreditCheck();
                caSoftCreditCheckService.save(caSoftCreditCheckDTO, user);
            }
        }
        return user;
    }

    @Override
    public User saveAcquisition(UserDTO userDTO, String template, MultipartFile image, Long tenantId, String apiSource) {
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

        User user = null;
        if (userDTO.getAcctId() == null) {
            user = saveCaRegisterationUser(userDTO, template, image, tenantId, apiSource);
        }
        return user;
    }

    private JsonNode getMeasuresArrayForUpdate(String jsonObject) {
        String jsonString = jsonObject;

        // Create an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse the JSON string into a JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Extract the "sections" array
//            JsonNode sectionsNode = rootNode.get("sections");

            JsonNode contentNode = rootNode.get("content");

            if (contentNode != null) {
                for (JsonNode sectionNode : contentNode) {
                    // Extract the "content" object within each section
//                    JsonNode contentNode = sectionNode.get("content");

//                    if (contentNode != null) {
                    // Extract the "measures" array within the content object
                    JsonNode measuresNode = contentNode.get("measures");
                    return measuresNode;

//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //    @Async
    public void sendForSigning(User user, UserDTO userDTO) {
        String accessToken =
                dataExchangeDocuSign.getAccessTokenViaRefreshToken().getAccessToken();
        Entity entity = entityService.findEntityByUserId(user.getAcctId());
        System.out.println("1 **************************************************************************************");
        System.out.println("Customer Acquisition:  getCustomerType> " + userDTO.getCustomerType() + " getOrganization> " + entity.getOrganization().getOrganizationName());
        System.out.println("**************************************************************************************");
        List<DocumentSigningTemplate> signingTemplates = signingTemplateRepository
                .findByFunctionalityAndCustomerTypeAndOrganizationAndEnabled("Customer Acquisition"
                        , userDTO.getCustomerType(), entity.getOrganization(), true); // find by+ customer_type +

        if (!signingTemplates.isEmpty()) {
            DocumentSigningTemplate template = signingTemplates.get(0);
            String[] templateIdActionId = template.getExtTemplateId().split("_");
            TemplateData request = getTemplateData(userDTO, templateIdActionId[2]);
            CreateTemplateResponse response =
                    dataExchangeDocuSign.createDocument(templateIdActionId[0], request, accessToken);
            if (response != null && response.getCode() != null && response.getCode() == 400) {
                LOGGER.error(response.getMessage());
            } else if (response != null) {
                try {
                    signingRequestTrackerRepository.save(SigningRequestTracker.builder()
                            .documentSigningTemplate(template)
                            .extTemplateId(template.getExtTemplateId())
                            .extRequestId(response.getRequests().getRequestId())
                            .entity(entity)
                            .requestDateTime(new Date(response.getRequests().getSignSubmittedTime()))
                            .requestMessage(new ObjectMapper().writeValueAsString(templateIdActionId))
                            .status("")
                            .expiryDate(new Date(response.getRequests().getExpireBy()))
                            .build());
                } catch (JsonProcessingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        } else {
            LOGGER.error("Document template not found for functionality: Customer Acquisition, customer type: " +
                    userDTO.getCustomerType() + " and organization:" + entity.getOrganization().getOrganizationName());
        }
    }


    //    {
//        "templates": {
//        "field_data": {
//            "field_text_data": {"Company": "SI", "Text1": "sample", "Full name": "test name"},
//            "field_boolean_data": {},
//            "field_date_data": {"Date1": "Nov 08 2022"}
//        },
//        "actions": [{
//            "action_id": "{{Zoho-action_id}}",
//                    "action_type": "SIGN",
//                    "recipient_name": "Mohammad Altamash",
//                    "role": "ts1",
//                    "recipient_email": "testna@solarinformatics.com",
//                    "recipient_phonenumber": "",
//                    "recipient_countrycode": "",
//                    "private_notes": "",
//                    "verify_recipient": true,
//                    "verification_type": "EMAIL",
//        }],
//        "notes": "1001"
//    }
//    }
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

    @Override
    public List<CaUserTemplateDTO> getAllCaUser() {
        SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        List<UserTemplate> userTemplates = userRepository.findAllCaUsers(
                Arrays.asList(ECustomerDetailStates.LEAD.getName(), ECustomerDetailStates.PROSPECT.getName(), ECustomerDetailStates.CUSTOMER.getName(), ECustomerDetailStates.INTERIMCUSTOMER.getName()));
        userTemplates.sort(Comparator.comparing(UserTemplate::getGeneratedAt).reversed());
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        EntityDetail entityDetail;
        CaUserTemplateDTO userTemplateDTO;
        for (UserTemplate userTemplate : userTemplates) {
            userTemplateDTO = caUserTemplateDTO(userTemplate);
            //add profileUrl
            entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
            if (entityDetail != null) {
                userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
            }
            userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));

            if (userTemplate.getReferralId() != null) {
                Optional<EntityRole> entityRoleOptional = entityRoleRepository.findById(userTemplate.getReferralId());
                if (entityRoleOptional.isPresent()) {
                    EntityRole entityRole = entityRoleOptional.get();
                    if (entityRole.getFunctionalRoles() != null) {
                        if (entityRole.getEntity() != null) {
                            FunctionalRoles functionalRoles = entityRole.getFunctionalRoles();
                            Entity entityDB = entityRole.getEntity();
                            userTemplateDTO.setAgentDesignation(functionalRoles.getName());
                            userTemplateDTO.setAgentName(entityDB.getEntityName());
                        }
                    }
                }
            }
            if (userTemplateDTO.getAgentDesignation() == null || userTemplateDTO.getAgentName() == null) {
                userTemplateDTO.setAgentDesignation("Unassigned");
                userTemplateDTO.setAgentName(" ");
            }
            userTemplateDtoList.add(userTemplateDTO);
        }
        return userTemplateDtoList;
    }

    @Override
    public List<CaUserTemplateDTO> getAllCaUserByType(String states) {
        SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        List<UserTemplate> userTemplates = null;
        if (states.equalsIgnoreCase(Constants.CA_TAB.LEAD)) {
            userTemplates = userRepository.findAllCaUsersByType(Arrays.asList(ECustomerDetailStates.LEAD.getName()));
        } else if (states.equalsIgnoreCase(Constants.CA_TAB.NEW_REQUEST)) {
            userTemplates = userRepository.findAllCaUsersByType(Arrays.asList(ECustomerDetailStates.INTERIMCUSTOMER.getName()));
        } else if (states.equalsIgnoreCase(Constants.CA_TAB.PROSPECT)) {
            userTemplates = userRepository.findAllCaUsersByType(Arrays.asList(ECustomerDetailStates.PROSPECT.getName()));
        } else if (states.equalsIgnoreCase(Constants.CA_TAB.COMPLETED)) {
            userTemplates = userRepository.findAllCaUsersByType(Arrays.asList(ECustomerDetailStates.CUSTOMER.getName(),
                    ECustomerDetailStates.CLOSED.getName()));
        } else {
            throw new NotFoundException("Invalid State: " + states);
        }
        userTemplates.sort(Comparator.comparing(UserTemplate::getGeneratedAt).reversed());
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        EntityDetail entityDetail;
        CaUserTemplateDTO userTemplateDTO;
        for (UserTemplate userTemplate : userTemplates) {
            userTemplateDTO = caUserTemplateDTO(userTemplate);
            //add profileUrl
            entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
            if (entityDetail != null) {
                userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
            }
            userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));

            if (userTemplate.getReferralId() != null) {
                Optional<EntityRole> entityRoleOptional = entityRoleRepository.findById(userTemplate.getReferralId());
                if (entityRoleOptional.isPresent()) {
                    EntityRole entityRole = entityRoleOptional.get();
                    if (entityRole.getFunctionalRoles() != null) {
                        if (entityRole.getEntity() != null) {
                            FunctionalRoles functionalRoles = entityRole.getFunctionalRoles();
                            Entity entityDB = entityRole.getEntity();
                            userTemplateDTO.setAgentDesignation(functionalRoles.getName());
                            userTemplateDTO.setAgentName(entityDB.getEntityName());
                        }
                    }
                }
            }
            if (userTemplateDTO.getAgentDesignation() == null || userTemplateDTO.getAgentName() == null) {
                userTemplateDTO.setAgentDesignation("Unassigned");
                userTemplateDTO.setAgentName(" ");
            }
            userTemplateDtoList.add(userTemplateDTO);
        }
        return userTemplateDtoList;
    }

    //TODO: Need to delete
    @Override
    public Map getAllCustomerCount() {
        return null;
    }

//    @Override
//    public CaUserTemplateDTO getAllCustomerCount() {
////        UserTemplate userTemplates = userRepository.findAllCustomerCount();
////        CaUserTemplateDTO userTemplateDTO;
////            userTemplateDTO = caUserTemplateDTO(userTemplates);
////        return userTemplateDTO.builder().residential(userTemplates.getResidential()).individual(userTemplates.getIndividual()).commercial(userTemplates.getCommercial()).nonProfit(userTemplates.getNonProfit()).build();
//    return  null;
//    }

    @Override
    public UserDTO getCaUserDetail(Long entityId) {
        String isChecked = "null";
        Entity entity = entityService.findById(entityId);
        List<LocationMapping> utilityLocationMappingList = null;
        List<PhysicalLocation> utilityPhysicalLocations = null;
        List<LocationMapping> userLocationMappingList = null;
        List<PhysicalLocation> userPhysicalLocations = null;
        List<LocationMapping> userPhysicalLocationsIsPrimary = null;
        List<CaUtilityDTO> caUtilityDTOList = new ArrayList<>();
        CaReferralInfoDTO caReferralInfoDTO = null;
        CaSoftCreditCheckDTO caSoftCreditCheckDTO = null;
        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.UserLevelPrivilegeByEntityId(entity.getId());
        EntityDetail entityDetail = null;
        if (userLevelPrivilege == null) {
            throw new NotFoundException(UserLevelPrivilege.class, entity.getId());
        }
        User user = findById(userLevelPrivilege.getUser().getAcctId());
        List<CaUtility> caUtilityList = caUtilityService.getByEntity(entity);
        CaReferralInfo caReferralInfo = caReferralInfoService.getByEntity(entity);
        CaSoftCreditCheck softCreditCheck = caSoftCreditCheckService.getByEntity(entity);
        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
        userLocationMappingList = locationMappingService.findBySourceId(user.getAcctId());
        entityDetail = entityDetailRepository.findByEntityId(entityId);

        if (userLocationMappingList != null && userLocationMappingList.size() > 0)
            userPhysicalLocations = physicalLocationService.getPhysicalLocationById(userLocationMappingList.stream().map(LocationMapping::getLocationId).collect(Collectors.toList()));
        if (caUtilityList != null && caUtilityList.size() > 0) {
            for (CaUtility caUtility : caUtilityList) {
                CaUtilityDTO caUtilityDTO = toCaUtilityDTO(caUtility);
                List<DocuLibrary> docuLibraryList = docuLibraryService.findByCodeRefId(String.valueOf(caUtilityDTO.getId()));
                utilityLocationMappingList = locationMappingService.findBySourceId(caUtility.getId());
                if (utilityLocationMappingList != null && utilityLocationMappingList.size() > 0)
                    utilityPhysicalLocations = physicalLocationService.getPhysicalLocationById(utilityLocationMappingList.stream().map(LocationMapping::getLocationId).collect(Collectors.toList()));
                caUtilityDTO.setCreatedAt(null);
                caUtilityDTO.setPhysicalLocations(utilityPhysicalLocations != null ? toPhysicalLocationDTOs(utilityPhysicalLocations) : null);
                List<String> urls = new ArrayList<>();
                for (DocuLibrary docu : docuLibraryList) {
                    urls.add(docu.getUri());
                }
                caUtilityDTO.setFileUrls(urls);
                caUtilityDTOList.add(caUtilityDTO);
            }
        }
        if (caReferralInfo != null) {
            caReferralInfoDTO = toCaReferralInfoDTO(caReferralInfo);
            if (caReferralInfoDTO.getRepresentativeId() != null) {
                Optional<EntityRole> entityRoleOptional = entityRoleRepository.findById(caReferralInfoDTO.getRepresentativeId());
                if (entityRoleOptional.isPresent()) {
                    EntityRole entityRole = entityRoleOptional.get();
                    if (entityRole.getFunctionalRoles() != null) {
                        if (entityRole.getEntity() != null) {
                            FunctionalRoles functionalRoles = entityRole.getFunctionalRoles();
                            Entity entityDB = entityRole.getEntity();
                            caReferralInfoDTO.setAgentDesignation(functionalRoles.getName());
                            caReferralInfoDTO.setAgentName(entityDB.getEntityName());
                        }
                    }
                }
            }
            if (caReferralInfoDTO.getAgentDesignation() == null || caReferralInfoDTO.getAgentName() == null) {
                caReferralInfoDTO.setAgentDesignation("Unassigned");
                caReferralInfoDTO.setAgentName(" ");
            }
        }
        if (softCreditCheck != null) {
            caSoftCreditCheckDTO = toCaSoftCreditCheck(softCreditCheck);
            isChecked = caSoftCreditCheckDTO.getIsCheckedLater() != null ? (caSoftCreditCheckDTO.getIsCheckedLater() == Boolean.FALSE ? "0" : "1") : "null";
        }
        UserDTO userDto = toUserDTO(user);
        userDto.setIsChecked(isChecked);
        userDto.setPhone(entity.getContactPersonPhone());
        if (customerDetail != null) {
            userDto.setCustomerType(customerDetail.getCustomerType());//individual / commercial
            userDto.setCustomerState(customerDetail.getStates()); //lead/ prospect
        }
        userDto.setEntityType(entity.getEntityType()); // customer, employee
        userDto.setEntityId(entity.getId());
        userDto.setOrganizationId(entity.getOrganization() != null ? entity.getOrganization().getId() : null);
        userDto.setCaUtility(caUtilityDTOList);
        userDto.setCaSoftCreditCheck(caSoftCreditCheckDTO);
        userDto.setCaReferralInfo(caReferralInfoDTO);
        userDto.setPhysicalLocations(userPhysicalLocations != null ? toPhysicalLocationDTOWithPrimary(userPhysicalLocations, userLocationMappingList) : null);
        if (entityDetail != null) {
            userDto.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
        }
        userDto.setCompKey(null);
        userDto.setRoles(null);
        userDto.setAddresses(null);
        userDto.setUserType(null);
        userDto.setCreatedAt(null);
        userDto.setUpdatedAt(null);
        userDto.setStatus(null);
        return userDto;
    }

    private User updateCaUser(UserDTO userDTO, Boolean sendForSigning) {
        /**
         * Validate email either already exist or not
         * If not exist already then process.
         */
        if (userDTO.getEmailAddress() == null) {
            throw new InvalidValueException("Email address must be provide");
        } else {
            Entity emailAddrExists = entityService.findByEmailAddressAndEntityType(userDTO.getEmailAddress(), userDTO.getEntityType()); // aaa  bbb ccc
            if (emailAddrExists == null) {
            } else if (emailAddrExists.getId().equals(userDTO.getEntityId())) {
            } else {
                throw new AlreadyExistsException("Email " + userDTO.getEmailAddress());
            }
        }
        User user = findById(userDTO.getAcctId());//findUserByEntityId(userDTO.getAcctId());
        user.setFirstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : user.getFirstName());
        user.setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : user.getLastName());
        user.setDataOfBirth(userDTO.getDataOfBirth() != null ? userDTO.getDataOfBirth() : user.getDataOfBirth());
        user.setEmailAddress(userDTO.getEmailAddress() != null ? userDTO.getEmailAddress() : user.getEmailAddress());

        Entity entity = entityService.findById(userDTO.getEntityId());//findEntityByUserId(user.getAcctId());
        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
        if (customerDetail.getStates().equals(EUserType.CUSTOMER.getName()) || user.getUserType().getName().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
        }
        if (customerDetail.getStates().equals(EUserType.CUSTOMER.getName()) || customerDetail.getStates().equals(EUserType.INTERIMCUSTOMER.getName()) || user.getUserType().getName().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
        } else if (!customerDetail.getStates().equals(ECustomerDetailStates.APPROVAL_PENDING.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.PROSPECT.getName());
        }
        if (sendForSigning != null && sendForSigning == true) {
            customerDetail.setStates(ECustomerDetailStates.INTERIMCUSTOMER.getName());
        }

        entity.setContactPersonPhone(userDTO.getPhone() != null ? userDTO.getPhone() : entity.getContactPersonPhone());
        entity.setContactPersonEmail(userDTO.getEmailAddress() != null ? userDTO.getEmailAddress() : entity.getContactPersonEmail());
        if (user.getFirstName() != null && user.getLastName() != null) {
            entity.setEntityName(user.getFirstName().concat(" ").concat(user.getLastName()));
        }

        user = userRepository.save(user);
        if (user.getUserName() == null) {
            user.setUserName("CA_NONE_" + user.getAcctId());
            userRepository.save(user);
        }
        entityService.save(entity);
        customerDetailService.save(customerDetail);

        if (userDTO.getPhysicalLocations() != null) {
            List<PhysicalLocation> physicalLocation = new ArrayList<>();
            physicalLocation.addAll(updateLocationToDefaultTypeAndCategory(userDTO.getPhysicalLocations(), Constants.LOCATION_CATEGORY_CONSTANTS.USER, Constants.LOCATION_TYPE.BILLING));
            physicalLocationRepository.saveAll(physicalLocation);
            locationMappingService.saveAll(getCaLocationMappings(physicalLocation, user, null, AppConstants.PRIMARY_INDEX_LOCATION_FALSE));
        }
        return user;
    }

    private Entity validateCustomerType(User user, UserDTO userDTO, Boolean sendForSigning) {
        Entity entity = entityService.findById(userDTO.getEntityId());//findEntityByUserId(user.getAcctId());
        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
        if (customerDetail.getStates().equals(EUserType.CUSTOMER.getName())
                || user.getUserType().getName().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
        }
        if (customerDetail.getStates().equals(EUserType.CUSTOMER.getName()) || customerDetail.getStates().equals(EUserType.INTERIMCUSTOMER.getName()) || user.getUserType().getName().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
        } else {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.PROSPECT.getName());
        }
        if (sendForSigning != null && sendForSigning == true) {
            customerDetail.setStates(ECustomerDetailStates.INTERIMCUSTOMER.getName());
        }
        entity.setContactPersonPhone(userDTO.getPhone() != null ? userDTO.getPhone() : entity.getContactPersonPhone());
        entity.setContactPersonEmail(userDTO.getEmailAddress() != null ? userDTO.getEmailAddress() : entity.getContactPersonEmail());
        if (user.getFirstName() != null && user.getLastName() != null) {
            entity.setEntityName(user.getFirstName().concat(" ").concat(user.getLastName()));
        }
        entityService.save(entity);
        customerDetailService.save(customerDetail);
        return entity;
    }

    private List<CaUtility> saveOrUpdateCaUtilityDTO(UserDTO userDTO, User user, List<MultipartFile> utilityMultipartFiles) {
        ArrayList<PhysicalLocation> physicalLocation = new ArrayList<>();
        List<CaUtility> caUtilityList = new ArrayList<>();
        List<CaUtilityDTO> caUtilityDTOs = userDTO.getCaUtility();
        for (CaUtilityDTO caUtilityDTO : caUtilityDTOs) {
            CaUtility caUtility = caUtilityService.save(caUtilityDTO, user, utilityMultipartFiles);
            if (caUtilityDTO.getPhysicalLocations() != null) {
                physicalLocation.addAll(updateLocationToDefaultTypeAndCategory(caUtilityDTO.getPhysicalLocations(), Constants.LOCATION_CATEGORY_CONSTANTS.UTILITY, Constants.LOCATION_TYPE.SITE));
                physicalLocationRepository.saveAll(physicalLocation);
                locationMappingService.saveAll(getCaLocationMappings(physicalLocation, user, caUtility, AppConstants.PRIMARY_INDEX_LOCATION_FALSE));
            }
            caUtilityList.add(caUtility);
        }
        return caUtilityList;
    }

    private User saveCaUser(UserDTO userDTO) {
        User user = null;
        List<PhysicalLocation> physicalLocation = new ArrayList<>();
        List<MultipartFile> userMultipartFiles = null;
        if ((userDTO.getEmailAddress() == null || userDTO.getEmailAddress().isEmpty()) ||
                (userDTO.getFirstName() == null || userDTO.getFirstName().isEmpty()) ||
                (userDTO.getLastName() == null || userDTO.getLastName().isEmpty()) ||
                (userDTO.getCustomerType() == null || userDTO.getCustomerType().isEmpty())) {
//            return null;
            // throw new NullPointerException("EmailAddress|FirstName|LastName|CustomerType can't be null");
            throw new AlreadyExistsException("You must provide these all <<< EmailAddress|FirstName|LastName|CustomerType >>> ");
        } else {
            if (userDTO.getEmailAddress() != null && entityService.findByEmailAddressAndEntityType(userDTO.getEmailAddress(), userDTO.getEntityType()) != null) {
                throw new AlreadyExistsException("Email " + userDTO.getEmailAddress() + " is already taken");
            }
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
            User finalUser = userRepository.save(user);

            if (finalUser.getAcctId() == null) {
                throw new NotFoundException("User not saved in DB");
            } else {
                finalUser.setUserName("CA_NONE_" + finalUser.getAcctId());
                userRepository.save(finalUser);
            }
            Entity entity = toEntity(userDTOtoEntity(userDTO, user, "Customer", EUserStatus.ACTIVE.getStatus(), true));
            entity.setContactPersonPhone(userDTO.getPhone());
            entity.setOrganization(organization);
            entityService.save(entity);
            String customerType = userDTO.getCustomerType();
            CustomerDetail customerDetail = toCustomerDetail(CustomerDetailDTO.builder().customerType(userDTO.getCustomerType())
                    .isContractSign(false).isActive(true).isCustomer(false).hasLogin(false).mobileAllowed(false).signUpDate(new Date())
                    .priorityIndicator(false).states(ECustomerDetailStates.LEAD.toString()).entityId(entity.getId()).status(EUserStatus.INACTIVE.getStatus()).build());
            customerDetail = customerDetailService.save(customerDetail);
            userLevelPrivilegeService.save(UserLevelPrivilege.builder()
                    .user(user)
                    .createdAt(userDTO.getCreatedAt())
                    .updatedAt(userDTO.getUpdatedAt())
                    .entity(entity)
                    .organization(organization)
                    .build());
            if (userDTO.getPhysicalLocations() != null) {
                physicalLocation.addAll(updateLocationToDefaultTypeAndCategory(userDTO.getPhysicalLocations(), Constants.LOCATION_CATEGORY_CONSTANTS.USER, Constants.LOCATION_TYPE.BILLING));
                physicalLocationRepository.saveAll(physicalLocation);
                locationMappingService.saveAll(getCaLocationMappings(physicalLocation, user, null, AppConstants.PRIMARY_INDEX_LOCATION_TRUE));
            }
            if (userDTO.getPhotoBase64() != null) {
                userMultipartFiles = new ArrayList<>();
                userMultipartFiles.add(convertPhotoBase64ToMultipart(userDTO.getPhotoBase64(), Constants.NEW_USER_CONSTANTS.PROFILE));
            }
            //this will upload user files
            doAttachmentToUser(userMultipartFiles, user, Constants.CUSTOMER_ACQ.CA_USER);
        }
        return user;
    }

    private Organization getOrganization(String status, Boolean ind, Long parentOrgId) {
        Organization organization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId(status, ind, parentOrgId);
        if (organization == null)
            throw new NotFoundException(Organization.class, "PrimaryIndicator,Status", "true, active");
        return organization;
    }

    private List<DocuLibrary> doAttachmentToUser(List<MultipartFile> multipartFiles, User user, String businessInfoPath) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
        List<DocuLibrary> docuLibraryList = new ArrayList<>();
        DocuLibrary docuLibrary = null;
        String directoryPath = "tenant/" + utility.getCompKey() + AppConstants.REGISTER_NEW_USER_PATH + user.getAcctId() + "/" + businessInfoPath;
        try {
            if (multipartFiles != null && !multipartFiles.isEmpty()) {
                for (MultipartFile multipartFile : multipartFiles) {
                    if (multipartFile != null && !multipartFile.isEmpty()) {
                        //if not profile then take original name
                        String originalFileName = multipartFile.getOriginalFilename();

                        //for profile photo adding /PROFILE directory in path
                        if (multipartFile.getName().equalsIgnoreCase(Constants.NEW_USER_CONSTANTS.PROFILE)) {
                            directoryPath += AppConstants.REGISTER_NEW_USER_PROFILE_PATH;
                            originalFileName = multipartFile.getName();
                        }      //for business logo adding /BUSINESS directory in path
                        else if (multipartFile.getName().equalsIgnoreCase(Constants.NEW_USER_CONSTANTS.BUSINESS_LOGO)) {
                            directoryPath += AppConstants.REGISTER_NEW_USER_BUSINESS_LOGO_PATH;
                            originalFileName = multipartFile.getName();
                        }
                        String uri = storageService.storeInContainer(multipartFile, appProfile, directoryPath,
                                timeStamp + "-" + originalFileName, utility.getCompKey(), false);
                        docuLibrary = docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                                .docuName(multipartFile.getOriginalFilename())
                                .uri(uri)
                                .docuType(multipartFile.getContentType())
                                .visibilityKey(true)
                                .codeRefType(Constants.NEW_USER_CONSTANTS.REGISTER_NEW_USER)
                                .codeRefId(String.valueOf(user.getAcctId()))
                                .referenceTime(timeStamp)
                                .build());
                        if (docuLibrary.getDocuId() != null)
                            docuLibraryList.add(docuLibrary);
                    }
                }
            }
        } catch (URISyntaxException | StorageException | IOException e) {
            LOGGER.error(e.getMessage());
        }
        return docuLibraryList;
    }

    private MultipartFile convertPhotoBase64ToMultipart(String base64String, String fileName) {
        MockMultipartFile mockMultipartFile = null;
        try {
            byte[] decoderBytes = Base64.getDecoder().decode(base64String);
            mockMultipartFile = new MockMultipartFile(fileName, decoderBytes);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
//            throw new IllegalArgumentException("ILegalBase64"+ fileName);
        }
        return mockMultipartFile;
    }

    private String convertMultipartToBase64(File file) {
        String base64 = null;
        try {
            byte[] content = Files.readAllBytes(file.toPath());
            base64 = Base64.getEncoder().encodeToString(content);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return base64;
    }

    private String getEntityType(String customerType) {
        if (customerType.equalsIgnoreCase(EEntityType.COMMERCIAL.getEntityType())) {
            return EEntityType.COMMERCIAL.getEntityType();
        } else if (customerType.equalsIgnoreCase(EEntityType.INDIVIDUAL.getEntityType())) {
            return EEntityType.INDIVIDUAL.getEntityType();
        } else if (customerType.equalsIgnoreCase(EEntityType.BUSINESS.getEntityType())) {
            return EEntityType.BUSINESS.getEntityType();
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

    private List<LocationMapping> getLocationMappings(List<PhysicalLocation> physicalLocations, User user, Entity entity) {
        List<LocationMapping> locationMappings = new ArrayList<>();
        physicalLocations.forEach(loc -> {
            String primaryInd = null;
            if (loc.getId() == null) throw new NotFoundException("Physical location not saved in DB");
            Long sourceId = null;
            if (loc.getCategory().equalsIgnoreCase(Constants.LOCATION_CATEGORY_CONSTANTS.ENTITY))
                sourceId = entity.getId();
            else if (loc.getCategory().equalsIgnoreCase(Constants.LOCATION_CATEGORY_CONSTANTS.USER)) {
                sourceId = user.getAcctId();
                primaryInd = "Y";
            }
            locationMappings.add(LocationMapping.builder()
                    .locationId(loc.getId())
                    .sourceType(loc.getCategory())
                    .sourceId(sourceId)
                    .primaryInd(primaryInd).build());
        });
        return locationMappings;
    }

    private List<LocationMapping> getCaLocationMappings(List<PhysicalLocation> physicalLocations, User user, CaUtility utility, String primaryInd) {
        List<LocationMapping> locationMappings = new ArrayList<>();
        physicalLocations.forEach(loc -> {
            String primaryIndDB = primaryInd;
            Long sourceId = null;
            String sourceType = null;
            Long locId = null;
            Long locMappingId = null;
            if (loc.getId() == null) throw new NotFoundException("Physical location not saved in DB");
            if (loc.getCategory().equalsIgnoreCase(Constants.LOCATION_CATEGORY_CONSTANTS.UTILITY)) {
                sourceId = utility.getId();
                primaryIndDB = AppConstants.PRIMARY_INDEX_LOCATION_TRUE;
            }
            if (loc.getCategory().equalsIgnoreCase(Constants.LOCATION_CATEGORY_CONSTANTS.USER)) {
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
        List<LocationMapping> primaryUserLocations = locationMappingService.findAllBySourceIdAndSourceTypeAndPrimaryInd(user.getAcctId(), Constants.LOCATION_CATEGORY_CONSTANTS.USER, AppConstants.PRIMARY_INDEX_LOCATION_TRUE);
        if (primaryUserLocations.size() == 0) {
            Optional<LocationMapping> firstUserLocationMapping = locationMappings.stream()
                    .filter(mapping -> Constants.LOCATION_CATEGORY_CONSTANTS.USER.equals(mapping.getSourceType()))
                    .findFirst();

            if (firstUserLocationMapping.isPresent()) {
                LocationMapping userMapping = firstUserLocationMapping.get();
                userMapping.setPrimaryInd(AppConstants.PRIMARY_INDEX_LOCATION_TRUE);
            }
        }
        return locationMappings;
    }

    @Override
    public UserDTO createOrUpdateUserForEntity(RegistrationData registrationData, Account account, Entity entity, EmployeeManagementDTO employeeManagementDTO) {
        UserDTO userDTO = null;
        if (registrationData.getUser().getAcctId() != null) {

        } else {
            UserDTO signupUser = registrationData.getUser();
            User userNameExists = findByUserName(signupUser.getUserName());
            if (userNameExists != null) {
                throw new AlreadyExistsException(signupUser.getUserName());
            }
            User user = UserMapper.toUser(signupUser);
            user.setPassword(encoder.encode(signupUser.getPassword()));

            // Set UserType
            if (signupUser.getUserType() != null) {
                UserType userType = userTypeService.findByName(EUserType.get(signupUser.getUserType()));
                user.setUserType(userType);
            }
            userDTO = UserMapper.toUserAndSubscriptionsDTO(saveOrUpdateForEntity(user));

            if (userDTO != null) {
                //creating account against user
                account.setUser(UserMapper.toUser(userDTO));
                Account acctCreated = accountService.add(account, null, null);
                if (acctCreated != null) {
                    //saving single value
                    List<UserLevelPrivilege> userLevelPrivileges = userLevelPrivilegeService.add(null, UserLevelPrivilegeWrapperDTO.builder()
                            .accountId(acctCreated.getId())
                            .organizationIds(Arrays.asList(1l))
                            .entityIds(Arrays.asList(entity.getId())).build());
                }
            }
        }
        return userDTO;
    }

    @Override
    public User saveOrUpdateForEntity(User user) {
        user.setStatus(EUserStatus.ACTIVE.getStatus());
        Date date = new Date();
        user.setActiveDate(date);
        user.setRegisterDate(date);
        user = userRepository.save(user);
        User finalUser = user;
        return userRepository.save(finalUser);
    }

    @Override
    public boolean isValidateUserName(String userName) {
        if (findByUserName(userName) != null) {
            return true;
        }
        return false;
    }

    @Override
    public List<UserRoleTemplateDTO> getAllUsersByCustomerType(String customerType) {
        return userRepository.findAllUsersByCustomerType(customerType);
    }

    @Override
    public void callbackDocuSign(ZResponse zResponse) {
        try {
            ZRequests request = zResponse.getRequests();
            String requestId = request.getRequestId();
            Optional<SigningRequestTracker> signingRequestTrackerOptional = signingRequestTrackerRepository.findByExtRequestId(requestId);
            if (signingRequestTrackerOptional.isPresent()) {
                String accessToken = dataExchangeDocuSign.getAccessTokenViaRefreshToken().getAccessToken();
                CreateTemplateResponse createTemplateResponse = dataExchangeDocuSign.getDocumentDetails(requestId, accessToken);
                String notes = createTemplateResponse.getRequests().getNotes();
                MasterTenant masterTenant = masterTenantService.findByCompanyKey(Long.valueOf(notes));
                DBContextHolder.setTenantName(masterTenant.getDbName());
                SigningRequestTracker requestTracker = signingRequestTrackerOptional.get();
                Notifications notifications = zResponse.getNotifications();
                if ("inprogress".equals(request.getRequestStatus())
                        && notifications != null
                        && "RequestSubmitted".equals(notifications.getOperationType())
                        && request.getActions().get(0).getActionId() != null) {
                    saveExternalCallBackLog(requestTracker, request, "NEW");
                } else if ("completed".equals(request.getRequestStatus())
                        && notifications != null
                        && "RequestCompleted".equals(notifications.getOperationType())
                        && request.getActions().get(0).getActionId() != null) {
                    handleCompletedCallback(requestTracker, request, notifications, requestId, accessToken,
                            createTemplateResponse, notes);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void handleCompletedCallback(SigningRequestTracker requestTracker, ZRequests request,
                                         Notifications notifications, String requestId, String accessToken,
                                         CreateTemplateResponse createTemplateResponse, String notes)
            throws URISyntaxException, StorageException {
        requestTracker.setStatus(EContractStatus.COMPLETED.getName());
        SigningRequestTracker updatedRequestTracker = signingRequestTrackerRepository.save(requestTracker);
        Entity entity = requestTracker.getEntity();
        List<UserLevelPrivilege> userLevelPrivileges = entity.getUserLevelPrivileges();
        UserLevelPrivilege priv = userLevelPrivileges.stream()
                .filter(p -> p.getEntity().getId().equals(entity.getId()))
                .findFirst().orElse(null);
        if (priv != null) {
            activateCustomer(priv.getUser(), entity);
            saveExternalCallBackLog(requestTracker, request, "APPLIED");
            String requestName = createTemplateResponse.getRequests().getRequestName();
            docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                    .organization(entity.getOrganization())
                    .entity(entity)
                    .docuType("application/pdf")
                    .codeRefType("SIGNREQ")
                    .codeRefId(String.valueOf(requestTracker.getId()))
                    .docuName(requestName)
                    .visibilityKey(true)
                    .uri(getUploadedDocumentUrl(requestId, accessToken, requestName, notes))
                    .build());
        }
    }

    private void activateCustomer(User user, Entity entity) {
        //User user = account.getUser();
        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
//        user.setStatus(EUserStatus.ACTIVE.getStatus());
//        user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
        user.setAuthentication(EAuthenticationType.STANDARD.getName());
        saveUser(user);
        customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
        customerDetail.setCustomer(true);
        customerDetail.setStatus(EUserStatus.ACTIVE.getStatus());
        customerDetail.setContractSign(true);
        entity.setIsDocAttached(true);
        entityService.save(entity);
        //  customerDetailService.save(customerDetail);
    }

    private String getUploadedDocumentUrl(String requestId, String accessToken, String requestName, String notes)
            throws URISyntaxException, StorageException {
        byte[] bytes = dataExchangeDocuSign.getDocumentPDF(requestId, accessToken);
        return storageService.uploadByteArray(bytes, appProfile, "tenant/docuSign/" + notes,
                requestName);
    }

    private void saveExternalCallBackLog(SigningRequestTracker requestTracker, ZRequests request, String status) {
        requestTracker.setStatus(EContractStatus.SIGNED_PENDING.getName());
        SigningRequestTracker updatedSigningRequestTracker = signingRequestTrackerRepository.save(requestTracker);
        externalCallBackLogRepository.save(ExternalCallBackLog.builder()
                .refCode("SIGNREQ")
                .refId(requestTracker)
                .extRequestId(requestTracker.getExtRequestId()) // id for our request from Zoho
                .callBackId(request.getZsdocumentid()) // id send by Zoho (if available)
                .dateTime(new Date(request.getActionTime()))
                .status(status) // NEW > when received. APPLIED > when document is received asa PDF and saved in Docu Library
                .build());
    }

    @Override
    public User findUserByEntityId(Long entityId) {
        Entity entityData = entityService.findById(entityId);
        // Account account = null;
        if (entityData != null) {
            List<UserLevelPrivilege> privileges = userLevelPrivilegeService.UserLevelPrivilegeByEntity(entityData);
            if (privileges != null && privileges.size() > 0) {
                UserLevelPrivilege userLevelPrivilege = privileges.stream().findFirst().get();
                User user = userLevelPrivilege.getUser();
                return user;
            }
        } else {
            throw new NotFoundException("Can not find entity with id =" + entityData);
        }
        return null;
    }

    @Override
    public List<UtilityInformationTile> getCaUtilityByEntity(Long entityId) {
        UserDTO userDTO = getCaUserDetail(entityId);
        PortalAttributeValueTenantDTO portalAttributeValueTenantDTO = null;
        List<UtilityInformationTile> utilityInformationTiles = new ArrayList<>();
        for (CaUtilityDTO caUtilityDTO : userDTO.getCaUtility()) {
            if (caUtilityDTO != null && caUtilityDTO.getUtilityProviderId() != null) {
                portalAttributeValueTenantDTO =
                        portalAttributeOverrideService.findPortalAttributeValueById(caUtilityDTO.getUtilityProviderId());
                PhysicalLocation physicalLocation = getUtilityAddress(caUtilityDTO.getId());
                UtilityInformationTile utilityInformationTile = UtilityInformationTile.builder()
                        .caUtility(caUtilityDTO.getId())
                        .entityId(entityId)
                        .accountHolderName(caUtilityDTO.getAccountHolderName() != null ? caUtilityDTO.getAccountHolderName() : null)
                        .utilityAccountAddress(physicalLocation != null ? physicalLocation.getAdd1() != null ? physicalLocation.getAdd1() : null : null)
                        .utilityProvider(portalAttributeValueTenantDTO.getDescription())
                        .premiseNumber(caUtilityDTO.getPremise() != null ? caUtilityDTO.getPremise() : null)
                        .averageMonthlyBill(caUtilityDTO.getAverageMonthlyBill() != null ? caUtilityDTO.getAverageMonthlyBill() : null)
                        .build();
                utilityInformationTiles.add(utilityInformationTile);
            }
        }
        return utilityInformationTiles;
    }

    private PhysicalLocation getUtilityAddress(Long caUtilityId) {
        LocationMapping locationMapping =
                locationMappingService.findBySourceIdAndSourceTypeAndPrimaryInd(caUtilityId, Constants.LOCATION_CATEGORY_CONSTANTS.UTILITY, "Y");
        if (locationMapping != null) {
            return physicalLocationService.findPhysicalLocationById(locationMapping.getLocationId());
        } else {
            return null;
        }
    }


    @Override
    public com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse getAllCustomerList(String groupBy, String groupByName, Integer size, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, size);
        CustomerManagementPaginationTile result = new CustomerManagementPaginationTile();
        try {
            CustomerManagementGroupBy groupByType = CustomerManagementGroupBy.get(groupBy);
            Page<UserTemplate> userTemplates = getCustomerGroupByResult(groupBy, groupByName, groupByType, pageable);
            result.setTotalPages(userTemplates.getTotalPages());
            result.setTotalElements(userTemplates.getTotalElements());
            List<CustomerManagementTile> data = (!groupBy.equalsIgnoreCase(groupByType.NONE.getType()) && groupByName == null) ? CustomerManagementTileMapper.toCustomerManagementTilesGroupBy(userTemplates.getContent()) : CustomerManagementTileMapper.toCustomerManagementTiles(userTemplates.getContent());

            result.setGroupBy(groupBy);
            result.setData(data);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse.builder().code(HttpStatus.OK.value()).message(AppConstants.DATA_FOUND_SUCCESSFULLY).data(result).build();
    }

    private Page<UserTemplate> getCustomerGroupByResult(String groupBy, String groupByName, CustomerManagementGroupBy groupByType, Pageable pageable) {
        Page<UserTemplate> userTemplates = null;

        if (!groupBy.equalsIgnoreCase(CustomerManagementGroupBy.NONE.getType()) && groupByName != null) {
            userTemplates = userRepository.getAllCustomerLists(true, groupBy, groupByName, pageable);
        } else {
            switch (groupByType) {
                case NONE:
                    userTemplates = userRepository.getAllCustomerLists(false, null, null, pageable);
                    break;
                case CUSTOMER_TYPE:
                    userTemplates = userRepository.getAllCustomerTypeList(false, pageable);
                    break;
                case REGION:
                    userTemplates = userRepository.getAllCustomerRegionList(false, pageable);
                    break;
                case SOURCE:
                    userTemplates = userRepository.getAllCustomerSourceList(false, pageable);
                    break;
            }
        }
        return userTemplates;
    }

    @Override
    public CaUserTemplateDTO getCustomerSaleAgentByEntityId(String entityId) {
        return null;
    }

    //TODO NEED TO REMOVE
    @Override
    public List<CaUserTemplateDTO> getAllCustomerByTypes(List<String> customerType) {
        SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        List<UserTemplate> userTemplates = userRepository.findAllCustomerByType(customerType);
//        String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllSubscriptionDetails";
//                Arrays.asList(EUserType.RESIDENTIAL.getName(),EUserType.COMMERCIAL.getName(),EUserType.NONPROFIT.getName()));
        userTemplates.sort(Comparator.comparing(UserTemplate::getGeneratedAt).reversed());
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        EntityDetail entityDetail;
        CaUserTemplateDTO userTemplateDTO;
        Optional<DocuLibrary> docuLibraryOptional;
        //getting sub count here
        Map subCountData = getAllCustomerListSubscriptionSize();
        Map<String, Long> subCount = (Map<String, Long>) subCountData.get("data");
        Map subscriptionData = getAllCustomerListSubscription();
        Map<String, List<MongoCustomerDetailWoDTO>> subscriptionDataId = (Map<String, List<MongoCustomerDetailWoDTO>>) subscriptionData.get("data");
        for (UserTemplate userTemplate : userTemplates) {
            userTemplateDTO = caUserTemplateDTO(userTemplate);
            //add profileUrl
            entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
            if (entityDetail != null) {
                userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
            }
            docuLibraryOptional = docuLibraryService.findByCodeRefId(String.valueOf(userTemplate.getAccountId())).stream().findFirst();

            if (docuLibraryOptional.isPresent()) {
                // String profileUrl = storageService.getBlobUrl(storageContainer, docuLibraryOptional.get().getUri());
                userTemplateDTO.setProfileUrl(docuLibraryOptional.get().getUri());
            }
            userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));
            Optional<Long> firstCount = subCount.entrySet().stream()
                    .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
                    .map(e -> e.getValue())
                    .findFirst();
            if (firstCount.isPresent()) {
                userTemplateDTO.setSubscriptionTotal(String.valueOf(firstCount.get()));
            }
            userTemplateDTO.setMongoCustomerDetailWoDTO(subscriptionDataId.get(String.valueOf(userTemplateDTO.getAccountId())));
            userTemplateDtoList.add(userTemplateDTO);
        }
        return userTemplateDtoList;
    }

    //TODO NEED TO REMOVE
    @Override
    public List<CaUserTemplateDTO> getAllCustomerListByProject(List<String> projectType) {
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        if (projectType.size() > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
//            List<UserTemplate> userTemplates = userRepository.getAllCustomerLists(Arrays.asList(ECustomerDetailStates.CUSTOMER.getName()));
//            userTemplates.sort(Comparator.comparing(UserTemplate::getGeneratedAt).reversed());
            EntityDetail entityDetail;
            CaUserTemplateDTO userTemplateDTO;
            Optional<DocuLibrary> docuLibraryOptional;
            //getting sub count here
            Map subCountData = getAllCustomerListSubscriptionSize();
            Map<String, Long> subCount = (Map<String, Long>) subCountData.get("data");
            Map subscriptionData = getAllCustomerListSubscriptionByProject(projectType);
            Map<String, List<MongoCustomerDetailWoDTO>> subscriptionDataId = (Map<String, List<MongoCustomerDetailWoDTO>>) subscriptionData.get("data");
            if (subscriptionDataId.size() > 0) {
//                for (UserTemplate userTemplate : userTemplates) {
//                    userTemplateDTO = caUserTemplateDTO(userTemplate);
//                    //add profileUrl
//                    entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
//                    if (entityDetail != null) {
//                        userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
//                    }
//                    docuLibraryOptional = docuLibraryService.findByCodeRefId(String.valueOf(userTemplate.getAccountId())).stream().findFirst();
//                    if (docuLibraryOptional.isPresent()) {
//                        // String profileUrl = storageService.getBlobUrl(storageContainer, docuLibraryOptional.get().getUri());
//                        userTemplateDTO.setProfileUrl(docuLibraryOptional.get().getUri());
//                    }
//                    userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));
//                    Optional<Long> firstCount = subCount.entrySet().stream()
//                            .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
//                            .map(e -> e.getValue())
//                            .findFirst();
//                    Optional<List<MongoCustomerDetailWoDTO>> subscriptionId = subscriptionDataId.entrySet().stream()
//                            .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
//                            .map(e -> e.getValue())
//                            .findFirst();
//                    if (firstCount.isPresent()) {
//                        userTemplateDTO.setSubscriptionTotal(String.valueOf(firstCount.get()));
//                    }
//                    userTemplateDTO.setMongoCustomerDetailWoDTO(subscriptionDataId.get(String.valueOf(userTemplateDTO.getAccountId())));
//                    userTemplateDtoList.add(userTemplateDTO);
//                }
            }
        }

        return userTemplateDtoList;
    }

    //TODO NEED TO REMOVE
    @Override
    public List<CaUserTemplateDTO> getAllCustomerListByProjectGroup() {
        SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
//        List<UserTemplate> userTemplates = userRepository.getAllCustomerLists(Arrays.asList(ECustomerDetailStates.CUSTOMER.getName()));
//        userTemplates.sort(Comparator.comparing(UserTemplate::getGeneratedAt).reversed());
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        EntityDetail entityDetail;
        CaUserTemplateDTO userTemplateDTO;
        Optional<DocuLibrary> docuLibraryOptional;
        //getting sub count here
        Map subCountData = getAllCustomerListSubscriptionSize();
        Map<String, Long> subCount = (Map<String, Long>) subCountData.get("data");
        Map subscriptionData = getAllCustomerListSubscriptionByProjectGroup();
        Map<String, List<MongoCustomerDetailWoDTO>> subscriptionDataId = (Map<String, List<MongoCustomerDetailWoDTO>>) subscriptionData.get("data");
//        for (UserTemplate userTemplate : userTemplates) {
//            userTemplateDTO = caUserTemplateDTO(userTemplate);
//            //add profileUrl
//            entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
//            if (entityDetail != null) {
//                userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
//            }
//            docuLibraryOptional = docuLibraryService.findByCodeRefId(String.valueOf(userTemplate.getAccountId())).stream().findFirst();
//            if (docuLibraryOptional.isPresent()) {
//                // String profileUrl = storageService.getBlobUrl(storageContainer, docuLibraryOptional.get().getUri());
//                userTemplateDTO.setProfileUrl(docuLibraryOptional.get().getUri());
//            }
//            userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));
//            Optional<Long> firstCount = subCount.entrySet().stream()
//                    .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
//                    .map(e -> e.getValue())
//                    .findFirst();
//            Optional<List<MongoCustomerDetailWoDTO>> subscriptionId = subscriptionDataId.entrySet().stream()
//                    .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
//                    .map(e -> e.getValue())
//                    .findFirst();
//            if (firstCount.isPresent()) {
//                userTemplateDTO.setSubscriptionTotal(String.valueOf(firstCount.get()));
//            }
//            userTemplateDTO.setMongoCustomerDetailWoDTO(subscriptionDataId.get(String.valueOf(userTemplateDTO.getAccountId())));
//            if (subscriptionDataId.containsKey(String.valueOf(userTemplateDTO.getAccountId()))) {
//                Map<String, List<MongoCustomerDetailWoDTO>> variantNameMap = subscriptionDataId.get(String.valueOf(userTemplateDTO.getAccountId()))
//                        .stream().collect(Collectors.groupingBy(MongoCustomerDetailWoDTO::getSubAlias));
//                for (String name : variantNameMap.keySet()) {
//                    CaUserTemplateDTO temp = new CaUserTemplateDTO(userTemplateDTO);
//
//                    temp.setProject(name);
//                    userTemplateDtoList.add(temp);
//                    userTemplateDtoList.size();
//                }
//            } else {
//                userTemplateDTO.setProject("No Subscription");
//                userTemplateDtoList.add(userTemplateDTO);
//            }
//        }
        return userTemplateDtoList;
    }
//TODO NEED TO REMOVE

    @Override
    public List<CaUserTemplateDTO> getAllCustomerByTypesGroup() {
        SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        List<UserTemplate> userTemplates = userRepository.findAllCustomerByTypeGroup();
        String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllProductName";
//                Arrays.asList(EUserType.RESIDENTIAL.getName(),EUserType.COMMERCIAL.getName(),EUserType.NONPROFIT.getName()));
        userTemplates.sort(Comparator.comparing(UserTemplate::getGeneratedAt).reversed());
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        EntityDetail entityDetail;
        CaUserTemplateDTO userTemplateDTO;
        Optional<DocuLibrary> docuLibraryOptional;
        //getting sub count here
        Map subCountData = getAllCustomerListSubscriptionSize();
        Map<String, Long> subCount = (Map<String, Long>) subCountData.get("data");
        Map subscriptionData = getAllCustomerListSubscription();
        Map<String, List<MongoCustomerDetailWoDTO>> subscriptionDataId = (Map<String, List<MongoCustomerDetailWoDTO>>) subscriptionData.get("data");
        for (UserTemplate userTemplate : userTemplates) {
            userTemplateDTO = caUserTemplateDTO(userTemplate);
            //add profileUrl
            entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
            if (entityDetail != null) {
                userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
            }
            docuLibraryOptional = docuLibraryService.findByCodeRefId(String.valueOf(userTemplate.getAccountId())).stream().findFirst();

            if (docuLibraryOptional.isPresent()) {
                // String profileUrl = storageService.getBlobUrl(storageContainer, docuLibraryOptional.get().getUri());
                userTemplateDTO.setProfileUrl(docuLibraryOptional.get().getUri());
            }
            userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));
            Optional<Long> firstCount = subCount.entrySet().stream()
                    .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
                    .map(e -> e.getValue())
                    .findFirst();
            if (firstCount.isPresent()) {
                userTemplateDTO.setSubscriptionTotal(String.valueOf(firstCount.get()));
            }
            userTemplateDTO.setMongoCustomerDetailWoDTO(subscriptionDataId.get(String.valueOf(userTemplateDTO.getAccountId())));
            userTemplateDtoList.add(userTemplateDTO);
        }
        return userTemplateDtoList;
    }

//    @Override
//    public List<UserRoleTemplateDTO> getAllCustomerByTypes(String customerType)  {
//        return userRepository.findAllCustomerByType(customerType);
//    }

    @Override
    public Map getAllCustomerListSubscriptionSize() {
        List<String> gardenIdList = oragnizationDetailRepository.getAllGarden();
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map<String, Long> subscriptionsCount = new HashMap<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        if (gardenIdList != null) {
            String ids = String.join(",", gardenIdList);
            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllGardensSubscription";
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Collections.singletonList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());
                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                List<Long> accountIds = mongoDataList.stream().map(x -> x.getAccountId() != null ? Long.parseLong(x.getAccountId()) : 0).collect(Collectors.toList());
                List<UserSubscriptionTemplateWoDTO> userDetailList = userRepository.findUsersByAccountId(accountIds);
                for (UserSubscriptionTemplateWoDTO userDetailSql : userDetailList) {
                    List<String> subscriptions = new ArrayList<>();
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), null);
                    for (MongoCustomerDetailWoDTO mongoData : mongoDataList) {
                        if (mongoData.getAccountId() != null && Long.parseLong(mongoData.getAccountId()) == userDetailSql.getAccountId()) {
                            if (subscriptionsCount.containsKey(mongoData.getAccountId())) {
                                subscriptions.add(mongoData.getSubId());
                            }
                            mongoData.setFirstName(userDetailSql.getFirstName());
                            mongoData.setLastName(userDetailSql.getFirstName());
                            mongoData.setCustomerType(userDetailSql.getCustomerType());
                            mongoData.setEntityId(userDetailSql.getEntityId());
                            mongoData.setEntityName(userDetailSql.getEntityName());
                            mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                            mongoDataResults.add(mongoData);
                        }
                    }
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions.stream().count());
//                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
                }
            }


        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "Subscriptions not found");
        }


        response.put("data", subscriptionsCount);
//        response.put("data", mongoDataResults.stream().collect(
//                Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
        return response;
    }

    public Map getAllCustomerListSubscriptionByProject(List<String> projectType) {
        List<String> gardenIdList = oragnizationDetailRepository.getAllGarden();
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map<String, List<String>> subscriptionsCount = new HashMap<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        if (gardenIdList != null) {
            String ids = String.join(",", projectType);
//            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllGardensSubscription";
            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllSubscriptionDetailsByProject?productType=" + ids;
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Collections.singletonList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());
                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                List<Long> accountIds = mongoDataList.stream().map(x -> x.getAccountId() != null ? Long.parseLong(x.getAccountId()) : 0).collect(Collectors.toList());
                List<UserSubscriptionTemplateWoDTO> userDetailList = userRepository.findUsersByAccountId(accountIds);
                for (UserSubscriptionTemplateWoDTO userDetailSql : userDetailList) {
                    List<String> subscriptions = new ArrayList<>();
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), null);
                    for (MongoCustomerDetailWoDTO mongoData : mongoDataList) {
                        if (mongoData.getAccountId() != null && Long.parseLong(mongoData.getAccountId()) == userDetailSql.getAccountId()) {
                            if (subscriptionsCount.containsKey(mongoData.getAccountId())) {
                                subscriptions.add(mongoData.getSubId());
                                subscriptions.add(mongoData.getVariantId());
                                subscriptions.add(mongoData.getSubName());
                                subscriptions.add(mongoData.getSubAlias());
                                subscriptions.add(mongoData.getVariantName());
                                subscriptions.add(mongoData.getDefaultValue());
                                subscriptions.add(mongoData.getSubValueCN());
                            }
                            mongoData.setFirstName(userDetailSql.getFirstName());
                            mongoData.setLastName(userDetailSql.getFirstName());
                            mongoData.setCustomerType(userDetailSql.getCustomerType());
                            mongoData.setEntityId(userDetailSql.getEntityId());
                            mongoData.setEntityName(userDetailSql.getEntityName());
                            mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                            mongoDataResults.add(mongoData);
                        }
                    }
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
//                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
                }
            }


        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "Subscriptions not found");
        }


//        response.put("data", subscriptionsCount);
        response.put("data", mongoDataResults.stream().collect(
                Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
        return response;
    }


    public Map getAllCustomerListSubscriptionByProjectGroup() {
        List<String> gardenIdList = oragnizationDetailRepository.getAllGarden();
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map<String, List<String>> subscriptionsCount = new HashMap<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        if (gardenIdList != null) {
            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllSubscriptionDetailsByProjectGroup";
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Collections.singletonList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());
                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                List<Long> accountIds = mongoDataList.stream().map(x -> x.getAccountId() != null ? Long.parseLong(x.getAccountId()) : 0).collect(Collectors.toList());
                List<UserSubscriptionTemplateWoDTO> userDetailList = userRepository.findUsersByAccountId(accountIds);
                for (UserSubscriptionTemplateWoDTO userDetailSql : userDetailList) {
                    List<String> subscriptions = new ArrayList<>();
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), null);
                    for (MongoCustomerDetailWoDTO mongoData : mongoDataList) {
                        if (mongoData.getAccountId() != null && Long.parseLong(mongoData.getAccountId()) == userDetailSql.getAccountId()) {
                            if (subscriptionsCount.containsKey(mongoData.getAccountId())) {
                                subscriptions.add(mongoData.getSubId());
                                subscriptions.add(mongoData.getVariantId());
                                subscriptions.add(mongoData.getSubName());
                                subscriptions.add(mongoData.getSubAlias());
                                subscriptions.add(mongoData.getVariantName());
                                subscriptions.add(mongoData.getDefaultValue());
                                subscriptions.add(mongoData.getSubValueCN());
                            }
                            mongoData.setFirstName(userDetailSql.getFirstName());
                            mongoData.setLastName(userDetailSql.getFirstName());
                            mongoData.setCustomerType(userDetailSql.getCustomerType());
                            mongoData.setEntityId(userDetailSql.getEntityId());
                            mongoData.setEntityName(userDetailSql.getEntityName());
                            mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                            mongoDataResults.add(mongoData);
                        }
                    }
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
//                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
                }

            }


        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "Subscriptions not found");
        }


//        response.put("data", subscriptionsCount);
        response.put("data", mongoDataResults.stream().collect(
                Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
        return response;
    }


    @Override
    public Map getAllCustomerListSubscription() {
        List<String> gardenIdList = oragnizationDetailRepository.getAllGarden();
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map<String, List<String>> subscriptionsCount = new HashMap<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        if (gardenIdList != null) {
            String ids = String.join(",", gardenIdList);
//            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllGardensSubscription";
            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllSubscriptionDetails";
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Collections.singletonList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());
                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                List<Long> accountIds = mongoDataList.stream().map(x -> x.getAccountId() != null ? Long.parseLong(x.getAccountId()) : 0).collect(Collectors.toList());
                List<UserSubscriptionTemplateWoDTO> userDetailList = userRepository.findUsersByAccountId(accountIds);
                for (UserSubscriptionTemplateWoDTO userDetailSql : userDetailList) {
                    List<String> subscriptions = new ArrayList<>();
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), null);
                    for (MongoCustomerDetailWoDTO mongoData : mongoDataList) {
                        if (mongoData.getAccountId() != null && Long.parseLong(mongoData.getAccountId()) == userDetailSql.getAccountId()) {
                            if (subscriptionsCount.containsKey(mongoData.getAccountId())) {
                                subscriptions.add(mongoData.getSubId());
                                subscriptions.add(mongoData.getVariantId());
                                subscriptions.add(mongoData.getSubName());
                                subscriptions.add(mongoData.getSubAlias());
                                subscriptions.add(mongoData.getVariantName());
                                subscriptions.add(mongoData.getDefaultValue());
                                subscriptions.add(mongoData.getSubValueCN());
                            }
                            mongoData.setFirstName(userDetailSql.getFirstName());
                            mongoData.setLastName(userDetailSql.getFirstName());
                            mongoData.setCustomerType(userDetailSql.getCustomerType());
                            mongoData.setEntityId(userDetailSql.getEntityId());
                            mongoData.setEntityName(userDetailSql.getEntityName());
                            mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                            mongoDataResults.add(mongoData);
                        }
                    }
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
//                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
                }

            }


        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "Subscriptions not found");
        }


//        response.put("data", subscriptionsCount);
        response.put("data", mongoDataResults.stream().collect(
                Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
        return response;
    }

    @Override
    public Map getAllCustomerVarientId() {
        List<String> gardenIdList = oragnizationDetailRepository.getAllGarden();
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map<String, List<String>> subscriptionsCount = new HashMap<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        if (gardenIdList != null) {
            String ids = String.join(",", gardenIdList);
            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllGardensSubscription";
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Collections.singletonList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());
                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                List<Long> accountIds = mongoDataList.stream().map(x -> x.getAccountId() != null ? Long.parseLong(x.getAccountId()) : 0).collect(Collectors.toList());
                List<UserSubscriptionTemplateWoDTO> userDetailList = userRepository.findUsersByAccountId(accountIds);
                for (UserSubscriptionTemplateWoDTO userDetailSql : userDetailList) {
                    List<String> varientId = new ArrayList<>();
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), null);
                    for (MongoCustomerDetailWoDTO mongoData : mongoDataList) {
                        if (mongoData.getAccountId() != null && Long.parseLong(mongoData.getAccountId()) == userDetailSql.getAccountId()) {
                            if (subscriptionsCount.containsKey(mongoData.getAccountId())) {
                                varientId.add(mongoData.getVariantId());
                            }
                            mongoData.setFirstName(userDetailSql.getFirstName());
                            mongoData.setLastName(userDetailSql.getFirstName());
                            mongoData.setCustomerType(userDetailSql.getCustomerType());
                            mongoData.setEntityId(userDetailSql.getEntityId());
                            mongoData.setEntityName(userDetailSql.getEntityName());
                            mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                            mongoDataResults.add(mongoData);
                        }
                    }
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), varientId);
//                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
                }
            }


        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "Subscriptions not found");
        }


        response.put("data", subscriptionsCount);
//        response.put("data", mongoDataResults.stream().collect(
//                Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
        return response;
    }

    @Override
    public List<CaUserTemplateDTO> getAllCustomerByRegion(List<String> region) {
        SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        List<UserTemplate> userTemplates = userRepository.findAllCustomerByRegion(region);
//                Arrays.asList(EUserType.RESIDENTIAL.getName(),EUserType.COMMERCIAL.getName(),EUserType.NONPROFIT.getName()));
        userTemplates.sort(Comparator.comparing(UserTemplate::getGeneratedAt).reversed());
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        EntityDetail entityDetail;
        CaUserTemplateDTO userTemplateDTO;
        Optional<DocuLibrary> docuLibraryOptional;
        //getting sub count here
        Map subCountData = getAllCustomerListSubscriptionSize();
        Map<String, Long> subCount = (Map<String, Long>) subCountData.get("data");
        Map subscriptionData = getAllCustomerListSubscription();
        Map<String, List<MongoCustomerDetailWoDTO>> subscriptionDataId = (Map<String, List<MongoCustomerDetailWoDTO>>) subscriptionData.get("data");
        for (UserTemplate userTemplate : userTemplates) {
            userTemplateDTO = caUserTemplateDTO(userTemplate);
            //add profileUrl
            entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
            if (entityDetail != null) {
                userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
            }
            docuLibraryOptional = docuLibraryService.findByCodeRefId(String.valueOf(userTemplate.getAccountId())).stream().findFirst();

            if (docuLibraryOptional.isPresent()) {
                // String profileUrl = storageService.getBlobUrl(storageContainer, docuLibraryOptional.get().getUri());
                userTemplateDTO.setProfileUrl(docuLibraryOptional.get().getUri());
            }
            userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));
            Optional<Long> firstCount = subCount.entrySet().stream()
                    .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
                    .map(e -> e.getValue())
                    .findFirst();
            if (firstCount.isPresent()) {
                userTemplateDTO.setSubscriptionTotal(String.valueOf(firstCount.get()));
            }
            userTemplateDTO.setMongoCustomerDetailWoDTO(subscriptionDataId.get(String.valueOf(userTemplateDTO.getAccountId())));
            userTemplateDtoList.add(userTemplateDTO);
        }
        return userTemplateDtoList;
    }

    @Override
    public List<CaUserTemplateDTO> getAllCustomerByRegionGroup() {
        SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        List<UserTemplate> userTemplates = userRepository.findAllCustomerByRegionGroup();
//                Arrays.asList(EUserType.RESIDENTIAL.getName(),EUserType.COMMERCIAL.getName(),EUserType.NONPROFIT.getName()));
        userTemplates.sort(Comparator.comparing(UserTemplate::getGeneratedAt).reversed());
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        EntityDetail entityDetail;
        CaUserTemplateDTO userTemplateDTO;
        Optional<DocuLibrary> docuLibraryOptional;
        //getting sub count here
        Map subCountData = getAllCustomerListSubscriptionSize();
        Map<String, Long> subCount = (Map<String, Long>) subCountData.get("data");
        Map subscriptionData = getAllCustomerListSubscription();
        Map<String, List<MongoCustomerDetailWoDTO>> subscriptionDataId = (Map<String, List<MongoCustomerDetailWoDTO>>) subscriptionData.get("data");
        for (UserTemplate userTemplate : userTemplates) {
            userTemplateDTO = caUserTemplateDTO(userTemplate);
            //add profileUrl
            entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
            if (entityDetail != null) {
                userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
            }
            docuLibraryOptional = docuLibraryService.findByCodeRefId(String.valueOf(userTemplate.getAccountId())).stream().findFirst();

            if (docuLibraryOptional.isPresent()) {
                // String profileUrl = storageService.getBlobUrl(storageContainer, docuLibraryOptional.get().getUri());
                userTemplateDTO.setProfileUrl(docuLibraryOptional.get().getUri());
            }
            userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));
            Optional<Long> firstCount = subCount.entrySet().stream()
                    .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
                    .map(e -> e.getValue())
                    .findFirst();
            if (firstCount.isPresent()) {
                userTemplateDTO.setSubscriptionTotal(String.valueOf(firstCount.get()));
            }
            userTemplateDTO.setMongoCustomerDetailWoDTO(subscriptionDataId.get(String.valueOf(userTemplateDTO.getAccountId())));
            userTemplateDtoList.add(userTemplateDTO);
        }
        return userTemplateDtoList;
    }


    @Override
    public Map getAllSubscriptionByCustomer(List<Long> customerId) {
        List<String> gardenIdList = oragnizationDetailRepository.getAllGarden();
        List<MongoCustomerDetailWoDTO> mongoDataResults = new ArrayList<>();
        Map<String, Long> subscriptionsCount = new HashMap<>();
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        if (gardenIdList != null) {
            String ids = String.join(",", gardenIdList);
//            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllGardensSubscription";
            String dynamicMappingUrl = MONGO_BASE_URL + "/product/showAllSubscriptionDetails";
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Collections.singletonList(DBContextHolder.getTenantName()));
            ResponseEntity<MongoCustomerDetailWoMasterDTO> staticValues = WebUtils.submitRequest(HttpMethod.GET, String.format(dynamicMappingUrl), null, headers, MongoCustomerDetailWoMasterDTO.class);
            if (staticValues.getBody().getMongoCustomerDetailWoDTO() != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.SUB_DETAIL_GET_VARIANT_SUBSCRIPTIONS.getMessage());
                List<MongoCustomerDetailWoDTO> mongoDataList = staticValues.getBody().getMongoCustomerDetailWoDTO();
                List<Long> accountIds = mongoDataList.stream().map(x -> x.getAccountId() != null ? Long.parseLong(x.getAccountId()) : 0).collect(Collectors.toList());
                accountIds = customerId;
                List<UserSubscriptionTemplateWoDTO> userDetailList = userRepository.findUsersByAccountId(accountIds);
                for (UserSubscriptionTemplateWoDTO userDetailSql : userDetailList) {
                    List<String> subscriptions = new ArrayList<>();
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), null);
                    for (MongoCustomerDetailWoDTO mongoData : mongoDataList) {
                        if (mongoData.getAccountId() != null && Long.parseLong(mongoData.getAccountId()) == userDetailSql.getAccountId()) {
                            if (subscriptionsCount.containsKey(mongoData.getAccountId())) {
                                subscriptions.add(mongoData.getSubId());
                            }
                            mongoData.setFirstName(userDetailSql.getFirstName());
                            mongoData.setLastName(userDetailSql.getFirstName());
                            mongoData.setCustomerType(userDetailSql.getCustomerType());
                            mongoData.setEntityId(userDetailSql.getEntityId());
                            mongoData.setEntityName(userDetailSql.getEntityName());
                            mongoData.setProfileUrl(userDetailSql.getProfileUrl());
                            mongoDataResults.add(mongoData);
                        }
                    }
                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions.stream().count());
//                    subscriptionsCount.put(String.valueOf(userDetailSql.getAccountId()), subscriptions);
                }
            }


        } else {
            response.put("code", HttpStatus.NOT_FOUND);
            response.put("message", "Subscriptions not found");
        }


//        response.put("data", subscriptionsCount );
//        response.put("data", mongoDataResults.stream().collect(
//                Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
        response.put("data", mongoDataResults.stream().collect(
                Collectors.groupingBy(MongoCustomerDetailWoDTO::getAccountId)));
        return response;
    }


    public UserDetailTemplateWoDTO findCustomerByAccountId(Long accountId) {
        return userRepository.findCustomerByAccountId(accountId);
    }

    @Override
    public List<UserSubscriptionTemplateWoDTO> findUsersAndLocationByAccountId(List<Long> accountIds) {
        return userRepository.findUsersAndLocationByAccountId(accountIds);
    }

    @Override
    public Entity saveInEntity(Entity entity) {
        return entityService.save(entity);
    }

    @Override
    public CustomerDetail saveInCustomerDetail(CustomerDetail customerDetail) {
        return customerDetailService.save(customerDetail);
    }

    @Override
    public UserLevelPrivilege saveInUserLevelPrivilege(UserLevelPrivilege userLevelPrivilege) {

        return userLevelPrivilegeRepository.save(userLevelPrivilege);
    }


    @Override
    public User updateCaUserStatus(UserDTO userDTO) {
        User user = null;

        if (userDTO.getStatus().equalsIgnoreCase("LEAD") || userDTO.getStatus().equalsIgnoreCase("PROSPECT"))
            if (userDTO.getEntityId() != null) {
                user = updateCaUserDetails(userDTO);
            }
        return user;
    }

    @Override
    public User updateCaUserStatusV2(UserDTO userDTO) {
        User user = null;

        if (userDTO.getStatus().equalsIgnoreCase("LEAD") || userDTO.getStatus().equalsIgnoreCase("PROSPECT") || userDTO.getStatus().equalsIgnoreCase("APPROVAL PENDING") || userDTO.getStatus().equalsIgnoreCase("REQUEST PENDING") || userDTO.getStatus().equalsIgnoreCase("CONTRACT PENDING") || userDTO.getStatus().equalsIgnoreCase("NEW-REQUEST"))
            if (userDTO.getEntityId() != null) {
                user = updateCaUserDetailsV2(userDTO);
            }
        return user;
    }

    private User updateCaUserDetails(UserDTO userDTO) {
        User user = findById(userDTO.getAcctId());//findUserByEntityId(userDTO.getAcctId());
        Entity entity = entityService.findById(userDTO.getEntityId());//findEntityByUserId(user.getAcctId());
        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
        if (user != null) {
            user.setStatus("INACTIVE");
        }
        userRepository.save(user);
        if (customerDetail != null) {
            customerDetail.setStatus("INACTIVE");
            customerDetail.setNotes(userDTO.getNotes());
            customerDetailService.save(customerDetail);
        }
        if (entity != null) {
            entity.setStatus("INACTIVE");
            entity.setIsDeleted(true);
            entityService.save(entity);
        }
        if (userDTO.getNotes() != null) {
            userDTO.setCompKey(user.getCompKey() != null ? user.getCompKey() : userDTO.getCompKey());
            userDTO.setEmailAddress(user.getEmailAddress());
            emailRejectSignUpRequest(userDTO);
        }

        return user;
    }

    private User updateCaUserDetailsV2(UserDTO userDTO) {
        User user = findById(userDTO.getAcctId());//findUserByEntityId(userDTO.getAcctId());
        Entity entity = entityService.findById(userDTO.getEntityId());//findEntityByUserId(user.getAcctId());
        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
        if (user != null) {
            user.setStatus("INACTIVE");
        }
        userRepository.save(user);
        if (customerDetail != null) {
            customerDetail.setStatus("INACTIVE");
            customerDetail.setNotes(userDTO.getNotes());
            customerDetail.setStates(userDTO.getCaseStatus());
            customerDetailService.save(customerDetail);
        }
        if (entity != null) {
            entity.setStatus("INACTIVE");
            entity.setIsDeleted(true);
            entityService.save(entity);
        }
        if (userDTO.getNotes() != null) {
            userDTO.setCompKey(user.getCompKey() != null ? user.getCompKey() : userDTO.getCompKey());
            userDTO.setEmailAddress(user.getEmailAddress());
            emailRejectSignUpRequest(userDTO);
        }

        return user;
    }


    @Override
    public List<String> generateCaUsersCSV(Long compKey) {
        List<String> blobUrls = new ArrayList<>();
        try {
//            String leads = "ALL";
            MasterTenant company = masterTenantService.findByCompanyKey(compKey);
            StringBuilder groupName = new StringBuilder();
            String[] fieldDetails = {company.getCompanyName()};
            blobUrls.add(caUsersJob(fieldDetails));
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return blobUrls;
    }

    private String caUsersJob(String[] fieldDetails) {
        String blobUrl = "";
        JobManagerTenant jobManagerTenant = null;
        try {
            ObjectNode messageJson = new ObjectMapper().createObjectNode();
            jobManagerTenant = jobManagerTenantService.add(EJobName.EXPORT_CUSTOMER.toString(), null,
                    EJobStatus.RUNNING.toString(), null, LOGGER);
            messageJson.put("msg", "File Is In Process");

            List<UserTemplate> allCaUsers = userRepository.findAllCaUsers(Arrays.asList(ECustomerDetailStates.LEAD.getName(), ECustomerDetailStates.PROSPECT.getName(), ECustomerDetailStates.CUSTOMER.getName(), ECustomerDetailStates.INTERIMCUSTOMER.getName()));
            LOGGER.info("All ca users data:" + allCaUsers);
            blobUrl = downloadCaUsersFile(allCaUsers, jobManagerTenant.getId(), fieldDetails);

            messageJson.put("msg", "File Generated Successfully");
            messageJson.put("blobUrl", blobUrl);
            jobManagerTenant.setRequestMessage(messageJson.toString());

            jobManagerTenantService.update(jobManagerTenant, EJobStatus.COMPLETED.toString(), null, LOGGER);

        } catch (Exception ex) {
            LOGGER.error("Ca users Job Id:" + jobManagerTenant.getId(), ex);
        }
        return blobUrl;
    }

    private String downloadCaUsersFile(List<UserTemplate> caUsersCsvData, Long jobId, String[] fieldDetails) {

        String blobUrl = "";
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        String caUsersFileName = fieldDetails[0] + "CustomerExport_REQID[" + jobId + "]_[" + currentDateTime + "].csv";

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            OutputStreamWriter osNew = new OutputStreamWriter(stream, "UTF-8");
            PrintWriter writer1 = new PrintWriter(osNew);
            CsvPreference preferences = new CsvPreference.Builder('"', ';', "\n").build();
            ICsvBeanWriter csvWriter = new CsvBeanWriter(writer1, preferences);

            String[] csvHeader = {"acct_id", "name", "type", "email", "phone", "zipcode"
                    , "salesAgent", "region", "status", "generatedAt"};
            csvWriter.writeHeader(csvHeader);

            for (UserTemplate data : caUsersCsvData) {

                String[] nameMapping = {"acctId", "userName", "userType", "emailAddress", "phone", "zipCode"
                        , "entityId", "region", "status", "generatedAt"};

                try {
                    UserDTO userDTO = UserDTO.builder().acctId(data.getAccountId()).userName(data.getFirstName() + " " + data.getLastName())
                            .userType(data.getCustomerType()).emailAddress(data.getEmailAddress())
                            .phone(data.getPhone()).zipCode(data.getZipCode()).entityId(data.getEntityId())
                            .region(data.getRegion()).status(data.getStatus()).generatedAt(data.getGeneratedAt()).build();
                    csvWriter.write(userDTO, nameMapping);

                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            csvWriter.close();
            byte[] byteArray = stream.toByteArray();
            blobUrl = Utility.uploadToStorage(storageService, byteArray, appProfile, "tenant/" + utility.getCompKey()
                            + "/ca/users", caUsersFileName,
                    utility.getCompKey(), false);

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
        return blobUrl;
    }


    /**
     * created date :25/01/2023
     * created by : sana
     * description :  this method will upload contract document on storage
     *
     * @param file     its uploaded contract document file
     * @param notes
     * @param entityId of user for which we are uploading contract document
     * @return return url of uploaded document
     */
    @Override
    public String getUploadedDocumentUrl(MultipartFile file, String notes, Long entityId) {
        try {
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
                    DocumentSigningTemplate template = documentSigningTemplateService.findByFunctionalityAndCustomerTypeAndOrganizationAndEnabled(functionality, customerType, organization, true).stream().findFirst().get();
                    StringBuilder templateNameBuilder = documentSigningTemplateService.getDocSigningTemplateName(entity, organization, contract, file, template, format);
                    url = storageService.uploadByteArray(file.getBytes(), appProfile, "tenant/docuSign/" + notes, templateNameBuilder.toString());
                    List<DocuLibrary> docuLibraryList = docuLibraryService.findByCodeRefIdAndCodeRefType(String.valueOf(entity.getId()), "SIGNREQ");
                    if (docuLibraryList != null && docuLibraryList.size() > 0) {
                        Optional<DocuLibrary> docuLibraryOptional = docuLibraryList.stream().findFirst();
                        if (docuLibraryOptional.isPresent()) {
                            DocuLibrary docuLibrary = docuLibraryOptional.get();
                            docuLibrary.setDocuName(templateNameBuilder.toString());
                            docuLibrary.setUri(url);
                            docuLibraryService.saveOrUpdate(docuLibrary);
                        }
                    } else {
                        docuLibraryService.saveOrUpdate(DocuLibrary.builder()
                                .organization(organization)
                                .entity(entity)
                                .docuType("application/pdf")
                                .codeRefType("SIGNREQ")
                                .codeRefId(String.valueOf(entity.getId()))
                                .docuName(templateNameBuilder.toString())
                                .visibilityKey(true)
                                .uri(url)
                                .build());
                    }
                }
                customerDetail.setCustomer(true);
                customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
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

    /**
     * Description: Method for saveing and updating user for entity
     * Created By: Ibtehaj
     *
     * @param entity
     * @param employeeDetailDTO
     * @param userDTO
     * @return
     */
    @Override
    public User createOrUpdateUserForEntity(Entity entity, EmployeeDetailDTO employeeDetailDTO, UserDTO userDTO) {

        UserType userType = userTypeService.findById(2L);
        User user = findUserByEntityId(entity.getId());
        Date date = new Date();
        String authentication = (entity.getStatus() == null || entity.getStatus().equalsIgnoreCase("INACTIVE")) && Boolean.TRUE.equals(!employeeDetailDTO.getIsActive()) ?
                EAuthenticationType.NA.getName() : EAuthenticationType.STANDARD.getName();
        String status = AppConstants.STATUS_INACTIVE;
        String userName = null;
        String password = null;
        Integer privLevel = null;
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleService.findByName(ERole.ROLE_EMPLOYEE.getName()));

        if (userDTO != null) {
            if (userDTO.getUserName() != null) {
                User userNameExists = findByUserName(userDTO.getUserName());
                if ((user != null && userNameExists != null && !userNameExists.getUserName().equalsIgnoreCase(user.getUserName()))) {
                    throw new AlreadyExistsException("UserName already exists " + userDTO.getUserName());
                } else {
                    userName = userDTO.getUserName();
                }
            }
            password = (userDTO.getPassword() != null && userDTO.getAcctId() == null) ? encoder.encode(userDTO.getPassword()) : null;
            privLevel = userDTO.getPrivLevel() != null ? userDTO.getPrivLevel() : null;
            status = AppConstants.STATUS_ACTIVE;
        }
        if (user != null) {
            password = user.getPassword() != null ? user.getPassword() : (password != null ? password : (userDTO != null ? (userDTO.getPassword() != null ? encoder.encode(userDTO.getPassword()) : null) : null));
            privLevel = (privLevel == null && user.getPrivLevel() != null) ? user.getPrivLevel() :
                    (privLevel != null) ? privLevel : null;
            userName = (userName == null && user.getUserName() != null) ? user.getUserName() :
                    (userName != null) ? userName : null;
        }
        User updatedUser = buildUserForEntity(user, employeeDetailDTO, entity, date, userType, userName, password, status, authentication, privLevel, roleSet);
        User resultUser = userRepository.save(updatedUser);
        if (resultUser.getUserName() == null) {
            resultUser.setUserName("CA_NONE_" + resultUser.getAcctId());
            userRepository.save(resultUser);
        }

        return resultUser;
    }

    /**
     * Description: Method to build or update user for entity
     * Created By: Ibtehaj
     *
     * @param user
     * @param employeeDetailDTO
     * @param entity
     * @param date
     * @param userType
     * @param userName
     * @param password
     * @param status
     * @param authentication
     * @param privLevel
     * @return
     */
    private User buildUserForEntity(User user, EmployeeDetailDTO employeeDetailDTO, Entity entity, Date date, UserType userType, String userName, String password, String status,
                                    String authentication, Integer privLevel, Set<Role> roles) {

        if (user == null) {
            user = User.builder()
                    .authentication(authentication)
                    .firstName(employeeDetailDTO.getFirstName())
                    .lastName(employeeDetailDTO.getLastName())
                    .gender(employeeDetailDTO.getGender())
                    .dataOfBirth(employeeDetailDTO.getDateOfBirth())
                    .emailAddress(entity.getContactPersonEmail())
                    .userName(userName)
                    .password(password)
                    .privLevel(privLevel)
                    .language("EN")
                    .registerDate(date)
                    .userType(userType)
                    .isEmailVerified(false)
                    .status(status)
                    .roles(roles).build();
        } else {
            user.setAuthentication(authentication);
            user.setFirstName(employeeDetailDTO.getFirstName());
            user.setLastName(employeeDetailDTO.getLastName());
            user.setGender(employeeDetailDTO.getGender());
            user.setDataOfBirth(employeeDetailDTO.getDateOfBirth());
            user.setEmailAddress(entity.getContactPersonEmail());
            user.setLanguage(user.getLanguage() != null ? user.getLanguage() : "EN");
            user.setRegisterDate(user.getRegisterDate() != null ? user.getRegisterDate() : date);
            user.setUserType(user.getUserType() != null ? user.getUserType() : userType);
            user.setStatus(status);
            user.setUserName(userName);
            user.setPassword(password);
            user.setPrivLevel(privLevel);
            if (user.getRoles() == null || user.getRoles().size() == 0) {
                user.setRoles(roles);
            }
        }
        return user;
    }

    @Override
    public ResponseEntity<Object> generatePasswordEmail(UserDTO userDTO) {
        masterTenantService.setCurrentDb(userDTO.getCompKey());
        if ((userDTO.getEmailAddress() == null || userDTO.getEmailAddress().isEmpty()) ||
                userDTO.getAcctId() == null || userDTO.getCompKey() == null ||
                (userDTO.getUserType() == null || userDTO.getUserType().isEmpty())) {
            throw new AlreadyExistsException("You must provide these all <<< EmailAddress|AcctId|UserType|CompKey >>> ");
        } else {

            MasterTenant masterTenant = masterTenantService.findByCompanyKey(userDTO.getCompKey());
            CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(userDTO.getCompKey());
            WorkflowHookMaster workFlowHookMaster = workFlowHookMasterRepository.findByHookConstant(Constants.HOOK_CONSTANT.REGISTER_YOUR_INTEREST);
            if (workFlowHookMaster != null) {
                try {
                    List<WorkflowHookMap> workflowHookMaps = workflowHookMapRepository.findListByHookId(workFlowHookMaster.getId());
                    if (workflowHookMaps.size() > 0) {
                        Optional<WorkflowHookMap> mapPasswordTemplate = workflowHookMaps.stream().filter(s -> s.getEmailTemplate().getMsgTmplName().equals(Constants.MESSAGE_TEMPLATE.REGISTER_YOUR_INTEREST_PASSWORD)).findFirst();
                        Map<String, String> placeholderValues = new HashMap<>();
                        placeholderValues.put("google_play", blobService + Constants.MESSAGE_TEMPLATE.GOOGLE_PLAY);
                        placeholderValues.put("facebook", blobService + Constants.MESSAGE_TEMPLATE.FACEBOOK);
                        placeholderValues.put("apple", blobService + Constants.MESSAGE_TEMPLATE.APPLE);
                        placeholderValues.put("linkedin", blobService + Constants.MESSAGE_TEMPLATE.LINKEDIN);
                        placeholderValues.put("youtube", blobService + Constants.MESSAGE_TEMPLATE.YOUTUBE);
                        placeholderValues.put("twitter", blobService + Constants.MESSAGE_TEMPLATE.TWITTER);
                        placeholderValues.put("solar_amps", blobService + Constants.MESSAGE_TEMPLATE.SOLAR_AMPS);
                        placeholderValues.put("company_name", masterTenant.getCompanyName() != null ? masterTenant.getCompanyName() : "");
                        placeholderValues.put("company_key", String.valueOf(userDTO.getCompKey()));
                        placeholderValues.put("company_logo", masterTenant.getCompanyLogo() != null ? masterTenant.getCompanyLogo() : null);
                        placeholderValues.put("user_name", String.valueOf(userDTO.getUserName()));
                        JSONObject json = new JSONObject();
                        json.put("setPassword", "true");
                        json.put("acctId", String.valueOf(userDTO.getAcctId()));
                        json.put("userType", userDTO.getUserType() != null ? userDTO.getUserType() : "");
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
                        String htmlCodeUserMsg = solarAmpsService.getMessage(mapPasswordTemplate.get().getEmailTemplate().getTemplateHTMLCode(), placeholderValues);
                        solarAmpsService.sendEmail(placeholderValues, userDTO.getEmailAddress(), null, null,
                                mapPasswordTemplate.get().getEmailTemplate().getSubject(), null, mapPasswordTemplate.get().getEmailTemplate().getParentTmplId(), htmlCodeUserMsg);
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                    return new ResponseEntity<>(APIResponse.builder().message(ex.getMessage())
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return new ResponseEntity<>(APIResponse.builder().message("Email sent successfully.")
                .code(HttpStatus.OK.value()).build(), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Object> saveRegisterInterestUser(UserDTO userDTO) {
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
                entityService.save(entity);
                String customerType = userDTO.getCustomerType();
                CustomerDetail customerDetail = toCustomerDetail(CustomerDetailDTO.builder().customerType(userDTO.getCustomerType())
                        .isContractSign(false).isActive(true).isCustomer(false).hasLogin(false).mobileAllowed(false).signUpDate(new Date())
                        .priorityIndicator(false).states(ECustomerDetailStates.LEAD.toString()).entityId(entity.getId()).status(EUserStatus.INACTIVE.getStatus()).build());
                customerDetail = customerDetailService.save(customerDetail);
                userLevelPrivilegeService.save(UserLevelPrivilege.builder()
                        .user(user)
                        .createdAt(userDTO.getCreatedAt())
                        .updatedAt(userDTO.getUpdatedAt())
                        .entity(entity)
                        .organization(organization)
                        .build());
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
    public UserDTO getClientUserDetail(Long accountId) {
        String isChecked = "null";
        Optional<User> userOptional = userRepository.findById(accountId);
        UserLevelPrivilege userLevel = userLevelPrivilegeService.userLevelPrivilegeByAccountId(accountId);
        Entity entity = entityService.findById(userLevel.getEntity().getId());
        List<LocationMapping> utilityLocationMappingList = null;
        List<PhysicalLocation> utilityPhysicalLocations = null;
        List<LocationMapping> userLocationMappingList = null;
        List<PhysicalLocation> userPhysicalLocations = null;
        List<CaUtilityDTO> caUtilityDTOList = new ArrayList<>();
        CaReferralInfoDTO caReferralInfoDTO = null;
        UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.UserLevelPrivilegeByEntityId(entity.getId());
        EntityDetail entityDetail = null;
        if (userLevelPrivilege == null) {
            throw new NotFoundException(UserLevelPrivilege.class, entity.getId());
        }
        User user = findById(userLevelPrivilege.getUser().getAcctId());
        List<CaUtility> caUtilityList = caUtilityService.getByEntity(entity);
        CaReferralInfo caReferralInfo = caReferralInfoService.getByEntity(entity);
        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
        userLocationMappingList = locationMappingService.findBySourceId(user.getAcctId());
        if (userLocationMappingList != null && userLocationMappingList.size() > 0)
            userPhysicalLocations = physicalLocationService.getPhysicalLocationById(userLocationMappingList.stream().map(LocationMapping::getLocationId).collect(Collectors.toList()));
        if (caUtilityList != null && caUtilityList.size() > 0) {
            List<String> urls = new ArrayList<>();
            for (CaUtility caUtility : caUtilityList) {
                CaUtilityDTO caUtilityDTO = toCaUtilityDTO(caUtility);
                List<DocuLibrary> docuLibraryList = docuLibraryService.findByCodeRefId(String.valueOf(caUtilityDTO.getId()));
                utilityLocationMappingList = locationMappingService.findBySourceId(caUtility.getId());
                if (utilityLocationMappingList != null && utilityLocationMappingList.size() > 0)
                    utilityPhysicalLocations = physicalLocationService.getPhysicalLocationById(utilityLocationMappingList.stream().map(LocationMapping::getLocationId).collect(Collectors.toList()));
                caUtilityDTO.setCreatedAt(null);
                caUtilityDTO.setPhysicalLocations(utilityPhysicalLocations != null ? toPhysicalLocationDTOs(utilityPhysicalLocations) : null);
                for (DocuLibrary docu : docuLibraryList) {
                    urls.add(docu.getUri());
                }
                caUtilityDTO.setFileUrls(urls);
                caUtilityDTOList.add(caUtilityDTO);
            }
        }
        if (caReferralInfo != null) {
            caReferralInfoDTO = toCaReferralInfoDTO(caReferralInfo);
            entityDetail = entityDetailRepository.findByEntityId(caReferralInfoDTO.getEntityId());
            if (caReferralInfoDTO.getRepresentativeId() != null) {
                Optional<EntityRole> entityRoleOptional = entityRoleRepository.findById(caReferralInfoDTO.getRepresentativeId());
                if (entityRoleOptional.isPresent()) {
                    EntityRole entityRole = entityRoleOptional.get();
                    if (entityRole.getFunctionalRoles() != null) {
                        if (entityRole.getEntity() != null) {
                            FunctionalRoles functionalRoles = entityRole.getFunctionalRoles();
                            Entity entityDB = entityRole.getEntity();
                            caReferralInfoDTO.setAgentDesignation(functionalRoles.getName());
                            caReferralInfoDTO.setAgentName(entityDB.getEntityName());
                        }
                    }
                }
            }
            if (caReferralInfoDTO.getAgentDesignation() == null || caReferralInfoDTO.getAgentName() == null) {
                caReferralInfoDTO.setAgentDesignation("Unassigned");
                caReferralInfoDTO.setAgentName(" ");
            }
        }
        UserDTO userDto = toUserDTO(user);
        userDto.setIsChecked(isChecked);
        userDto.setPhone(entity.getContactPersonPhone());
        if (customerDetail != null) {
            userDto.setCustomerType(customerDetail.getCustomerType());//individual / commercial
            userDto.setCustomerState(customerDetail.getStates()); //lead/ prospect
            userDto.setIsSubmitted(String.valueOf(customerDetail.getIsSubmitted())); //lead/ prospect
        }
        userDto.setEntityType(entity.getEntityType()); // customer, employee
        userDto.setEntityId(entity.getId());
        userDto.setCaUtility(caUtilityDTOList);
        userDto.setCaReferralInfo(caReferralInfoDTO);
        userDto.setPhysicalLocations(userPhysicalLocations != null ? toPhysicalLocationDTOs(userPhysicalLocations) : null);
        if (entityDetail != null) {
            userDto.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
        }
        userDto.setCompKey(null);
        userDto.setRoles(null);
        userDto.setAddresses(null);
        userDto.setUserType(null);
        userDto.setCreatedAt(null);
        userDto.setUpdatedAt(null);
        userDto.setStatus(null);
        return userDto;
    }

    @Override
    public User saveOrUpdateClientUser(UserDTO userDTO, List<MultipartFile> utilityMultipartFiles, Boolean sendForSigning) {
        User user = null;
        if (userDTO.getAcctId() == null) {
            user = saveCaUser(userDTO);
        } else if (userDTO.getAcctId() != null) {
            user = updateClientUser(userDTO, sendForSigning);
        }
        if (user != null) {
            if (userDTO.getCaUtility() != null) {
                saveOrUpdateCaUtilityDTO(userDTO, user, utilityMultipartFiles);
            }
            if (user != null && userDTO.getCaReferralInfo() != null) {
                CaReferralInfoDTO caReferralInfoDTO = userDTO.getCaReferralInfo();
                caReferralInfoService.save(caReferralInfoDTO, user);
                if (sendForSigning != null && sendForSigning) {
                    sendForSigning(user, userDTO);
                }
            }
            if (user != null && userDTO.getCaSoftCreditCheck() != null) {
                CaSoftCreditCheckDTO caSoftCreditCheckDTO = userDTO.getCaSoftCreditCheck();
                caSoftCreditCheckService.save(caSoftCreditCheckDTO, user);
            }
        }
        return user;
    }

    private User updateClientUser(UserDTO userDTO, Boolean sendForSigning) {
        /**
         * Validate email either already exist or not
         * If not exist already then process.
         */
        if (userDTO.getEmailAddress() == null) {
            throw new InvalidValueException("Email address must be provide");
        } else {
            Entity emailAddrExists = entityService.findByEmailAddressAndEntityType(userDTO.getEmailAddress(), userDTO.getEntityType()); // aaa  bbb ccc
            if (emailAddrExists == null) {
            } else if (emailAddrExists.getId().equals(userDTO.getEntityId())) {
            } else {
                throw new AlreadyExistsException("Email " + userDTO.getEmailAddress());
            }
        }
        User user = findById(userDTO.getAcctId());//findUserByEntityId(userDTO.getAcctId());
        user.setFirstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : user.getFirstName());
        user.setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : user.getLastName());
        user.setDataOfBirth(userDTO.getDataOfBirth() != null ? userDTO.getDataOfBirth() : user.getDataOfBirth());
        user.setEmailAddress(userDTO.getEmailAddress() != null ? userDTO.getEmailAddress() : user.getEmailAddress());

        Entity entity = entityService.findById(userDTO.getEntityId());//findEntityByUserId(user.getAcctId());
        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
        if ((customerDetail.getStates().equals(ECustomerDetailStates.REQUEST_PENDING.getName()) ||
                customerDetail.getStates().equals(ECustomerDetailStates.APPROVAL_PENDING.getName()))
                &&
                user.getUserType().getName().toString().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.APPROVAL_PENDING.getName());
            customerDetail.setIsSubmitted(true);
        } else if (customerDetail.getStates().equals(EUserType.CUSTOMER.getName()) || user.getUserType().getName().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
            customerDetail.setIsSubmitted(true);
        } else if (customerDetail.getStates().equals(EUserType.CUSTOMER.getName()) || customerDetail.getStates().equals(EUserType.INTERIMCUSTOMER.getName()) || user.getUserType().getName().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setIsSubmitted(true);
        } else {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.PROSPECT.getName());
            customerDetail.setIsSubmitted(true);
        }
        if (sendForSigning != null && sendForSigning == true) {
            customerDetail.setStates(ECustomerDetailStates.INTERIMCUSTOMER.getName());
        }

        entity.setContactPersonPhone(userDTO.getPhone() != null ? userDTO.getPhone() : entity.getContactPersonPhone());
        entity.setContactPersonEmail(userDTO.getEmailAddress() != null ? userDTO.getEmailAddress() : entity.getContactPersonEmail());
        if (user.getFirstName() != null && user.getLastName() != null) {
            entity.setEntityName(user.getFirstName().concat(" ").concat(user.getLastName()));
        }

        user = userRepository.save(user);
        if (user.getUserName() == null) {
            user.setUserName("CA_NONE_" + user.getAcctId());
            userRepository.save(user);
        }
        entityService.save(entity);
        customerDetailService.save(customerDetail);

        if (userDTO.getPhysicalLocations() != null) {
            List<PhysicalLocation> physicalLocation = new ArrayList<>();
            physicalLocation.addAll(updateLocationToDefaultTypeAndCategory(userDTO.getPhysicalLocations(), Constants.LOCATION_CATEGORY_CONSTANTS.USER, Constants.LOCATION_TYPE.BILLING));
            physicalLocationRepository.saveAll(physicalLocation);
            locationMappingService.saveAll(getCaLocationMappings(physicalLocation, user, null, AppConstants.PRIMARY_INDEX_LOCATION_FALSE));
        }
        return user;
    }

    @Override
    public User updateUserPassword(Long id, String newPass) {
        User user = null;
        User userData = findById(id);
        Set<Role> roles = new HashSet<>();
        Role role = roleService.findByName(ERole.ROLE_NEW_CUSTOMER.toString());
        if (role == null) {
            //  response.put("error", "cannot activate your user");
            throw new InvalidValueException("this role doesn't exist in DB", role.getName());
        }
        roles.add(role);
        userData.setRoles(roles);
        userData.setStatus(EUserStatus.ACTIVE.getStatus());
        userData.setEmailVerified(true);
        userData.setPassword(newPass);
        //userData.setUserType(userTypeService.findByName(EUserType.INTERIMCUSTOMER));
        userData.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
        userData.setAuthentication(EAuthenticationType.STANDARD.getName());
        Entity entity = entityService.findEntityByUserId(userData.getAcctId());
        if (entity != null) {
            CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
            user = userRepository.save(userData);
            if (user != null && customerDetail != null) {
                customerDetail.setStates(ECustomerDetailStates.REQUEST_PENDING.getName());
                customerDetail.setStatus(EUserStatus.ACTIVE.getStatus());
                customerDetail.setActive(true);
                customerDetail.setHasLogin(true);
                customerDetailService.save(customerDetail);
            }
        }
        return user;
    }

    @Override
    public ResponseEntity<Object> assignLeads(List<Long> entityIds, Long entityRoleId) {
        try {
            List<CaReferralInfo> existCaReferralInfoList = caReferralInfoService.getAllByEntityIds(entityIds);
            existCaReferralInfoList.stream().forEach(caInfo -> caInfo.setRepId(entityRoleId));
            List<Long> newEntityIdList = entityIds.stream().filter(val -> existCaReferralInfoList.stream()
                    .noneMatch(obj -> obj.getEntity().getId().equals(val))).collect(Collectors.toList());

            if (newEntityIdList.size() > 0) {
                newEntityIdList.stream().forEach(newEntityId -> existCaReferralInfoList.add(CaReferralInfo.builder()
                        .entity(entityService.findById(newEntityId)).repId(entityRoleId).build()));
            }

            caReferralInfoService.saveAll(existCaReferralInfoList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(APIResponse.builder().message("Error assigning Agent to leads.")
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value()).build(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(APIResponse.builder().message("Successfully assigned Agent to leads.")
                .code(HttpStatus.OK.value()).build(), HttpStatus.OK);
    }

    private void emailRejectSignUpRequest(UserDTO userDTO) {
        MasterTenant masterTenant = masterTenantService.findByCompanyKey(userDTO.getCompKey());
        CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(userDTO.getCompKey());
        WorkflowHookMaster workFlowHookMaster = workFlowHookMasterRepository.findByHookConstant(Constants.HOOK_CONSTANT.REJECT_SIGN_UP_REQUEST);
        if (workFlowHookMaster != null) {
            try {
                List<WorkflowHookMap> workflowHookMaps = workflowHookMapRepository.findListByHookId(workFlowHookMaster.getId());
                if (workflowHookMaps.size() > 0) {
                    Optional<WorkflowHookMap> mapTemplate = workflowHookMaps.stream().filter(s -> s.getEmailTemplate().getMsgTmplName().equals(Constants.MESSAGE_TEMPLATE.REJECT_SIGN_UP_REQUEST)).findFirst();
                    Map<String, String> placeholderValues = new HashMap<>();
                    placeholderValues.put("google_play", blobService + Constants.MESSAGE_TEMPLATE.GOOGLE_PLAY);
                    placeholderValues.put("apple", blobService + Constants.MESSAGE_TEMPLATE.APPLE);
                    placeholderValues.put("linkedin", blobService + Constants.MESSAGE_TEMPLATE.LINKEDIN);
                    placeholderValues.put("youtube", blobService + Constants.MESSAGE_TEMPLATE.YOUTUBE);
                    placeholderValues.put("twitter", blobService + Constants.MESSAGE_TEMPLATE.TWITTER);
                    placeholderValues.put("solar_amps", blobService + Constants.MESSAGE_TEMPLATE.SOLAR_AMPS);
                    placeholderValues.put("company_name", masterTenant.getCompanyName() != null ? masterTenant.getCompanyName() : "");
                    placeholderValues.put("company_logo", masterTenant.getCompanyLogo() != null ? masterTenant.getCompanyLogo() : null);
                    placeholderValues.put("message", String.valueOf(userDTO.getNotes()));
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
                    String htmlCodeUserMsg = solarAmpsService.getMessage(mapTemplate.get().getEmailTemplate().getTemplateHTMLCode(), placeholderValues);
                    solarAmpsService.sendEmail(placeholderValues, userDTO.getEmailAddress(), null, null,
                            mapTemplate.get().getEmailTemplate().getSubject(), null, mapTemplate.get().getEmailTemplate().getParentTmplId(), htmlCodeUserMsg);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
                throw new NotFoundException("Template|HOOK not saved in DB");
            }
        }
    }

    @Override
    public ResponseEntity<Object> generateForgetPasswordEmail(UserDTO userDTO) {
        masterTenantService.setCurrentDb(userDTO.getCompKey());

        // Check if email address and username are provided
        if ((userDTO.getEmailAddress() == null || userDTO.getEmailAddress().isEmpty()) ||
                (userDTO.getUserName() == null || userDTO.getUserName().isEmpty())) {
            throw new AlreadyExistsException("You must provide Email Address and Username ");
        } else {

            // Find user by  username
            User userByUsername = userRepository.findByUserName(userDTO.getUserName());

            // Case 1: Username does not exist
            if (userByUsername == null) {
                return new ResponseEntity<>(APIResponse.builder().message("Username does not exist")
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            //Case 2:  Check if the provided email does not matches the email associated with the found username
            if (!userByUsername.getEmailAddress().equalsIgnoreCase(userDTO.getEmailAddress())) {
                return new ResponseEntity<>(APIResponse.builder().message("The email provided does not match the associated username.")
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                userDTO.setAcctId(userByUsername.getAcctId());
                MasterTenant masterTenant = masterTenantService.findByCompanyKey(userDTO.getCompKey());
                CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(userDTO.getCompKey());
                WorkflowHookMaster workFlowHookMaster = workFlowHookMasterRepository.findByHookConstant(Constants.HOOK_CONSTANT.FORGET_PASSWORD_HOOK);
                if (workFlowHookMaster != null) {
                    try {
                        List<WorkflowHookMap> workflowHookMaps = workflowHookMapRepository.findListByHookId(workFlowHookMaster.getId());
                        if (workflowHookMaps.size() > 0) {

                            Optional<WorkflowHookMap> mapPasswordTemplate = workflowHookMaps.stream().filter(s -> s.getEmailTemplate().getMsgTmplName().equals(Constants.MESSAGE_TEMPLATE.FORGET_PASSWORD)).findFirst();
                            Map<String, String> placeholderValues = new HashMap<>();
                            placeholderValues.put("google_play", blobService + Constants.MESSAGE_TEMPLATE.GOOGLE_PLAY);
                            placeholderValues.put("facebook", blobService + Constants.MESSAGE_TEMPLATE.FACEBOOK);
                            placeholderValues.put("apple", blobService + Constants.MESSAGE_TEMPLATE.APPLE);
                            placeholderValues.put("linkedin", blobService + Constants.MESSAGE_TEMPLATE.LINKEDIN);
                            placeholderValues.put("youtube", blobService + Constants.MESSAGE_TEMPLATE.YOUTUBE);
                            placeholderValues.put("twitter", blobService + Constants.MESSAGE_TEMPLATE.TWITTER);
                            placeholderValues.put("solar_amps", blobService + Constants.MESSAGE_TEMPLATE.SOLAR_AMPS);
                            placeholderValues.put("company_name", masterTenant.getCompanyName() != null ? masterTenant.getCompanyName() : "");
                            placeholderValues.put("company_key", String.valueOf(userDTO.getCompKey()));
                            placeholderValues.put("company_logo", masterTenant.getCompanyLogo() != null ? masterTenant.getCompanyLogo() : null);
                            placeholderValues.put("user_name", String.valueOf(userByUsername.getUserName()));
                            String setPasswordUrl = null;
                            String companyLoginName = null;
                            if (masterTenant.getLoginUrl() != null) {
                                setPasswordUrl = masterTenant.getLoginUrl().split("#")[0] + "#" + "/reset-password";
                                companyLoginName = masterTenant.getLoginUrl().substring(masterTenant.getLoginUrl().lastIndexOf('/') + 1);
                            }
                            placeholderValues.put("login_url", setPasswordUrl);
                            placeholderValues.put("ID", getUniquePasswordLinkSalt(userDTO));
                            placeholderValues.put("company_login_name", companyLoginName);
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
                            String htmlCodeUserMsg = solarAmpsService.getMessage(mapPasswordTemplate.get().getEmailTemplate().getTemplateHTMLCode(), placeholderValues);
                            solarAmpsService.sendEmail(placeholderValues, userDTO.getEmailAddress(), null, null,
                                    mapPasswordTemplate.get().getEmailTemplate().getSubject(), null, mapPasswordTemplate.get().getEmailTemplate().getParentTmplId(), htmlCodeUserMsg);
                        }
                    } catch (Exception ex) {
                        ex.getMessage();
                        return new ResponseEntity<>(APIResponse.builder().message(ex.getMessage())
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.value()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                return new ResponseEntity<>(APIResponse.builder().message("Email sent successfully.")
                        .code(HttpStatus.OK.value()).build(), HttpStatus.OK);
            }

        }
    }


    private String getUniquePasswordLinkSalt(UserDTO userDTO) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat() * AppConstants.SALT_CHARS.length());
            salt.append(AppConstants.SALT_CHARS.charAt(index));
        }
        String saltStr = salt.toString();
        UniqueResetLink uniqueResetLink = new UniqueResetLink();
        uniqueResetLink.setTenantId(userDTO.getCompKey());
        uniqueResetLink.setUserAccount(userDTO.getAcctId());
        uniqueResetLink.setAdminAccount(null);
        uniqueResetLink.setUniqueText(saltStr);
        uniqueResetLink.setUsedIndicator(false);
        uniqueResetLinkService.save(uniqueResetLink);
        return saltStr;
    }


    //TODO: Need to remove when revamping customer management apis
    @Override
    public List<CaUserTemplateDTO> getAllCustomerListsByAcctId(Long accountId) {
        SimpleDateFormat formatter = new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT);
        List<UserTemplate> userTemplates = userRepository.getAllCustomerListsByAcctId(Arrays.asList(ECustomerDetailStates.CUSTOMER.getName()), accountId);
        List<CaUserTemplateDTO> userTemplateDtoList = new ArrayList<>();
        EntityDetail entityDetail;
        CaUserTemplateDTO userTemplateDTO;
        Optional<DocuLibrary> docuLibraryOptional;
        //getting sub count here
        Map subCountData = getAllCustomerListSubscriptionSize();
        Map<String, Long> subCount = (Map<String, Long>) subCountData.get("data");
        Map subscriptionData = getAllCustomerListSubscription();
        Map<String, List<MongoCustomerDetailWoDTO>> subscriptionDataId = (Map<String, List<MongoCustomerDetailWoDTO>>) subscriptionData.get("data");
        for (UserTemplate userTemplate : userTemplates) {
            userTemplateDTO = caUserTemplateDTO(userTemplate);
            //add profileUrl
            entityDetail = entityDetailRepository.findByEntityId(userTemplate.getEntityId());
            if (entityDetail != null) {
                userTemplateDTO.setProfileUrl(entityDetail.getUri() != null ? entityDetail.getUri() : null);
            }
            docuLibraryOptional = docuLibraryService.findByCodeRefId(String.valueOf(userTemplate.getAccountId())).stream().findFirst();
            if (docuLibraryOptional.isPresent()) {
                // String profileUrl = storageService.getBlobUrl(storageContainer, docuLibraryOptional.get().getUri());
                userTemplateDTO.setProfileUrl(docuLibraryOptional.get().getUri());
            }
            userTemplateDTO.setGeneratedAt(formatter.format(userTemplate.getGeneratedAt()));
            Optional<Long> firstCount = subCount.entrySet().stream()
                    .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
                    .map(e -> e.getValue())
                    .findFirst();
            Optional<List<MongoCustomerDetailWoDTO>> subscriptionId = subscriptionDataId.entrySet().stream()
                    .filter(v -> v.getKey().equals(String.valueOf(userTemplate.getAccountId())))
                    .map(e -> e.getValue())
                    .findFirst();
            if (firstCount.isPresent()) {
                userTemplateDTO.setSubscriptionTotal(String.valueOf(firstCount.get()));
            }
            userTemplateDTO.setMongoCustomerDetailWoDTO(subscriptionDataId.get(String.valueOf(userTemplateDTO.getAccountId())));
            userTemplateDtoList.add(userTemplateDTO);
        }
        return Arrays.asList(userTemplateDtoList.stream().findFirst().get());
    }

    @Override
    public List<YearDTO> countByYear() {
        LocalDateTime localDateTime = Utility.toLocalDateTime(new Date());
        List<YearDTO> yearDTOS = new ArrayList<>();
        try {
            for (int i = -4; i <= 0; i++) {
                LocalDateTime currentYear = localDateTime.plusYears(i);
                List<UserCountDTO> userCountDTOList = userRepository.countByYear(Utility.toLocalDateTime(
                                Utility.getStartOfYear(currentYear.toString(), "yyyy-MM-dd")),
                        Utility.toLocalDateTime(Utility.getEndOfYear(currentYear.toString(), "yyyy-MM-dd")));
                if (!userCountDTOList.isEmpty()) {
                    userCountDTOList.forEach(uc -> {
                        uc.setMonthName(Month.of(uc.getMonth()).name());
                    });
                    yearDTOS.add(YearDTO.builder().year(currentYear.getYear()).userCountDTOS(userCountDTOList).build());
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            ex.getMessage();
        }
        return yearDTOS;
    }

    @Override
    public User updateCaRegisteredUser(String sectionJson, MultipartFile image, UserDTO userDTO, String projectId, String requestType, Long compKey) {
        JsonNode measures = getMeasuresArrayForUpdate(sectionJson);
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
            } else if (measure.get("code").toString().replaceAll("^\"|\"$", "").equalsIgnoreCase(Constants.MEASURES_FOR_LEAD_GENERATION.DATE_OF_BIRTH)) {
                Object objDefaultValue = measure.get("default_value");
                if (!objDefaultValue.toString().equalsIgnoreCase("null") &&
                        !(objDefaultValue instanceof String) && measure.get("default_value") != null) {
                    String dateOfBirth = measure.get("default_value").toString().replace("{\"$date\":\"", "").replace("\"}", "");
                    userDTO.setDataOfBirth(Utility.convertStringDateInToDate(dateOfBirth));
                }
            }
        }
        User user = null;
        user = updateCaUser(userDTO, sectionJson, projectId, requestType, image, compKey);
        return user;
    }

    @Override
    public ErrorDTO validationCheck(String json, Boolean isSectionJson) {
        JsonNode measures = null;
        if (isSectionJson) {
            measures = getMeasuresArrayForUpdate(json);
        } else {
            measures = AcquisitionUtils.getMeasuresArray(json);
        }
        ErrorDTO errorDTO = ErrorDTO.builder().isValidationFailed(false).message("Successful").build();

        for (JsonNode measure : measures) {
            String code = measure.get("code").toString().replaceAll("^\"|\"$", "");

            switch (code) {
                case Constants.MEASURES_FOR_LEAD_GENERATION.F_NAME:
                    if (measure.get(Constants.CODE.DEFAULT_VALUE) != null && !isValidName(measure.get(Constants.CODE.DEFAULT_VALUE).asText()) || measure.get(Constants.CODE.DEFAULT_VALUE).asText().length() > 20) {
                        errorDTO.setValidationFailed(true);
                        errorDTO.setMessage("Invalid First Name");
                        return errorDTO;
                    }
                    break;
                case Constants.MEASURES_FOR_LEAD_GENERATION.L_NAME:
                    if (measure.get(Constants.CODE.DEFAULT_VALUE) != null && !isValidName(measure.get(Constants.CODE.DEFAULT_VALUE).asText()) || measure.get(Constants.CODE.DEFAULT_VALUE).asText().length() > 20) {
                        errorDTO.setValidationFailed(true);
                        errorDTO.setMessage("Invalid Last Name");
                        return errorDTO;
                    }
                    break;
                case Constants.MEASURES_FOR_LEAD_GENERATION.PHONE_NUMBER:
                    JsonNode defaultValueNode = measure.get(Constants.CODE.DEFAULT_VALUE);
                    if (defaultValueNode != null && !defaultValueNode.isNull()) {
                        String phoneNumber = defaultValueNode.asText();
                        if (!isValidPhoneNumber(phoneNumber)) {
                            errorDTO.setValidationFailed(true);
                            errorDTO.setMessage("Invalid Phone Number. It should be numeric and between 5 to 15 digits.");
                            return errorDTO;
                        }
                    }
                    break;
                case Constants.MEASURES_FOR_LEAD_GENERATION.EMAIL:
                    if (measure.get(Constants.CODE.DEFAULT_VALUE) != null && !isValidEmail(measure.get(Constants.CODE.DEFAULT_VALUE).asText())) {
                        errorDTO.setValidationFailed(true);
                        errorDTO.setMessage("Invalid email address");
                        return errorDTO;
                    }
                    break;
                case Constants.MEASURES_FOR_LEAD_GENERATION.LEGAL_BUISNESS_NAME:
                    if (measure.get(Constants.CODE.DEFAULT_VALUE) != null && measure.get(Constants.CODE.DEFAULT_VALUE).asText().length() > 40) {
                        errorDTO.setValidationFailed(true);
                        errorDTO.setMessage("Legal Business Name exceeds the limit of 40 characters.");
                        return errorDTO;
                    }
                    break;
            }
        }

        return errorDTO;
    }

    private boolean isValidName(String name) {
        return name.matches("^[a-zA-Z][a-zA-Z\\s.`\\-,]*[a-zA-Z\\s.]$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{5,15}");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    private User updateCaUser(UserDTO userDTO, String sectionJson, String projectId, String requestType, MultipartFile image, Long compKey) {
        Entity entity = entityService.findById(userDTO.getEntityId());
        String primaryIndAddress = AppConstants.PRIMARY_INDEX_LOCATION_FALSE;
        Integer countOfLocation = locationMappingRepository.findCountBySourceId(userDTO.getAcctId());
        if (countOfLocation == 0) {
            throw new NotFoundException("Please Provide Address");
        }
        /**
         * Validate email either already exist or not
         * If not exist already then process.
         */
        if (userDTO.getEmailAddress() == null) {
            throw new InvalidValueException("Email address must be provide");
        } else {
            Entity emailAddrExists = entityService.findByEmailAddressAndEntityType(userDTO.getEmailAddress(), entity.getEntityType()); // aaa  bbb ccc
            if (emailAddrExists == null) {
            } else if (emailAddrExists.getId().equals(userDTO.getEntityId())) {
            } else {
                throw new AlreadyExistsException("Email " + userDTO.getEmailAddress());
            }
        }
        dataExchange.addOrUpdateAcquisitionSection(sectionJson, projectId, requestType);
        User user = findById(userDTO.getAcctId());//findUserByEntityId(userDTO.getAcctId());
        user.setFirstName(userDTO.getFirstName() != null ? userDTO.getFirstName() : user.getFirstName());
        user.setLastName(userDTO.getLastName() != null ? userDTO.getLastName() : user.getLastName());
        user.setDataOfBirth(userDTO.getDataOfBirth() != null ? userDTO.getDataOfBirth() : user.getDataOfBirth());
        user.setEmailAddress(userDTO.getEmailAddress() != null ? userDTO.getEmailAddress() : user.getEmailAddress());

        CustomerDetail customerDetail = customerDetailService.findByEntity(entity);
        if (customerDetail.getStates().equals(EUserType.CUSTOMER.getName()) || user.getUserType().getName().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.CUSTOMER.getName());
        }
        if (customerDetail.getStates().equals(EUserType.CUSTOMER.getName()) || customerDetail.getStates().equals(EUserType.INTERIMCUSTOMER.getName()) || user.getUserType().getName().equals(EUserType.CUSTOMER.getName())) {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
        } else {
            user.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            customerDetail.setStates(ECustomerDetailStates.PROSPECT.getName());
        }
        entity.setContactPersonPhone(userDTO.getPhone() != null ? userDTO.getPhone() : entity.getContactPersonPhone());
        entity.setContactPersonEmail(userDTO.getEmailAddress() != null ? userDTO.getEmailAddress() : entity.getContactPersonEmail());
        if (user.getFirstName() != null && user.getLastName() != null) {
            entity.setEntityName(user.getFirstName().concat(" ").concat(user.getLastName()));
        }

        user = userRepository.save(user);
        if (user.getUserName() == null) {
            user.setUserName("CA_NONE_" + user.getAcctId());
            userRepository.save(user);
        }
        entityService.save(entity);
        customerDetailService.save(customerDetail);

        if (userDTO.getPhysicalLocations() != null) {
            List<PhysicalLocationDTO> physicalLocationDTOS = userDTO.getPhysicalLocations();
            List<PhysicalLocation> physicalLocation1 = physicalLocationRepository.findByIdIn(physicalLocationDTOS.stream().map(physicalLocationDTO -> physicalLocationDTO.getId()).collect(Collectors.toList()));
            List<LocationMapping> locationMappingDB = locationMappingService.findAllBySourceIdAndSourceType(userDTO.getAcctId(), "USER");
            if (locationMappingDB.isEmpty()) {
                primaryIndAddress = AppConstants.PRIMARY_INDEX_LOCATION_TRUE;
            }

            locationMappingService.saveAll(getAcquisitionLocationMappings(physicalLocation1, user, null, primaryIndAddress));
        }
        if (image != null) {
            checkIfImageAlreadyExists(entity, image, compKey);
        }
        return user;
    }

    private User saveCaRegisterationUser(UserDTO userDTO, String template, MultipartFile image, Long tenantId, String apiSource) {
        String oid = null;
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;
        List<MultipartFile> userMultipartFiles = null;
        if ((userDTO.getEmailAddress() == null || userDTO.getEmailAddress().isEmpty()) ||
                (userDTO.getFirstName() == null || userDTO.getFirstName().isEmpty()) ||
                (userDTO.getLastName() == null || userDTO.getLastName().isEmpty()) ||
                (userDTO.getCustomerType() == null || userDTO.getCustomerType().isEmpty())) {
            throw new AlreadyExistsException("You must provide these all <<< EmailAddress|FirstName|LastName|CustomerType >>> ");
        } else {
            if (userDTO.getEmailAddress() != null && entityService.findByEmailAddressAndEntityType(userDTO.getEmailAddress(), userDTO.getEntityType()) != null) {
                throw new AlreadyExistsException("Email " + userDTO.getEmailAddress() + " is already taken");
            }
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
            User finalUser = userRepository.save(user);

            if (finalUser.getAcctId() == null) {
                throw new NotFoundException("User not saved in DB");
            } else {
                finalUser.setUserName("CA_NONE_" + finalUser.getAcctId());
                userRepository.save(finalUser);
            }
            Entity entity = toEntity(userDTOtoEntity(userDTO, user, "Customer", EUserStatus.ACTIVE.getStatus(), true));
            entity.setContactPersonPhone(userDTO.getPhone());
            entity.setOrganization(organization);
            Entity entity1 = entityService.save(entity);
            String customerType = userDTO.getCustomerType();
            String states = ECustomerDetailStates.LEAD.toString();
            if ("customerManagement".equals(apiSource)) {
                states = ECustomerDetailStates.CUSTOMER.getName();
            }
            CustomerDetail customerDetail = toCustomerDetail(CustomerDetailDTO.builder().customerType(userDTO.getCustomerType())
                    .isContractSign(false).isActive(true).isCustomer(false).hasLogin(false).mobileAllowed(false).signUpDate(new Date())
                    .priorityIndicator(false).states(states).entityId(entity.getId()).status(EUserStatus.INACTIVE.getStatus()).build());
            customerDetail.setLeadSource(ECustomerDetailStates.MANUAL.getName());
            customerDetail = customerDetailService.save(customerDetail);
            userLevelPrivilegeService.save(UserLevelPrivilege.builder()
                    .user(user)
                    .createdAt(userDTO.getCreatedAt())
                    .updatedAt(userDTO.getUpdatedAt())
                    .entity(entity)
                    .organization(organization)
                    .build());
            try {
                BaseResponse data = dataExchange.saveOrUpdateAcquisitionProject(AcquisitionUtils.addEntityIdInTemplate(template, entity1.getId()));
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
            try {
                employeeManagementService.uploadToStorage(image, entity1.getId(), tenantId);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (StorageException e) {
                throw new RuntimeException(e);
            }
            UserMapping userMapping = userMappingService.save(new UserMappingDTO().builder()
                    .entityId(entity1.getId())
                    .ref_id(oid)
                    .module("Acquistion")
                    .build());
        }
        return user;
    }

    @Override
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

    private void checkIfImageAlreadyExists(Entity entity, MultipartFile image, Long compKey) {
        String uri = null;
        try {
            uri = storageService.storeInContainer(image, appProfile, "tenant/" + compKey
                    + AppConstants.PATHS.ENTITY_PROFILE_PICTURE, image.getOriginalFilename(), compKey, false);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (StorageException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        EntityDetail entityDetail = entityDetailRepository.findByEntityId(entity.getId());
        if (entityDetail != null) {
            entityDetail.setUri(uri);
            entityDetail.setFileName(image.getOriginalFilename());
            entityDetailRepository.save(entityDetail);
        } else {
            EntityDetail newEntityDetail = new EntityDetail();
            newEntityDetail.setEntity(entity);
            newEntityDetail.setUri(uri);
            newEntityDetail.setFileName(image.getOriginalFilename());
            entityDetailRepository.save(newEntityDetail);
        }
    }


    private String getEntityType(Long entityId) {

        Entity entity = entityRepository.findById(entityId).orElse(null);

        if (entity != null) {
            return entity.getEntityType();
        }
        return null;
    }

    @Override
    public com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse updateUserLandingPage(UserDTO userDTO) {
        com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse baseResponse = new com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse();
        try {
            if (userDTO.getAcctId() == null && userDTO.getEntityId() == null) {
                baseResponse.setMessage("No IDs provided.");
                baseResponse.setCode(HttpStatus.CONFLICT.value());
                return baseResponse;
            }
            if (userDTO.getEntityId() == null) {
                UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(userDTO.getAcctId());
                if (userLevelPrivilege != null && userLevelPrivilege.getEntity() != null) {
                    userDTO.setEntityId(userLevelPrivilege.getEntity().getId());
                }
            }
            String entityType = getEntityType(userDTO.getEntityId());

            if (entityType != null) {
                // Check if the entityType is "Customer" or "Employee" before updating the landing page
                if ("Customer".equalsIgnoreCase(entityType) || "Employee".equalsIgnoreCase(entityType)) {
                    baseResponse = updateLandingPageUrl(userDTO.getEntityId(), entityType, userDTO.getDefaultLandingPageUrl());
                } else {
                    baseResponse.setMessage("Invalid Entity Type. Only 'customer' or 'employee' are allowed.");
                    baseResponse.setCode(HttpStatus.CONFLICT.value());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return baseResponse;
    }

    @Override
    public com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse updateLandingPageUrl(Long entityId, String entityType, String landingDefaultUrl) {
        try {
            if ("Customer".equalsIgnoreCase(entityType)) {
                CustomerDetail customerDetail = customerDetailRepository.findByEntityId(entityId);
                customerDetail.setLandingDefaultUrl(landingDefaultUrl);
                customerDetailService.save(customerDetail);
            } else if ("Employee".equalsIgnoreCase(entityType)) {
                EmployeeDetail employeeDetail = employeeDetailRepository.findByEntityId(entityId);
                employeeDetail.setLandingDefaultUrl(landingDefaultUrl);
                employeeDetailService.save(employeeDetail);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse.builder().code(HttpStatus.CONFLICT.value()).message("ERROR").data(e.getMessage()).build();
        }
        return com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse.builder().code(HttpStatus.OK.value()).message("Landing page updated successfully").build();

    }

}