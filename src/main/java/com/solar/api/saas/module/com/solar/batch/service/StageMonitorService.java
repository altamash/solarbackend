package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataTempStage;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface StageMonitorService {

    ResponseEntity<String> getMongoSubscriptionsAndMeasures() throws Exception;

    ResponseEntity<String>  transferSubscriptionsToStageDefinition();

    void saveOnTempStage(List<ExtDataTempStage> extDataTempStage) throws Exception;

    void saveOnStageDefinition(List<ExtDataStageDefinition> extDataStageDefinition) throws Exception;

    ExtDataTempStage findByIdOnTempStage(Long id) throws NotFoundException;

    ExtDataStageDefinition findByIdOnStageDefinition(Long id) throws NotFoundException;

    List<ExtDataTempStage> getAllTempStage();

    ExtDataTempStage findBySubIdOnTempStage(String subId);

    ExtDataStageDefinition findBySubIdOnStageDefinition(String subId);

    boolean existsOnTempStage(String subsId);

    boolean existsOnStageDefinition(String subsId);

    List<ExtDataStageDefinition> getAllSubscriptions(String egauge);
    List<ExtDataStageDefinition> getAllSubscriptionsByMpAndSubsIds(String mp,List<String> subsIds);
}
