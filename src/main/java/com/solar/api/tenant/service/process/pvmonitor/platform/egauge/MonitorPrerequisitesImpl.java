package com.solar.api.tenant.service.process.pvmonitor.platform.egauge;

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
        constants.put("LOGIN_NEW", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.solaxcloud.com/phoebus/login/loginNew").build());
        constants.put("ALL_SITES", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.solaxcloud.com/phoebus/userIndex/getUserAllSiteId").build());
        constants.put("GET_POWER", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.solaxcloud.com/phoebus/userIndex/getPower").build());
        constants.put("GET_REALTIME_INFO", APIConstantsImpl.builder().method(HttpMethod.GET)
                .url("https://www.solaxcloud.com:9443/proxy/api/getRealtimeInfo.do").build());
        constants.put("GET_CURRENT_DATA", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.solaxcloud.com/phoebus/userIndex/getCurrentData").build());
        constants.put("GET_SITE_TOTAL_POWER", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.solaxcloud.com/phoebus/site/getSiteTotalPower").build());
        constants.put("GET_SITE_TOTAL_POWER_MONTHLY", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.solaxcloud.com/phoebus/userIndex/getCurrentData").build());
    }

    public static MonitorPrerequisitesImpl getInstance() {
        if (MonitorPrerequisitesImpl.epvMonitorPrerequisites == null) {
            epvMonitorPrerequisites = new MonitorPrerequisitesImpl();
        }
        return epvMonitorPrerequisites;
    }

}
