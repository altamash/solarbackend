package com.solar.api.tenant.mapper.tiles.dataexport.employeedetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.tiles.dataexport.customerdetail.DataExportCustomerTile;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataExportEmployeePaginationTile {

    private  Integer totalPages;
    private Long totalElements;
    private List<DataExportEmployeeTile> dataExportEmployeeTileList;
}
