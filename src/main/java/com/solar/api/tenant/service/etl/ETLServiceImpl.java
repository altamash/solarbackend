package com.solar.api.tenant.service.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.solar.api.Constants;
import com.solar.api.helper.Acquisition.AcquisitionUtils;
import com.solar.api.saas.configuration.DBContextHolder;
import com.solar.api.saas.model.tenant.MasterTenant;
import com.solar.api.saas.repository.MasterTenantRepository;
import com.solar.api.saas.service.integration.mongo.DataExchange;
import com.solar.api.tenant.mapper.ca.CaUserTemplateDTO;
import com.solar.api.tenant.mapper.user.*;
import com.solar.api.tenant.model.BaseResponse;
import com.solar.api.tenant.model.TenantConfig;
import com.solar.api.tenant.model.contract.*;
import com.solar.api.tenant.model.user.userMapping.UserMapping;
import com.solar.api.tenant.repository.*;
import com.solar.api.tenant.service.preferences.TenantConfigService;
import com.solar.api.tenant.service.userMapping.UserMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.ca.CaSoftCreditCheckMapper.toCaSoftCreditCheck;

@Service
public class ETLServiceImpl implements ETLService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserMappingService userMappingService;
    @Autowired
    private MasterTenantRepository masterTenantRepository;
    @Autowired
    private TenantConfigService tenantConfigService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ETLRepository etlRepository;

    @Autowired
    private ETLStageRepository etlStageRepository;

    @Autowired
    private DataExchange dataExchange;

    /*
    this is just an ETL function
     */
    @Override
    public void createAcquisitionProjectForUsersETL(Long compKey) {
        try {
            MasterTenant masterTenant = masterTenantRepository.findByCompanyKey(compKey);
            DBContextHolder.setTenantName(masterTenant.getDbName());
            String commercialTemplateJson = null;
            String individualTemplateJson = null;
            List<CaUserTemplateDTO> usersETL = etlRepository.findAllCaUsersETL();
            Optional<TenantConfig> commercialTenantConfig = getTenantConfig(EEntityType.COMMERCIAL);
            Optional<TenantConfig> individualTenantConfig = getTenantConfig(EEntityType.INDIVIDUAL);
            saveUserMappingIntoETLStage();
            List<ETLStage> etlStageRecords = etlStageRepository.findAll();
            // Find unmatched CaUserTemplateDTO records
            List<CaUserTemplateDTO> unmatchedUserETL = usersETL.stream()
                    .filter(userMapping -> etlStageRecords.stream()
                            .noneMatch(etlStage -> etlStage.getEntityId().longValue() == userMapping.getEntityId().longValue()))
                    .collect(Collectors.toList());

            if (commercialTenantConfig.isPresent()) {
                commercialTemplateJson = fetchTemplateJson(commercialTenantConfig.get());
            }
            if (individualTenantConfig.isPresent()) {
                individualTemplateJson = fetchTemplateJson(individualTenantConfig.get());
            }
            if (individualTemplateJson != null) {
                List<CaUserTemplateDTO> individualUnmatchedUserETL = unmatchedUserETL.stream().filter(userETL -> userETL.getCustomerType()!=null && userETL.getCustomerType().equalsIgnoreCase(EEntityType.INDIVIDUAL.name())).collect(Collectors.toList());
                if(individualUnmatchedUserETL.size()>0) {
                    processIndividualUsersETL(individualUnmatchedUserETL, individualTemplateJson);
                }
                } else {
                LOGGER.error("No Individual Template found");
            }

            if (commercialTemplateJson != null) {
                List<CaUserTemplateDTO> commercialUnmatchedUserETL = unmatchedUserETL.stream().filter(userETL -> userETL.getCustomerType()!=null && userETL.getCustomerType().equalsIgnoreCase(EEntityType.COMMERCIAL.name())).collect(Collectors.toList());
                if(commercialUnmatchedUserETL.size()>0) {
                      processCommercialUsersETL(commercialUnmatchedUserETL, commercialTemplateJson);
                }
            } else {
                LOGGER.error("No commercial Template found");
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred: " + e.getMessage(), e);
        }
    }

    private void processIndividualUsersETL(List<CaUserTemplateDTO> individualUsersETL, String individualTemplateJson) {
        for (CaUserTemplateDTO userETL : individualUsersETL) {
            try {
                String individualUserJson = updateJsonWithUserDTO(userETL, individualTemplateJson);
                UserMapping userMapping = saveETLTemplateAndUserMapping(userETL, individualUserJson);
                if (userMapping != null) {
                    updateETLTable(userMapping);
                    LOGGER.info("project Saved successfully");
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred while processing an individual user: " + e.getMessage(), e);
            }
        }
    }

    private void processCommercialUsersETL(List<CaUserTemplateDTO> commercialUsersETL, String commercialTemplateJson) {
        for (CaUserTemplateDTO userETL : commercialUsersETL) {
            try {
                String commercialUserJson = updateJsonWithUserDTO(userETL, commercialTemplateJson);
                UserMapping userMapping = saveETLTemplateAndUserMapping(userETL, commercialUserJson);
                if (userMapping != null) {
                    updateETLTable(userMapping);
                    LOGGER.info("project Saved successfully");
                }
            } catch (Exception e) {
                LOGGER.error("An error occurred while processing an individual user: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void updateETLTable(UserMapping userMapping){
        ETLStage etlStage = ETLStage.builder()
                .userMappingId(userMapping.getId())
                .entityId(userMapping.getEntityId())
                .build();
        etlStageRepository.save(etlStage);
    }


    private String updateJsonWithUserDTO(CaUserTemplateDTO userETL, String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(json);
        // Navigate to the "sections" array
        if (rootNode.isObject() && rootNode.has("sections")) {
            JsonNode sectionsNode = rootNode.get("sections");
            if (sectionsNode.isArray()) {
                for (JsonNode sectionNode : sectionsNode) {
                    if (sectionNode.isObject() && sectionNode.has("content")) {
                        JsonNode contentNode = sectionNode.get("content");
                        if (contentNode.isObject() && contentNode.has("measures")) {
                            JsonNode measuresNode = contentNode.get("measures");
                            if (measuresNode.isArray()) {
                                for (JsonNode measureNode : measuresNode) {
                                    if (measureNode.isObject() && measureNode.has("code")) {
                                        String code = measureNode.get("code").asText();
                                        // Here, you can map the code to a corresponding field in the UserDTO
                                        // and set it as the "default_value" in the JSON
                                        if ("FNAME".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", userETL.getFirstName());
                                        } else if ("LNAME".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", userETL.getLastName());
                                        } else if ("PHNE_NUM".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", userETL.getPhone());
                                        } else if ("EMAIL".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", userETL.getEmailAddress());
                                        } else if ("ZIP_CD".equals(code)) {
                                            ((ObjectNode) measureNode).put("default_value", userETL.getZipCode());
                                        } else if ("DOB".equals(code)) {
                                            ((ObjectNode) measureNode).putNull("default_value");
                                        } else if ("EXE_DT".equals(code)) {
                                            ((ObjectNode) measureNode).putNull("default_value");
                                        } else if ("UTL_PRVDR".equals(code)) {
                                            ((ObjectNode) measureNode).putNull("default_value");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Convert the updated JSON back to a string
        return objectMapper.writeValueAsString(rootNode);
    }

    private UserMapping saveETLTemplateAndUserMapping(CaUserTemplateDTO userETL, String json) throws Exception {
        UserMapping userMapping = null;
        try {
            String oid = null;
            BaseResponse data = dataExchange.saveOrUpdateAcquisitionProject(AcquisitionUtils.addEntityIdInTemplate(json, userETL.getEntityId()));
            JsonNode rootNode = objectMapper.readTree(data.getMessage().toString());
            JsonNode idNode = rootNode.get("_id");
            if (idNode != null) {
                oid = idNode.get("$oid").asText();
            }
            userMapping = userMappingService.save(new UserMappingDTO().builder()
                    .entityId(userETL.getEntityId())
                    .ref_id(oid)
                    .module("Acquistion")
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error while hitting mongo api", e);
        }
        return userMapping;
    }

    @Override
    public Optional<TenantConfig> getTenantConfig(EEntityType entityType) throws Exception {
        return tenantConfigService.findByParameter(entityType.name().toString());
    }

    @Override
    public String fetchTemplateJson(TenantConfig tenantConfig) {
        ResponseEntity responseEntity = dataExchange.showAcquisitionTemplate(
                tenantConfig.getText(),
                Constants.PRIVILEGE_LEVELS.SUPER_ADMIN_PRIV_LEVEL
        );

        if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
            return responseEntity.getBody().toString();
        }

        return null ;
    }

    private void saveUserMappingIntoETLStage( ) {
        List<ETLStage> etlStageRecords = etlStageRepository.findAll();
        List<Long> existingEntities = etlStageRecords.stream().map(etlStage -> etlStage.getEntityId()).collect(Collectors.toList());
        // Find unmatched UserMapping records
        List<UserMapping> unmatchedUserMappings = userMappingService.findByEntityIdsNotIn(existingEntities);
        // Now, map UserMapping objects to ETLStage objects
        List<ETLStage> unmatchedETLStages = unmatchedUserMappings.stream()
                .map(userMapping -> {
                    ETLStage etlStage = new ETLStage();
                    // Map attributes from UserMapping to ETLStage as needed
                    etlStage.setEntityId(userMapping.getEntityId());
                    etlStage.setUserMappingId(userMapping.getId());
                    return etlStage;
                })
                .collect(Collectors.toList());
// Save unmatched UserMapping records into etlStageRecords
        etlStageRepository.saveAll(unmatchedETLStages);

    }
}