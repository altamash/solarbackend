package com.solar.api.tenant.controller.v1.Auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.ResponseEntityResult;
import com.solar.api.configuration.JwtTokenUtil;
import com.solar.api.configuration.UserTenantInformation;
import com.solar.api.configuration.authorization.PermissionsUtil;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.InvalidValueException;
import com.solar.api.saas.mapper.permission.navigation.NavigationElementDTO;
import com.solar.api.saas.mapper.permission.navigation.NavigationElementMapper;
import com.solar.api.saas.mapper.permission.navigation.NavigationElementMapperMapper;
import com.solar.api.saas.mapper.permission.navigation.NavigationUserMapDTO;
import com.solar.api.saas.module.com.solar.scheduler.mapper.BaseResponse;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.saas.service.job.EJobName;
import com.solar.api.saas.service.job.EJobStatus;
import com.solar.api.tenant.mapper.payment.info.PaymentInfoMapper;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMapper;
import com.solar.api.tenant.mapper.user.RegistrationData;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.mapper.user.UserMapper;
import com.solar.api.tenant.mapper.user.VerificationDTO;
import com.solar.api.tenant.mapper.user.address.AddressMapper;
import com.solar.api.tenant.mapper.user.userType.UserTypeDTO;
import com.solar.api.tenant.mapper.user.userType.UserTypeMapper;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.JwtRequest;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.EntityDetail;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.payment.info.PaymentInfo;
import com.solar.api.tenant.model.process.JobManagerTenant;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;
import com.solar.api.tenant.repository.CustomerDetailRepository;
import com.solar.api.tenant.repository.contract.EntityDetailRepository;
import com.solar.api.tenant.repository.contract.UserLevelPrivilegeRepository;
import com.solar.api.tenant.service.*;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.extended.project.EmployeeDetailService;
import com.solar.api.tenant.service.process.billing.BillingService;
import com.solar.api.tenant.service.process.permission.navigation.UserNavigation;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.ErrorDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;

@CrossOrigin
@RequiredArgsConstructor
@RestController("AuthController")
public class AuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final AuthInterface authInterface;
    @Autowired
    private UserService userService;
    @Autowired
    private UserTypeService userTypeService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private BillingService billingService;
    @Autowired
    private UserNavigation userNavigation;
    @Autowired
    private PermissionsUtil permissionsUtil;
    private Map<String, String> mapValue = new HashMap<>();
    private Map<String, String> userDbMap = new HashMap<>();
    @Autowired
    private CustomerDetailService customerDetailService;
    @Autowired
    private EmployeeDetailService employeeDetailService;
    @Autowired
    private EntityDetailRepository entityDetailRepository;
    private final ResponseEntityResult responseEntityResult;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PreAuthorize("checkAccess()")
    @PostMapping("/userType")
    public UserType addUserType(@RequestBody UserTypeDTO userTypeDto) {
        if (EUserType.get(userTypeDto.getName()) == null) {
            throw new InvalidValueException("userType", userTypeDto.getName());
        }
        return userTypeService.saveOrUpdate(UserTypeMapper.toUserType(userTypeDto));
    }

    /*@PostMapping("/role")
    public Role addrole(@RequestBody RoleDTO roleDto) {
        if (ERole.get(roleDto.getName()) == null) {
            throw new InvalidValueException("role", roleDto.getName());
        }
        return roleService.save(RoleMapper.toRole(roleDto));
    }*/

    @PostMapping("/signup")
    public UserDTO signup(@RequestBody RegistrationData registrationData,
                          @RequestParam(value = "isSubsActive", required =
                                  false, defaultValue = "false") boolean isSubsActive,
                          @RequestParam(value = "isLegacy", required = false, defaultValue = "true") Boolean isLegacy) {
        registrationData.getCustomerSubscriptions().forEach(customerSubscriptionDTO -> {
            customerSubscriptionDTO.getCustomerSubscriptionMappings().forEach(customerSubscriptionMappingDTO -> {
                customerSubscriptionMappingDTO.setId(null);
            });
        });
        UserDTO signupUser = registrationData.getUser();
        User userNameExists = userService.findByUserName(signupUser.getUserName());
        if (userNameExists != null) {
            throw new AlreadyExistsException(signupUser.getUserName());
        }
        /*User userExists = userService.findByEmail(signupRequest.getEmail());
        if (userExists != null) {
            throw new AlreadyExistsException(signupRequest.getEmail());
        }*/
        User user = UserMapper.toUser(signupUser);
        user.setPassword(encoder.encode(signupUser.getPassword()));

        // Set UserType
        if (signupUser.getUserType() != null) {
            UserType userType = userTypeService.findByName(EUserType.get(signupUser.getUserType()));
            user.setUserType(userType);
        }

        // Set Roles
        Set<String> strRoles = signupUser.getRoles();
        Set<Role> roles = new HashSet<>();
        strRoles.forEach(role -> {
            role = role.toUpperCase();
            roles.add(roleService.findByName(role.startsWith("ROLE_") ? role : "ROLE_" + role));
        });
        user.setRoles(roles);

        // Add Addresses
        registrationData.getAddresses().forEach(address -> {
            if (address.getAddressType() == null) {
                throw new InvalidValueException("addressType");
            }
        });
        registrationData.getAddresses().forEach(address -> {
            if (address.getAddress1() == null) {
                throw new InvalidValueException("address1");
            }
        });
        Set<Address> addresses = AddressMapper.toAddresses(
                registrationData.getAddresses());

        // Set CustomerSubscriptions
        List<CustomerSubscription> customerSubscriptions = CustomerSubscriptionMapper.toCustomerSubscriptions(
                registrationData.getCustomerSubscriptions());

        // Set PaymentInfos
        Set<PaymentInfo> paymentInfos = PaymentInfoMapper.toPaymentInfos(
                registrationData.getPaymentInfos());
        UserDTO userDTO = UserMapper.toUserAndSubscriptionsDTO(userService.saveOrUpdate(user, addresses, customerSubscriptions, paymentInfos));

        //to activate subscription
        if (isSubsActive) {
            if (isLegacy) {
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                ObjectNode requestMessage = new ObjectMapper().createObjectNode();
                requestMessage.put("userAccountId", userDTO.getAcctId());
                requestMessage.put("type", "activate");

                for (CustomerSubscription customerSubscription : CustomerSubscriptionMapper.toCustomerSubscriptions(userDTO.getCustomerSubscriptions())) {
                    requestMessage.put("subscriptionId", customerSubscription.getId());
                    JobManagerTenant jobManager =
                            billingService.addJobManagerTenant(EJobName.ACTIVATION + "_" + userDTO.getAcctId() +
                                    "_" + customerSubscription.getId(), requestMessage, EJobStatus.RUNNING.toString());
                    try {
                        billingService.enqueActivation(userDTO.getAcctId(), String.valueOf(customerSubscription.getId()), null, jobManager, true);
                    } catch (ParseException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            } else {
                ObjectNode messageJson = new ObjectMapper().createObjectNode();
                ObjectNode requestMessage = new ObjectMapper().createObjectNode();
                requestMessage.put("userAccountId", userDTO.getAcctId());
                requestMessage.put("type", "activate");

                for (Subscription subscription : CustomerSubscriptionMapper.toSubscriptions(userDTO.getCustomerSubscriptions())) {
                    requestMessage.put("subscriptionId", subscription.getId().getOid());
                    JobManagerTenant jobManager =
                            billingService.addJobManagerTenant(EJobName.ACTIVATION.toString() + "_" + userDTO.getAcctId() +
                                    "_" + subscription.getId(), requestMessage, EJobStatus.RUNNING.toString());
                    try {
                        billingService.enqueActivation(userDTO.getAcctId(), subscription.getId().getOid(), null, jobManager, false);
                    } catch (ParseException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
        permissionsUtil.setNavigationUserMap(userDTO.getUserName(), User.class);
        return userDTO;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(HttpServletRequest request, @Valid @RequestBody JwtRequest data,
                                    @RequestHeader("Comp-Key") Long compKey) {
        try {
            return responseEntityResult.responseEntity(authInterface.tanentSignIn(request, data, compKey));
        } catch (Exception exception) {
            return responseEntityResult.responseEntity(APIResponse.builder().code(500).warning(null).message(null).error(exception.getMessage()).data(null).build());
        }
    }

    @PostMapping("/logout/abrupt")
    public ResponseEntity<?> signOut(HttpServletRequest request, @RequestHeader(name = "Comp-Key") Long compKey,
                                     @RequestHeader(name = "abrupt", required = false) String abrupt) {
        try {
            if (abrupt != null && !abrupt.equals("true")) {
                return responseEntityResult.responseEntity(APIResponse.builder().code(200).warning(null).message(null).error("Abrupt value can only be true").data(null).build());
            } else {
                return responseEntityResult.responseEntity(authInterface.signOutLog(request));
            }
        } catch (Exception exception) {
            return responseEntityResult.responseEntity(APIResponse.builder().code(500).warning(null).message(null).error(exception.getMessage()).data(null).build());
        }

    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/verification")
    public ObjectNode verifyAdmin(@RequestBody VerificationDTO verificationDTO) throws Exception {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        User admin = userService.findById(verificationDTO.adminId);
        boolean isValidPassword = encoder.matches(verificationDTO.getPassword(), admin.getPassword());
        if (isValidPassword) {
            return userService.verifyAdmin(verificationDTO);
        }
        objectNode.put("warning", "Invalid password");
        return objectNode;
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/passwordVerification/{id}")
    public String passwordVerification(@PathVariable("id") long id, @RequestBody UserDTO userDto) {
        User userData = userService.findById(id);
        String requestPassword = userDto.getPassword();
        String savedPassword = userData.getPassword();
        System.out.println(requestPassword);
        System.out.println(savedPassword);
        String result = BCrypt.hashpw(requestPassword, savedPassword);
        if (result.equals(savedPassword)) {
            return "Verified";
        }
        return "Not Verified";
    }

    @PostMapping("/navelements")
    public BaseResponse setNavigationUserMap() {
        return permissionsUtil.setNavigationUserMap();
    }

    @PostMapping("/navelementsAsync")
    public BaseResponse setNavigationUserMapAsync() {
        return permissionsUtil.setNavigationUserMapAsync();
    }

    @PostMapping("/navelements/{userName}")
    public List<NavigationUserMapDTO> setNavigationUserMap(@PathVariable String userName) {
        return NavigationElementMapperMapper.toNavigationUserMapDTOs(permissionsUtil.setNavigationUserMap(userName, User.class));
    }

    @GetMapping("/navelements")
    public List<NavigationElementDTO> getInMemoryNavigationElements() {
        return NavigationElementMapper.toNavigationElementDTOs(userNavigation.getInMemoryNavigationElements());
    }

    @GetMapping("/navelements/{id}")
    public NavigationElementDTO getInMemoryNavigationElementsById(@PathVariable Long id) {
        return NavigationElementMapper.toNavigationElementDTO(userNavigation.getInMemoryNavigationElementById(id));
    }

    @GetMapping("/navelements/user")
    public List<NavigationElementDTO> getUserNavigationElements() {
        return NavigationElementMapper.toNavigationElementDTOs(userNavigation.getUserNavigationElements());
    }

    @GetMapping("/navelements/user/{userName}")
    public List<NavigationElementDTO> getUserNavigationElements(@PathVariable String userName) {
        return NavigationElementMapper.toNavigationElementDTOs(userNavigation.getUserNavigationElements(userName));
    }
    @PostMapping("/validateToken")
    public boolean validateToken( @RequestParam("jwtToekn") String token,
                                  @RequestHeader("Comp-Key") Long compKey){
        return jwtTokenUtil.validateToken(token);
    }
}
