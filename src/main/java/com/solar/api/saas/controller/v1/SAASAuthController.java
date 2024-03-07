package com.solar.api.saas.controller.v1;


import com.solar.api.configuration.JwtTokenUtil;
import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.InvalidValueException;
import com.solar.api.saas.mapper.tenant.MasterTenantMapper;
import com.solar.api.saas.model.JwtRequestTenant;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.model.tenant.role.ETenantRole;
import com.solar.api.saas.model.tenant.role.TenantRole;
import com.solar.api.saas.model.tenant.type.ETenantType;
import com.solar.api.saas.model.tenant.type.TenantType;
import com.solar.api.saas.repository.TenantRoleRepository;
import com.solar.api.saas.repository.TenantTypeRepository;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.tenantDetails.TenantDetailsImpl;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@CrossOrigin
@RestController("SAASAuthController")
@RequestMapping(value = "/saas")
public class SAASAuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("saasAuthenticationManager")
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    @Qualifier("tenantDetailsService")
    private UserDetailsService jwtUserDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private TenantRoleRepository tenantRoleRepository;
    @Autowired
    private TenantTypeRepository tenantTypeRepository;

    private Map<String, String> mapValue = new HashMap<>();

    @PreAuthorize("checkAccess()")
    @PostMapping("/type")
    public TenantType addUserType(@RequestBody TenantType tenantType) {
        /*if (EUserType.get(userTypeDto.getName()) == null) {
            throw new InvalidValueException("type", userTypeDto.getName());
        }*/
        return tenantTypeRepository.save(tenantType);
    }

    @PreAuthorize("checkAccess()")
    @PostMapping("/role")
    public TenantRole addRole(@RequestBody TenantRole tenantRole) {
        /*if (EUserType.get(userTypeDto.getName()) == null) {
            throw new InvalidValueException("type", userTypeDto.getName());
        }*/
        return tenantRoleRepository.save(tenantRole);
    }

    @PostMapping("/signup")
    public MasterTenant signup(@RequestBody MasterTenant masterTenant) {
        MasterTenant userNameExists = masterTenantService.findByUserName(masterTenant.getUserName());
        if (userNameExists != null) {
            throw new AlreadyExistsException(masterTenant.getUserName());
        }
        masterTenant.setPassCode(encoder.encode(masterTenant.getPassCode()));

        // Set TenantType
        if (masterTenant.getType() != null) {
            TenantType tenantType = tenantTypeRepository.findByName(ETenantType.get(masterTenant.getType()));
            masterTenant.setTenantType(tenantType);
        }

        // Set TenantRoles
        Set<String> strRoles = masterTenant.getRoles();
        Set<TenantRole> roles = new HashSet<>();
        if (strRoles != null) {
            strRoles.forEach(role -> {
                if (ETenantRole.get(role) == null) {
                    throw new InvalidValueException("role", role);
                }
                switch (role) {
                    case "saas_admin":
                    case "SAAS_ADMIN":
                        roles.add(tenantRoleRepository.findByName(ETenantRole.ROLE_SAAS_ADMIN));
                        break;
                    case "site_admin":
                    case "SITE_ADMIN":
                        roles.add(tenantRoleRepository.findByName(ETenantRole.ROLE_SITE_ADMIN));
                        break;
                }
            });
            masterTenant.setTenantRoles(roles);
        }

        return masterTenantService.save(masterTenant);
    }

    @PostMapping("/signin")
    public ResponseEntity signin(@RequestBody JwtRequestTenant data) throws Exception {
        /*//set database parameter
        MasterTenant masterTenant = masterTenantService.findById(compKey);
        if(null == masterTenant || masterTenant.getStatus().toUpperCase().equals(EUserStatus.INACTIVE)){
            throw new SolarApiException("Please contact service provider.");
        }
        //Entry Client Wise value dbName store into bean.
        loadCurrentDatabaseInstance(masterTenant.getDbName(), data.getUserName());*/
        Authentication authentication = authenticate(data.getUserName(), data.getPassCode());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails tenantDetails = jwtUserDetailsService.loadUserByUsername(data.getUserName());
        final String token = jwtTokenUtil.generateToken(tenantDetails,
                String.valueOf(((TenantDetailsImpl) tenantDetails).getId()), null,null);
//        return ResponseEntity.ok(new JwtResponse(token));
        MasterTenant tenant = masterTenantService.findByUserNameFetchTenantRoles(data.getUserName());
//        loadCurrentDatabaseInstance(tenant.getDbName(), data.getUserName());
        tenant.setJwtToken(token);
//        setMetaDataAfterLogin();
        return ResponseEntity.ok(MasterTenantMapper.toMasterTenantDTO(tenant));
    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
//            ((AuthenticationManager) SpringContextHolder.getApplicationContext().getBean("saasAuthenticationManager"))
//                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            LOGGER.error("USER_DISABLED", e);
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            LOGGER.error("INVALID_CREDENTIALS", e);
            throw new Exception("INVALID_CREDENTIALS", e);
        }
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

}
