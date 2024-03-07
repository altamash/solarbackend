package com.solar.api.saas.repository;

import com.solar.api.saas.model.chart.views.BillingDetailView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BillingDetailViewRepository extends JpaRepository<BillingDetailView, String>,
        BillingDetailViewRepositoryCustom {

    @Query(value = "SELECT value, billing_code, billing_code, subscription_id, account_id, billing_month_year, month," +
            " description, bill_status  FROM billing_detail_v", nativeQuery = true)
    List<BillingDetailView> findByBillStatus(String billStatus);

    @Query(value = "SELECT value, billing_code, billing_code, subscription_id, account_id, billing_month_year, " +
            "description, bill_status  FROM billing_detail_v " +
            "where DATEDIFF(current_date(), a.somecolumn) < 10", nativeQuery = true)
    List<BillingDetailView> findBetweenMonths(String bmy);

}
