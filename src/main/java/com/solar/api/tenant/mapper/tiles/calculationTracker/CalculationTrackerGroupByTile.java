package com.solar.api.tenant.mapper.tiles.calculationTracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculationTrackerGroupByTile {
    private String source;
    private String customer;
    private String image;
    private String status;
    private Integer completed;
    private Integer total;
    private Double percentage;
    private Double totalAmount;
    private List<CalculationTrackerTile> calculationTrackerTileList;

}
