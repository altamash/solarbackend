package com.solar.api.configuration.authorization;

import com.solar.api.configuration.SpringContextHolder;
import com.solar.api.saas.service.process.permission.PermissionSetService;
import com.solar.api.tenant.service.SubscriptionService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        ApplicationContext context = SpringContextHolder.getApplicationContext();
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication,
                context.getBean(PermissionsUtil.class), context.getBean(PermissionSetService.class),
                context.getBean(SubscriptionService.class));
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
