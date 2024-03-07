package com.solar.api.tenant.model.contract;

import java.util.Arrays;

public enum ETenancyTier {
    STANDARD(1, "STANDARD"),
    PREMIUM(2, "PREMIUM"),
    ENTERPRISE(3, "ENTERPRISE");

    private Integer value;
    private String tenancyTier;

    ETenancyTier(Integer value, String tenancyTier) {
        this.value = value;
        this.tenancyTier = tenancyTier;
    }

    public Integer getValue() {
        return value;
    }

    public String getTenancyTier() {
        return tenancyTier;
    }

    public static ETenancyTier get(String tenancyTier) {
        return Arrays.stream(values()).filter(value -> tenancyTier.equalsIgnoreCase(value.tenancyTier)).findFirst().orElse(null);
    }

    public static ETenancyTier fromValue(Integer value) {
        for (ETenancyTier tenancyTier : values()) {
            if (tenancyTier.value.equals(value)) {
                return tenancyTier;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
