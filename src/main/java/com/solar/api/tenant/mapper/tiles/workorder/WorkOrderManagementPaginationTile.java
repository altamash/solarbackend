package com.solar.api.tenant.mapper.tiles.workorder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.tiles.projectmanagement.ProjectManagementTile;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderManagementPaginationTile<T> implements Serializable {
    private Integer totalPages;
    private Long totalElements;
    private String groupBy;
    private List<WorkOrderManagementTile> data;



}