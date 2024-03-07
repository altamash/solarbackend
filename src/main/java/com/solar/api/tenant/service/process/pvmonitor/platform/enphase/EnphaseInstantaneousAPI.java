package com.solar.api.tenant.service.process.pvmonitor.platform.enphase;

import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.helper.WebUtils;
import com.solar.api.tenant.mapper.pvmonitor.MonitorAPIResponse;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.repository.MonitorReadingDailyRepository;
import com.solar.api.tenant.repository.MonitorReadingRepository;
import com.solar.api.tenant.service.process.pvmonitor.InstantaneousAPI;
import com.solar.api.tenant.service.process.pvmonitor.MonitorUtils;
import com.solar.api.tenant.service.process.pvmonitor.platform.enphase.dto.EnphaseResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static com.solar.api.tenant.service.process.pvmonitor.platform.enphase.EnphaseAPI.MINUTES_INCREMENT;

@Service
public class EnphaseInstantaneousAPI implements InstantaneousAPI {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Utility utility;
    private final MonitorReadingDailyRepository monitorReadingDailyRepository;
    private final MonitorReadingRepository monitorReadingRepository;
    private final EnphaseCommons enphaseCommons;

    public EnphaseInstantaneousAPI(Utility utility, MonitorReadingDailyRepository monitorReadingDailyRepository, MonitorReadingRepository monitorReadingRepository, EnphaseCommons enphaseCommons) {
        this.utility = utility;
        this.monitorReadingDailyRepository = monitorReadingDailyRepository;
        this.monitorReadingRepository = monitorReadingRepository;
        this.enphaseCommons = enphaseCommons;
    }

    @Override
    public MonitorAPIResponse getInstantaneousData(ExtDataStageDefinition ext) {
        MonitorAPIResponse response = new MonitorAPIResponse();
        try {
            String siteId = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.SITEID);
            String apiUrl = String.format("https://enlighten.enphaseenergy.com/pv/public_systems/%s/today", siteId);
            ResponseEntity<EnphaseResponseDTO> apiResponse =
                    WebUtils.submitRequest(HttpMethod.GET, apiUrl, null, new HashMap<>(),
                            EnphaseResponseDTO.class);
            EnphaseResponseDTO enphaseResponseDTO = apiResponse.getBody();
            int rounding = utility.getCompanyPreference().getRounding(); // TODO: initialize at start
            double dailyYield;
            if (enphaseResponseDTO.getStats() != null && enphaseResponseDTO.getStats().get(0) != null) {
                dailyYield = utility.round(enphaseResponseDTO.getStats().get(0).getTotals().getProduction() / 1000d, rounding);
            } else {
                dailyYield = 0d;
            }
            Double currentValue = enphaseResponseDTO.getLatestPower().getValue();
            currentValue = utility.round((currentValue != null ? currentValue : 0) / 1000d, rounding);
            Date time = MonitorUtils.roundTimeToMinutes(new Date(), MINUTES_INCREMENT);
            String inverter = Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.INVRT);
            MonitorReadingDaily exists = monitorReadingDailyRepository.findBySubscriptionIdMongoAndDay(ext.getSubsId(), time);
            response.setBulkDailyRecords(Collections.singletonList(enphaseCommons.getMonitorReadingDailyDTO(exists,
                    ext.getSubsId(), time, siteId, inverter, dailyYield, currentValue)));
//            double delta = dailyYield - exists.getYieldValue();
            double peakValue = exists != null ? exists.getPeakValue() : currentValue;
//            double daysYield = exists != null ? exists.getYieldValue() : dailyYield;
            response.setMonitorReadingDTOs(Collections.singletonList(enphaseCommons.getMonitorReadingDTO(ext.getSubsId(), time, siteId,
                    inverter, currentValue, peakValue, apiUrl)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }
}
