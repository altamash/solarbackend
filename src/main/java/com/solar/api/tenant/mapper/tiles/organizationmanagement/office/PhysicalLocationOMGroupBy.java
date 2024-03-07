package com.solar.api.tenant.mapper.tiles.organizationmanagement.office;

import java.util.Arrays;

public enum PhysicalLocationOMGroupBy {
    NONE("NONE"),
    CATEGORY("CATEGORY"),
    TYPE("TYPE"),
    BUSINESS_UNIT("BUSINESS UNIT");
    String type;

    PhysicalLocationOMGroupBy(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static PhysicalLocationOMGroupBy get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }
}
