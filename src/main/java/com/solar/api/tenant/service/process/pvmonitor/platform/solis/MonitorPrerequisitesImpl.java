package com.solar.api.tenant.service.process.pvmonitor.platform.solis;

import com.solar.api.tenant.service.process.pvmonitor.APIConstants;
import com.solar.api.tenant.service.process.pvmonitor.APIConstantsImpl;
import com.solar.api.tenant.service.process.pvmonitor.MonitorPrerequisites;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MonitorPrerequisitesImpl implements MonitorPrerequisites {

    private Map<String, APIConstants> constants = new HashMap<>();
    private static MonitorPrerequisitesImpl epvMonitorPrerequisites;

    private MonitorPrerequisitesImpl() {
        constants.put("LOGIN2", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.soliscloud.com:15555/user/login2").urlSuffix("/user/login2").build());
        constants.put("LIST", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.soliscloud.com:15555/station/list").urlSuffix("/station/list").build());
        constants.put("LISTV2", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.soliscloud.com:15555/inverter/listV2").urlSuffix("/inverter/listV2").build());
        constants.put("RATED_POWER", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.soliscloud.com:15555/distributor/detail").urlSuffix("/distributor/detail").build());
        constants.put("INVERTER_CHART", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.soliscloud.com:15555/inverter/inverterChartCompare").urlSuffix("/inverter/inverterChartCompare").build());

    }

    public static MonitorPrerequisitesImpl getInstance() {
        if (MonitorPrerequisitesImpl.epvMonitorPrerequisites == null) {
            epvMonitorPrerequisites = new MonitorPrerequisitesImpl();
        }
        return epvMonitorPrerequisites;
    }

}
