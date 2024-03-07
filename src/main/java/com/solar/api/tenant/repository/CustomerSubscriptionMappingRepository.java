package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.SubscriptionRateMatrixHead;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerSubscriptionMappingRepository extends JpaRepository<CustomerSubscriptionMapping, Long>,
        CustomerSubscriptionMappingRepositoryCustom {
    /*
        Get all CustomerSubscriptionMapping records with subscriptionId and contactRateMatrixId.
        Criteria: There is nothing in effectiveDate and endDate. The effectiveDate is less than bill generation
        date and endDate is more than today or null. The end date is after today's date and there is no effectiveDate.
    */
    List<CustomerSubscriptionMapping> getBySubscription(Specification<CustomerSubscriptionMapping> spec);

    @Query(value = "SELECT sum(value) FROM customer_subscription_mapping where rate_code = 'KWDC' and " +
            "customer_subscription_id in (SELECT id FROM customer_subscription where subscription_rate_matrix_id = :subscriptionRateMatrixHeadId and subscription_status = 'ACTIVE')",
            nativeQuery = true)
    String findCumulativeKWDCofActiveSubs(Long subscriptionRateMatrixHeadId);
    @Query(value = "SELECT sum(value) FROM customer_subscription_mapping where rate_code = 'KWDC' and " +
            "customer_subscription_id in (SELECT id FROM customer_subscription where subscription_rate_matrix_id = :subscriptionRateMatrixHeadId and subscription_status = 'INACTIVE')",
            nativeQuery = true)
    String findCumulativeKWDCofInactiveSubs(Long subscriptionRateMatrixHeadId);
    @Query(value = "SELECT sum(value) FROM customer_subscription_mapping where rate_code = 'KWDC' and " +
            "customer_subscription_id in (SELECT id FROM customer_subscription where subscription_rate_matrix_id = :subscriptionRateMatrixHeadId and subscription_status = 'INVALID')",
            nativeQuery = true)
    String findCumulativeKWDCofInvalidSubs(Long subscriptionRateMatrixHeadId);
    List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingBySubscription(CustomerSubscription subscription);

    List<CustomerSubscriptionMapping> findCustomerSubscriptionMappingByRateCode(String rateCode);

    @Query("SELECT csm FROM CustomerSubscriptionMapping csm where csm.rateCode = :rateCode and csm.value = :value and" +
            " csm.subscriptionRateMatrixHead = :subscriptionRateMatrixHead")
    List<CustomerSubscriptionMapping> findByRateCodeValueMatrixHead(String rateCode, String value, SubscriptionRateMatrixHead subscriptionRateMatrixHead);

    CustomerSubscriptionMapping findByRateCodeAndSubscription(String rateCode, CustomerSubscription subscription);

    List<CustomerSubscriptionMapping> findBySubscription(CustomerSubscription subscription);

    List<CustomerSubscriptionMapping> findByValue(String value);

    @Query("SELECT csm FROM CustomerSubscriptionMapping csm where csm.subscription = :subscription and csm" +
            ".rateCode='ROLLDT'")
    CustomerSubscriptionMapping getRolloverDate(@Param("subscription") CustomerSubscription subscription);

    @Query("SELECT csm FROM CustomerSubscriptionMapping csm where csm.subscription = :custSubscription " +
            " and csm.rateCode in (:rateCodes)")
    public CustomerSubscriptionMapping getRateCode(@Param("custSubscription") CustomerSubscription custSubscription,
                                                   @Param("rateCodes") List<String> rateCodes);

    @Query("SELECT SUM(m.value) FROM CustomerSubscriptionMapping m, CustomerSubscription s" +
            " WHERE m.subscription.id = s.id" +
            " AND s.subscriptionStatus in ('ACTIVE', 'INACTIVE')" +
            " AND m.subscriptionRateMatrixHead.id = :subscriptionRateMatrixHeadId" +
            " AND m.rateCode = 'KWDC'")
    Double gardenCapacityConsumed(Long subscriptionRateMatrixHeadId);
}
