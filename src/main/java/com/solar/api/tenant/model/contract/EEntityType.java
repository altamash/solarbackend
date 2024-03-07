package com.solar.api.tenant.model.contract;

import java.util.Arrays;

public enum EEntityType {
    COMMERCIAL("COMMERCIAL"),
    INDIVIDUAL("INDIVIDUAL"),
    ORGANIZATION("ORGANIZATION"),
    INTERNAL("INTERNAL"),
    BUSINESS("BUSINESS"),
    RESIDENTIAL("RESIDENTIAL"),
    NONPROFIT("NON-PROFIT"),
    INDUSTRIAL("INDUSTRIAL");


    String entityType;

    EEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

    public static EEntityType get(String entityType) {
        return Arrays.stream(values()).filter(value -> entityType.equalsIgnoreCase(value.entityType)).findFirst().orElse(null);
    }
}
