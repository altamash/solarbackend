package com.solar.api.tenant.mapper.tiles.dataexport.employeedetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor

public class DataExportEmployeeTile {

    private String employeeName;
    private String email;
    private String designation;
    private String phone;
    private String employmentType;
    private String reportingManager;
    private String joiningDate;

    public DataExportEmployeeTile(String employeeName, String email, String designation, String phone, String employmentType, String reportingManager, String joiningDate) {
        this.employeeName = employeeName;
        this.email = email;
        this.designation = designation;
        this.phone = phone;
        this.employmentType = employmentType;
        this.reportingManager = reportingManager;
        this.joiningDate = joiningDate;
    }
}
