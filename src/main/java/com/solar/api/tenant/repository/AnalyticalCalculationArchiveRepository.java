package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.AnalyticalCalculationArchive;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticalCalculationArchiveRepository extends JpaRepository<AnalyticalCalculationArchive, Long> {

//    AnalyticalCalculationArchive findByAccountIdAndSubscriptionId(Long accountId,Long subscriptionId);
//    AnalyticalCalculationArchive findByAccountIdAndSubscriptionIdAndAnalysis(Long accountId,Long subscriptionId, String analysis);
}
