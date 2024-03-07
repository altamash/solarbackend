package com.solar.api.saas.service.process.upload.v2.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.Constants;
import com.solar.api.helper.Acquisition.AcquisitionUtils;
import com.solar.api.helper.Utility;
import com.solar.api.saas.model.attribute.PortalAttributeSAAS;
import com.solar.api.saas.model.attribute.PortalAttributeValueSAAS;
import com.solar.api.saas.service.PortalAttributeSAASService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.BaseResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.process.upload.v2.BulkUploadParser;
import com.solar.api.saas.service.process.upload.v2.BulkUploadService;
import com.solar.api.saas.service.process.upload.v2.EUploadEntitiy;
import com.solar.api.saas.service.process.upload.v2.UploadResponse;
import com.solar.api.saas.service.process.upload.v2.customer.mapper.CustomerMapper;
import com.solar.api.saas.service.process.upload.v2.customer.mapper.CustomerV2;
import com.solar.api.saas.service.process.upload.v2.customer.mapper.StagingResult;
import com.solar.api.saas.service.process.upload.v2.customer.validation.CustomerValidationResult;
import com.solar.api.saas.service.process.upload.v2.customer.validation.CustomersValidation;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.ca.CaUtilityDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationMapper;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.mapper.user.UserMappingDTO;
import com.solar.api.tenant.model.CustomerStageTable;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.contract.EEntityType;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.Organization;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.contractStatus.EContractStatus;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userMapping.UserMapping;
import com.solar.api.tenant.model.user.userType.ESourceType;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;
import com.solar.api.tenant.repository.CustomerStageTableRepository;
import com.solar.api.tenant.repository.LocationMappingRepository;
import com.solar.api.tenant.repository.UserRepository;
import com.solar.api.tenant.repository.contract.OrganizationRepository;
import com.solar.api.tenant.service.PaymentInfoService;
import com.solar.api.tenant.service.RoleService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.UserTypeService;
import com.solar.api.tenant.service.acquisition.AcquisitionService;
import com.solar.api.tenant.service.contract.OrganizationService;
import com.solar.api.tenant.service.extended.LocationMappingService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.etl.ETLService;
import com.solar.api.tenant.service.extended.PhysicalLocationService;
import com.solar.api.tenant.service.userMapping.UserMappingService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.saas.service.integration.BaseResponse.*;

@Service
public class BulkUploadCustomersServiceImpl implements BulkUploadService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String UPLOADED_CSV_FILE_PATH = "/upload/customers/csv";
    private static final String CSV_FILE_UPLOAD_RESPONSE_PATH = "/upload/responses";

    @Autowired
    private Utility utility;
    @Autowired
    private StorageService storageService;

    @Value("${app.profile}")
    private String appProfile;
    @Autowired
    private PortalAttributeSAASService portalAttributeSAASService;
    @Autowired
    private LocationMappingRepository locationMappingRepository;
    @Autowired
    private OrganizationService organizationService;
    private final CustomerStageTableRepository customerStageTableRepository;
    private final UserService userService;
    private final UserTypeService userTypeService;
    private final RoleService roleService;
    private final PhysicalLocationService physicalLocationService;
    private final BulkUploadParser bulkUploadParser;
    private final CustomersValidation customersValidation;
    private final OrganizationRepository organizationRepository;
    private final AcquisitionService acquisitionService;
    private final PaymentInfoService paymentInfoService;
    private final PasswordEncoder encoder;
    private final ETLService etlService;
    private final ObjectMapper objectMapper;
    private final DataExchange dataExchange;
    private final UserMappingService userMappingService;
    private final UserLevelPrivilegeService userLevelPrivilegeService;
    private final UserRepository userRepository;

    public BulkUploadCustomersServiceImpl(CustomerStageTableRepository customerStageTableRepository,
                                          UserService userService, UserTypeService userTypeService,
                                          RoleService roleService, PhysicalLocationService physicalLocationService, BulkUploadParser bulkUploadParser,
                                          CustomersValidation customersValidation, OrganizationRepository organizationRepository, AcquisitionService acquisitionService,
                                          PaymentInfoService paymentInfoService, PasswordEncoder encoder, ETLService etlService, ObjectMapper objectMapper,
                                          UserMappingService userMappingService, DataExchange dataExchange, UserLevelPrivilegeService userLevelPrivilegeService,
                                          UserRepository userRepository) {
        this.customerStageTableRepository = customerStageTableRepository;
        this.userService = userService;
        this.userTypeService = userTypeService;
        this.roleService = roleService;
        this.physicalLocationService = physicalLocationService;
        this.bulkUploadParser = bulkUploadParser;
        this.customersValidation = customersValidation;
        this.organizationRepository = organizationRepository;
        this.acquisitionService = acquisitionService;
        this.paymentInfoService = paymentInfoService;
        this.encoder = encoder;
        this.etlService = etlService;
        this.objectMapper = objectMapper;
        this.userMappingService = userMappingService;
        this.dataExchange = dataExchange;
        this.userLevelPrivilegeService = userLevelPrivilegeService;
        this.userRepository = userRepository;
    }

    @Override
    public BaseResponse<Object> validate(MultipartFile file, String uploadType, String customerType) {
        return validationResponse((StagingResult) stage(file, uploadType, customerType).getData(), uploadType, customerType);
    }

    @Override
    public BaseResponse<Object> validate(String customersJson, String uploadId, String uploadType, String customerType) {
        return validationResponse((StagingResult) stage(customersJson, uploadId).getData(), uploadType, customerType);
    }

    private BaseResponse<Object> validationResponse(StagingResult stagingResult, String uploadType, String customerType) {
        List<CustomerStageTable> stagedCustomers = stagingResult.getStagedCustomers();
        CustomerValidationResult data = (CustomerValidationResult) customersValidation
                .validate(stagedCustomers, uploadType, customerType)
                .setUploadId(stagingResult.getUploadId())
                .setFileStorageUri(stagingResult.getFileStorageUri());
        stagedCustomers.forEach(customer -> customer.setCsvJson(null));
        data.setStagedCustomers(stagedCustomers);
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    private List<CustomerV2> getCustomerV2Mappings(List<CustomerStageTable> stagedCustomers) {
        ObjectMapper mapper = new ObjectMapper();
        return stagedCustomers.stream()
                .map(m -> {
                    try {
                         CustomerV2 customerV2 = mapper.readValue(m.getCsvJson(), CustomerV2.class);
                         customerV2.setUploadType(m.getUploadType()); //lead, customer, prospect
                         customerV2.setCustomerType(m.getCustomerType()); //individual, commercial
                        return customerV2;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    @Override
    public BaseResponse<Object> stage(MultipartFile file, String uploadType, String customerType) {
        //uploadType = Lead, customer, prospect
        //customerType = individual, commercial
        BaseResponse<File> fileResponse = getFile(file);
        if (fileResponse.getCode() != HttpStatus.OK.value()) {
            return error(fileResponse.getCode(), fileResponse.getMessage());
        }
        File tempFile = fileResponse.getData();
        List<CustomerStageTable> stagedCustomers = new ArrayList<>();
        try {
            try (FileInputStream in = new FileInputStream(tempFile)) {
                List<CustomerV2> importedCustomers = bulkUploadParser.importCustomersFromCSV(in, null); // TODO: Check null
                importedCustomers = importedCustomers.stream()
                        .peek(customer -> {
                            customer.setUploadType(uploadType); //lead, customer, prospect
                            customer.setCustomerType(customerType); //individual, commercial
                        })
                        .collect(Collectors.toList());
                stagedCustomers.addAll(getStagedCustomers(importedCustomers, uploadType, customerType));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return error(e.getMessage());
        }
        stagedCustomers = customerStageTableRepository.saveAll(stagedCustomers);
        String uploadId = stagedCustomers.isEmpty() ? null : stagedCustomers.get(0).getUploadId();
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .data(StagingResult.builder()
                        .stagedCustomers(stagedCustomers)
                        .uploadId(uploadId)
                        .fileStorageUri(uploadCSVFile(fileResponse.getData(), file.getOriginalFilename(), uploadId))
                        .build())
                .build();
    }

    @Override
    public BaseResponse<Object> stage(String customersJson, String uploadId) {
        List<CustomerStageTable> stagedCustomers = new ArrayList<>();
        try {
            stagedCustomers.addAll(List.of(new ObjectMapper().readValue(customersJson, CustomerStageTable[].class)));
            for (CustomerStageTable customer : stagedCustomers) {
                customer.setCsvJson(new ObjectMapper().writeValueAsString(customer.getCsvJsonObject()));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return error(e.getMessage());
        }
        Map<Long, CustomerV2> customerV2Map = new HashMap<>();
        stagedCustomers.forEach(customer -> customerV2Map.put(customer.getId(), customer.getCsvJsonObject()));
        stagedCustomers = customerStageTableRepository.saveAll(stagedCustomers);
        stagedCustomers.forEach(customer -> customer.setCsvJsonObject(customerV2Map.get(customer.getId())));
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .data(StagingResult.builder()
                        .stagedCustomers(stagedCustomers)
                        .uploadId(stagedCustomers.isEmpty() ? uploadId : stagedCustomers.get(0).getUploadId())
                        .build())
                .build();
    }

    private List<CustomerStageTable> getStagedCustomers(List<CustomerV2> importedCustomers, String uploadType, String customerType) {
        List<CustomerStageTable> stagedCustomers = new ArrayList<>();
        importedCustomers = importedCustomers.stream().filter(v -> !v.getEmail().isEmpty()).collect(Collectors.toList());
        String uploadId = UUID.randomUUID().toString();
        importedCustomers.forEach(importedCustomer -> {
            try {
                String csvRowJson = new ObjectMapper().writeValueAsString(importedCustomer);
                stagedCustomers.add(CustomerStageTable.builder()
                        .uploadId(uploadId)
                        .csvJson(csvRowJson)
                        .csvJsonObject(importedCustomer)
                        .status("PENDING")
                        .uploadType(uploadType)//lead, customer, prospect
                        .customerType(customerType)//individual, commercial
                        .build());
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        return stagedCustomers;
    }

    private BaseResponse<File> getFile(MultipartFile file) {
        File tempFile;
        try {
            tempFile = Files.createTempFile(null, file.getOriginalFilename()).toFile();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return unprocessable(e.getMessage());
        }
        tempFile.deleteOnExit();
        try {
            file.transferTo(tempFile);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return unprocessable(e.getMessage());
        }
        return ok("", tempFile);
    }

    private String uploadCSVFile(File file, String fileName, String uploadId) {
        byte[] bytes;
        String finalFileName;
        try {
            String extension = FilenameUtils.getExtension(fileName);
            finalFileName = fileName.substring(0, fileName.length() - extension.length() - 1) +
                    "-" + uploadId + "-" + new SimpleDateFormat(Utility.SYSTEM_DATE_FORMAT).format(new Date()) + "." + extension;
            bytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Utility.uploadToStorage(storageService, bytes, appProfile,
                "tenant/" + utility.getCompKey() + UPLOADED_CSV_FILE_PATH,
                finalFileName, utility.getCompKey(), false);
    }

    @Override
    public BaseResponse<Object> upload(String uploadId, List<Long> correctRowIds, List<Long> correctStagedIds, String customerType) {
        List<Long> createdIds = new ArrayList<>();
        List<CustomerStageTable> stagedCustomersToImport = customerStageTableRepository.findAllById(correctStagedIds);
        if (stagedCustomersToImport.size() > 0) {
            List<CustomerV2> customersV2 = getCustomerV2Mappings(stagedCustomersToImport);
            List<User> users = addAssociatedData(customersV2, customerType, stagedCustomersToImport);
            List<String> uidPass = new ArrayList<>();
            postSaveCustomers(users, uidPass);
            saveMongoData(customerType, users, customersV2);
            createdIds.addAll(users.stream().map(m -> m.getAcctId()).collect(Collectors.toList()));
            try {
                List<CustomerStageTable> incorrectCustomers = customerStageTableRepository.findByUploadId(uploadId);
                ObjectMapper mapper = new ObjectMapper();
                incorrectCustomers.forEach(customer -> {
                    try {
                        customer.setCsvJsonObject(mapper.readValue(customer.getCsvJson(), CustomerV2.class));
                        customer.setCsvJson(null);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
                return BaseResponse.builder().code(HttpStatus.OK.value()).data(UploadResponse.builder()
                        .entityType(EUploadEntitiy.CUSTOMER.toString())
                        .created((long) users.size())
                        .createdIds(createdIds)
                        .uidPass(uidPass)
                        .incorrectCustomersJson(new ObjectMapper().writeValueAsString(incorrectCustomers))
                        .responseUrl(uploadUserUploadResult(correctRowIds, uidPass, "Line", "Login name", "Password")) // Upload to starage
                        .build()).build();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return notFound("No staged records found");
    }

    @Override
    public BaseResponse<Object> showProgress(String uploadId, List<Long> correctStagedIds) {
        List<CustomerStageTable> staged = customerStageTableRepository.findAllById(correctStagedIds);
        long total = staged.size();
        long done = (int) staged.stream().filter(m -> "DONE".equals(m.getStatus())).count();
        if (staged.size() == 0) {
            return BaseResponse.builder().code(HttpStatus.OK.value()).data(0).build();
        }
        staged = customerStageTableRepository.findByUploadId(uploadId);
        if (!staged.stream().anyMatch(m -> "PENDING".equals(m.getStatus()))) {
            customerStageTableRepository.deleteByUploadId(uploadId);
        }
        return BaseResponse.builder().code(HttpStatus.OK.value()).data((int) (done / (double) total * 100)).build();
    }

    private User setUserProperties(CustomerV2 customerV2, String customerType) {
        UserType userType = null;
        if (EUserType.INDIVIDUAL.getName().equalsIgnoreCase(customerType)) {
            userType = userTypeService.findByName(EUserType.CUSTOMER);
        } else if (EUserType.COMMERCIAL.getName().equalsIgnoreCase(customerType)) {
            userType = userTypeService.findByName(EUserType.CUSTOMER);
        }
        Long maxAcctId = userRepository.getNextMaxAcctId();
        User user = CustomerMapper.toUser(customerV2, userType, maxAcctId);
        if (user.getEmailAddress() == null || user.getEmailAddress().isEmpty()) {
            // If user doesn’t have an Email address Please keep user column blank and email address to be a
            // default email id
            user.setEmailAddress(FINAL_DEFAULT_EMAIL);
            // In cases the user column is blank the system will auto generate user id as FirstName + portal id
            // example SAM421 or COLIN211
        }
        if (user.getUserName() == null) {
            user.setUserName("");
        }
        if (user.getUploadPassword() != null) {
            user.setPassword(encoder.encode(user.getUploadPassword()));
        } else {
            user.setPassword("");
        }
        user.setFirstName(customerV2.getFirstName());
        user.setLastName(customerV2.getLastName());
        user.setUserType(userType);
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByName(ERole.ROLE_CUSTOMER.toString()));
        user.setRoles(roles);

        user.setStatus(EUserStatus.ACTIVE.getStatus());
        Date date = new Date();
        if (user.getActiveDate() == null) {
            user.setActiveDate(date);
        }
        if (user.getRegisterDate() == null) {
            user.setRegisterDate(date);
        }
        return user;
    }

    private List<User> addAssociatedData(List<CustomerV2> customersV2, String customerType, List<CustomerStageTable> stagedCustomers) {
        List<User> users = new ArrayList<>();
        try {
            for (int i = 0; i < customersV2.size(); i++) {
                CustomerV2 customerV2 = customersV2.get(i);
                User user = setUserProperties(customerV2, customerType);
                users.add(userService.saveUser(user));
                Organization organization = organizationService.findByStatusAndPrimaryIndicatorAndParentOrgId("ACTIVE", true, null);
                Entity entity = userService.saveInEntity(Entity.builder()
                        .entityName(customerV2.getFirstName() + (customerV2.getLastName() != null && !customerV2.getLastName().isEmpty() ?
                                " " + customerV2.getLastName() : ""))
                        .entityType(com.solar.api.saas.service.process.upload.EUploadEntitiy.CUSTOMER.toString())
                        .status(EUserStatus.ACTIVE.getStatus())
                        .isDocAttached(null)
                        .isDeleted(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .companyName(null)
                        .contactPersonEmail(customerV2.getEmail())
                        .contactPersonPhone(customerV2.getPhoneNumber())
                        .organization(organization)
                        .website(null)
                        .isActive(true)
                        .registerId(null)
                        .registerType(null)
                        .build());
                userService.saveInCustomerDetail(CustomerDetail.builder()
                        .entityId(entity.getId())
                        .states(customerV2.getUploadType().toUpperCase()) // Customer,prospect, lead
                        .isCustomer(true)
                        .leadSource(ESourceType.CSVUPLOAD.getName())  // csv upload
                        .customerType(customerV2.getCustomerType()) // indiviual or commercial
                        .phoneNo(customerV2.getPhoneNumber())
                        .selfInitiative(false)
                        .build());
                userService.saveInUserLevelPrivilege(UserLevelPrivilege.builder()
                        .entity(entity)
                        .user(user)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .organization(organization)
                        .build());
                if ("Customer".equalsIgnoreCase(customerV2.getUploadType())) {
                    PhysicalLocation physicalLocation = physicalLocationService.saveOrUpdate(PhysicalLocation.builder()
                            .locationName(customerV2.getLocationName())     // mysql (physical_location, location_mapping)
                            .organization(organization)                     // mysql (physical_location)
                            .locationType(customerV2.getLocationType())     // mysql (physical_location)
                            .add1(customerV2.getAddress1())                 // mysql (physical_location)
                            .add2(customerV2.getAddress2())                 // mysql (physical_location)
                            .add3(customerV2.getCountry())                   // mysql (physical_location)
                            .ext1(customerV2.getCity())                     // mysql (physical_location)
                            .ext2(customerV2.getState())                   // mysql (physical_location)
                            .zipCode(customerV2.getZipCode())               // mysql (physical_location)
                            .acctId(user.getAcctId())
                            .build());
                    locationMappingRepository.save(LocationMapping.builder()
                            .locationId(physicalLocation.getId())
                            .primaryInd("Y")
                            .sourceId(user.getAcctId())
                            .sourceType(EContractStatus.CustomerClient.getName())
                            .build());
                    PortalAttributeSAAS portalAttributeSAAS = portalAttributeSAASService.findByNameFetchPortalAttributeValues(Constants.PORTAL_ATTRIBUTE_VALUE.UTILITY_COMPANY);
                    PortalAttributeValueSAAS portalAttributeValueSAAS = portalAttributeSAAS.getPortalAttributeValuesSAAS().stream().filter(value -> value.getAttributeValue().equals(customerV2.getUtilityProvider()))
                            .findFirst()
                            .orElse(null);
                    acquisitionService.saveOrUpdateCaUtility(UserDTO.builder()
                            .acctId(user.getAcctId())
                            .physicalLocations(List.of(PhysicalLocationMapper.toPhysicalLocationDTO(physicalLocation)))
                            .caUtility(List.of(CaUtilityDTO.builder()
                                    .accountHolderName(customerV2.getAccountHolderName())   // mysql (ca_utility)
                                    .utilityProviderId(portalAttributeValueSAAS.getId())      // mysql (ca_utility)
                                    .premise(customerV2.getPremiseNumber())       // mysql (ca_utility)
                                    .referenceId(customerV2.getReferenceNumberId())   // mysql (ca_utility)
                                    .averageMonthlyBill(customerV2.getAverageMonthlyBill())
                                    .entityId(entity.getId())       // mysql (ca_utility)
                                    .build()))
                            .build(), null);
                    paymentInfoService.addOrUpdate(PaymentInfo.builder()
                            .paymentSource(customerV2.getPaymentMethod().toUpperCase())
                            .accountTitle(customerV2.getAccountTitle())
                            .bankName(customerV2.getBank())
                            .accountType(customerV2.getAccountType())
                            .acctId(user.getAcctId())
                            .build());
                    if (customerV2.getSoftCreditCheck().equalsIgnoreCase("Yes")) {
                        acquisitionService.saveAndUpdateSoftCreditCheck(CaSoftCreditCheckDTO.builder()
                                .referenceNo(customerV2.getSoftCreditReference())
                                .source(customerV2.getSoftCreditSource().toUpperCase())
                                .isCheckedLater(true)
                                .build(),
                                UserDTO.builder()
                                .acctId(user.getAcctId())
                                .entityId(entity.getId())
                                .build());
                    }
                    if (customerV2.getSoftCreditCheck().equalsIgnoreCase("NO")) {
                        acquisitionService.saveAndUpdateSoftCreditCheck(CaSoftCreditCheckDTO.builder()
                                .isCheckedLater(true)
                                .build(), null);
                    }
                    acquisitionService.saveAndUpdateReferralInfo(CaReferralInfoDTO.builder()
                            .source(customerV2.getHowDidYouHearAboutUs())
                            .promoCode(customerV2.getPromoCode())
                            .build(), UserDTO.builder()
                            .acctId(user.getAcctId())
                            .entityId(entity.getId())
                            .build());
                }
                CustomerStageTable stagedCustomer = stagedCustomers.get(i);
                stagedCustomer.setStatus("DONE");
                customerStageTableRepository.save(stagedCustomer);
            }
        } catch (Exception e) {
            LOGGER.error("Error occured while saving data in mysql", e);
        }
        return users;
    }

    private void postSaveCustomers(List<User> usersToDb, List<String> uidPass) {
        usersToDb.forEach(user -> {
            // In cases the user column is blank the system will auto generate user id as first_name + acct_id
            // example SAM421 or COLIN211
            if (user.getUserName() == null || user.getUserName().isEmpty()) {
                user.setUserName(user.getFirstName().trim() + user.getAcctId());
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
                "tenant/" + utility.getCompKey() + CSV_FILE_UPLOAD_RESPONSE_PATH,
                "Bulk User Upload - " + new Date() + ".csv", utility.getCompKey(), false);
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return unchanged if input is null or empty
        } else {
            return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
        }
    }


    @Override
    public String getMongoTemplate(String customerType) throws Exception {
        Optional<TenantConfig> tenantConfig = null;
        String templateJson = null;
        if (customerType.equalsIgnoreCase(EEntityType.INDIVIDUAL.toString())) {
            tenantConfig = etlService.getTenantConfig(EEntityType.INDIVIDUAL);
            if (tenantConfig.isPresent()) {
                templateJson = etlService.fetchTemplateJson(tenantConfig.get());
            }
        } else if (customerType.equalsIgnoreCase(EEntityType.COMMERCIAL.toString())) {
            tenantConfig = etlService.getTenantConfig(EEntityType.COMMERCIAL);
            if (tenantConfig.isPresent()) {
                templateJson = etlService.fetchTemplateJson(tenantConfig.get());
            }
        }
        return templateJson;
    }

    public void saveMongoData(String customerType, List<User> usersToDb, List<CustomerV2> customerListV2) {
        try {
            String mongoTemplate = getMongoTemplate(customerType);
            if (mongoTemplate != null) {
                processUsers(usersToDb, mongoTemplate, customerListV2);
            } else {
                LOGGER.error("No " + customerType + " Template found");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void processUsers(List<User> userList, String templateJson, List<CustomerV2> customerListV2) {
        try {
            List<UserLevelPrivilege> userLevelPrivileges = userLevelPrivilegeService.findByAcctIds(userList.stream().map(User::getAcctId).collect(Collectors.toList()));
            for (int i = 0; i < customerListV2.size(); i++) {
                Entity entity = null;
                CustomerV2 customerV2 = customerListV2.get(i);
                User user = userList.get(i);
                // Use stream to filter the list based on the entityId
                UserLevelPrivilege foundUserLevelPrivilege = userLevelPrivileges.stream()
                        .filter(userLevelPrivilege -> userLevelPrivilege.getUser().getAcctId().longValue() == user.getAcctId().longValue())
                        .findFirst()
                        .orElse(null);
                if (foundUserLevelPrivilege != null) {
                    entity = foundUserLevelPrivilege.getEntity();
                } else {
                    LOGGER.error("Entity not found for user: " + user.getUserName());
                }
                String userTemplateJson = updateJsonWithUserDTO(customerV2, templateJson);
                UserMapping userMapping = saveETLTemplateAndUserMapping(entity, userTemplateJson);
                if (userMapping != null) {
                    etlService.updateETLTable(userMapping);
                    LOGGER.info("project Saved successfully");
                }
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred while processing user: " + e.getMessage(), e);
        }
    }

    private String updateJsonWithUserDTO(CustomerV2 customerV2, String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        // Navigate to the "sections" array
        if (rootNode.isObject() && rootNode.has("sections")) {
            JsonNode sectionsNode = rootNode.get("sections");
            if (sectionsNode.isArray()) {
                for (JsonNode sectionNode : sectionsNode) {
                    if (sectionNode.isObject() && sectionNode.has("content")) {
                        JsonNode contentNode = sectionNode.get("content");
                        if (contentNode.isObject() && contentNode.has("measures")) {
                            JsonNode measuresNode = contentNode.get("measures");
                            if (measuresNode.isArray()) {
                                for (JsonNode measureNode : measuresNode) {
                                    if (measureNode.isObject() && measureNode.has("code")) {
                                        String code = measureNode.get("code").asText();
                                        // Here, you can map the code to a corresponding field in the UserDTO
                                        // and set it as the "default_value" in the JSON
                                        if ("FNAME".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", customerV2.getFirstName());
                                        } else if ("LNAME".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", customerV2.getLastName());
                                        } else if ("PHNE_NUM".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", customerV2.getPhoneNumber());
                                        } else if ("EMAIL".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", customerV2.getEmail());
                                        } else if ("ZIP_CD".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", customerV2.getZipCode());
                                        } else if ("DOB".equals(code)) {
                                            ((ObjectNode) measureNode).putNull("default_value");
                                        } else if ("EXE_DT".equals(code)) {
                                            ((ObjectNode) measureNode).putNull("default_value");
                                        } else if ("UTL_PRVDR".equals(code)) {
                                            ((ObjectNode) measureNode).putNull("default_value");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Convert the updated JSON back to a string
        return objectMapper.writeValueAsString(rootNode);
    }

    private UserMapping saveETLTemplateAndUserMapping(Entity entity, String json) throws Exception {
        UserMapping userMapping = null;
        try {
            String oid = null;
            com.solar.api.tenant.model.BaseResponse data = dataExchange.saveOrUpdateAcquisitionProject(AcquisitionUtils.addEntityIdInTemplate(json, entity.getId()));
            JsonNode rootNode = objectMapper.readTree(data.getMessage().toString());
            JsonNode idNode = rootNode.get("_id");
            if (idNode != null) {
                oid = idNode.get("$oid").asText();
            }
            userMapping = userMappingService.save(new UserMappingDTO().builder()
                    .entityId(entity.getId())
                    .ref_id(oid)
                    .module("Acquistion")
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error while hitting mongo api", e);
        }
        return userMapping;
    }

    @Override
    public BaseResponse<Object> deleteCustomer(String uploadId, int indexToDelete) {
        List<CustomerStageTable> customerStageTableList = customerStageTableRepository.findByUploadId(uploadId);
        if (indexToDelete >= 0 && indexToDelete <= customerStageTableList.size() - 1) {
            CustomerStageTable recordToDelete = customerStageTableList.get(indexToDelete);
            customerStageTableRepository.delete(recordToDelete);
        }
        customerStageTableList.remove(indexToDelete);
        customerStageTableList.forEach(customerStage->{
            try {
                CustomerV2 customerV2 = objectMapper.readValue(customerStage.getCsvJson(), CustomerV2.class);
                customerStage.setCsvJsonObject(customerV2);
                customerStage.setCsvJson(null);
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage());
            }
        });
        return BaseResponse.builder()
                .code(HttpStatus.OK.value())
                .data(StagingResult.builder()
                        .stagedCustomers(customerStageTableList)
                        .uploadId(uploadId)
                        .build()).build();
    }
}
