package com.solar.api.tenant.model.user.userType;

import java.util.Arrays;

public enum EUserType {
    HO("HO"),
    CUSTOMER("CUSTOMER"),
    INTERIMCUSTOMER("INTERIM-CUSTOMER"),
    COMMERCIAL("COMMERCIAL"),
    RESIDENTIAL("RESIDENTIAL"),
    NONPROFIT("NON-PROFIT"),
    INDIVIDUAL("INDIVIDUAL"),
    PROSPECT("PROSPECT"),
    ADMIN("ADMIN"),
//    INVESTOR("INVESTOR"),
    EMPLOYEE("EMPLOYEE");


    String name;

    EUserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EUserType get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
