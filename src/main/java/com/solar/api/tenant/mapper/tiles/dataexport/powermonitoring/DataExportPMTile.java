package com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataExportPMTile {
    private String projectName;
    private String custName;
    private String powerProdValue;
    private String date;
    private String time;
    private String subsId;
    private String variantId;

    public DataExportPMTile(String projectName, String custName, String powerProdValue, String date, String time, String variantId, String subsId) {
        this.projectName = projectName;
        this.custName = custName;
        this.powerProdValue = powerProdValue;
        this.date = date;
        this.time = time;
        this.variantId = variantId;
        this.subsId = subsId;
    }

    public DataExportPMTile(String projectName, String custName, String powerProdValue, String date, String variantId, String subsId) {
        this.projectName = projectName;
        this.custName = custName;
        this.powerProdValue = powerProdValue;
        this.date = date;
        this.variantId = variantId;
        this.subsId = subsId;
    }
}
