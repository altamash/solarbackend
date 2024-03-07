package com.solar.api.tenant.mapper.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectionTileDTO {
    private String day;
    private Double projectedYield;

    private  Double actualYield;

    private  String quarter;

    public ProjectionTileDTO( String day, Double projectedYield, Double actualYield) {
        this.day = day;
        this.projectedYield = projectedYield;
        this.actualYield= actualYield;
    }
    public ProjectionTileDTO(MonthlyYieldProjection data) {
        this.day = data.getDay();
        this.projectedYield = data.getTotalYield();
        this.actualYield = 0.0; // Set it to 0.0 as mentioned in the original constructor
    }

    public ProjectionTileDTO(QuarterlyYieldProjection quarterlyYieldProjection) {
        this.day = quarterlyYieldProjection.getYear();
        this.projectedYield = quarterlyYieldProjection.getTotalYield();
        this.actualYield = 0.0; // Set it to 0.0 as mentioned in the original constructor
        this.quarter =  quarterlyYieldProjection.getQuarter();
    }
}
