package com.solar.api.tenant.controller.v1;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.tenant.mapper.user.ResetPasswordDTO;
import com.solar.api.tenant.model.billing.BillingInvoice.PublishInfo;
import com.solar.api.tenant.service.ForgotPasswordService;
import com.solar.api.tenant.service.process.billing.publish.PublishInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@PreAuthorize("checkAccess()")
@CrossOrigin
@RestController("ForgotPasswordController")
@RequestMapping(value = "/forgotPassword")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @Autowired
    private PublishInfoService publishInfoService;

    @GetMapping("/email/{email_address}")
    public ObjectNode verifyUser(@RequestHeader("Comp-Key") Long compKey, @PathVariable("email_address") String email) throws IOException, MessagingException {
        return forgotPasswordService.verifyUser(email, compKey);
    }

    @GetMapping("/tokenVerification/{token}/{Comp-Key}")
    public ObjectNode verifyToken(@PathVariable("Comp-Key") Long compKey, @PathVariable("token") String token) {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        return forgotPasswordService.tokenVerification(token);
    }

    @PostMapping("/resetPassword")
    public ObjectNode resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return forgotPasswordService.resetPassword(resetPasswordDTO);
    }

    @PostMapping("/resetPasswordByEmail")
    public ObjectNode resetPasswordByEmail(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        return forgotPasswordService.resetPasswordByEmail(resetPasswordDTO);
    }

    @GetMapping("/generateOtp/{emailId}/{requestType}")
    public ObjectNode generateOtp(@RequestHeader("Comp-Key") Long compKey, @PathVariable("emailId") String emailId, @PathVariable("requestType") String requestType) throws IOException, MessagingException {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        return forgotPasswordService.generateOtp(emailId, compKey, requestType);
    }

    @GetMapping("/verifyOtp/{emailId}/{otp}")
    public ObjectNode verifyOtp(@RequestHeader("Comp-Key") Long compKey, @PathVariable("emailId") String emailId, @PathVariable("otp") Integer otp) throws IOException, MessagingException {
        MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
        DBContextHolder.setTenantName(masterTenant.getDbName());
        DBContextHolder.setLegacy(masterTenant.getLegacyBilling());
        return forgotPasswordService.verifyOtp(emailId, compKey, otp);
    }

    @PostMapping("/publish-info/{headId}")
    public PublishInfo save(@PathVariable("headId") Long headId) {
        return publishInfoService.save(headId);
    }
}
