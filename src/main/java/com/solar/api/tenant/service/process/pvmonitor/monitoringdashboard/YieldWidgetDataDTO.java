package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YieldWidgetDataDTO {
    private  Double yearlyYield;
    private  String currentYear;
    private  Double monthlyYield;
    private  String currentMonth;
    private  Double dailyYield;
    private  String currentDay;
    private  Double lifeTimeYield;
    private  String lifeTime;
    private  String uom;


}
