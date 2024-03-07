package com.solar.api.tenant.mapper.tiles.organizationmanagement;

public interface OrganizationManagementTemplate {
    Long getOrgId();

    String getUnitName();

    String getUnitImg();

    String getUnitType(); //unit_category_type

    String getUnitCategory(); //unit_category

    Long getUnitManagerId();

    String getUnitManagerName();

    String getUnitManagerPhone();

    String getUnitManagerEmail();

    String getUnitManagerImg();
    String getUnitDescription();

    Long getOfficeCount();

    Long getAssetCount();

    Long getCustomerCount();

    String getStatus();

    String getCreatedAt();

    String getUpdatedAt();
    Long getLinkedSiteCount();
}
