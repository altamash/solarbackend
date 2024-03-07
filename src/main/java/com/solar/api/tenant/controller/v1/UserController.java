package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.configuration.authorization.PermissionsUtil;
import com.solar.api.helper.Message;
import com.solar.api.helper.WebUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.mapper.tenant.MasterTenantDTO;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.StorageService;
import com.solar.api.saas.service.integration.docuSign.DataExchangeDocuSign;
import com.solar.api.saas.service.integration.docuSign.dto.callback.ZResponse;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.widget.InfoService;
import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.contract.EntityDTO;
import com.solar.api.tenant.mapper.contract.EntityMapper;
import com.solar.api.tenant.mapper.contract.LinkedContractDTO;
import com.solar.api.tenant.mapper.portalAttribute.PortalAttributeValueTenantDTO;
import com.solar.api.tenant.mapper.tiles.UtilityInformationTile;
import com.solar.api.tenant.mapper.user.*;
import com.solar.api.tenant.model.solarAmps.RequestADemoDTO;
import com.solar.api.tenant.model.user.TempPass;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.repository.docuSign.ExternalCallBackLogRepository;
import com.solar.api.tenant.repository.docuSign.SigningRequestTrackerRepository;
import com.solar.api.tenant.service.CustomerDetailService;
import com.solar.api.tenant.service.TempPassService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.UserTypeService;
import com.solar.api.tenant.service.contract.AccountService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.contract.OrganizationDetailService;
import com.solar.api.tenant.service.extended.document.DocuLibraryService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.ErrorDTO;
import com.solar.api.tenant.service.solarAmps.SolarAmpsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.user.UserMapper.*;

//@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("UserController")
@RequestMapping(value = "/user")
public class UserController {

    @Value("${app.mongoBaseUrl}")
    private String mongoBaseUrl;
    @Value("${app.profile}")
    private String appProfile;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserService userService;
    @Autowired
    private InfoService infoService;
    @Autowired
    private TempPassService tempPassService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MasterTenantRepository masterTenantRepository;

    @Autowired
    OrganizationDetailService organizationDetailService;

    //    @Autowired
//    private CaUtilityRepository caUtilityRepository;
    @Autowired
    private EntityService entityService;
    //    @Autowired
//    private EmailService emailService;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SigningRequestTrackerRepository signingRequestTrackerRepository;
    @Autowired
    private ExternalCallBackLogRepository externalCallBackLogRepository;
    @Autowired
    private DataExchangeDocuSign dataExchangeDocuSign;
    @Autowired
    private StorageService storageService;
    @Autowired
    private DocuLibraryService docuLibraryService;
    @Autowired
    private PermissionsUtil permissionsUtil;

    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private SolarAmpsService solarAmpsService;
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private DataExchange dataExchange;


    @PreAuthorize("checkAccess()")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.GONE);
    }

    @PreAuthorize("checkAccess()")
    @PutMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") long id, @RequestBody UserDTO userDto) {
        User userData = userService.findByIdFetchAll(id);
        User user = toUpdatedUser(userData, toUser(userDto));
        if (userDto.getPassword() != null) {
            user.setPassword(encoder.encode(userDto.getPassword()));
        }
        ResponseEntity<UserDTO> response = new ResponseEntity<>(toUserDTO(userService.update(user)), HttpStatus.OK);
        permissionsUtil.setNavigationUserMap(user.getUserName(), User.class);
        return response;
    }

    @PreAuthorize("checkAccess()")
    @PutMapping("/updatePassword/{id}/{newPass}")
    public ObjectNode updateUser(@PathVariable("id") long id, @PathVariable("newPass") String newPass) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        User isExist = userService.findById(id);
        if (isExist == null) {
            response.put("warning", "User invalid");
            return response;
        }
        String encodedPass = encoder.encode(newPass);
        User updateUser = userService.resetPassword(id, encodedPass);
        response.put("message",
                "Password changed successfully for user " + updateUser.getFirstName() + " " + updateUser.getLastName());
        return response;
    }


    @PutMapping("/setUserPassword/{compKey}/{id}/{newPass}/{confirmPass}")
    public ObjectNode setUserPassword(@PathVariable("compKey") Long compKey, @PathVariable("id") long id, @PathVariable("newPass") String newPass, @PathVariable("confirmPass") String confirmPass) {
        ObjectNode response = new ObjectMapper().createObjectNode();
//        DBContextHolder.setTenantName("ec".concat(String.valueOf(compKey)));
        masterTenantService.setCurrentDb(compKey);
        User isExist = userService.findById(id);
        if (isExist == null) {
            response.put("warning", "User invalid");
            return response;
        }
        if (newPass.equalsIgnoreCase(confirmPass)) {
            String encodedPass = encoder.encode(newPass);
            User updateUser = userService.updateUserPassword(id, encodedPass);
            if (updateUser == null) {
                response.put("warning", "Can n't find entity for this user");
                return response;
            }
            response.put("message",
                    "Password changed successfully for user " + updateUser.getFirstName() + " " + updateUser.getLastName());
        } else {
            response.put("message", "Your Password and Confirmation Password do not match. ");
        }
        return response;
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/get/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") long id) {
        User userData = userService.findByIdFetchRoles(id);
        return new ResponseEntity<>(toUserDTO(userData), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getByName/{username}")
    public ResponseEntity<UserDTO> getUserByName(@PathVariable("username") String username) {
        User userData = userService.findByUserNameFetchRoles(username);
        return new ResponseEntity<>(toUserDTO(userData), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/get/type/{type}")
    public ResponseEntity<List<UserDTO>> getUser(@PathVariable("type") String type) {
        EUserType userType = EUserType.get(type);
        if (userType == null) {
            return new ResponseEntity(Collections.emptyList(), HttpStatus.OK);
        }
        List<User> users = userService.findByUserType(EUserType.get(type));
        return new ResponseEntity(toUserDTOs(users), HttpStatus.OK);
    }

//    @PreAuthorize("checkAccess()")
//    @GetMapping("/get")
//    public ResponseEntity<List<UserDTO>> getAllUsers() {
//        return new ResponseEntity<>(toUserDTOs(userService.findAllFetchRoles()), HttpStatus.OK);
//    }

    /**
     * Fetching customers with respect to entityType = Customers
     * Author: Shariq
     *
     * @return
     */
    @PreAuthorize("checkAccess()")
    @GetMapping("/get")
    public ResponseEntity<List<UserDTO>> getAllCustomersFromEntity() {
        return new ResponseEntity<>(toUserDTOs(userService.findAllCustomersFromEntity()), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/get/text/{text}")
    public ResponseEntity<List<UserDTO>> getUserByText(@PathVariable("text") String text) {
        List<User> userData = userService.findByText(text);
        return new ResponseEntity<>(toUserDTOs(userData), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/get/count/{status}")
    public Long getCustomersCountByStatus(@PathVariable("status") String status) {
        return infoService.getCustomersCountByStatus(status);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/get/passwordGenerator")
    public String passwordGenerator() {
        List<TempPass> allData = tempPassService.getAllTempPass();
        List<TempPass> update = new ArrayList<>();
        allData.forEach(instance -> {
            instance.setEncPass(encoder.encode(instance.getClearPass()));
            System.out.println(instance);
            update.add(instance);
        });
        System.out.println(update);
        tempPassService.deleteAll();
        tempPassService.saveAll(update);
        return "200, SUCCESS";
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/resetCustomerPassword")
    public ObjectNode verifyAdmin(@RequestBody VerificationDTO verificationDTO) throws Exception {
        ObjectNode response = new ObjectMapper().createObjectNode();
        if (verificationDTO.getPassword() != null && !verificationDTO.getPassword().isEmpty()) {
            verificationDTO.setPassword(encoder.encode(verificationDTO.getPassword()));
            return userService.resetCustomerPassword(verificationDTO);
        } else {
            response.put("warning", "Password cannot be null");
        }
        return response;
    }

    @GetMapping("/getAllUserType/{Comp-Key}")
    public ResponseEntity<List<PortalAttributeValueTenantDTO>> getUserType(@PathVariable("Comp-Key") Long compKey) {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        return new ResponseEntity(userService.getAllUserType(), HttpStatus.OK);
    }

    @PostMapping("/selfRegistration/{Comp-Key}")
    public ObjectNode UserSelfRegistration(@PathVariable("Comp-Key") Long compKey,
                                           @RequestParam(value = "userDTO", required = true) String userDTO,
                                           @RequestParam(value = "userMultipartFiles", required = false) List<MultipartFile> userMultipartFiles,
                                           @RequestParam(value = "businessMultipartFiles", required = false) List<MultipartFile> businessMultipartFiles
    ) throws JsonProcessingException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        User user = userService.userSelfRegistration(userdto, userMultipartFiles, businessMultipartFiles);
        if (user != null && user.getAcctId() != null) {
            permissionsUtil.setNavigationUserMap(user.getUserName(), User.class);
            return response.put("message", "New account successfully registered with email:" + user.getEmailAddress());
        }
        return response.put("error", "Failed to create new account");
    }

    @PostMapping("/internalUserRegistration/{Comp-Key}")
    public ObjectNode internalUserRegistration(@PathVariable("Comp-Key") Long compKey,
                                               @RequestParam(value = "userDTO", required = true) String userDTO) throws JsonProcessingException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        User user = userService.internalUserRegistration(userdto);
        if (user.getAcctId() != null) {
            permissionsUtil.setNavigationUserMap(user.getUserName(), User.class);
            return response.put("message", "New account successfully registered with email:" + user.getEmailAddress());
        }
        return response.put("error", "Failed to create new account");
    }

    @GetMapping("/userId/{userId}")
    public ObjectNode getEntityId(@PathVariable("userId") Long userId) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        EntityDTO entityDto = EntityMapper.toEntityDTO(entityService.findEntityByUserId(userId));
        if (entityDto != null)
            response.put("EntityId", entityDto.getId());
        else
            response.putNull("EntityId");
        return response;
    }

//    @PostMapping("/callback/zohoSign")
//    public String callBackZohoSign(ZohoDTO zohoDTO) throws IOException {
//        DBContextHolder.setTenantName("ec1001");
//        emailService.sendEmail(new Email("coby.christiansen@solarinformatics.com"), new Content("text/html", "Callback Is Working"), "Test Email for callback URL");
//        return "success";
//    }

    //customer Acquisition registration
    @PreAuthorize("checkAccess()")
    @PostMapping("/caRegistration")
    public ObjectNode caRegistration(@RequestParam(value = "userDTO", required = true) String userDTO,
                                     @RequestParam(value = "utilityMultipartFiles", required = false) List<MultipartFile> utilityMultipartFiles,
                                     @RequestParam(value = "sendForSigning", required = false, defaultValue = "false") Boolean sendForSigning,
                                     @RequestParam(value = "templateIds", required = false) List<Long> ids
    ) throws JsonProcessingException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        //email validation code is added
        User user = userService.saveOrUpdateCaUser(userdto, utilityMultipartFiles, sendForSigning, ids);
//        permissionsUtil.setNavigationUserMap(user.getUserName(), User.class);
        if (user != null) {
            if (user.getAcctId() != null && userdto.getAcctId() == null) {
                return response.put("message", "New account successfully registered with email:" + user.getEmailAddress());
            } else if (user.getAcctId() != null) {
                return response.put("message", " Account successfully updated for email:" + user.getEmailAddress());
            }
        }
        return response.put("error", "Failed to create/update account");
    }


    @PostMapping("/updateUserDetails")
    public ObjectNode updateUserDetails(@RequestParam(value = "userDTO", required = true) String userDTO) throws
            JsonProcessingException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        User user = userService.updateCaUserStatus(userdto);
        if (user != null && user.getAcctId() != null) {
            return response.put("message", "User successfully Removed");
        }
        return response.put("error", "Failed to remove user");
    }

    @PostMapping("/updateUsersDetailsBulk")
    public ResponseEntity<ObjectNode> updateUsersDetailsBulk(@RequestBody List<UserDTO> userDTOList) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        List<UserDTO> failedUsers = new ArrayList<>();

        try {
            for (UserDTO userDTO : userDTOList) {
                User user = userService.updateCaUserStatusV2(userDTO);
                if (user == null || user.getAcctId() == null) {
                    failedUsers.add(userDTO);
                }
            }

            if (failedUsers.isEmpty()) {
                response.put("message", "All users successfully removed");
            } else {
                response.put("error", "Failed to remove some users");
                response.put("failedUsers", new ObjectMapper().valueToTree(failedUsers));
            }
        } catch (Exception e) {

            LOGGER.error(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }


    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllCaUsers")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCaUsers() {
        return new ResponseEntity<>(userService.getAllCaUser(), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllCaUsersByType/{states}")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCaUsersByType(@PathVariable("states") String states) {
        return new ResponseEntity<>(userService.getAllCaUserByType(states), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllCustomersCount")
    public Map getAllCustomersCount() {
        return userService.getAllCustomerCount();
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getCustomerSalesAgentByEntityId")
    public ResponseEntity<CaUserTemplateDTO> getCustomerSalesAgentByEntityId(@RequestParam("entityId") String
                                                                                     entityId) {
        return new ResponseEntity<>(userService.getCustomerSaleAgentByEntityId(entityId), HttpStatus.OK);
    }

    @ApiOperation(value = "Used in Customer Management")
    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllCustomerLists")
    public BaseResponse getAllCustomerLists(@RequestParam("groupBy") String groupBy,
                                            @RequestParam(value = "groupByName", required = false) String groupByName,
                                            @RequestParam("pageNumber") Integer pageNumber,
                                            @RequestParam("pageSize") Integer pageSize) {
        return userService.getAllCustomerList(groupBy, groupByName, pageSize, pageNumber);
    }

    @GetMapping("/getAllCustomerByType")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCustomerByTypes
            (@RequestParam("customerType") List<String> customerType) {
        return new ResponseEntity(userService.getAllCustomerByTypes(customerType), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllCustomerListsByProject")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCustomerListsByProject
            (@RequestParam("projectType") List<String> projectType) {
        return new ResponseEntity<>(userService.getAllCustomerListByProject(projectType), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllCustomerListsByProjectGroup")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCustomerListsByProjectGroup() {
        return new ResponseEntity<>(userService.getAllCustomerListByProjectGroup(), HttpStatus.OK);
    }

    @GetMapping("/getAllCustomerByTypeGroup")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCustomerByTypesGroup() {
        return new ResponseEntity(userService.getAllCustomerByTypesGroup(), HttpStatus.OK);
    }

    @GetMapping("/getAllCustomerByRegion")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCustomerByRegion
            (@RequestParam("region") List<String> region) {
        return new ResponseEntity(userService.getAllCustomerByRegion(region), HttpStatus.OK);
    }

    @GetMapping("/getAllCustomerByRegionGroup")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCustomerByRegionGroup() {
        return new ResponseEntity(userService.getAllCustomerByRegionGroup(), HttpStatus.OK);
    }

//    @GetMapping("/getAllCustomerByType")
//    public ResponseEntity<List<UserRoleTemplateDTO>> getAllCustomerByTypes(@RequestParam("customerType") String customerType) {
//        return new ResponseEntity(userService.getAllCustomerByTypes(customerType), HttpStatus.OK);
//    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getCaUserDetail/{entityId}")
    public ResponseEntity<UserDTO> getCaUserDetail(@PathVariable("entityId") Long entityId) {
        return new ResponseEntity<>(userService.getCaUserDetail(entityId), HttpStatus.OK);
    }

    @PostMapping("/callback/docuSign")
    public void callBackDocuSign(@RequestBody ZResponse zResponse) throws IOException {
        userService.callbackDocuSign(zResponse);
    }

    @GetMapping("/getAllUserByCustomerType")
    public ResponseEntity<List<UserRoleTemplateDTO>> getUsersByCustomerType(@RequestParam("customerType") String
                                                                                    customerType) {
        return new ResponseEntity(userService.getAllUsersByCustomerType(customerType), HttpStatus.OK);
    }

    @GetMapping("/ca/entity/{entityId}")
    public List<UtilityInformationTile> getCaUtilityByEntity(@PathVariable("entityId") Long entityId) {
        return userService.getCaUtilityByEntity(entityId);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getAllCustomersSubscriptionSize", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> showAllCustomersSubscriptionSize() {
        return new ResponseEntity(userService.getAllCustomerListSubscriptionSize(), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getAllCustomersSubscription", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> showAllCustomersSubscription() {
        return new ResponseEntity(userService.getAllCustomerListSubscription(), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getAllCustomerVarientId", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> getAllCustomerVarientId() {
        return new ResponseEntity(userService.getAllCustomerVarientId(), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/getAllSubscriptionByCustomer", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> showAllSubscriptionByCustomers(@RequestParam("customerId") List<Long> customerId) {
        return new ResponseEntity(userService.getAllSubscriptionByCustomer(customerId), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/showAllProductsGarden", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map> showAllProductsGarden(@RequestParam(value = "unitType", required = false) Long
                                                             unitTypeId, @RequestParam("OrgId") Long OrgId) throws JsonProcessingException {
        Map response = new HashMap();
        List<HashMap> data = new ArrayList<>();
        List<LinkedContractDTO> output = new ArrayList<>();

        if (unitTypeId == null) {
            String DYNAMIC_MAPPINGS_URL = mongoBaseUrl + "/product/showAllProductGardens";
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Tenant-id", Arrays.asList(DBContextHolder.getTenantName()));
            ResponseEntity<String> staticValues =
                    WebUtils.submitRequest(HttpMethod.GET, String.format(DYNAMIC_MAPPINGS_URL), null, headers, String.class);
            if (staticValues != null) {
                response.put("code", HttpStatus.OK);
                response.put("message", Message.ORG_DETAIL_GET_LINKED_CONTRACTS.getMessage());
            } else {
                response.put("code", HttpStatus.NOT_FOUND);
                response.put("message", "Linked Contracts not found");
            }
            output = organizationDetailService.getModifiedContracts(staticValues.getBody(), OrgId);
        } else {
            output = organizationDetailService.getModifiedContracts(null, OrgId, unitTypeId);
        }
        response.put("data", output);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/storeUploadedContractFile/entityId/{entityId}")
    public String storeUploadedContractFile(@PathVariable("entityId") Long
                                                    entityId, @RequestParam("file") MultipartFile file, @RequestParam(value = "notes", required = false) String
                                                    notes) {
        return userService.getUploadedDocumentUrl(file, notes, entityId);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping(value = "/validateUserName/{userName}")
    public boolean validateUserName(@PathVariable("userName") String userName) {
        return userService.isValidateUserName(userName);
    }

    @GetMapping("/getCompanyNameLike/{companyName}")
    public List<MasterTenantDTO> getCompanyNameLike(@PathVariable String companyName) {
        return masterTenantService.findAllByCompanyNameLike(companyName);
    }

    @PostMapping("/getRequestADemo")
    public ResponseEntity<?> getRequestADemo(@RequestBody RequestADemoDTO requestADemoDTO) {
        return solarAmpsService.requestADemo(requestADemoDTO);
    }

    @GetMapping("/getLoginUrlLike/{keyword}")
    public ObjectNode getLoginUrlLike(@PathVariable String keyword,
                                      @RequestParam(value = "isMobileLanding", required = false) boolean isMobileLanding) {
        ObjectNode messageJson = new ObjectMapper().createObjectNode();
        MasterTenantDTO masterTenantDTO = masterTenantService.findByLoginUrlLike(keyword, isMobileLanding);
        if (masterTenantDTO != null) {
            messageJson.put("compKey", masterTenantDTO.getCompanyKey());
            messageJson.put("companyCode", masterTenantDTO.getCompanyCode());
            messageJson.put("companyName", masterTenantDTO.getCompanyName());
            messageJson.put("companyLogo", masterTenantDTO.getCompanyLogo());
            messageJson.put("loginUrl", masterTenantDTO.getLoginUrl());
            messageJson.put("status", masterTenantDTO.getStatus());
            messageJson.put("landingText", masterTenantDTO.getLandingText());
            messageJson.put("landingDescription", masterTenantDTO.getLandingDescription());
            messageJson.put("landingImagesUrl", !masterTenantDTO.getLandingImagesUrl().isEmpty() ?
                    masterTenantDTO.getLandingImagesUrl().toString() : "");
        }
        return messageJson;
    }

    @PostMapping("/registerYourInterest")
    public ResponseEntity<?> registerYourInterest(@RequestParam(value = "userDTO", required = true) String userDTO,
                                                  @RequestParam(value = "utilityMultipartFiles", required = false) List<MultipartFile> utilityMultipartFiles,
                                                  @RequestParam(value = "sendForSigning", required = false, defaultValue = "false") Boolean sendForSigning
    ) throws JsonProcessingException {
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        masterTenantService.setCurrentDb(userdto.getCompKey());
        return userService.saveRegisterInterestUser(userdto);
    }

    @PostMapping("/generatePasswordEmail")
    public ResponseEntity<?> generatePasswordEmail(@RequestBody UserDTO userDTO) {
        return userService.generatePasswordEmail(userDTO);
    }

    @PreAuthorize("checkAccess()")
    @GetMapping("/getClientUserDetail/{accountId}")
    public ResponseEntity<UserDTO> getClientUserDetail(@PathVariable("accountId") Long accountId) {
        return new ResponseEntity<>(userService.getClientUserDetail(accountId), HttpStatus.OK);
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/clientRegistration")
    public ObjectNode clientRegistration(@RequestParam(value = "userDTO", required = true) String userDTO,
                                         @RequestParam(value = "utilityMultipartFiles", required = false) List<MultipartFile> utilityMultipartFiles,
                                         @RequestParam(value = "sendForSigning", required = false, defaultValue = "false") Boolean sendForSigning
    ) throws JsonProcessingException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        //email validation code is added
        String email = userdto.getEmailAddress();
        User user = userService.saveOrUpdateClientUser(userdto, utilityMultipartFiles, sendForSigning);
        if (user != null && user.getAcctId() != null && userdto.getAcctId() == null) {
            return response.put("message", "New account successfully registered with email:" + user.getEmailAddress());
        } else if (user != null && user.getAcctId() != null && userdto.getAcctId() != null) {
            return response.put("message", " Account successfully updated for email:" + user.getEmailAddress());
        }

        return response.put("error", "Failed to create/update account");
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/assignLeads")
    public ResponseEntity<?> assignLeads(@RequestParam("entityIds") String
                                                 entityIds, @RequestParam("entityRoleId") Long entityRoleId) {
        List<Long> entityIdList = Arrays.stream(entityIds.split(",")).map(Long::parseLong).collect(Collectors.toList());

        return userService.assignLeads(entityIdList, entityRoleId);
    }

    //TODO: Need to remove when revamping customer management apis
    @PreAuthorize("checkAccess()")
    @GetMapping("/getAllCustomerListsByAccountId")
    public ResponseEntity<List<CaUserTemplateDTO>> getAllCustomerListsByAcctId(@RequestParam("accountId") Long
                                                                                       accountId) {
        return new ResponseEntity<>(userService.getAllCustomerListsByAcctId(accountId), HttpStatus.OK);
    }

    @PostMapping(value = "/forgetPassword")
    public ResponseEntity<Object> generateForgetPasswordEmail(@RequestBody UserDTO userDTO) {
        return userService.generateForgetPasswordEmail(userDTO);
    }

    @PutMapping("/updateUserPassword/{id}/{newPass}")
    public ObjectNode updateUserPassword(@RequestHeader("Comp-Key") Long compKey, @PathVariable("id") long id,
                                         @PathVariable("newPass") String newPass) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        masterTenantService.setCurrentDb(compKey);
        User isExist = userService.findById(id);
        if (isExist == null) {
            response.put("warning", "User invalid");
            return response;
        }
        User updateUser = userService.resetPassword(id, newPass); // we are getting already encoded password from fe
        response.put("message",
                "Password changed successfully for user " + updateUser.getFirstName() + " " + updateUser.getLastName());
        return response;
    }

    @GetMapping("/loadCustomerFilterData")
    public BaseResponse loadCustomerFilterData(@RequestParam(value = "exportDTO", required = false) String exportDTO) {
        return customerDetailService.loadCustomerFilterData(exportDTO);
    }


    @GetMapping("/countByYear")
    public List<YearDTO> countByYear() {
        return userService.countByYear();
    }

    @GetMapping("/getCustomerReadingExportData")
    public BaseResponse getCustomerReadingExportData(@RequestParam("customerType") List<String> customerType,
                                                     @RequestParam("states") List<String> states,
                                                     @RequestParam(value = "salesAgentId", required = false) List<String> salesAgentId,
                                                     @RequestParam("startDate") String startDate,
                                                     @RequestParam("endDate") String endDate,
                                                     @RequestParam("pageNumber") Integer pageNumber,
                                                     @RequestParam("pageSize") Integer pageSize) {
        return customerDetailService.getCustomerReadingExportData(customerType, states, salesAgentId, startDate, endDate, pageNumber, pageSize);
    }

    @GetMapping("/getCustomerSalesAgent")
    public BaseResponse getCustomerSalesAgent() {
        return customerDetailService.getCustomerSalesAgent();
    }

    @PostMapping("/saveLeadGeneration")
    public ObjectNode saveLeadGeneration(@RequestParam("template") String template,
                                         @RequestParam(value = "image", required = false) MultipartFile image,
                                         @RequestParam(value = "userDTO", required = true) String userDTO,
                                         @RequestHeader("Comp-Key") Long compKey,
                                         @RequestParam(value = "apiSource", required = false) @ApiParam("customerManagement") String apiSource
    ) throws JsonProcessingException {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        ObjectNode response = new ObjectMapper().createObjectNode();
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        ErrorDTO errorDTO = userService.validationCheck(template, false);
        if (errorDTO.isValidationFailed()) {
            throw new IllegalStateException(errorDTO.getMessage());
        }
        User user = userService.saveAcquisition(userdto, template, image, compKey, apiSource);
        if (user != null) {
            if (user.getAcctId() != null && userdto.getAcctId() == null) {
                return response.put("message", "New account successfully registered with email:" + user.getEmailAddress());
            } else if (user.getAcctId() != null) {
                return response.put("message", " Account successfully updated for email:" + user.getEmailAddress());
            }
        }
        return response.put("error", "Failed to create/update account");
    }


    @PostMapping("/updatePersonalInfo/{projectId}/{requestType}")
    public ObjectNode updatePersonalInfo(@RequestParam("template") String sectionJson,
                                         @PathVariable("projectId") String projectId,
                                         @PathVariable("requestType") String requestType,
                                         @RequestParam(value = "image", required = false) MultipartFile image,
                                         @RequestParam(value = "userDTO", required = true) String userDTO,
                                         @RequestHeader("Comp-Key") Long compKey
    ) throws JsonProcessingException {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        ObjectNode response = new ObjectMapper().createObjectNode();
        UserDTO userdto = objectMapper.readValue(userDTO, UserDTO.class);
        ErrorDTO errorDTO = userService.validationCheck(sectionJson, true);
        if (errorDTO.isValidationFailed()) {
            return response.put("error", errorDTO.getMessage());
        }
        //email validation code is added
        User user = userService.updateCaRegisteredUser(sectionJson, image, userdto, projectId, requestType, compKey);
        //        permissionsUtil.setNavigationUserMap(user.getUserName(), User.class);
        if (user != null) {
            if (user.getAcctId() != null && userdto.getAcctId() == null) {
                return response.put("message", "New account successfully registered with email:" + user.getEmailAddress());
            } else if (user.getAcctId() != null) {
                return response.put("message", " Account successfully updated for email:" + user.getEmailAddress());
            }
        }
        return response.put("error", "Failed to create/update account");
    }

    @PostMapping("/updateUserLandingPage")
    public BaseResponse updateUserLandingPage(@RequestBody UserDTO userDTO) {
        return userService.updateUserLandingPage(userDTO);
    }
}
