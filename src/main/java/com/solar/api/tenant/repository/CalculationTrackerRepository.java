package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerPeriodDTO;
import com.solar.api.tenant.model.billing.calculation.CalculationTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface CalculationTrackerRepository extends JpaRepository<CalculationTracker, Long> {

    @Query("SELECT DISTINCT new com.solar.api.tenant.mapper.billing.calculation.CalculationTrackerPeriodDTO( bh.billingMonthYear," +
            "DATE_FORMAT(str_to_date(concat('01-',bh.billingMonthYear),'%d-%m-%Y'), '%b-%Y')) FROM BillingHead bh " +
            "where STR_TO_DATE(CONCAT('01-', bh.billingMonthYear), '%d-%m-%Y') <= LAST_DAY(CURDATE()) " +
            "order by str_to_date(concat('01-', bh.billingMonthYear),'%d-%m-%Y') desc")
    List<CalculationTrackerPeriodDTO> findAllBillingPeriod();
}
