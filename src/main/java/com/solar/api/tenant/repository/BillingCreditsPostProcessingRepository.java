package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.billingCredits.BillingCreditsPostProcessing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

// TODO: Update subscription_matrix_v view
public interface BillingCreditsPostProcessingRepository extends JpaRepository<BillingCreditsPostProcessing, String> {

    @Query(value = "SELECT rate_code, value, customer_subscription_id, subscription_rate_matrix_id, " +
            "measure_definition_id  FROM subscription_matrix_v", nativeQuery = true)
    List<BillingCreditsPostProcessing> getAll();

    @Query(value = "select * FROM subscription_matrix_v " +
            "where subscription_rate_matrix_id=:rateMatrixId and (customer_subscription_id =:customerSubscriptionId " +
            "or customer_subscription_id is null)", nativeQuery = true)
    List<BillingCreditsPostProcessing> customerSubscriptionId(Long rateMatrixId, Long customerSubscriptionId);

    @Query(value = "SELECT rate_code, value, customer_subscription_id, subscription_rate_matrix_id, " +
            "measure_definition_id  FROM subscription_matrix_v " +
            "where value=:gardenId", nativeQuery = true)
    BillingCreditsPostProcessing findByValue(String gardenId);

}
