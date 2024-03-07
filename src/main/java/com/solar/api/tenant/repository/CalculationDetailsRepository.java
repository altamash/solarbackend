package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.billing.calculation.BillByGardenTableTemplate;
import com.solar.api.tenant.model.billing.calculation.CalculationDetails;
import com.solar.api.tenant.model.billing.calculation.CalculationTracker;
import com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CalculationDetailsRepository extends JpaRepository<CalculationDetails, Long> {

    @Query("select cd from CalculationDetails cd where cd.sourceId = :sourceId")
    Optional<CalculationDetails> findBySourceId(@Param("sourceId") Long sourceId);

    @Query("select cd from CalculationDetails cd where cd.calculationTracker=:calculationTracker and cd.sourceId=:sourceId and cd.source=:source")
    CalculationDetails findByCalculationTrackerAndSourceIdAndSource(CalculationTracker calculationTracker, Long sourceId, String source);

    @Query(value = "SELECT edsdb.ref_id as variantId,edsdb.variant_alias as variantAlias, count(edsdb.subs_id) as subsCount, edsdb.billing_json as billJson " +
            "FROM ext_data_stage_definition_billing edsdb  where edsdb.subs_status='active' group by edsdb.variantId,edsdb.variant_alias,edsdb.subs_id,edsdb.billing_json", nativeQuery = true)
    List<BillByGardenTableTemplate> getBillByGardenTable();

    @Query("select cd from CalculationDetails cd where cd.sourceId in (:sourceIds)")
    List<CalculationDetails> findAllBySourceIds(@Param("sourceIds") List<Long> sourceIds);

    @Query("select cd from CalculationDetails cd where cd.state in (:status)")
    List<CalculationDetails> findAllByStatus(@Param("status") String status);

    @Query("select new com.solar.api.tenant.model.dataexport.payment.PaymentDataDTO(cd.errorMessage, edsd.refType,edsd.refId) " +
            " from CustomerSubscription cs " +
            "inner join BillingHead bh on bh.subscriptionId = cs.id " +
            "inner join CalculationDetails cd on cd.sourceId = bh.id " +
            "inner join ExtDataStageDefinition edsd on edsd.subsId = cs.extSubsId " +
            "where cs.userAccount.acctId in (:accountId)")
    List<PaymentDataDTO> findSourceAndError(@Param("accountId") List<Long> accountId);

    @Query("select cd from CalculationDetails cd " +
            "inner join BillingHead bh on bh.id = cd.sourceId " +
            "where cd.state in (:status) and bh.billingMonthYear in (:periods)")
    List<CalculationDetails> findAllByStatusAndPeriods(@Param("status") String status, @Param("periods") List<String> periods);

}
