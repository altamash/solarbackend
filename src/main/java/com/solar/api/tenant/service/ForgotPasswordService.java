package com.solar.api.tenant.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sendgrid.Response;
import com.solar.api.tenant.mapper.user.ResetPasswordDTO;

import java.io.IOException;

public interface ForgotPasswordService {

    ObjectNode verifyUser(String email, Long compKey) throws IOException;

    Response sendLink(String email, String url, String salt, Long compKey) throws IOException;

    ObjectNode tokenVerification(String token);

    ObjectNode resetPassword(ResetPasswordDTO resetPasswordDTO);

    ObjectNode resetPasswordByEmail(ResetPasswordDTO resetPasswordDTO);

    ObjectNode generateOtp(String email, Long compKey, String requestType) throws IOException;

    public Response sendOtpEmail(String email, Integer otp, Long compKey) throws IOException;

    ObjectNode verifyOtp(String email, Long compKey, Integer otp) throws IOException;

}
