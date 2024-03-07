package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.service.integration.docuSign.dto.callback.ZResponse;
import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeDetailDTO;
import com.solar.api.tenant.mapper.extended.project.EmployeeManagementDTO;
import com.solar.api.tenant.mapper.tiles.UtilityInformationTile;
import com.solar.api.tenant.mapper.user.*;
import com.solar.api.tenant.mapper.user.userType.UserTypeDTO;
import com.solar.api.tenant.mapper.workOrder.UserDetailTemplateWoDTO;
import com.solar.api.tenant.mapper.workOrder.UserSubscriptionTemplateWoDTO;
import com.solar.api.tenant.model.ca.CaUtility;
import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.extended.physicalLocation.LocationMapping;
import com.solar.api.tenant.model.extended.physicalLocation.PhysicalLocation;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.permission.AvailablePermissionSet;
import com.solar.api.tenant.model.permission.PermissionGroup;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.TempPass;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.ErrorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    List<User> getAllUsers();

    User findById(Long id);

    User findByIdNoThrow(Long id);

    User findByIdFetchRoles(Long id);

    User findByIdFetchAddresses(Long id);

    User findByIdFetchAll(Long id);

    User findByEmailAddress(String email);

    User findByEmailAddressFetchRoles(String email);

    User findByUserName(String userName);

    List<User> findAllByUserName(String userName);

    User findByUserNameFetchRoles(String userName);

    User findByUserNameFetchPermissions(String userName);

    List<User> findAllFetchRoles();

    List<User> findAllCustomersFromEntity();

    List<User> findAllFetchPermissions();

    List<User> findAll();

    List<User> findByText(String text);

    List<User> findByUserType(EUserType userType);

    List<User> findByAcctIdIn(List<Long> ids);

    User saveOrUpdate(User user, Set<Address> addresses, List<CustomerSubscription> customerSubscriptions,
                      Set<PaymentInfo> paymentInfos);

    User saveUser(User user);

    List<User> saveAll(List<User> users);

    User resetPassword(Long id, String newPass);

    User setPassword(Long id, String newPass);

    ObjectNode verifyAdmin(VerificationDTO verificationDTO);

    ObjectNode resetCustomerPassword(VerificationDTO verificationDTO);

    User update(User user);

    void deleteUser(Long id);

    Optional<User> passwordVerification(Long id, String password);

    List<TempPass> passwordGenerator();

    List<Long> findUserBySubsId(List<Long> subIds);

    User getLoggedInUser();

    // User permissions
    Set<Role> addRole(Long userId, Long roleId);

    Set<Role> removeRole(Long userId, Long roleId);

    Set<PermissionGroup> addPermissionGroup(Long userId, Long permissionGroupId);

    Set<PermissionGroup> removePermissionGroup(Long userId, Long permissionGroupId);

    Set<AvailablePermissionSet> addPermissionSet(Long userId, Long availablePermissionSetId);

    Set<AvailablePermissionSet> removePermissionSet(Long userId, Long availablePermissionSetId);

    User userSelfRegistration(UserDTO userDto, List<MultipartFile> multipartFile,
                              List<MultipartFile> businessMultipartFiles);

    List<UserTypeDTO> getAllUserType();

    User internalUserRegistration(UserDTO userDto);

    User saveOrUpdateCaUser(UserDTO userDTO, List<MultipartFile> utilityMultipartFiles, Boolean sendForSigning, List<Long> ids);

    User saveAcquisition(UserDTO userDTO, String template, MultipartFile image, Long tenantId, String apiSource);

    List<CaUserTemplateDTO> getAllCaUser();

    List<CaUserTemplateDTO> getAllCaUserByType(String states);

    Map getAllCustomerCount();

    BaseResponse getAllCustomerList(String groupBy, String groupByName, Integer size, int pageNumber);

    CaUserTemplateDTO getCustomerSaleAgentByEntityId(String entityId);

    List<CaUserTemplateDTO> getAllCustomerListByProject(List<String> projectType);

    List<CaUserTemplateDTO> getAllCustomerListByProjectGroup();

    List<CaUserTemplateDTO> getAllCustomerByTypes(List<String> customerType);

    List<CaUserTemplateDTO> getAllCustomerByTypesGroup();

    List<CaUserTemplateDTO> getAllCustomerByRegion(List<String> region);

    List<CaUserTemplateDTO> getAllCustomerByRegionGroup();

//    List<UserRoleTemplateDTO> getAllCustomerByTypes(String customerType);

    UserDTO getCaUserDetail(Long userId);

    UserDTO createOrUpdateUserForEntity(RegistrationData registrationData, Account account, Entity entity, EmployeeManagementDTO employeeManagementDTO);

    User saveOrUpdateForEntity(User user);

    boolean isValidateUserName(String userName);

    List<UserRoleTemplateDTO> getAllUsersByCustomerType(String customerType);

    void callbackDocuSign(ZResponse zResponse);

    User findUserByEntityId(Long entityId);

    List<UtilityInformationTile> getCaUtilityByEntity(Long entityId);

    ResponseEntity<Object> saveRegisterInterestUser(UserDTO userDTO);

    Map getAllCustomerListSubscriptionSize();

    Map getAllCustomerListSubscription();

    Map getAllCustomerVarientId();

    Map getAllSubscriptionByCustomer(List<Long> customerId);

    UserDetailTemplateWoDTO findCustomerByAccountId(Long accountId);

    List<UserSubscriptionTemplateWoDTO> findUsersAndLocationByAccountId(List<Long> accountIds);

    /**
     * For Internal Usage
     *
     * @param entity
     * @return
     */
    Entity saveInEntity(Entity entity);

    CustomerDetail saveInCustomerDetail(CustomerDetail customerDetail);

    UserLevelPrivilege saveInUserLevelPrivilege(UserLevelPrivilege userLevelPrivilege);

    User updateCaUserStatus(UserDTO userDTO);

    User updateCaUserStatusV2(UserDTO userDTO);

    List<String> generateCaUsersCSV(Long compKey);

    String getUploadedDocumentUrl(MultipartFile file, String notes, Long entityId);

    User createOrUpdateUserForEntity(Entity entity, EmployeeDetailDTO employeeDetailDTO, UserDTO userDTO);

    ResponseEntity<Object> generatePasswordEmail(UserDTO userDTO);

    UserDTO getClientUserDetail(Long accountId);

    User saveOrUpdateClientUser(UserDTO userDTO, List<MultipartFile> utilityMultipartFiles, Boolean sendForSigning);

    User updateUser(Long id, String newPass);

    User updateUserPassword(Long id, String newPass);

    ResponseEntity<Object> assignLeads(List<Long> entityIds, Long entityRoleId);

    ResponseEntity<Object> generateForgetPasswordEmail(UserDTO userDTO);

    //TODO: Need to remove when revamping customer management apis
    List<CaUserTemplateDTO> getAllCustomerListsByAcctId(Long accountId);

    List<YearDTO> countByYear();

    User updateCaRegisteredUser(String sectionJson, MultipartFile image, UserDTO userDTO, String projectId, String requestType, Long compKey);

    ErrorDTO validationCheck(String sectionJson, Boolean isSectionJson);

    List<User> findByRoleName(String roleName);

    List<LocationMapping> getAcquisitionLocationMappings(List<PhysicalLocation> physicalLocationsDTO, User user, CaUtility utility, String primaryInd);

    BaseResponse updateLandingPageUrl(Long entityId, String entityTpe, String landingPageUrl);

    BaseResponse updateUserLandingPage(UserDTO userDTO);
}
