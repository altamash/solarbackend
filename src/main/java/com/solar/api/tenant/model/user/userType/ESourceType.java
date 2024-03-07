package com.solar.api.tenant.model.user.userType;

import java.util.Arrays;

public enum ESourceType {
    SELF_SIGNUP("Self Signup"),
    MANUAL("Manual"),
    CSVUPLOAD("Csv Upload");


    String name;

    ESourceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ESourceType get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
