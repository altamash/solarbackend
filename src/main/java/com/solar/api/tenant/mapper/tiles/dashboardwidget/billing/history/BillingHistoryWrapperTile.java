package com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.history;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillingHistoryWrapperTile {
    private Map<String, List<BillingHistoryDataTile>> graphData;
    private List<String> xAxis;

}
