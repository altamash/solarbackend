package com.solar.api.tenant.controller.v1.Auth;

import com.solar.api.configuration.APIAccessInterceptor;
import com.solar.api.configuration.JwtTokenUtil;
import com.solar.api.configuration.UserTenantInformation;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.process.upload.mapper.Customer;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.mapper.user.UserMapper;
import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.JwtRequest;
import com.solar.api.tenant.model.companyPreference.CompanyPreference;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.EntityDetail;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.customer.CustomerDetail;
import com.solar.api.tenant.model.extended.project.EmployeeDetail;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.userType.EAuthenticationType;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.repository.CustomerDetailRepository;
import com.solar.api.tenant.repository.contract.EntityDetailRepository;
import com.solar.api.tenant.repository.project.EmployeeDetailRepository;
import com.solar.api.tenant.service.CompanyPreferenceService;
import com.solar.api.tenant.service.CustomerDetailService;
import com.solar.api.tenant.service.UserService;
import com.solar.api.tenant.service.UserTypeService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import com.solar.api.tenant.service.extended.project.EmployeeDetailService;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.ErrorDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import static com.vladmihalcea.hibernate.util.LogUtils.LOGGER;

@Service
@RequiredArgsConstructor
public class AuthControllerService implements AuthInterface {


    private final JwtTokenUtil jwtTokenUtil;
    private final CustomerDetailService customerDetailService;
    private final MasterTenantService masterTenantService;
    private final UserLevelPrivilegeService userLevelPrivilegeService;
    private final UserTypeService userTypeService;
    private final UserService userService;
    private final CompanyPreferenceService companyPreferenceService;
    private final EntityDetailRepository entityDetailRepository;
    private final EmployeeDetailService employeeDetailService;
    @Qualifier("tenantAuthenticationManager")
    @Autowired
    private AuthenticationManager authenticationManager;
    @Qualifier("userDetailsService")
    @Autowired
    private UserDetailsService jwtUserDetailsService;
    @Autowired
    private EmployeeDetailRepository employeeDetailRepository;
    private Map<String, String> mapValue = new HashMap<>();

    @Override
    public APIResponse tanentSignIn(HttpServletRequest request, @Valid JwtRequest data, Long compKey) {
        try {
            HttpSession session = request.getSession();
            String sessionId = session.getId();
            session.setAttribute("sessionId", sessionId);
            MasterTenant masterTenant = masterTenantService.findById(compKey);
            if (masterTenant.getId() == null) {
                return APIResponse.builder().code(200).warning(null).message("Enter A Valid Comp-Key ").error("Data Not Found For The following Comp-Key :" + compKey).data(null).build();
            }

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(data.getUserName(), data.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userService.findByUserNameFetchRoles(data.getUserName());
            Entity entity = null;
            String landingPageURl = null;
            UserLevelPrivilege userLevelPrivilege1 = null;
            String entityProfilePic = "";
            Long entityDetailId = 0l;
            UserDTO userDTO = UserMapper.toUserDTO(user);
            if (user != null) {

                UserLevelPrivilege userLevelPrivilege = userLevelPrivilegeService.userLevelPrivilegeByAccountId(user.getAcctId());

                if (data.getIsMobileLogin() != null && data.getIsMobileLogin()) {
                    if (user.getUserType() != null && user.getUserType().getId()==2) {
                        return (APIResponse.builder().code(403).warning(null).message(null)
                                .error("You do not have permission to login on mobile using admin Type").data(null).build());
                    }

                    if (userLevelPrivilege != null && userLevelPrivilege.getEntity() != null) {
                        CustomerDetail customerDetail = customerDetailService.findByEntity(userLevelPrivilege.getEntity());
                        if (customerDetail != null && !customerDetail.isMobileAllowed()) {
                            return (APIResponse.builder().code(403).warning(null).message(null)
                                    .error("You do not have permission to login on mobile").data(null).build());
                        }
                    }
                }
                if((user.getAuthentication() != null && !user.getAuthentication().equalsIgnoreCase(EAuthenticationType.STANDARD.getName())) ||user.getAuthentication()==null || user.getUserType()==null){
                    return (APIResponse.builder().code(403).warning(null).message(null)
                            .error("Login is not allowed for this user").data(null).build());
                }
                if (userLevelPrivilege != null && userLevelPrivilege.getEntity() != null) {
                    entity = userLevelPrivilege.getEntity();
                }
            }

            loadCurrentDatabaseInstance(masterTenant, data.getUserName());
            final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(data.getUserName());
            final String token = jwtTokenUtil.generateToken(userDetails, String.valueOf(masterTenant.getId()),
                    masterTenant.getTenantTier(), sessionId);
            CompanyPreference companyPreference = companyPreferenceService.findByCompanyKey(masterTenant.getCompanyKey());
            EmployeeDetail employeeDetail=null;
            CustomerDetail customerDetail =null;

            if (entity != null) {

                String entityType = entity.getEntityType();
                EntityDetail entityDetail = entityDetailRepository.findByEntityId(entity.getId());
                if (entityDetail != null) {
                    entityProfilePic = entityDetail.getUri() != null ? entityDetail.getUri() : null;
                    entityDetailId = entityDetail.getId() != null ? entityDetail.getId() : null;
                }
                if (EUserType.CUSTOMER.getName().equalsIgnoreCase(entityType)) {
                    customerDetail = customerDetailService.findByEntity(entity);
                    landingPageURl = customerDetail != null ? customerDetail.getLandingDefaultUrl() : null;

                } else if (EUserType.EMPLOYEE.getName().equalsIgnoreCase(entityType)) {
                    employeeDetail = employeeDetailService.findByEntity(entity);
                    landingPageURl = employeeDetail != null ? employeeDetail.getLandingDefaultUrl() : null;
                }
                if (user.getUserType().getId() == 2) {
                    userDTO.setDesignation(employeeDetail != null && employeeDetail.getDesignation() != null ? employeeDetail.getDesignation() : "-");
                }

                if (user.getUserType().getId() == 1) {
                    userDTO.setDesignation(customerDetail != null && customerDetail.getCustomerType() != null ? customerDetail.getCustomerType() : "-");
                }
                if (landingPageURl == null) {
                    if (user.getUserType().getId() == 2) {
                        landingPageURl = companyPreference.getAdminLanding() != null ? companyPreference.getAdminLanding() : "";
                    } else if (user.getUserType().getId() == 1) {
                        landingPageURl = companyPreference.getCompanyLanding() != null ? companyPreference.getCompanyLanding() : "";
                    }
                }
            }

                userDTO.setJwtToken(token);
                setMetaDataAfterLogin();
                MDC.put("Comp-Key", String.valueOf(compKey));
                LOGGER.info("Logged in as " + data.getUserName());


                userDTO.setLandingPageUrl(landingPageURl);
                userDTO.setEntityProfileUri(entityProfilePic);
                userDTO.setEntityDetailId(entityDetailId);
                if (entity != null) {
                    userDTO.setEntityId(entity.getId());
                }


            return (APIResponse.builder().code(200).warning(null).message(null).error(null).data(userDTO). build());
        } catch (Exception exception) {
            if (exception instanceof BadCredentialsException) {
                // Handle BadCredentialsException separately
                BadCredentialsException badCredentialsException = (BadCredentialsException) exception;
                // Your specific handling for BadCredentialsException
                return APIResponse.builder()
                        .code(401) // Unauthorized status code for bad credentials
                        .warning(null)
                        .message("UNAUTHORIZED")
                        .error(badCredentialsException.getMessage())
                        .data(null)
                        .build();
            } else if (exception instanceof DisabledException ) {
                DisabledException disabledException = (DisabledException) exception;

                // Your specific handling for BadCredentialsException
                return APIResponse.builder()
                        .code(401) // Unauthorized status code for bad credentials
                        .warning(null)
                        .message("User disabled")
                        .error(disabledException.getMessage())
                        .data(null)
                        .build();
            } else {
                // Handle other exceptions
                return APIResponse.builder()
                        .code(500)
                        .warning(null)
                        .message(null)
                        .error(exception.getMessage())
                        .data(null)
                        .build();
            }
        }
    }

    private void loadCurrentDatabaseInstance(MasterTenant masterTenant, String userName) {
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        mapValue.put(userName, masterTenant.getDbName());
    }

    private Map<String, String> userDbMap = new HashMap<>();

    @Bean(name = "userTenantInfo")
    @ApplicationScope
    public UserTenantInformation setMetaDataAfterLogin() {
        UserTenantInformation tenantInformation = new UserTenantInformation();
        if (mapValue.size() > 0) {
            for (String key : mapValue.keySet()) {
                if (null == userDbMap.get(key)) {
                    userDbMap.putAll(mapValue);
                } else {
                    userDbMap.put(key, mapValue.get(key));
                }
            }
            mapValue = new HashMap<>();
        }
        tenantInformation.setMap(userDbMap);
        return tenantInformation;
    }


    @Override
    public APIResponse signOutLog(HttpServletRequest request) {
        return (APIResponse.builder().code(200).warning(null).message("SignOut Logged.").error(null).data(null).build());
    }
}
