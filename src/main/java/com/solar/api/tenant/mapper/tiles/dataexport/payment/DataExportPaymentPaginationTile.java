package com.solar.api.tenant.mapper.tiles.dataexport.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataExportPaymentPaginationTile {

    private Integer totalPages;
    private Long totalElements;
    private List<DataExportPaymentTile> dataExportPaymentTileList;
}
