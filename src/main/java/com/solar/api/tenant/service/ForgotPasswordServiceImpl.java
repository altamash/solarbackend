package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.solar.api.AppConstants;
import com.solar.api.saas.service.EmailService;
import com.solar.api.tenant.mapper.user.ResetPasswordDTO;
import com.solar.api.tenant.model.user.UniqueResetLink;
import com.solar.api.tenant.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

import static com.solar.api.tenant.mapper.user.UserMapper.toUserDTO;

@Service
//@Transactional("tenantTransactionManager")
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private static final String SUBJECT = "OTP For Reset Password";

    @Value("${app.fehostc}")
    private String feHostC;
    @Value("${app.fehosta}")
    private String feHostA;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;

    @Autowired
    UniqueResetLinkService uniqueResetLinkService;

    @Autowired
    EmailService emailService;

    @Autowired
    OtpService otpService;

    /**
     * @param email
     * @return
     * @throws IOException
     */
    @Override
    public ObjectNode verifyUser(String email, Long compKey) throws IOException {

        ObjectNode response = new ObjectMapper().createObjectNode();

        User userData = userService.findByEmailAddressFetchRoles(email);

        if (userData != null) {

            Long userID = toUserDTO(userData).getAcctId();
            StringBuilder salt = new StringBuilder();
            Random rnd = new Random();

            while (salt.length() < 18) {

                int index = (int) (rnd.nextFloat() * AppConstants.SALT_CHARS.length());
                salt.append(AppConstants.SALT_CHARS.charAt(index));
            }

            String saltStr = salt.toString();

            UniqueResetLink uniqueResetLink = new UniqueResetLink();
            uniqueResetLink.setTenantId(compKey);
            uniqueResetLink.setUserAccount(userID);
            uniqueResetLink.setAdminAccount(null);
            uniqueResetLink.setUniqueText(saltStr);
            uniqueResetLink.setUsedIndicator(false);
            uniqueResetLinkService.save(uniqueResetLink);

            sendLink(email, feHostC, saltStr, compKey);
//            if (userData.getUserType().getId() == 1l) {
//                sendLink(email, feHostC, saltStr);
//            } else {
//                sendLink(email, feHostA, saltStr);
//            }
            response.put("message", "Password reset email has been sent to " + email + ".");

        } else {

            response.put("error", "Invalid email address.");
        }
        return response;
    }

    /**
     * @param email
     * @param url
     * @param salt
     * @return
     * @throws IOException
     */
    @Override
    public Response sendLink(String email, String url, String salt, Long compKey) throws IOException {
        Email toEmail = new Email(email);
        Content content = new Content("text/html", "To reset your password, click "
                + "<a href='" + url + "#/reset-password" + "?ID=" + salt + "&TID=" + compKey + "'>"
                + "here"
                + "</a>");
        return emailService.sendEmail(toEmail, content, SUBJECT);
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
                response.put("error", "You've already reset your password with this token. Please request token again" +
                        ".");
                return response;
            }

            response.put("message", "Token verification successful.");

        } else {

            response.put("error", "Password reset token not found");
        }
        return response;
    }

    /**
     * @param resetPasswordDTO
     * @return
     */
    @Override
    public ObjectNode resetPassword(ResetPasswordDTO resetPasswordDTO) {

        ObjectNode response = new ObjectMapper().createObjectNode();

        UniqueResetLink uniqueResetLinkData = uniqueResetLinkService.findByUniqueText(resetPasswordDTO.token);

        if (uniqueResetLinkData != null) {

            if (uniqueResetLinkData.getUsedIndicator()) {

                response.put("error", "You've already reset your password with this token. Please request token again" +
                        ".");
                return response;

            } else {
                //Set tokenUse=true
                uniqueResetLinkData.setUsedIndicator(true);
                uniqueResetLinkService.save(uniqueResetLinkData);

                //Set new Password
                User userData = userService.findById(uniqueResetLinkData.getUserAccount());
                userData.setPassword(encoder.encode(resetPasswordDTO.newPassword));
                userService.saveUser(userData);
                response.put("message", "Password changed successfully!");
                return response;
            }

        } else {

            response.put("error", "Password reset token not found");
            return response;
        }
    }

    @Override
    public ObjectNode generateOtp(String email, Long compKey, String requestType) throws IOException {

        ObjectNode response = new ObjectMapper().createObjectNode();
        User userData = userService.findByEmailAddress(email);

        if (userData != null) {
            Long userID = userData.getAcctId();
            Integer otpCode = otpService.generateOTP(email, requestType);

            if (otpCode != null) {
                UniqueResetLink uniqueResetLink = new UniqueResetLink();
                uniqueResetLink.setTenantId(compKey);
                uniqueResetLink.setUserAccount(userID);
                uniqueResetLink.setAdminAccount(null);
                //uniqueResetLink.setUniqueText(saltStr);
                uniqueResetLink.setUsedIndicator(false);
                uniqueResetLinkService.save(uniqueResetLink);

                sendOtpEmail(email, otpCode, compKey);
                return response.put("message", "Generated OTP has been sent to your registered email : " + email + ".");
            }
        }
        return response.put("error", "Invalid email : " + email + ".");
    }

    @Override
    public Response sendOtpEmail(String email, Integer otp, Long compKey) throws IOException {
        Email toEmail = new Email(email);
        Content content = new Content("text/html", "<p>You're required to use the following "
                + "One Time Pin to reset your password :</p>"
                + "<p><b>" + otp + "</b></p>"
                + "<br>"
                + "<p>Note: this OTP is set to expire in 1 minute.</p>");
        return emailService.sendEmail(toEmail, content, SUBJECT);
    }

    @Override
    public ObjectNode verifyOtp(String email, Long compKey, Integer otp) throws IOException {
        ObjectNode response = new ObjectMapper().createObjectNode();
        User userData = userService.findByEmailAddress(email);
        if (userData != null) {
            Integer verifyCode = otpService.getOtp(email);
            if (verifyCode != 0) {
                if (otp.equals(verifyCode)) {
                    return response.put("message", "OTP has verified successfully.");
                } else {
                    return response.put("error", "Invalid OTP.");
                }
            }
            return response.put("error", "OTP doesn't exists.");
        } else {
            return response.put("error", "Invalid email : " + email + ".");
        }

    }

    @Override
    public ObjectNode resetPasswordByEmail(ResetPasswordDTO resetPasswordDTO) {
        ObjectNode response = new ObjectMapper().createObjectNode();

        User userData = userService.findByEmailAddress(resetPasswordDTO.emailId);
        if (userData != null) {
            userData.setPassword(encoder.encode(resetPasswordDTO.newPassword));
            userService.saveUser(userData);
            return response.put("message", "Password changed successfully!");
        }
        return response.put("error", "Invalid email : " + resetPasswordDTO.emailId + ".");
    }

}
