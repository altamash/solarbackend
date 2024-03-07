package com.solar.api.tenant.service.process.pvmonitor;

import com.solar.api.saas.service.integration.mongo.response.subscription.VariantDTO;
import com.solar.api.tenant.mapper.projection.projectrevenue.ProjectProjectionRevenueDTO;
import com.solar.api.tenant.model.dataexport.powermonitoring.DataDTO;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinitionDTO;
import com.solar.api.tenant.model.stage.monitoring.InverterSubscriptionDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ExtDataStageDefinitionService {
    List<ExtDataStageDefinition> findAll();

    List<ExtDataStageDefinition> findAllBySubsIdIn(List<String> subIds);

    List<ExtDataStageDefinition> findAllByRefIdIn(List<String> variantIds, String subsStatus);

    List<ExtDataStageDefinition> findAllForCurrentUsers(Long accId);
    List<InverterSubscriptionDTO> findAllInverterSubscriptionDTO(List<String> variantIds);
    List<InverterSubscriptionDTO> findAllInverterSubscriptionDTOForCurrentUsers(List<String> variantIds,Long acctId);

    List<ExtDataStageDefinition> findAllByRefIdAndAcctIdIn(List<String> variantIds, Long acctId, String subsStatus);
    List<String> getDistinctByMonPlatform();

    List<ExtDataStageDefinition> findAllByMpPlatform(List<String> mps);

    List<ExtDataStageDefinition> findAllUniqueProjects();

    List<ExtDataStageDefinitionDTO> findAllSubsAndCustomerBySubIds(List<String> subIds);

    String findAllSubscriptionsByVariantId(String variantId,Long acctId);
    List<DataDTO> findAllProjectsForFilters();
    List<DataDTO> findAllSubscriptionsByVariantIdForFilters(List<String> variantIds);
    List<DataDTO> findAllCustomersByVariantIdForFilters(List<String> variantIds);

    List<ExtDataStageDefinition> findMonthlyProjectionsForAllGardens();
    ExtDataStageDefinition findByCustomerSubscriptionId(Long subsId);
    List<ProjectProjectionRevenueDTO> getProjectProjectionRevenueDetails(String variantId);

    List<DataDTO> findSubCountBySubsStatusAndRefIdIn(List<String> variantIds, String subsStatus);

    ResponseEntity<?> findAllCustomersWithSubscriptions(String subStatus);

    ResponseEntity<?> findAllVariantsByProductId(String productId);
}