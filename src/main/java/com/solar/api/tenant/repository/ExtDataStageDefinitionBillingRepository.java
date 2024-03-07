package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.stage.billing.ExtDataStageDefinitionBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ExtDataStageDefinitionBillingRepository extends JpaRepository<ExtDataStageDefinitionBilling, Long> {
    ExtDataStageDefinitionBilling findBySubsId(String subId);
    List<ExtDataStageDefinitionBilling> findAllBySubsIdIn(List<String> subIds);
    @Query("SELECT edsdb FROM ExtDataStageDefinitionBilling edsdb where edsdb.subsStatus = :status")
    List<ExtDataStageDefinitionBilling> findSubsByStatus(@Param("status") String status);

    @Query("SELECT edsdb.physicalLocationId FROM ExtDataStageDefinitionBilling edsdb where edsdb.refId = :variantId")
    Long findLocIdByVariantId(@Param("variantId")String variantId);

    @Query("SELECT edsdb.variantAlias FROM ExtDataStageDefinitionBilling edsdb where edsdb.refId = :variantId")
    String findVariantAliasByVariantId(@Param("variantId")String variantId);

    @Query("SELECT edsdb FROM ExtDataStageDefinitionBilling edsdb where edsdb.refId = :refId")
    List<ExtDataStageDefinitionBilling> findAllByRefId(@Param("refId")String refId);
}
