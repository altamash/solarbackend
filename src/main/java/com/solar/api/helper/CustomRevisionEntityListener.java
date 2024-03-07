package com.solar.api.helper;

import com.solar.api.tenant.model.CustomRevisionEntity;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.service.UserService;
import org.hibernate.envers.RevisionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CustomRevisionEntityListener implements RevisionListener, ApplicationContextAware {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static ApplicationContext applicationContext;

    @Override
    public void newRevision(Object revisionEntity) {
        try {
            CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                String userName = authentication.getName();
                customRevisionEntity.setUserName(userName);
                UserService userService = ((UserService) applicationContext.getBean("userServiceImpl"));
                User user = userService.findByUserName(userName);
                customRevisionEntity.setFullName(user.getFirstName() + (user.getLastName() != null ?
                        " " + user.getLastName() : ""));
            }
            if (RequestContextHolder.getRequestAttributes() != null) {
                customRevisionEntity.setClientIp(WebUtils.getClientIp(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest()));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CustomRevisionEntityListener.applicationContext = applicationContext;
    }
}
