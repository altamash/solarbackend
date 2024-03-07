package com.solar.api.tenant.mapper.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDetailDTO {
    private String employeeName;
    private String employeeEmail;
    private String employeePhone;
    private String profileUrl;
    private Long entityId;
    private Long acctId;


    public EmployeeDetailDTO(String employeeName, String employeeEmail, String employeePhone, String profileUrl) {
        this.employeeName = employeeName;
        this.employeeEmail = employeeEmail;
        this.employeePhone = employeePhone;
        this.profileUrl = profileUrl;
    }

    public EmployeeDetailDTO( Long acctId,String employeeName, String profileUrl) {
        this.employeeName = employeeName;
        this.profileUrl = profileUrl;
        this.acctId = acctId;
    }
}

