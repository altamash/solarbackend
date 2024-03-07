package com.solar.api.tenant.model.user.userType;

import java.util.Arrays;

public enum EAuthenticationType {
    NA("NA"),
    STANDARD("STANDARD");
    String name;

    EAuthenticationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EAuthenticationType get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
