package com.solar.api.tenant.mapper.tiles.projectmanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectManagementPaginationTile<T> implements Serializable {
    private int pageNumber;
    private int numberOfElements;
    private long totalElements;
    private int totalPages;
    private String groupBy;
    private List<ProjectManagementTile> data;



}