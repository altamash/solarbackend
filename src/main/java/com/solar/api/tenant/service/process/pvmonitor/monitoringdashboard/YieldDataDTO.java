package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YieldDataDTO {
    private  Double yield;
    private  String day;

}
