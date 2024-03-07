package com.solar.api.tenant.model.user.contractStatus;

import com.solar.api.tenant.model.user.userType.ESourceType;

import java.util.Arrays;

public enum EContractStatus {
    SIGNED_PENDING("Signed Pending"),
    COMPLETED("Completed"),
    CustomerClient("Customer - Client");

    String name;

    EContractStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EContractStatus get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
