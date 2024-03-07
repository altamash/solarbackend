package com.solar.api.saas.module.com.solar.batch.service.tigo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.tenant.model.stage.monitoring.tigo.TigoResponseDTO;
import org.springframework.http.ResponseEntity;

public interface TigoService {

    TigoResponseDTO hitURL() throws JsonProcessingException;

    ResponseEntity<String> getMinuteData(String startTime, String endTime);
    ResponseEntity<String> getHistoricData(String startTime, String endTime);
}
