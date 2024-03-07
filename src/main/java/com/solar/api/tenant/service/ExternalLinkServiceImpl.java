package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.solar.api.AppConstants;
import com.solar.api.exception.InvalidValueException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.EmailService;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.tenant.mapper.user.ExternalLinkDTO;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.mapper.user.UserMapper;
import com.solar.api.tenant.model.contract.Account;
import com.solar.api.tenant.model.contract.Entity;
import com.solar.api.tenant.model.contract.UserLevelPrivilege;
import com.solar.api.tenant.model.user.EUserStatus;
import com.solar.api.tenant.model.user.UniqueResetLink;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.model.user.role.Role;
import com.solar.api.tenant.service.contract.AccountService;
import com.solar.api.tenant.service.contract.EntityService;
import com.solar.api.tenant.service.contract.UserLevelPrivilegeService;
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.solar.api.tenant.mapper.user.UserMapper.userToUserDTO;

@Service
//@Transactional("tenantTransactionManager")
public class ExternalLinkServiceImpl implements ExternalLinkService {

    // private static final String SUBJECT = "Activate Your Password";

    @Value("${app.fehostc}")
    private String feHostC;
    @Value("${app.fehosta}")
    private String feHostA;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserService userService;
    @Autowired
    private UniqueResetLinkService uniqueResetLinkService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MasterTenantService masterTenantService;
    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @Autowired
    private RoleService roleService;

    @Autowired
    private UserLevelPrivilegeService userLevelPrivilegeService;
    /**
     * @param email
     * @return
     * @throws IOException
     */
    //generate salt value
    @Override
    public String verifyUser(String email, Long compKey, String subject) throws IOException {

        String saltStr = null;
        User userData = userService.findByEmailAddress(email);

        if (userData != null) {
            Long userID = userToUserDTO(userData).getAcctId();
            StringBuilder salt = new StringBuilder();
            Random rnd = new Random();

            while (salt.length() < 18) {

                int index = (int) (rnd.nextFloat() * AppConstants.SALT_CHARS.length());
                salt.append(AppConstants.SALT_CHARS.charAt(index));
            }

            saltStr = salt.toString();
            UniqueResetLink uniqueResetLink = new UniqueResetLink();
            uniqueResetLink.setTenantId(compKey);
            uniqueResetLink.setUserAccount(userID);
            uniqueResetLink.setAdminAccount(null);
            uniqueResetLink.setUniqueText(saltStr);
            uniqueResetLink.setUsedIndicator(false);
            uniqueResetLinkService.save(uniqueResetLink);
        }
//            sendLink(email, feHostC, saltStr, compKey, subject);
//            if (userData.getUserType().getId() == 1l) {
//                sendLink(email, feHostC, saltStr);
//            } else {
//                sendLink(email, feHostA, saltStr);
//            }
        return saltStr;
    }

    /**
     * @param email
     * @param url
     * @param salt
     * @return
     * @throws IOException
     */
    @Override
    public Response sendLink(String email, String url, String salt, Long compKey, String subject) throws IOException {
        Email toEmail = new Email(email);
        Content content = new Content("text/html", "To Activate your user, click "
                + "<a href='" + url + "#/auth/login" + "?ID=" + salt + "&TID=" + compKey + "'>"
                + "here"
                + "</a>");
        return emailService.sendEmail(toEmail, content, subject);
    }

    /**
     * @param token
     * @return
     */
    @Override
    public ObjectNode tokenVerification(String token) {

        ObjectNode response = new ObjectMapper().createObjectNode();

        UniqueResetLink uniqueResetLinkData = uniqueResetLinkService.findByUniqueText(token);
        if (uniqueResetLinkData != null) {
            if (uniqueResetLinkData.getUsedIndicator()) {
                response.put("error", "You've already used this token. Please request token again" +
                        ".");
                return response;
            }

            response.put("message", "Token verification successful.");

        } else {

            response.put("error", "Token not found");
        }
        return response;
    }

    /**
     * @param externalLinkDTO
     * @return
     */
    @Override
    public ObjectNode resetUserStatus(ExternalLinkDTO externalLinkDTO) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(externalLinkDTO.getCompKey());
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        Set<Role> roles = new HashSet<>();
        UniqueResetLink uniqueResetLinkData = uniqueResetLinkService.findByUniqueText(externalLinkDTO.getToken());
        if (uniqueResetLinkData != null) {
            if (uniqueResetLinkData.getUsedIndicator()) {
                response.put("error", " Account already verified, go to the login page.");
                response.put("identifier", masterTenant.getCompanyCode());
                return response;

            } else {

                uniqueResetLinkData.setUsedIndicator(true);
                uniqueResetLinkService.save(uniqueResetLinkData);
                //Set new status
                User userData = userService.findByIdNoThrow(uniqueResetLinkData.getUserAccount());
                Account accountData = accountService.findByUser(userData);
                Role role = roleService.findByName(ERole.ROLE_NEW_CUSTOMER.toString());
                if (role == null) {
                    response.put("error", "cannot activate your user");
                    throw new InvalidValueException("this role doesn't exist in DB", role.getName());

                }
                if (accountData == null) {
                    response.put("error", "cannot activate your user");
                    throw new NotFoundException(Account.class, "Account not found for userID=", userData.getAcctId() + "");
                }
                roles.add(role);
                userData.setRoles(roles);
                userData.setStatus(EUserStatus.ACTIVE.getStatus());
                userData.setEmailVerified(true);
                userService.saveUser(userData);
                accountData.setStatus(EUserStatus.ACTIVE.getStatus());
                accountService.save(accountData);
                response.put("message", "Change Applied successfully!");
                response.put("identifier", masterTenant.getCompanyCode());

                return response;
            }
        }
        return null;
    }


    /**
     * @param externalLinkDTO
     * @return
     */
    @Override
    public ObjectNode resetInternalUserStatus(ExternalLinkDTO externalLinkDTO) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(externalLinkDTO.getCompKey());
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        UniqueResetLink uniqueResetLinkData = uniqueResetLinkService.findByUniqueText(externalLinkDTO.getToken());
        if (uniqueResetLinkData != null) {
            if (uniqueResetLinkData.getUsedIndicator()) {
                response.put("error", " Account already verified, go to the login page.");
                response.put("identifier", masterTenant.getCompanyCode());
                return response;

            } else {

                uniqueResetLinkData.setUsedIndicator(true);
                uniqueResetLinkService.save(uniqueResetLinkData);
                //Set new status
                User userData = userService.findByIdNoThrow(uniqueResetLinkData.getUserAccount());
                userData.setStatus(EUserStatus.ACTIVE.getStatus());
                userData.setEmailVerified(true);
                userService.saveUser(userData);
                response.put("message", "Change Applied successfully!");
                response.put("identifier", masterTenant.getCompanyCode());
                return response;
            }
        }
        return null;
    }

    @Override
    public ObjectNode resetCAUserStatus(ExternalLinkDTO externalLinkDTO) {
        ObjectNode response = new ObjectMapper().createObjectNode();
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(externalLinkDTO.getCompKey());
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        Set<Role> roles = new HashSet<>();
        UniqueResetLink uniqueResetLinkData = uniqueResetLinkService.findByUniqueText(externalLinkDTO.getToken());
        if (uniqueResetLinkData != null) {
            if (uniqueResetLinkData.getUsedIndicator()) {
                response.put("error", " Account already verified, go to the login page.");
                response.put("identifier", masterTenant.getCompanyCode());
                return response;

            } else {
                uniqueResetLinkData.setUsedIndicator(true);
                uniqueResetLinkService.save(uniqueResetLinkData);
                //Set new status
                User userData = userService.findByIdNoThrow(uniqueResetLinkData.getUserAccount());
                Account accountData = accountService.findByUser(userData);
                Role role = roleService.findByName(ERole.ROLE_ADMIN.toString());
                if (role == null) {
                    response.put("error","cannot activate your user");
                    throw new InvalidValueException("this role doesn't exist in DB", role.getName());

                } if (accountData == null) {
                    response.put("error","cannot activate your user");
                    throw new NotFoundException(Account.class, "Account not found for userID=", userData.getAcctId() + "");
                }
                roles.add(role);
                userData.setRoles(roles);
                userData.setStatus(EUserStatus.ACTIVE.getStatus());
                userData.setEmailVerified(true);
                userService.saveUser(userData);
                accountData.setStatus(EUserStatus.ACTIVE.getStatus());
                accountService.save(accountData);
                response.put("message", "Change Applied successfully!");
                response.put("identifier", masterTenant.getCompanyCode());

                return response;
            }
        }
        return null;
    }

    @Override
    public Map tokenSessionExpiryVerification(String token, Long compKey) {
        Map response = new HashMap();
        UserDTO userDTO = UserDTO.builder().build();
        MasterTenant masterTenant = masterTenantService.findByCompanyKey(compKey);
        if(masterTenant != null) {
            UniqueResetLink uniqueResetLinkData = uniqueResetLinkService.findByUniqueText(token);
            if (uniqueResetLinkData != null) {
                 User userData = userService.findByIdNoThrow(uniqueResetLinkData.getUserAccount());
                if (userData != null) {
                    userDTO.setAcctId(userData.getAcctId());
                    userDTO.setCompKey(compKey);
                    userDTO.setCompanyName(masterTenant.getCompanyName());
                    userDTO.setUserType(userData.getUserType().getName().toString());
                    userDTO.setUserName(userData.getUserName());
                    userDTO.setEmailAddress(userData.getEmailAddress());
                }
                userDTO.setLoginUrl(masterTenant.getLoginUrl() != null ? masterTenant.getLoginUrl() : null);
                response.put("message", "Token verification successful.");
                response.put("code", HttpStatus.OK);
                response.put("data", userDTO);

                if (isPasswordSetLinkSessionExpired(uniqueResetLinkData)) {
                    response.put("message", "Session Expired" + ".");
                    response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
                    response.put("data", userDTO);
                } else if (uniqueResetLinkData.getUsedIndicator()) {
                    response.put("message", "You've already used this token. Please request token again" + ".");
                    response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
                    response.put("data", userDTO);
                }
                if(!uniqueResetLinkData.getUsedIndicator()){
                    uniqueResetLinkData.setUsedIndicator(true);
                    uniqueResetLinkService.save(uniqueResetLinkData);
                }
            } else {
                response.put("message", "Token not found");
                response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
                response.put("data", null);
            }

        }
        else{
            response.put("message", "compKey not found");
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("data", null);
        }

        return response;
    }

    private boolean isPasswordSetLinkSessionExpired(UniqueResetLink uniqueResetLink) {
        LocalDateTime localDateTimeDB = uniqueResetLink.getGeneratedOn();
        if ((uniqueResetLink.getGeneratedOn().plusMinutes(30l).isEqual(LocalDateTime.now())) ||
                (uniqueResetLink.getGeneratedOn().plusMinutes(30l).isBefore(LocalDateTime.now()))) {
            return true;
        }
        return false;
    }
}
