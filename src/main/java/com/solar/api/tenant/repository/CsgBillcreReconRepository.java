package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.CsgBillcreRecon;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CsgBillcreReconRepository extends JpaRepository<CsgBillcreRecon, Long> {
    CsgBillcreRecon findBySubscriptionIdAndGardenIdAndPremiseNoAndPeriodStartDateAndPeriodEndDate(Long subscriptionId
            , String gardenId, String premiseNo, String periodStartDate, String periodEndDate);

    List<CsgBillcreRecon> findAll(Specification<CsgBillcreRecon> spec);
}
