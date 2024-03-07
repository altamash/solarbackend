package com.solar.api.tenant.mapper.tiles.dataexport.customerdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataExportCustomerPaginationTile {

    private  Integer totalPages;
    private Long totalElements;
    private List<DataExportCustomerTile> dataExportCustomerTileList;
}
