package com.solar.api.configuration.authorization;

import com.solar.api.helper.WebUtils;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.model.tenant.role.ETenantRole;
import com.solar.api.saas.service.process.permission.PermissionSetService;
import com.solar.api.saas.service.tenantDetails.TenantDetailsImpl;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.ERole;
import com.solar.api.tenant.service.SubscriptionService;
import com.solar.api.tenant.service.userDetails.UserDetailsImpl;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Authentication authentication;
    private PermissionsUtil permissionsUtil;
    private PermissionSetService permissionSetService;
    private SubscriptionService subscriptionService;

    public CustomMethodSecurityExpressionRoot(Authentication authentication, PermissionsUtil permissionsUtil,
                                              PermissionSetService permissionSetService, SubscriptionService subscriptionService) {
        super(authentication);
        this.authentication = authentication;
        this.permissionsUtil = permissionsUtil;
        this.permissionSetService = permissionSetService;
        this.subscriptionService = subscriptionService;
    }

    public boolean checkAccess() {
        /*if (BasicErrorController.class.getSimpleName().equals(WebUtils.getControllerName())) {
            return true;
        }*/
        if (authentication instanceof AnonymousAuthenticationToken) {
            return true;
        }
        // TODO: remove following after permissions are set
        if (hasPermission(ERole.ROLE_ADMIN.toString())
                || hasPermission(ERole.ROLE_GLOBAL_ADMIN.toString())
                || hasPermission(ERole.ROLE_CUSTOMER.toString())
                || hasPermission(ERole.ROLE_EPC_CUSTOMER.toString())
                || hasPermission(ERole.ROLE_NEW_CUSTOMER.toString())
                || hasPermission(ETenantRole.ROLE_SAAS_ADMIN.toString())) {
            return true;
        }
        return hasPermission(WebUtils.getRequestMethod() + "_" + WebUtils.getRequestUrlPattern());
    }

    private boolean hasPermission(String... authorities) {
        Set<Long> permissionSetIds = new HashSet<>();
        if (getPrincipal() instanceof UserDetailsImpl) {
            permissionSetIds = permissionsUtil.getPermissionSetIds(authentication.getName(), User.class);
        } else if (getPrincipal() instanceof TenantDetailsImpl) {
            permissionSetIds = permissionsUtil.getPermissionSetIds(authentication.getName(), MasterTenant.class);
        }
        Set<Long> finalPermissionSetIds = permissionSetIds;
        return permissionSetService.getPermissionSetIdPermissionNameMap().entrySet().stream()
                .filter(e -> finalPermissionSetIds.contains(e.getKey()))
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toSet()).contains(authorities[0]);
    }

    // TODO: remove following after permissions are set
    private boolean hasPermission(String authority) {
        return authentication.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet()).contains(authority);
    }

    public boolean checkSubscriptionAccess(Long id) {
        return subscriptionService.getPrivilegedCustomerSubscriptions()
                .contains(subscriptionService.findCustomerSubscriptionById(id));
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }
}
