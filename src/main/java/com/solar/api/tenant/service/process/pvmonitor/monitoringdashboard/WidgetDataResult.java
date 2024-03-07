package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
public interface WidgetDataResult {
    String getRefId();
    String getRefType();
    String getSystemSize();
    String getMp();
    String getAddress();
    String getState();
    String getGoogleCoordinates();
    String getGeoLat();
    String getGeoLong();

    String getInstallationType();
    Long getMaxId();

    String getGardenType();

}
