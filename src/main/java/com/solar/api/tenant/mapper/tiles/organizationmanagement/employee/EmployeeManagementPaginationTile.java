package com.solar.api.tenant.mapper.tiles.organizationmanagement.employee;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.OrganizationManagementTile;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeManagementPaginationTile<T> implements Serializable {
    private Integer totalPages;
    private Long totalElements;
    private List<EmployeeManagementTile> data;
}