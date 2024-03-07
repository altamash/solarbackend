package com.solar.api.tenant.model.dataexport.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeExportDTO {

    private List<EmployeeDataDTO> employee;
    private List<String> employeementType;
    private List<EmployeeDataDTO> reportingManager;
    private String startDate;
    private String endDate;

}
