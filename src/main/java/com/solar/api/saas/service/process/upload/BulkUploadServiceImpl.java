package com.solar.api.saas.service.process.upload;

import com.solar.api.exception.NotFoundException;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.process.migration.MigrationParserFactory;
import com.solar.api.saas.service.process.upload.mapper.Customer;
import com.solar.api.saas.service.process.upload.mapper.CustomerMapper;
import com.solar.api.tenant.mapper.ca.CaUtilityMapper;
import com.solar.api.tenant.mapper.contract.ContractMapper;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeMapper;
import com.solar.api.tenant.mapper.cutomer.CustomerDetailDTO;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoMapper;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.mapper.user.UserMapper;
import com.solar.api.tenant.mapper.user.address.AddressMapper;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.EAuthenticationType;
import com.solar.api.tenant.model.user.userType.ECustomerDetailStates;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.repository.PaymentTransactionDetailRepository;
import com.solar.api.tenant.repository.PaymentTransactionHeadRepository;
import com.solar.api.tenant.repository.StageRepository;
import com.solar.api.tenant.repository.SubscriptionRateMatrixHeadRepository;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.ca.CaUtilityService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.contract.OrganizationService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.job.JobManagerTenantService;
import com.solar.api.tenant.service.override.measureDefinition.MeasureDefinitionOverrideService;
import com.solar.api.tenant.service.process.billing.invoice.BillInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.contract.EntityMapper.toEntity;
import static com.solar.api.tenant.mapper.contract.EntityMapper.userDTOtoEntity;
import static com.solar.api.tenant.mapper.cutomer.CustomerDetailMapper.toCustomerDetail;
import static com.solar.api.tenant.mapper.user.UserMapper.customerToUserDTO;

@Service
public class BulkUploadServiceImpl implements BulkUploadService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static String DEFAULT_PASSWORD = "novel1234+";
    @Autowired
    private MigrationParserFactory parserFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;

    @Autowired
    private EntityService entityService;
    @Autowired
    private PhysicalLocationService physicalLocationService;

    @Autowired
    private CaUtilityService caUtilityService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MeasureDefinitionOverrideService measureDefinitionOverrideService;
    @Autowired
    private BillInvoiceService billInvoiceService;
    @Autowired
    private BillingHeadService billingHeadService;
    @Autowired
    private JobManagerTenantService jobManagerTenantService;
    @Autowired
    private SubscriptionRateMatrixHeadRepository headRepository;
    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private PaymentTransactionHeadRepository transactionHeadRepository;
    @Autowired
    private PaymentTransactionDetailRepository transactionDetailRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private Utility utility;
    @Autowired
    private BulkUploadParser bulkUploadParser;
    @Autowired
    private StorageService storageService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;

    @Autowired
    private CustomerDetailService customerDetailService;

    @Value("${app.profile}")
    private String appProfile;


    @Override
    public UploadResponse upload(String entity, File file, List<Long> correctRowIds, Long rateMatrixId, Boolean isLegacy) {
        if (file == null) {
            return null;
        }
        try {
            Class clazz = Class.forName(EUploadEntitiy.get(entity).getEntityPath());
            try (FileInputStream in = new FileInputStream(file)) {
                if (clazz == User.class) {
                    return uploadUsersFromCSV(in, correctRowIds, null);
                } else if (clazz == Address.class) {
                    /**
                     * Dump data in
                     * Location Mapping
                     * Physical Location
                     */
                    return uploadAddressesFromCSV(in, correctRowIds, null);
                }
                   else if (clazz == Entity.class) {
                        return uploadLeadFromCSV(in, correctRowIds, null);
                } else if (clazz == PaymentInfo.class) {
                    return uploadPaymentInfosFromCSV(in, correctRowIds, null);
                } else if (clazz == CustomerSubscription.class) {
                    /**
                     * Dump data in mongo for subscriptions
                     */
                    return uploadSubscriptionMappingsFromCSV(in, correctRowIds, rateMatrixId, null, isLegacy);
                } else if (clazz == CaUtility.class) {
                    /**
                     * Dump data in caUtility
                     */
                    return uploadUsersFromCSV(in, correctRowIds, null);
                }
//                else if (clazz == PaymentTransactionDetail.class) {
//                    return uploadPaymentsFromCSV(parser, in, jobManagerTenant);
//                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public UploadResponse uploadUsersFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                             JobManagerTenant jobManagerTenant)
            throws IOException {
        List<User> importedUsers = bulkUploadParser.importCustomersFromCSV(inputStream, correctRowIds);
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
//        List<String> migratedExternalIds = new ArrayList<>();
//        List<String> updatedExternalIds = new ArrayList<>();
//        final AtomicBoolean[] toAdd = {new AtomicBoolean(false)};
//        List<String> passwords = new ArrayList<>();
        List<User> usersToSave = new ArrayList<>();
//        List<Stage> stagesToSave = new ArrayList<>();
//        String encodedDefaultPassword = encoder.encode(DEFAULT_PASSWORD);
//        String finalDefaultEmail = "customerservice@novelenergy.biz";
        for (User importedUser : importedUsers) {///////////////////////////
            if (importedUser.getAction().equals(EUploadAction.INSERT.getAction())) {
                createdCount.getAndIncrement();
//                toAdd[0].set(true);
                importedUser.setPassword(importedUser.getUploadPassword());
            } else if (importedUser.getAction().equals(EUploadAction.UPDATE.getAction())) {
                if (importedUser.getAcctId() != null) {
                    User userFromDb = userService.findByIdNoThrow(importedUser.getAcctId()); //?
                    if (userFromDb != null) {
                        String uploadPassword = importedUser.getUploadPassword();
                        if (uploadPassword != null) {
                            userFromDb.setPassword(uploadPassword);
                        }
                        importedUser = UserMapper.toUpdatedUser(userFromDb, importedUser);
                        importedUser.setUploadPassword(uploadPassword);
                        updatedCount.getAndIncrement();
                        updatedIds.add(userFromDb.getAcctId());

                    }
                }
            }
            ///////////////////////////
//            Stage stage = stageRepository.findByExternalId(user.getExternalId());
            /*if (stage != null) {
                user.setAcctId(stage.getUser().getAcctId());
                user.setCreatedAt(stage.getUser().getCreatedAt());
                updatedCount.getAndIncrement();
                updatedIds.add(stage.getUser().getAcctId());
                updatedExternalIds.add(user.getExternalId());
            } else {
                migratedCount.getAndIncrement();
                toAdd[0].set(true);
            }*/
            if (importedUser.getEmailAddress() == null || importedUser.getEmailAddress().isEmpty()) {
                // If user doesn’t have an Email address Please keep user column blank and email address to be a
                // default novel email id
                importedUser.setEmailAddress(FINAL_DEFAULT_EMAIL);
                // In cases the user column is blank the system will auto generate user id as FirstName + portal id
                // example SAM421 or COLIN211
            }
            if (importedUser.getUserName() == null) {
                importedUser.setUserName("");
            }
            if (importedUser.getUploadPassword() != null) {
//                passwords.add(user.getPassword());
                importedUser.setPassword(encoder.encode(importedUser.getUploadPassword()));
            } else {
                importedUser.setPassword("");
            }

            importedUser.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.findByName(ERole.ROLE_CUSTOMER.toString()));
            importedUser.setRoles(roles);

            importedUser.setStatus(EUserStatus.ACTIVE.getStatus());
            Date date = new Date();
            if (importedUser.getActiveDate() == null) {
                importedUser.setActiveDate(date);
            }
            if (importedUser.getRegisterDate() == null) {
                importedUser.setRegisterDate(date);
            }
            usersToSave.add(importedUser);
//            if (toAdd[0].get()) {
//                migratedExternalIds.add(user.getExternalId());
//                toAdd[0] = new AtomicBoolean(false);
//            }
        }
        /*List<String> uidPass = new ArrayList<>();
        usersToSave = updateUidPass(usersToSave, uidPass);*/
        List<User> savedUsers = userService.saveAll(usersToSave);

        /**
         * To save data in following tables
         *
         * Entity
         * Account
         * CustomerDetail
         * UserLevelPrivilege
         *
         */
        entityUpload(savedUsers);
        savedUsers.forEach(user -> {
            if (!updatedIds.contains(user.getAcctId())) {
                createdIds.add(user.getAcctId());
//                stagesToSave.add(Stage.builder()
//                        .user(user)
//                        .externalId(user.getExternalId())
//                        .build());
            }
        });
//        stageRepository.saveAll(stagesToSave);
        List<String> uidPass = new ArrayList<>();
        savedUsers = postSaveCustomers(savedUsers, uidPass);
//        List<String> userNames = new ArrayList<>();
//        savedUsers.forEach(user -> {
//            userNames.add(user.getUserName());
//        });
        LOGGER.info(updatedCount.get() + " updated and " + createdCount.get() + " created");
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EUploadEntitiy.CUSTOMER.toString())
                .created(createdCount.get())
                .updated(updatedCount.get())
                .createdIds(createdIds)
                .updatedIds(updatedIds)
//                .migratedExternalIds(migratedExternalIds)
//                .updatedExternalIds(updatedExternalIds)
//                .userNames(userNames)
//                .passwords(passwords)
                .uidPass(uidPass)
                .responseUrl(uploadUserUploadResult(correctRowIds, uidPass, "Line", "Login name", "Password")) // Upload to starage
                .build();
    }

    private void entityUpload(List<User> savedUsers) {

        savedUsers.forEach(u -> {
            Entity entity = userService.saveInEntity(Entity.builder()
                    .entityName(u.getFirstName() + " " + u.getLastName())
                    .entityType(u.getUserType() != null ? u.getUserType().getName().getName() : null)
                    .status(u.getStatus())
                    .isDocAttached(null)
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .companyName(null)
                    .contactPersonEmail(u.getEmailAddress() != null ? u.getEmailAddress() : null)
                    .contactPersonPhone(null)
                    .website(null)
                    .isActive(true)
                    .registerId(null)
                    .registerType(null)
                    .build());
            userService.saveInCustomerDetail(CustomerDetail.builder()
                    .entityId(entity.getId())
                    .states(u.getUserType() != null ? u.getUserType().getName().getName() : null)
                    .isCustomer(true).build());
            userService.saveInUserLevelPrivilege(UserLevelPrivilege.builder()
                    .entity(entity)
//                    .account()
                    .build());
        });
    }

    @Override
    public UploadResponse uploadEntityFromCSV(InputStream inputStream, List<Long> correctRowIds, JobManagerTenant jobManagerTenant) throws IOException {
        return null;
    }

    @Override
    public UploadResponse uploadLocationsFromCSV(InputStream inputStream, List<Long> correctRowIds, JobManagerTenant jobManagerTenant) throws IOException {
        List<PhysicalLocation> importedLocations = bulkUploadParser.importLocationsFromCSV(inputStream, correctRowIds);
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<PhysicalLocation> physicalLocationsToSave = new ArrayList<>();
        importedLocations.forEach(location -> {
            /////////////////
            Entity entity = entityService.findById(location.getEntityId());
            location.setEntityId(entity.getId());
            if (location.getAction().equals(EUploadAction.INSERT.getAction())) {
                createdCount.getAndIncrement();
//                toAdd[0].set(true);
                physicalLocationsToSave.add(location);
            } else if (location.getAction().equals(EUploadAction.UPDATE.getAction())) {
                if (location.getId() != null) {
                    PhysicalLocation physicalLocationFromDB = physicalLocationService.findById(location.getId()); //?
                    if (physicalLocationFromDB != null) {
                        location = PhysicalLocationMapper.toUpdatedPhysicalLocation(physicalLocationFromDB, location);
                        updatedCount.getAndIncrement();
                        updatedIds.add(physicalLocationFromDB.getId());
                        physicalLocationsToSave.add(location);
                    }
                }
            }
            if (location.getAdd1() == null) {
                location.setAdd1("");
            }
            if (location.getLocationType() == null) {
                location.setLocationType("");
            }
            /////////////////
        });
        List<PhysicalLocation> savedAddresses = physicalLocationService.saveAll(physicalLocationsToSave);
        savedAddresses.forEach(address -> {
            if (!updatedIds.contains(address.getId())) {
                createdIds.add(address.getId());
            }
        });
        LOGGER.info(updatedCount.get() + " updated and " + createdCount.get() + " created");
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EUploadEntitiy.ADDRESS.toString())
                .created(createdCount.get())
                .updated(updatedCount.get())
                .createdIds(createdIds)
                .updatedIds(updatedIds)
                .build();
    }

    @Override
    public UploadResponse uploadUtilitiesFromCSV(InputStream inputStream, List<Long> correctRowIds, JobManagerTenant jobManagerTenant) throws IOException {
        List<CaUtility> importedUtilities = bulkUploadParser.importUtilitiesFromCSV(inputStream, correctRowIds);
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<CaUtility> caUtilitiesToSave = new ArrayList<>();
        importedUtilities.forEach(utility -> {
            /////////////////
            Entity entity = entityService.findById(utility.getEntityId());
            if (entity != null) {
                utility.setEntity(entity);
            }
            if (utility.getAction().equals(EUploadAction.INSERT.getAction())) {
                createdCount.getAndIncrement();
//                toAdd[0].set(true);
                caUtilitiesToSave.add(utility);
            } else if (utility.getAction().equals(EUploadAction.UPDATE.getAction())) {
                if (utility.getId() != null) {
                    CaUtility caUtilityFromDB = caUtilityService.getById(utility.getId()); //?
                    if (caUtilityFromDB != null) {
                        CaUtilityMapper.toUpdateCaUtility(caUtilityFromDB, utility);
                        updatedCount.getAndIncrement();
                        updatedIds.add(caUtilityFromDB.getId());
                        caUtilityService.save(CaUtilityMapper.toCaUtilityDTO(utility));
                    }
                }
            }
            /////////////////
        });
        List<CaUtility> savedUtilities = caUtilityService.saveAll(caUtilitiesToSave);
        savedUtilities.forEach(address -> {
            if (!updatedIds.contains(address.getId())) {
                createdIds.add(address.getId());
            }
        });
        LOGGER.info(updatedCount.get() + " updated and " + createdCount.get() + " created");
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EUploadEntitiy.CAUTILITY.toString())
                .created(createdCount.get())
                .updated(updatedCount.get())
                .createdIds(createdIds)
                .updatedIds(updatedIds)
                .build();
    }

    private List<User> postSaveCustomers(List<User> usersToDb, List<String> uidPass) {
        List<User> usersWithUserName = new ArrayList<>();
        usersToDb.forEach(user -> {
            // In cases the user column is blank the system will auto generate user id as first_name + acct_id
            // example SAM421 or COLIN211
            if (user.getUserName() == null || user.getUserName().isEmpty()) {
                user.setUserName(user.getFirstName().trim() + user.getAcctId());
                usersWithUserName.add(user);
            }
            // acct_id + first_name + "!"
            String password = null;
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                password = user.getAcctId() + user.getFirstName() + "!";
                user.setPassword(encoder.encode(password));
            }
            if (user.getUploadPassword() != null) {
                password = user.getUploadPassword();
            }

            uidPass.add(user.getUserName() + " / " + password);
        });
//        return userService.saveAll(usersWithUserName);
        return usersWithUserName;
    }

    private String uploadUserUploadResult(List<Long> correctRowIds, List<String> uidPass, String... headers) {
        List<List> values = new ArrayList<>();
        for (int i = 0; i < uidPass.size(); i++) {
            List<Object> row = new ArrayList<>();
            row.add(correctRowIds.get(i) + 2);
            String[] uidPassParts = uidPass.get(i).split("/");
            row.add(uidPassParts[0].trim());
            row.add(uidPassParts[1].trim());
            values.add(row);
        }
        byte[] bytes = Utility.getCSVBytes(Arrays.asList(headers), values);
        return Utility.uploadToStorage(storageService, bytes, appProfile,
                "tenant/" + utility.getCompKey() + "/upload/responses",
                "Bulk User Upload - " + new Date() + ".csv", utility.getCompKey(), false);
    }

    @Override
    public UploadResponse uploadAddressesFromCSV(InputStream inputStream,
                                                 List<Long> correctRowIds, JobManagerTenant jobManagerTenant)
            throws IOException {
        List<Address> importedAddresses = bulkUploadParser.importAddressesFromCSV(inputStream, correctRowIds);
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<Address> addressesToSave = new ArrayList<>();
        importedAddresses.forEach(address -> {
            /////////////////
            User user = userService.findById(address.getAcctId());
            address.setUserAccount(user);
            if (address.getAction().equals(EUploadAction.INSERT.getAction())) {
                createdCount.getAndIncrement();
//                toAdd[0].set(true);
                addressesToSave.add(address);
            } else if (address.getAction().equals(EUploadAction.UPDATE.getAction())) {
                if (address.getId() != null) {
                    Address addressFromDb = addressService.findByIdNoThrow(address.getId()); //?
                    if (addressFromDb != null) {
                        address = AddressMapper.toUpdatedAddress(addressFromDb, address);
                        updatedCount.getAndIncrement();
                        updatedIds.add(addressFromDb.getId());
                        addressesToSave.add(address);
                    }
                }
            }
            if (address.getAddress1() == null) {
                address.setAddress1("");
            }
            if (address.getAddressType() == null) {
                address.setAddressType("");
            }
            /////////////////
        });
        List<Address> savedAddresses = addressService.save(addressesToSave);
        savedAddresses.forEach(address -> {
            if (!updatedIds.contains(address.getId())) {
                createdIds.add(address.getId());
            }
        });
        LOGGER.info(updatedCount.get() + " updated and " + createdCount.get() + " created");
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EUploadEntitiy.ADDRESS.toString())
                .created(createdCount.get())
                .updated(updatedCount.get())
                .createdIds(createdIds)
                .updatedIds(updatedIds)
                .build();
    }

    @Override
    public UploadResponse uploadPaymentInfosFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                                    JobManagerTenant jobManagerTenant) throws IOException {
        List<PaymentInfo> importedPaymentInfos = bulkUploadParser.importPaymentInfoFromCSV(inputStream, correctRowIds);
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<PaymentInfo> savedPaymentInfos = new ArrayList<>();
//        List<String> migratedExternalIds = new ArrayList<>();
//        List<String> updatedExternalIds = new ArrayList<>();
//        final AtomicBoolean[] toAdd = {new AtomicBoolean(false)};
//        List<String> invalidExternalIds = new ArrayList<>();
        importedPaymentInfos.forEach(paymentInfo -> {
//            Stage stage = stageRepository.findByExternalId(paymentInfo.getExternalId());
//            if (stage == null) {
//                invalidExternalIds.add(paymentInfo.getExternalId());
//            } else {
            /////////////////
            User user = userService.findById(paymentInfo.getAcctId());
            paymentInfo.setPortalAccount(user);
            paymentInfo.setAcctId(user.getAcctId());
            if (paymentInfo.getAction().equals(EUploadAction.INSERT.getAction())) {
                createdCount.getAndIncrement();
//                toAdd[0].set(true);

            } else if (paymentInfo.getAction().equals(EUploadAction.UPDATE.getAction())) {
                if (paymentInfo.getId() != null) {
                    PaymentInfo paymentInfoFromDb = paymentInfoService.findByIdNoThrow(paymentInfo.getId()); //?
                    if (paymentInfoFromDb != null) {
                        paymentInfo = PaymentInfoMapper.toUpdatedPaymentInfo(paymentInfoFromDb, paymentInfo);
                        updatedCount.getAndIncrement();
                        updatedIds.add(paymentInfoFromDb.getId());
                    }
                }
            }
            /////////////////
                /*User user = userService.findById(paymentInfo.getAcctId());
                paymentInfo.setPortalAccount(user);
                paymentInfo.setAcctId(user.getAcctId());
                PaymentInfo paymentInfoFromDb =
                    paymentInfoService.findById(paymentInfo.getId());
                if (paymentInfoFromDb == null) {
                    migratedCount.getAndIncrement();
                    toAdd[0].set(true);
                } else {
                    paymentInfo.setId(paymentInfoFromDb.getId());
                    paymentInfo.setCreatedAt(paymentInfoFromDb.getCreatedAt());
                    updatedCount.getAndIncrement();
                    updatedIds.add(paymentInfoFromDb.getId());
//                    updatedExternalIds.add(paymentInfo.getExternalId());
                }*/
            savedPaymentInfos.add(paymentInfoService.addOrUpdate(paymentInfo));
//                if (toAdd[0].get()) {
//                    createdIds.add(pInfo.getId());
////                    migratedExternalIds.add(paymentInfo.getExternalId());
//                    toAdd[0] = new AtomicBoolean(false);
//                }
//            }
        });
        LOGGER.info(updatedCount.get() + " updated and " + createdCount.get() + " created");
//        if (!invalidExternalIds.isEmpty()) {
//            LOGGER.info("Following invalid external ids found: " + invalidExternalIds.toArray(new String[0]));
//        }
        savedPaymentInfos.forEach(pInfo -> {
            if (!updatedIds.contains(pInfo.getId())) {
                createdIds.add(pInfo.getId());
            }
        });
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EUploadEntitiy.PAYMENT_INFO.toString())
                .created(createdCount.get())
                .updated(updatedCount.get())
                .createdIds(createdIds)
                .updatedIds(updatedIds)
//                .migratedExternalIds(migratedExternalIds)
//                .updatedExternalIds(updatedExternalIds)
//                .invalidExternalIds(invalidExternalIds)
//                .unmappedExternalIds(stageRepository.findExternalIdsWithoutPaymentInfo())
                .build();
    }

    @Override
    public UploadResponse uploadSubscriptionMappingsFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                                            Long rateMatrixId, JobManagerTenant jobManagerTenant, Boolean isLegacy)
            throws IOException {
        Map<CustomerSubscription, List<CustomerSubscriptionMapping>> migratedSubscriptionMappings =
                bulkUploadParser.importSubscriptionMappingsFromCSV(inputStream, correctRowIds, rateMatrixId);
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<Long> updatedIds = new ArrayList<>();
        List<MeasureDefinitionTenantDTO> measureDefinitions =
                measureDefinitionOverrideService.findByCodes(migratedSubscriptionMappings.values().stream().flatMap(List::stream).collect(Collectors.toList()).stream().map(m -> m.getRateCode()).collect(Collectors.toSet()));

        migratedSubscriptionMappings.entrySet().forEach(mapping -> {

            //////////////////////////////////
            if (mapping.getKey().getAction().equals(EUploadAction.INSERT.getAction())) {
                createdCount.getAndIncrement();
//                toAdd[0].set(true);

            } else if (mapping.getKey().getAction().equals(EUploadAction.UPDATE.getAction())) {
                if (mapping.getKey().getId() != null) {
//                    CustomerSubscription subscriptionFromDb = subscriptionService.findCustomerSubscriptionByIdNoThrow(mapping.getKey().getId()); //?
//                    if (subscriptionFromDb != null) {
//                        subscriptionFromDb = CustomerSubscriptionMapper.toUpdatedCustomerSubscription(subscriptionFromDb, mapping.getKey());
                    updatedCount.getAndIncrement();
                    updatedIds.add(mapping.getKey().getId());
//                    }
                }
            }
            //////////////////////////////////
            CustomerSubscription key = mapping.getKey();
//            String externalId = key.getExternalId();
//            Stage stage = stageRepository.findByExternalId(externalId);
//            if (stage == null) {
//                invalidExternalIds.add(externalId);
//            } else {
            User user = userService.findByIdNoThrow(key.getUserAccountId());
            key.setUserAccount(user);
//                key.setUserAccountId(user.getAcctId());
            key.setSubscriptionStatus(ESubscriptionStatus.INACTIVE.getStatus());
            SubscriptionRateMatrixHead matrixHead =
                    headRepository.findById(key.getSubscriptionRateMatrixId()).get();
            key.setSubscriptionTemplate(matrixHead.getSubscriptionTemplate());
            key.setCustomerSubscriptionMappings(new ArrayList<>());
            List<CustomerSubscriptionMapping> customerSubscriptionMappingList = removeUnmatchedCSMRateCods(mapping.getValue(), measureDefinitions);
            customerSubscriptionMappingList.forEach(m -> {
                m.setSubscription(key);
                m.setSubscriptionRateMatrixHead(matrixHead);
                m.setMeasureDefinition(measureDefinitions.stream().filter(fd -> fd.getCode().equals(m.getRateCode())).findFirst().get());
                //INVRPTID invoice report id
                m.setMeasureDefinitionId(measureDefinitions.stream().filter(fd -> fd.getCode().equals(m.getRateCode())).findFirst().get().getId());

            });
            key.setCustomerSubscriptionMappings(customerSubscriptionMappingList);
//                migratedExternalIds.add(externalId);
//                migratedCount.getAndIncrement();
//            }
        });

        List<CustomerSubscription> subscriptions = subscriptionService.addCustomerSubscriptions(migratedSubscriptionMappings.keySet().stream().collect(Collectors.toList()), isLegacy);

//        createdIds.addAll(subscriptions.stream().map(s -> s.getId()).collect(Collectors.toList()));
        createdIds.addAll(subscriptions.stream().filter(cs -> !updatedIds.contains(cs.getId())).map(s -> s.getId()).collect(Collectors.toList()));

        LOGGER.info(createdCount.get() + " subscriptions created");
//        if (!invalidExternalIds.isEmpty()) {
//            LOGGER.info("Following invalid external ids found: " + invalidExternalIds.toArray(new String[0]));
//        }
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        /*subscriptions.forEach(s -> {
            if (!updatedIds.contains(s.getId())) {
                createdIds.add(s.getId());
            }
        });*/
        return UploadResponse.builder()
                .entityType(EUploadEntitiy.CUSTOMER_SUBSCRIPTION.toString())
                .created(createdCount.get())
                .createdIds(createdIds)
                .updated(updatedCount.get())
                .updatedIds(updatedIds)
                .build();
    }

    private static List<CustomerSubscriptionMapping> removeUnmatchedCSMRateCods(List<CustomerSubscriptionMapping> csmList, List<MeasureDefinitionTenantDTO> measureDefinitions) {
        List<CustomerSubscriptionMapping> customerSubscriptionMappingList = new ArrayList<>();
        measureDefinitions.forEach(md -> {
            Optional<CustomerSubscriptionMapping> customerSubscriptionMapping = csmList.stream()
                    .filter(csm -> csm.getRateCode().equalsIgnoreCase(md.getCode())).findFirst();
            if (customerSubscriptionMapping.isPresent()) {
                customerSubscriptionMappingList.add(customerSubscriptionMapping.get());
            }
        });
        return customerSubscriptionMappingList;
    }
    /*@Override
    public UploadResponse uploadPaymentsFromCSV(EMigrationParserLocation parser, InputStream inputStream,
                                                JobManagerTenant jobManagerTenant) throws IOException {
        billInvoiceService.addPaymentTransactionHeadsForInvoices();
        String location = parser.getLocation();
        MigrationParser migrationParser =
                parserFactory
                        .getMigrationParser(location);
        List<PaymentTransactionDetail> transactionDetails = migrationParser.importCustomerPaymentFromCSV(inputStream);
        List<PaymentTransactionDetail> toSave = new ArrayList<>();
        List<Long> invalidInvoiceIds = new ArrayList<>();
        List<Long> addedDetailIds;
        List<Long> updatedDetailIds = new ArrayList<>();
        List<Long> invalidDetailIds = new ArrayList<>();
        List<String> invalidCodes = new ArrayList<>();
        transactionDetails.forEach(detail -> {
            Long invoiceRefId = detail.getInvoiceRefId();
            if (invoiceRefId == null) {
                return;
            }
            BillingInvoice billingInvoice;
            try {
                billingInvoice = billInvoiceService.findById(invoiceRefId);
            } catch (NotFoundException e) {
                // if invalid skip and log
                LOGGER.error("Invalid invoice_ref_id ", invoiceRefId, e);
                invalidInvoiceIds.add(invoiceRefId);
                return;
            }
            String paymentCode = detail.getPaymentCode();
            if (!"BILLING".equalsIgnoreCase(paymentCode)) {
                LOGGER.warn("payment_code is not BILLING (" + paymentCode + ")");
                invalidCodes.add(paymentCode);
                return;
            }
            addToSaveable(detail, billingInvoice, toSave, updatedDetailIds, invalidDetailIds);
        });
        List<PaymentTransactionDetail> details = transactionDetailRepository.saveAll(toSave);
        addedDetailIds = details.stream().map(d -> d.getPayDetId()).collect(Collectors.toList());
        addedDetailIds.removeAll(updatedDetailIds);
        addedDetailIds.removeAll(invalidDetailIds);

        List<Long> markedPaidIds = postImportPayments(details);
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EUploadEntitiy.PAYMENT_TRANSACTION_DETAIL.toString())
                .invalidInvoiceIds(invalidInvoiceIds)
                .addedDetailIds(addedDetailIds)
                .updatedDetailIds(updatedDetailIds)
                .invalidDetailIds(invalidDetailIds)
                .invalidCodes(invalidCodes)
                .markedPaidIds(markedPaidIds)
                .build();
    }

    private void addToSaveable(PaymentTransactionDetail detail, BillingInvoice billingInvoice,
                               List<PaymentTransactionDetail> toSave,
                               List<Long> updatedDetailIds,
                               List<Long> invalidDetailIds) {
        BillingHead billingHead = billingHeadService.findByInvoice(billingInvoice);
        if (billingHead.getBillStatus().equals(EBillStatus.PAID.getStatus())) {
            LOGGER.info("Cannot process billing invoice " + billingInvoice.getId() + " is " + EBillStatus.PAID
            .getStatus());
            return;
        }
        Long payDetId = detail.getPayDetId();
        PaymentTransactionHead transactionHead = transactionHeadRepository.findByInvoice(billingInvoice);
        List<PaymentTransactionDetail> details =
                transactionDetailRepository.findByPaymentTransactionHead(transactionHead);
        Date reconDate = detail.getReconDate();
        if (reconDate != null) {
            if (Utility.isBefore(DateTime.now().toDate(), reconDate)) {
                detail.setReconDate(null);
                detail.setReconExpectedDate(reconDate);
            }
        }
        if ((detail.getIssuerId() != null && !detail.getIssuerId().isEmpty()) || "MANUAL".equals(detail
        .getIssuerReconStatus())) {
            detail.setIssuerReconStatus(EReconStatus.get("COMPLETED").getStatus());
        }
        detail.setAmt(utility.round(detail.getAmt(), utility.getCompanyPreference().getRounding()));
        if (payDetId == null) {
            detail.setLineSeqNo((details.size() +
                    toSave.stream().filter(s -> s.getPayDetId() == null && s.getInvoiceRefId().longValue() ==
                    billingInvoice.getId().longValue()).count() + 1));
            detail.setPaymentTransactionHead(transactionHead);
            toSave.add(detail);
        } else {
            Optional<PaymentTransactionDetail> paymentTransactionDetailDb =
                    transactionDetailRepository.findById(payDetId);
            if (!paymentTransactionDetailDb.isPresent()) {
                // if present and not in record skip and log
                LOGGER.warn("Invalid pay_det_id " + payDetId);
                invalidDetailIds.add(payDetId);
                return;
            } else {
                PaymentTransactionDetail paymentTransactionDetail = paymentTransactionDetailDb.get();
                if ("INVALID".equalsIgnoreCase(paymentTransactionDetail.getStatus())) {
                    // if invalid status then override whole record and log
                    paymentTransactionDetail = PaymentMapper.resetPaymentTransactionDetail(paymentTransactionDetail,
                            detail);
                    LOGGER.info("PaymentTransactionDetail record " + payDetId + " has been revalidated");
                } else {
                    paymentTransactionDetail =
                            PaymentMapper.toUpdatedPaymentTransactionDetail(paymentTransactionDetail, detail);
                }
                paymentTransactionDetail.setInvoiceRefId(billingInvoice.getId());
                toSave.add(paymentTransactionDetail);
                updatedDetailIds.add(payDetId);
            }
        }
    }

    private List<Long> postImportPayments(List<PaymentTransactionDetail> details) {
        List<Long> markedPaidIds = new ArrayList<>();
        details.stream().filter(utility.distinctByKey(PaymentTransactionDetail::getPaymentTransactionHead)).forEach
        (detail -> {
            PaymentTransactionHead transactionHead = detail.getPaymentTransactionHead();
            BillingInvoice invoice = transactionHead.getInvoice();
            List<PaymentTransactionDetail> paymentDetails =
                    transactionDetailRepository.findByPaymentTransactionHead(transactionHead);
            Double totalAmt = paymentDetails.stream().mapToDouble(d -> d.getAmt()).sum();
            int rounding = utility.getCompanyPreference().getRounding();
            totalAmt = utility.round(totalAmt, rounding);
            transactionHead.setNet(totalAmt);
            transactionHeadRepository.save(transactionHead);
            BillingHead billingHead = billingHeadService.findByInvoice(invoice);
            Double invoiceAmount = utility.round(billingHead.getAmount(), rounding);
            Double variance = totalAmt - invoiceAmount;
            if (variance >= 0) {
                billingHead.setBillStatus(EBillStatus.PAID.getStatus());
                markedPaidIds.add(billingHeadService.addOrUpdateBillingHead(billingHead).getId());
            } else {
                LOGGER.info("Variance " + variance + " less than total invoice");
            }
        });
        return markedPaidIds;
    }*/

    @Override
    public UploadResponse uploadLeadFromCSV(InputStream inputStream, List<Long> correctRowIds,
                                             JobManagerTenant jobManagerTenant)
            throws IOException, ParseException {
        List<Customer> importedUsers = bulkUploadParser.importLeadsFromCSV(inputStream, correctRowIds);
        AtomicInteger createdCount = new AtomicInteger();
        List<Long> createdIds = new ArrayList<>();
        List<User> usersToSave = new ArrayList<>();
        List<CustomerDetail> customerDetailsToSave = new ArrayList<>();
        List<User> savedUsers =  new ArrayList<>();
        for (Customer importedCustomer : importedUsers) {
            User userMapped = CustomerMapper.toUser(importedCustomer);
            if (importedCustomer.getAction().equals(EUploadAction.INSERT.getAction())) {
                createdCount.getAndIncrement();
                userMapped.setPassword(userMapped.getUploadPassword());
                userMapped.setAuthentication(EAuthenticationType.NA.getName());
                userMapped.setUserType(userTypeService.findByName(EUserType.CUSTOMER));
                Set<Role> roles = new HashSet<>();
                roles.add(roleService.findByName(ERole.ROLE_PROSPECT.toString()));
                userMapped.setRoles(roles);
                userMapped.setStatus(EUserStatus.INACTIVE.getStatus());
                userMapped.setRegisterDate(new Date());
                //userMapped.setCreatedAt(LocalDateTime.parse(importedCustomer.getCreationDate()));
                Organization organization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId("ACTIVE", true, null);
                if (organization == null)
                    throw new NotFoundException(Organization.class, "PrimaryIndicator,Status", "true, active");
                UserDTO userDTO = customerToUserDTO(importedCustomer);
                Entity entity = toEntity(userDTOtoEntity(userDTO, userMapped, "Customer", EUserStatus.ACTIVE.getStatus(),true));
                entity.setOrganization(organization);
                User savedUser = userService.saveUser(userMapped);
                savedUsers.add(savedUser);
                entityService.save(entity);
                CustomerDetail customerDetail = toCustomerDetail(CustomerDetailDTO.builder().customerType(userDTO.getCustomerType())
                        .isContractSign(false).isActive(true).isCustomer(false).hasLogin(false).mobileAllowed(false).signUpDate(new Date())
                        .priorityIndicator(false).states(ECustomerDetailStates.LEAD.toString()).entityId(entity.getId()).leadSource(userDTO.getLeadSource())
                        .status(EUserStatus.INACTIVE.getStatus()).build());

                customerDetailsToSave.add(customerDetail);
                UserLevelPrivilege userLevelPrivilege = UserLevelPrivilege.builder()
                        .user(userMapped)
                        .createdAt(userDTO.getCreatedAt())
                        .updatedAt(userDTO.getUpdatedAt())
                        .entity(entity)
                        .organization(organization)
                        .build();
                userLevelPrivilege.setEntity(entity);
                userLevelPrivilege.setOrganization(organization);
                userMapped.setUserLevelPrivileges(Arrays.asList(userLevelPrivilege));
            }
        }
        leadUploadInChildTables(savedUsers,customerDetailsToSave);
        savedUsers.forEach(user -> {
                createdIds.add(user.getAcctId());
        });
        List<String> uidPass = new ArrayList<>();
        if (jobManagerTenant != null) {
            jobManagerTenantService.setCompleted(jobManagerTenant, LOGGER);
        }
        return UploadResponse.builder()
                .entityType(EUploadEntitiy.CUSTOMER.toString())
                .created(createdCount.get())
                .createdIds(createdIds)
                .uidPass(uidPass)
                .responseUrl(uploadUserUploadResult(correctRowIds, uidPass, "Line", "Login name", "Password")) // Upload to starage
                .build();
    }

    private void leadUploadInChildTables(List<User> savedUsers,List<CustomerDetail> customerDetails) {
        savedUsers.forEach(u -> {
            Optional<UserLevelPrivilege> userLevelPrivilegeOptional = u.getUserLevelPrivileges().stream().findFirst();
            if(userLevelPrivilegeOptional.isPresent()) {
                userLevelPrivilegeService.save(userLevelPrivilegeOptional.get());
            }
        });
        customerDetailService.saveAll(customerDetails);
    }

}
