package com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter;

import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMGroupBy;

import java.util.Arrays;

import static com.solar.api.AppConstants.type;

public enum LinkedSiteGroupBy {

    NONE("NONE"),
    GARDEN_TYPE("GARDEN TYPE"),
    GARDEN_OWNER("GARDEN OWNER");

    String type;
    LinkedSiteGroupBy(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter.LinkedSiteGroupBy get(String type) {
        return Arrays.stream(values()).filter(value -> type.equalsIgnoreCase(value.type)).findFirst().orElse(null);
    }
}
