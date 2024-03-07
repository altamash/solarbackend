package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public interface EmployeeDTO {
    Long getId();

    String getEmployeeDesignation();

    String getEmployeeEmail();

    String getEmployeeName();

    String getEmployeePhone();

    String getCompanyName();

    Long getEntityRoleId();

    Long getFunctionalRoleId();

    String getImageUri();

    Long getEntityDetailId();
}
