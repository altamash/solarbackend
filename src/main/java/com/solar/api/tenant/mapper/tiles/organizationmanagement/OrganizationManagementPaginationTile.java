package com.solar.api.tenant.mapper.tiles.organizationmanagement;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationManagementPaginationTile<T> implements Serializable {
    private Integer totalPages;
    private Long totalElements;
    private List<OrganizationManagementTile> data;
}
