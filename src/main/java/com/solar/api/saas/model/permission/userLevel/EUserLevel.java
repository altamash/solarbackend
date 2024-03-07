package com.solar.api.saas.model.permission.userLevel;

import java.util.Arrays;

public enum EUserLevel {
    HO("HO"),
    CUSTOMER("CUSTOMER");

    String name;

    EUserLevel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EUserLevel get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
