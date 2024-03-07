package com.solar.api.tenant.configuration;

import com.solar.api.saas.configuration.DBContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT_ID = "tenant_ref";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = DBContextHolder.getTenantName();
        return StringUtils.isNotBlank(tenant) ? tenant : DEFAULT_TENANT_ID;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
