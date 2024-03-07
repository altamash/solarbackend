package com.solar.api.configuration.authorization;

import com.solar.api.tenant.model.user.Address;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.service.AddressService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

public class SolarPermissionEvaluator implements PermissionEvaluator, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
//        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)) {
        if ((auth == null) || !(permission instanceof String)) {
            return false;
        }
        return validateAccess(auth, targetDomainObject, permission.toString());
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType,
                                 Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return validateAccess(auth, targetType, permission.toString());
    }

    private boolean validateAccess(Authentication auth, Object targetDomainObject, String permission) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ERole.ROLE_CUSTOMER.toString()))) {
            if (Address.class.getSimpleName().equals(permission)) {
                if (targetDomainObject instanceof Long) {
                    Address address =
                            applicationContext.getBean(AddressService.class).findByIdNoThrow((Long) targetDomainObject);
                    if (address != null && address.getUserAccount().getAcctId() != targetDomainObject) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
