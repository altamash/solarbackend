package com.solar.api.saas.model.tenant.type;

import java.util.Arrays;

public enum ETenantType {
    SAAS_ADMIN("SAAS ADMIN"),
    SITE_ADMIN("SITE_ADMIN");

    String name;

    ETenantType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ETenantType get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
