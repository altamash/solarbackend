package com.solar.api.tenant.service.process.pvmonitor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.solar.api.Constants;
import com.solar.api.helper.Utility;
import com.solar.api.saas.service.integration.mongo.response.subscription.VariantDTO;
import com.solar.api.tenant.mapper.customerSupport.CustomerDTO;
import com.solar.api.tenant.mapper.customerSupport.CustomerMapper;
import com.solar.api.tenant.mapper.customerSupport.CustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.customerSupport.CustomerSubscriptionMapper;
import com.solar.api.tenant.mapper.projection.projectrevenue.ProjectProjectionRevenueDTO;
import com.solar.api.tenant.mapper.projection.projectrevenue.ProjectProjectionRevenueMapper;
import com.solar.api.tenant.mapper.tiles.EntityFunctionalRoleTile;
import com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO;
import com.solar.api.tenant.repository.UserGroup.EntityRoleRepository;
import com.solar.api.tenant.repository.stage.monitoring.ExtDataStageDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.json.JSONObject;

@Service
public class ExtDataStageDefinitionServiceImpl implements ExtDataStageDefinitionService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private ExtDataStageDefinitionRepository extDataStageDefinitionRepository;

    @Autowired
    private Utility utility;

    @Autowired
    private EntityRoleRepository entityRoleRepository;
    @Override
    public List<ExtDataStageDefinition> findAll() {
        return extDataStageDefinitionRepository.findAll();
    }

    @Override
    public List<ExtDataStageDefinition> findAllBySubsIdIn(List<String> subIds) {
        return extDataStageDefinitionRepository.findAllBySubsIdInAndSubsStatus(subIds,"ACTIVE");
    }

    @Override
    public List<ExtDataStageDefinition> findAllByRefIdIn(List<String> variantIds, String subsStatus) {
        return extDataStageDefinitionRepository.findAllBySubsStatusAndRefIdIn(subsStatus, variantIds);
    }

    @Override
    public List<ExtDataStageDefinition> findAllForCurrentUsers(Long acctId) {
        return extDataStageDefinitionRepository.findAllForCurrentUsers(acctId);
    }

    @Override
    public List<InverterSubscriptionDTO> findAllInverterSubscriptionDTO(List<String> variantIds) {
        return extDataStageDefinitionRepository.findAllInverterSubscriptionDTO(variantIds);
    }

    @Override
    public List<InverterSubscriptionDTO> findAllInverterSubscriptionDTOForCurrentUsers(List<String> variantIds, Long acctId) {
        return extDataStageDefinitionRepository.findAllInverterSubscriptionDTOForCurrentUsers(variantIds, acctId);
    }

    @Override
    public List<ExtDataStageDefinition> findAllByRefIdAndAcctIdIn(List<String> variantIds, Long acctId, String subsStatus) {
        return extDataStageDefinitionRepository.findAllBySubsStatusAndRefIdAndAcctIdIn(subsStatus, variantIds, acctId);
    }

    @Override
    public List<String> getDistinctByMonPlatform() {
        return extDataStageDefinitionRepository.getDistinctByMonPlatform();
    }

    @Override
    public List<ExtDataStageDefinition> findAllByMpPlatform(List<String> mps) {
        return extDataStageDefinitionRepository.getAllByMonPlatformIn(mps);
    }

    @Override
    public List<ExtDataStageDefinition> findAllUniqueProjects() {
        List<ExtDataStageDefinition> dataStageDefinitions = extDataStageDefinitionRepository.findAllUniqueProjects();
        AtomicReference<String> projectionIndicator = new AtomicReference<>();
      try {
          dataStageDefinitions = dataStageDefinitions.stream()
                  .filter(dsd -> {
                      projectionIndicator.set(getJsonString(dsd.getMpJson(), Constants.RATE_CODES.PROJECTION_INDICATOR));
                      // Use the value obtained from the JSON
                      return projectionIndicator.get() == null || projectionIndicator.get().equalsIgnoreCase("false");
                  }).collect(Collectors.collectingAndThen(
                          Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtDataStageDefinition::getRefId))),
                          list -> new ArrayList<>(list)));
      }catch (Exception e) {
          LOGGER.error(e.getMessage(), e);
      }
    return dataStageDefinitions;
    }
    @Override
    public List<ExtDataStageDefinitionDTO> findAllSubsAndCustomerBySubIds(List<String> subIds) {
        return extDataStageDefinitionRepository.findAllSubsAndCustomer(subIds);
    }

    @Override
    public String findAllSubscriptionsByVariantId(String variantId, Long acctId) {
        return extDataStageDefinitionRepository.findAllSubscriptionsByAcctIdAndVariantId(variantId, acctId);
    }

    @Override
    public List<DataDTO> findAllProjectsForFilters() {
        return extDataStageDefinitionRepository.findAllProjectsForFilters();
    }

    @Override
    public List<DataDTO> findAllSubscriptionsByVariantIdForFilters(List<String> variantIds) {
        return extDataStageDefinitionRepository.findAllSubscriptionsByVariantIdForFilters(variantIds);
    }

    @Override
    public List<DataDTO> findAllCustomersByVariantIdForFilters(List<String> variantIds) {
        List<DataDTO> result = extDataStageDefinitionRepository.findAllCustomersByVariantIdForFilters(variantIds);
        result.stream().forEach(customer -> customer.setSubsIds(findAllSubscriptionsByVariantId(customer.getVariantId(), customer.getAcctId())));
        return result;
    }

    @Override
    public List<ExtDataStageDefinition> findMonthlyProjectionsForAllGardens() {
        List<ExtDataStageDefinition> dbResult = extDataStageDefinitionRepository.findMonthlyProjectionsForAllGardens();
        Map<String, ExtDataStageDefinition> uniqueEntries = new HashMap<>();
        for (ExtDataStageDefinition ext : dbResult) {
            String key = ext.getRefId() + Utility.getMeasureAsJson(ext.getMpJson(), Constants.RATE_CODES.PRCTNGEFF);
            if (!uniqueEntries.containsKey(key)) {
                uniqueEntries.put(key, ext);
            }
        }

        List<ExtDataStageDefinition> distinctResult = new ArrayList<>(uniqueEntries.values());
        return distinctResult;
    }

    @Override
    public ExtDataStageDefinition findByCustomerSubscriptionId(Long subsId) {
        return extDataStageDefinitionRepository.findByCustomerSubscriptionId(subsId);
    }

    @Override
    public List<ProjectProjectionRevenueDTO> getProjectProjectionRevenueDetails(String variantId) {

        return ProjectProjectionRevenueMapper.toProjectProjectionRevenueDTO(extDataStageDefinitionRepository.getProjectProjectionRevenueDetails(variantId));
    }

    private String getJsonString(String json,String key) {
        JSONObject jsonObject =  new JSONObject(json);
        return jsonObject.optString(key,null);
    }

    @Override
    public List<DataDTO> findSubCountBySubsStatusAndRefIdIn(List<String> variantIds, String subsStatus) {
        return extDataStageDefinitionRepository.findSubCountBySubsStatusAndRefIdIn(subsStatus, variantIds);
    }

    @Override
    public ResponseEntity<?> findAllCustomersWithSubscriptions(String subStatus) {
        try {
            List<CustomerSubscriptionDTO> result = extDataStageDefinitionRepository.findAllCustomersWithSubscriptions();
            if (result == null || result.isEmpty()) {
                return utility.buildSuccessResponse(HttpStatus.NOT_FOUND, "Data not found", null);
            } else {
                // Grouping by acctId
                Map<Long, List<CustomerSubscriptionDTO>> groupedByAcctId = result.stream().collect(Collectors.groupingBy(CustomerSubscriptionDTO::getAcctId));
                // Transforming the grouped data
                List<CustomerDTO> transformedData = groupedByAcctId.entrySet().stream()
                        .map(entry -> {
                            // Creating a map for the transformed entry
                            CustomerDTO customerDTO = null;
                            long acctId = entry.getKey();
                            List<CustomerSubscriptionDTO> subscriptions = entry.getValue();
                            // Extracting customer data from the first subscription in the list
                            CustomerSubscriptionDTO firstSubscription = subscriptions.stream().findFirst().orElse(null);
                            if (firstSubscription != null) {
                                subscriptions = CustomerSubscriptionMapper.toCustomerSubscriptionDTOList(subscriptions);
                                customerDTO = CustomerMapper.toCustomerDTO(firstSubscription);
                                customerDTO.setSubscriptions(subscriptions);
                                return customerDTO;
                            } else {
                                return null;
                            }
                        }).collect(Collectors.toList());
                return utility.buildSuccessResponse(HttpStatus.OK, "Data found successfully", transformedData);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not found");
        }
    }

    @Override
    public ResponseEntity<?> findAllVariantsByProductId(String productId) {
        List<ExtDataStageDefinitionDTO> dataStageDefinitions = extDataStageDefinitionRepository.findAllUniqueGardenByProductId(productId);
        List<VariantDTO> variantDTOS = null;
        if (dataStageDefinitions == null || dataStageDefinitions.isEmpty()) {
            return utility.buildSuccessResponse(HttpStatus.NOT_FOUND, "Data not found", null);
        } else {
            try {
                dataStageDefinitions =  filterBasedOnProjectionIndicator(dataStageDefinitions);
                List<Long> projectOwnerEntityRoleIds = dataStageDefinitions.stream().map(extDataStageDefinition -> getJsonKeyValue(extDataStageDefinition.getExtJson(), Constants.RATE_CODES.projectOwnerEntityRoleId)).collect(Collectors.toList());
                List<EntityFunctionalRoleTile> entityFunctionalRoleTiles = entityRoleRepository.findByEntityRoleIdIn(projectOwnerEntityRoleIds);
                variantDTOS =  dataStageDefinitions.stream()
                        .map(extDataStageDefinition -> {
                            // Assuming entityFunctionalRoleTiles is a list available in the scope
                            Optional<EntityFunctionalRoleTile> matchingTile = entityFunctionalRoleTiles.stream()
                                    .filter(entityFunctionalRoleTile -> entityFunctionalRoleTile.getEntityRoleId() == getJsonKeyValue(extDataStageDefinition.getExtJson(), Constants.RATE_CODES.projectOwnerEntityRoleId))
                                    .findFirst();
                           String utilityCompany = getJsonString(extDataStageDefinition.getMpJson(), Constants.RATE_CODES.PROJECTION_INDICATOR);
                                return VariantDTO.builder()
                                        ._id(extDataStageDefinition.getRefId())
                                        .variantAlias(extDataStageDefinition.getRefType())
                                        .variantName(extDataStageDefinition.getRefType())
                                        .gardenOwnerAcctId(matchingTile.map(EntityFunctionalRoleTile::getAcctId).orElse(null))
                                        .gardenOwnerEntityName(matchingTile.map(EntityFunctionalRoleTile::getEntityName).orElse(null))
                                        .gardenOwnerEntityId(matchingTile.map(EntityFunctionalRoleTile::getEntityId).orElse(null))
                                        .gardenImageUrl(extDataStageDefinition.getGardenImageUri()!= null? extDataStageDefinition.getGardenImageUri().toString() : null)
                                        .utilityCompany(utilityCompany)
                                        .build();
                        })
                        .filter(Objects::nonNull) // Remove null entries if any
                        .collect(Collectors.toList());
            } catch (JsonProcessingException e) {
                LOGGER.error("Error processing JSON", e);
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing JSON");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return utility.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data not found");
            }
            return utility.buildSuccessResponse(HttpStatus.OK, "Data found successfully", variantDTOS);
        }
    }

    private Long getJsonKeyValue(String json,String key) {
        JSONObject jsonObject =  new JSONObject(json);
        return jsonObject.optLong(key);
    }
    private List<ExtDataStageDefinitionDTO> filterBasedOnProjectionIndicator(List<ExtDataStageDefinitionDTO> dataStageDefinitions) throws JsonProcessingException {
        AtomicReference<String> projectionIndicator = new AtomicReference<>();
        return dataStageDefinitions.stream()
                .filter(dsd -> {
                    projectionIndicator.set(getJsonString(dsd.getMpJson(), Constants.RATE_CODES.PROJECTION_INDICATOR));
                    // Use the value obtained from the JSON
                    return projectionIndicator.get() == null || projectionIndicator.get().equalsIgnoreCase("false");
                }).collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ExtDataStageDefinitionDTO::getRefId))),
                        ArrayList::new));
    }

}