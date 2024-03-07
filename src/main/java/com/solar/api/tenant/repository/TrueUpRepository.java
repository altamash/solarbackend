package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.report.TrueUp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrueUpRepository extends JpaRepository<TrueUp, Long> {

    TrueUp findBySubscriptionId(Long subscriptionId);

    List<TrueUp> findBySubscriptionRateMatrixId(Long subscriptionRateMatrixId);
}
