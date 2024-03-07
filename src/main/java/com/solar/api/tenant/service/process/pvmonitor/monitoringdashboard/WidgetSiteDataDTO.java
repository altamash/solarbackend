package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WidgetSiteDataDTO {
    private String treesPlanted;
    private String co2Reduction;
    private String barrels;
    private String carCharges;
    private String milesCover;
    private String phoneCharges;

    //sites overview
    private String totalSites;
    private String totalPlatforms;
    private String totalSystemSize;
    private String totalLocation;
    List<WidgetDataDTO> sitesData;
}
