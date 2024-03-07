package com.solar.api.tenant.mapper.tiles.projectmanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.employee.EmployeeDetailDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectManagementFilterDTO {

    private List<String> status;
    private List<String> template;
    private List<String> type;
    private List<String> owner;
    private List<String> createdAt;
    private List<EmployeeDetailDTO> employeeDetailDTOList;


}
