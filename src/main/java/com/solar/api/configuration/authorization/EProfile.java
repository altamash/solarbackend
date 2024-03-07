package com.solar.api.configuration.authorization;

import java.util.Arrays;

public enum EProfile {

    LOCAL("local"),
    DEV("dev"),
    STAGE("stage"),
    PREPROD("preprod"),
    PROD("prod"),
    NEWPROD("newprod");

    String name;

    EProfile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EProfile get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
