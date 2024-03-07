package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.widget.InfoService;
import com.solar.api.tenant.mapper.user.ExternalLinkDTO;
import com.solar.api.tenant.service.ExternalLinkService;
import com.solar.api.tenant.service.TempPassService;
import com.solar.api.tenant.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

@CrossOrigin
@RestController("ExternalLinkController")
@RequestMapping(value = "/externalLink")
public class ExternalLinkController {
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
    private ExternalLinkService externalLinkService;

    @GetMapping("/email/{email_address}")
    public String verifyUser(@RequestHeader("Comp-Key") Long compKey, @PathVariable("email_address") String email) throws IOException, MessagingException {
        String SUBJECT = "Activate Your Password";
        String salt = externalLinkService.verifyUser(email, compKey, SUBJECT);
        return salt;
    }

    @GetMapping("/tokenVerification/{token}/{Comp-Key}")
    public ObjectNode verifyToken(@PathVariable("Comp-Key") Long compKey, @PathVariable("token") String token) {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        return externalLinkService.tokenVerification(token);
    }

    @PostMapping("/resetUserStatus")
    public ObjectNode resetUserStatus(@RequestBody ExternalLinkDTO externalLinkDTO) {
        return externalLinkService.resetUserStatus(externalLinkDTO);
    }

    @PostMapping("/resetInternalUserStatus")
    public ObjectNode resetInternalUserStatus(@RequestBody ExternalLinkDTO externalLinkDTO) {
        return externalLinkService.resetInternalUserStatus(externalLinkDTO);
    }

    @PostMapping("/resetCAUserStatus")
    public ObjectNode resetCAUserStatus(@RequestBody ExternalLinkDTO externalLinkDTO) {
        return externalLinkService.resetCAUserStatus(externalLinkDTO);
    }

    @GetMapping("/tokenSessionExpiryVerification/{token}/{Comp-Key}")
    public Map tokenSessionExpiryVerification(@PathVariable("Comp-Key") Long compKey, @PathVariable("token") String token) {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        return externalLinkService.tokenSessionExpiryVerification(token, compKey);
    }
}
