package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.FinancialAccrual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FinancialAccrualRepository extends JpaRepository<FinancialAccrual, Long> {

    @Query("SELECT SUM(accruedAmount) from FinancialAccrual fc WHERE refId=:refId and accrualCategoryId=:accrualCategoryId")
    Double getSumOfAccruedAmount(@Param("refId") Long refId, @Param("accrualCategoryId") Long accrualCategoryId );
}
