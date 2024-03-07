package com.solar.api.saas.service.process.aspect.workflow;

import com.solar.api.helper.Utility;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.service.MasterTenantService;
import com.solar.api.saas.service.workflow.HookValidator;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.ExternalLinkService;
import com.solar.api.tenant.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Configuration
public class InternalUserRegistrationAfterAopAspect {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Value("${app.fehostc}")
    private String feHostC;
    @Value("${app.fehosta}")
    private String feHostA;

    @Autowired
    private HookValidator hookValidator;
    @Autowired
    private UserService userService;
    @Autowired
    private ExternalLinkService externalLinkService;
    @Autowired
    private MasterTenantService masterTenantService;

    // executed only when a method executes successfully.
    @AfterReturning(value = "execution(* com.solar.api.tenant.service.UserServiceImpl.internalUserRegistration(..))", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String SUBJECT = "Activate Your User";
        if (result instanceof User) {
            User user = ((User) result);
            try {
                String userEmail = user.getEmailAddress();
                MasterTenant company = masterTenantService.findByCompanyKey(user.getCompKey());
                String tenantName = company.getCompanyName();
                String saltStr = externalLinkService.verifyUser(userEmail, user.getCompKey(), SUBJECT);
                String url = feHostA + "/#/verified" + "?ID=" + saltStr + "&TID=" + user.getCompKey()+" &TYPE=INTERNAL";
                Map<String, String> placeholderValues = new HashMap<>();
                String dateTime = user.getCreatedAt().format(DateTimeFormatter.ofPattern(Utility.SYSTEM_DATE_TIME_FORMAT));
                String customerName = userService.findById(user.getAcctId()).getFirstName();
                placeholderValues.put("user_name", customerName);
                placeholderValues.put("link", url);
                placeholderValues.put("user_email",userEmail);
                placeholderValues.put("tenant_name", tenantName);
                placeholderValues.put("tenant_id", user.getCompKey().toString());
                String[][] subjectParams = new String[][]{{customerName,url},{userEmail, tenantName}};
                hookValidator.hookFinder(null, "internal_user_registration", user.getAcctId(), null, placeholderValues, null, subjectParams);

            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        LOGGER.info("{} returned with value {}", joinPoint, result);
    }

    // executed in two situations — when a method executes successfully or it throws an exception.
    @After(value = "execution(* com.solar.api.tenant.service.UserServiceImpl.internalUserRegistration(..))")
    public void after(JoinPoint joinPoint) {
        LOGGER.info("after execution of {}", joinPoint);
    }
}