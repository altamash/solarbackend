package com.solar.api.tenant.model.user.role;

import java.util.Arrays;

public enum ERole {
    ROLE_ADMIN("ADMIN"),
    ROLE_GLOBAL_ADMIN("GLOBAL_ADMIN"),
    ROLE_CUSTOMER("CUSTOMER"),
    ROLE_EPC_CUSTOMER("EPC_CUSTOMER"),
    ROLE_NEW_CUSTOMER("ROLE_NEW_CUSTOMER"),
    ROLE_PROSPECT("ROLE_PROSPECT"),

    ROLE_EMPLOYEE("ROLE_ADMIN"),

    SALES_REPRESENTATIVE("SALES_REPRESENTATIVE"),

    ACQUISITION_MANAGER("ACQUISITION_MANAGER"),

    ROLE_MANAGER("ROLE_MANAGER");


    String name;

    ERole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ERole get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
