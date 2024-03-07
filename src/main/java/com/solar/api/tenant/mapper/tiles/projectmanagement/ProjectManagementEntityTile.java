package com.solar.api.tenant.mapper.tiles.projectmanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.employee.EmployeeDetailDTO;
import com.solar.api.tenant.model.contract.Entity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectManagementEntityTile {
    private Entity entity;
    private String projectId;

}
