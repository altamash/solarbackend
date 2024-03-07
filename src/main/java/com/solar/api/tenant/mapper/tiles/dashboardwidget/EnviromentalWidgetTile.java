package com.solar.api.tenant.mapper.tiles.dashboardwidget;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EnviromentalWidgetTile {
    private String treesPlanted;
    private String co2Reduction;
    private String barrels;
    private String carCharges;
    private String milesCover;
    private String phoneCharges;
}
