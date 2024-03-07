package com.solar.api.saas.configuration;

import org.springframework.stereotype.Component;

@Component
public class DBContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> legacyHolder = new ThreadLocal<>();

    public static void setTenantName(String dbType) {
        contextHolder.set(dbType);
    }

    public static String getTenantName() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }

    public static void setLegacy(Boolean legacy) {
        legacyHolder.set(legacy);
    }

    public static Boolean isLegacy() {
        return legacyHolder.get();
    }

}
