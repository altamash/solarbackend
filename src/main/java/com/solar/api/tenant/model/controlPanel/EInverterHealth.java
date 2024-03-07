package com.solar.api.tenant.model.controlPanel;

import java.util.Arrays;

public enum EInverterHealth {

    EXCELLENT("EXCELLENT"),
    GOOD("GOOD"),
    AVERAGE("AVERAGE");

    String health;

    EInverterHealth(String health) {
        this.health = health;
    }

    public String getHealth() {
        return health;
    }

    public static EInverterHealth get(String health) {
        return Arrays.stream(values()).filter(value -> health.equalsIgnoreCase(value.health)).findFirst().orElse(null);
    }
}
