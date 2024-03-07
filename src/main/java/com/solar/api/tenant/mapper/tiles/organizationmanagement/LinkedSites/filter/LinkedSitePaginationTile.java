package com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.LinkedSites.LinkedSiteTile;
import com.solar.api.tenant.mapper.tiles.organizationmanagement.office.PhysicalLocationOMTile;
import lombok.*;

import java.io.Serializable;
import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LinkedSitePaginationTile<T> implements Serializable {
    private Integer totalPages;
    private Long totalElements;
    private String groupBy;
    private List<LinkedSiteTile> data;
}
