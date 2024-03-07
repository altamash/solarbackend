package com.solar.api.tenant.mapper;

import java.util.Date;

public interface EntityGroupTemplate {
    Long getEntityGroupId();
    String getEntityName();
    String getEmail();
    String getDesignation();
    String getContactNumber();
    String getEmployeeType();
    Date getJoiningDate();
    String getImageURI();
    Long getEntityRoleId();
    Long getEntityId();
}
