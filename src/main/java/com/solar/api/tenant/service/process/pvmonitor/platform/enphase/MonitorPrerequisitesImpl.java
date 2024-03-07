package com.solar.api.tenant.service.process.pvmonitor.platform.enphase;

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
        constants.put("GETTOKENV2", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.semsportal.com/api/v2/Auth/GetTokenV2").build());
        constants.put("INVERTER_BY_Sn", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.semsportal.com/api/v1/PowerStation/GetInverterBySn").build());
        constants.put("INVERTER_YIELD_RATIO_BY_Sn", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.semsportal.com/api/v1/PowerStation/GetInverterYieldRatioChartsBySn").build());
        constants.put("INVERTER_PAC_BY_DAY", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.semsportal.com/api/v2/PowerStationMonitor/GetInverterPacByDay").build());
        constants.put("INVERTER_PW_INCOME_BY_MONTH", APIConstantsImpl.builder().method(HttpMethod.POST)
                .url("https://www.semsportal.com/api/v2/PowerStationMonitor/GetInverterPowerAndIncomeByMonth").build());

    }

    public static MonitorPrerequisitesImpl getInstance() {
        if (MonitorPrerequisitesImpl.epvMonitorPrerequisites == null) {
            epvMonitorPrerequisites = new MonitorPrerequisitesImpl();
        }
        return epvMonitorPrerequisites;
    }

}
