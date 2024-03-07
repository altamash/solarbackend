package com.solar.api.saas.model.tenant.role;

import java.util.Arrays;

public enum ETenantRole {

    ROLE_SAAS_ADMIN("SAAS_ADMIN"),
    ROLE_SITE_ADMIN("SITE_ADMIN");

    String name;

    ETenantRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ETenantRole get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
