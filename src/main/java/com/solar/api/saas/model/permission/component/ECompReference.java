package com.solar.api.saas.model.permission.component;

import java.util.Arrays;

public enum ECompReference {

    Batch("Batch"),
    UI("UI"),
    API("API"),
    System("System");

    String type;

    ECompReference(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ECompReference get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findAny().orElse(null);
    }
}
