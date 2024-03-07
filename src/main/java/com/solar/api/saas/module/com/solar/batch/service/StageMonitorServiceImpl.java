package com.solar.api.saas.module.com.solar.batch.service;

import com.solar.api.Constants;
import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.saas.service.integration.mongo.response.subscription.MongoSubscriptionPMMasterDTO;
import com.solar.api.tenant.mapper.contract.StageMapper;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataTempStage;
import com.solar.api.tenant.model.subscription.ESubscriptionStatus;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataStageDefinitionRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataTempStageRepository;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StageMonitorServiceImpl implements StageMonitorService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    ExtDataTempStageRepository tempStageRepository;
    @Autowired
    ExtDataStageDefinitionRepository definitionStageRepository;
    @Autowired
    private TenantConfigService tenantConfigService;
    @Autowired
    private DataExchange dataExchange;

    @Override
    public ResponseEntity<String> getMongoSubscriptionsAndMeasures() {
        try {

            TenantConfig tenantConfig = tenantConfigService.findByCategory(Constants.TENANT_CONFIG_CATEGORY.POWER_MONITER);
            MongoSubscriptionPMMasterDTO mongoSubscriptions = dataExchange.getAllFilteredMeasuresAndSubscriptions(DBContextHolder.getTenantName(),
                    tenantConfig.getParameter());

            List<ExtDataTempStage> dataTempStageList = StageMapper.fromMongotoSolarDTOList(mongoSubscriptions.getMongoSubscriptionPMDTO());
            saveOnTempStage(StageMapper.mapMonitoringPlatform(dataTempStageList));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }

    }

    @Override
    public ResponseEntity<String> transferSubscriptionsToStageDefinition() {
        try {

            saveOnStageDefinition(StageMapper.toStageDefinitions(getAllTempStage()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }

    }

    @Override
    public void saveOnTempStage(List<ExtDataTempStage> extDataTempStage) throws Exception {

        if(extDataTempStage.size() != 0) {
            for(ExtDataTempStage tempData :extDataTempStage){
                if(existsOnTempStage(tempData.getSubsId())){
                    tempData.setId(findBySubIdOnTempStage(tempData.getSubsId()).getId());
                }
            }
            tempStageRepository.saveAll(extDataTempStage);
        }
    }

    @Override
    public void saveOnStageDefinition(List<ExtDataStageDefinition> extDataStageDefinition) throws Exception {

        if(extDataStageDefinition.size() != 0) {
            for(ExtDataStageDefinition tempData : extDataStageDefinition){
                if(existsOnStageDefinition(tempData.getSubsId())){
                    tempData.setId(findBySubIdOnStageDefinition(tempData.getSubsId()).getId());
                }
            }
            definitionStageRepository.saveAll(extDataStageDefinition);
        }
    }
    @Override
    public ExtDataTempStage findByIdOnTempStage(Long id) throws NotFoundException {
        return tempStageRepository.findById(id).orElseThrow(() -> new NotFoundException(ExtDataTempStage.class, id));
    }

    @Override
    public ExtDataStageDefinition findByIdOnStageDefinition(Long id) throws NotFoundException {
        return definitionStageRepository.findById(id).orElseThrow(() -> new NotFoundException(ExtDataStageDefinition.class, id));
    }
    @Override
    public List<ExtDataTempStage> getAllTempStage() {
        return tempStageRepository.findAll();
    }

    @Override
    public ExtDataTempStage findBySubIdOnTempStage(String subId) {
        return tempStageRepository.findBySubsId(subId);
    }

    @Override
    public ExtDataStageDefinition findBySubIdOnStageDefinition(String subId) {
        return definitionStageRepository.findBySubsId(subId);
    }
    @Override
    public boolean existsOnTempStage(String subsId) {
        return tempStageRepository.existsBySubsId(subsId);
    }

    @Override
    public boolean existsOnStageDefinition(String subsId) {
        return definitionStageRepository.existsBySubsId(subsId);
    }

    @Override
    public List<ExtDataStageDefinition> getAllSubscriptions(String monPlatform) {
        return definitionStageRepository.findAllBySubsStatusAndMonPlatform(ESubscriptionStatus.ACTIVE.getStatus(), monPlatform);
    }
    @Override
    public List<ExtDataStageDefinition> getAllSubscriptionsByMpAndSubsIds(String mp,List<String> subsIds) {
        return definitionStageRepository.findAllBySubsStatusAndMonPlatformAndSubsIdIn(ESubscriptionStatus.ACTIVE.getStatus(), mp,subsIds);
    }
    private String extractPrjStatusValue(String mpJson) {
        try {
            JSONObject jsonObject = new JSONObject(mpJson);
            return jsonObject.getString("PRJ_STATUS");
        } catch (JSONException e) {
            LOGGER.error("Error extracting PRJ_STATUS from mp_json: " + e.getMessage());
            return null;
        }
    }

    private String updateMpJson(String mpJson, String key, String value) {
        try {
            JSONObject jsonObject = new JSONObject(mpJson);
            jsonObject.put(key, value);
            return jsonObject.toString();
        } catch (JSONException e) {
            LOGGER.error("Error updating mp_json: " + e.getMessage());
            return mpJson;
        }
    }
}
