package com.solar.api.tenant.mapper.contract;

public interface OrgDetailDTO {
    String getUnitName();

    Long getUnitTypeId();

    Long getUnitId();

    String getUnitManager();
    Long getOrgDetailId();
    Long getEntityRoleId();

    String getParentName();

    Long getParentId();

    String getDetails();

    //    PhysicalLocationDTO getPhysicalLocationDto();
    Long getLocId();

    String getAdd1();

    String getAdd2();

    String getZipCode();

    String getGeoLat();

    String getGeoLong();

    String getExt1();

    String getExt2();

    String getContactPerson();

    String getEmail();
}
