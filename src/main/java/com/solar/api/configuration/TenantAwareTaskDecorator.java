package com.solar.api.configuration;

import com.solar.api.saas.configuration.DBContextHolder;
import org.springframework.core.task.TaskDecorator;

public class TenantAwareTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        String tenantName = DBContextHolder.getTenantName();
        Boolean isLegacy = DBContextHolder.isLegacy();
        return () -> {
            try {
                DBContextHolder.setTenantName(tenantName);
                DBContextHolder.setLegacy(isLegacy);
                runnable.run();
            } finally {
                DBContextHolder.setTenantName(null);
                DBContextHolder.setLegacy(null);
            }
        };
    }
}
