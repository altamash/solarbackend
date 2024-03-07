package com.solar.api.saas.module.com.solar.batch.service.tigo;

import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

public interface TigoServiceV2 {

    ResponseEntity<String> getMinuteData(String startTime, String endTime,String subsIds, Boolean forceUpdate);
}
