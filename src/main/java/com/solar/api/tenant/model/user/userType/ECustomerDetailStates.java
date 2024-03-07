package com.solar.api.tenant.model.user.userType;

import java.util.Arrays;

public enum ECustomerDetailStates {
    LEAD("LEAD"),
    PROSPECT("PROSPECT"),
    CUSTOMER("CUSTOMER"),
    INTERIMCUSTOMER("INTERIM-CUSTOMER"),
    APPROVAL_PENDING("APPROVAL PENDING"),
    CLOSED("CLOSED"),
    REQUEST_PENDING("REQUEST PENDING"),
    CONTRACT_PENDING("CONTRACT PENDING"),
    RESOLVED("RESOLVED"),
    DEFERRED("DEFERRED"),
    SELF_SIGNUP("SELF-SIGN-UP"),
    MANUAL("Manual");


    String name;

    ECustomerDetailStates(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ECustomerDetailStates get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
