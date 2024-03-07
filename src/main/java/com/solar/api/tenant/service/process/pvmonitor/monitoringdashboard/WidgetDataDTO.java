package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WidgetDataDTO {
    private String refId;
    private String refType;
    private String systemSize;
    private String address;
    private String sunrise;
    private String sunset;
    private String mp;
    private String treesPlanted;
    private String co2Reduction;
    private String barrels;
    private String carCharges;
    private String milesCover;
    private String phoneCharges;
    private String state;
    private String googleCoordinates;
    private String geoLat;
    private String geoLong;
    private  String installationType;
    private  String weather;
    private  String gardenType;
    private Long subscriptionCount;
    private String timeZone;

}
