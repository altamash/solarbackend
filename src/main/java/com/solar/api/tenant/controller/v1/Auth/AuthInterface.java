package com.solar.api.tenant.controller.v1.Auth;

import com.solar.api.tenant.model.APIResponse;
import com.solar.api.tenant.model.JwtRequest;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public interface AuthInterface {
    APIResponse tanentSignIn(HttpServletRequest request, @Valid JwtRequest data, Long compKey) throws Exception;

    APIResponse signOutLog(HttpServletRequest request);

}
