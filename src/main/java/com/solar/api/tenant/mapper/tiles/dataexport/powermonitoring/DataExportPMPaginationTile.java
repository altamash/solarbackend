package com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataExportPMPaginationTile {
    private  Integer totalPages;
    private Long totalElements;
    private List<DataExportPMTile> dataExportPMTileList;

}
