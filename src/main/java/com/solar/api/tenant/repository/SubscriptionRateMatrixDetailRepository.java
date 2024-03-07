package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRateMatrixDetailRepository extends JpaRepository<SubscriptionRateMatrixDetail, Long> {
    List<SubscriptionRateMatrixDetail> findByRateCode(String subscriptionCode);

    List<SubscriptionRateMatrixDetail> findBySubscriptionRateMatrixId(Long id);

    List<SubscriptionRateMatrixDetail> findByDefaultValue(String defaultValue);

    SubscriptionRateMatrixDetail findBySubscriptionRateMatrixIdAndRateCode(Long id, String code);

    @Query("SELECT ccm from CustomerSubscriptionMapping ccm WHERE subscription = :subscription AND " +
            "subscriptionRateMatrixId = :subscriptionRateMatrixId" +
            " AND level = 1 ORDER BY sequenceNumber")
    List<SubscriptionRateMatrixDetail> getMappingsForCalculationOrderedBySequence(@Param("subscription") CustomerSubscription subscription,
                                                                                  @Param("subscriptionRateMatrixId") Long subscriptionRateMatrixId);

    @Query("SELECT ccm from CustomerSubscriptionMapping ccm WHERE subscription = :subscription AND " +
            "subscriptionRateMatrixId = :subscriptionRateMatrixId" +
            " AND level = 0")
    List<SubscriptionRateMatrixDetail> getMappingsWithStaticValues(@Param("subscription") CustomerSubscription subscription,
                                                                   @Param("subscriptionRateMatrixId") Long subscriptionRateMatrixId);

    @Query("SELECT srmd.rateCode from SubscriptionRateMatrixDetail srmd WHERE subscriptionRateMatrixId = " +
            ":subscriptionRateMatrixId AND varyByCustomer = :varyByCustomer")
    List<String> findRateCodesBySubscriptionRateMatrixIdAndVaryByCustomer(Long subscriptionRateMatrixId,
                                                                          Boolean varyByCustomer);

    SubscriptionRateMatrixDetail findBySubscriptionCodeAndSubscriptionRateMatrixIdAndRateCode(String subscriptionCode
            , Long subscriptionRateMatrixId, String rateCode);

    @Query(value = "SELECT id,allow_others_to_edit,subscription_code,flags,level,maintain_bill_history,mandatory," +
            "sequence_number,financial_billing_definition_id," +
            "system_used,vary_by_customer,subscription_rate_matrix_id, default_value, rate_code,created_at," +
            "updated_at,subscription_rate_matrix_head_id " +
            "from subscription_rate_matrix_detail " + "where rate_code in ('SCSG','SCSGN')", nativeQuery = true)
    List<SubscriptionRateMatrixDetail> findDefaultValueForPaymentDownload();

    @Query("select detail from SubscriptionRateMatrixDetail detail where subscriptionRateMatrixId = :subscriptionRateMatrixId and level = 0 and varyByCustomer = 1")
    List<SubscriptionRateMatrixDetail> getRequiredForUpload(@Param("subscriptionRateMatrixId") Long subscriptionRateMatrixId);

    @Query("select detail.rateCode from SubscriptionRateMatrixDetail detail where subscriptionRateMatrixId = :subscriptionRateMatrixId and varyByCustomer = 1")
    List<String> getRequiredCodesForSubscriptionMapping(@Param("subscriptionRateMatrixId") Long subscriptionRateMatrixId);
}
