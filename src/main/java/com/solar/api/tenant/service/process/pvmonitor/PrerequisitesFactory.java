package com.solar.api.tenant.service.process.pvmonitor;

import com.solar.api.Constants;

public class PrerequisitesFactory {
    public static MonitorPrerequisites getAPIPrerequisites(String externalAPI) {
        switch (externalAPI) {
            case "SOLAX":
                return com.solar.api.tenant.service.process.pvmonitor.platform.solax.MonitorPrerequisitesImpl.getInstance();
            case "SOLIS":
                return com.solar.api.tenant.service.process.pvmonitor.platform.solis.MonitorPrerequisitesImpl.getInstance();
            case "GOODWE":
                return com.solar.api.tenant.service.process.pvmonitor.platform.goodwe.MonitorPrerequisitesImpl.getInstance();
            case Constants.MONITOR_PLATFORM.EGAUGE:
                return com.solar.api.tenant.service.process.pvmonitor.platform.egauge.MonitorPrerequisitesImpl.getInstance();
            default:
                throw new IllegalStateException("Unexpected value: " + externalAPI);
        }
    }
}
