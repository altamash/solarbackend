package com.solar.api.configuration;

import com.solar.api.exception.SolarApiException;
import com.solar.api.saas.configuration.DBContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class RequestAuthorizationIntercept {

    @Autowired
    private ApplicationContext applicationContext;

    @Around("@annotation(com.solar.api.configuration.RequestAuthorization)")
    public Object checkPermission(ProceedingJoinPoint pjp) throws Throwable {
        UserTenantInformation tenantInformation = applicationContext.getBean(UserTenantInformation.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (null == userDetails) {
            throw new RuntimeException("Access is Denied. Please login again or contact service provider");
        }
        Map<String, String> map = tenantInformation.getMap();
        String tenantName = map.get(userDetails.getUsername());
        if (tenantName != null && tenantName.equals(DBContextHolder.getTenantName())) {
            return pjp.proceed();
        }
        throw new SolarApiException("Access is Denied. Please login again or contact service provider");
    }
}
