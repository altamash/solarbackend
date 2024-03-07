package com.solar.api.tenant.model.customerSupport;

import java.util.Arrays;

public enum EConversationStatus {
    OPEN("Open"),
    RESOLVED("Resolved"),
    CLOSED("Closed");

    String name;

    EConversationStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EConversationStatus get(String name) {
        return Arrays.stream(values()).filter(value -> name.equalsIgnoreCase(value.name)).findFirst().orElse(null);
    }
}
