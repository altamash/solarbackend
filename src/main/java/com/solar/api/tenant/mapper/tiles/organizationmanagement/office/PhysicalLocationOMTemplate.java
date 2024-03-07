package com.solar.api.tenant.mapper.tiles.organizationmanagement.office;

public interface PhysicalLocationOMTemplate {
    Long getLocationId();

    Long getOrgId();

    Long getIsLeaf();

    String getLocationName();

    String getLocationCategory();

    String getLocationType();

    String getBusinessUnit();

    String getTimeZone();

    String getAddress();

    String getContactPersonName();

    String getContactPersonImage();

    String getGroupBy();
}
