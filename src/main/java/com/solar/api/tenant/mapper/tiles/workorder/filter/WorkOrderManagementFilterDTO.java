package com.solar.api.tenant.mapper.tiles.workorder.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.customerSupport.CustomerDTO;
import com.solar.api.tenant.model.dataexport.employee.EmployeeDataDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class WorkOrderManagementFilterDTO {
    private List<String> status;
    private List<String> type;
    private List<String> requesterType;
    private List<CustomerDTO> requester;
    private List<EmployeeDataDTO> supportAgent;
    private List<String> billable;


}
