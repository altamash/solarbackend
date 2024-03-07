package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.AnalyticalCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnalyticalCalculationRepository extends JpaRepository<AnalyticalCalculation, Long> {

    AnalyticalCalculation findByAccountIdAndSubscriptionId(Long accountId,Long subscriptionId);

    AnalyticalCalculation findByAccountIdAndSubscriptionIdAndAnalysis(Long accountId,Long subscriptionId, String analysis);

    @Query("select ac from AnalyticalCalculation ac")
    List<AnalyticalCalculation> findCurrentValues();
}
