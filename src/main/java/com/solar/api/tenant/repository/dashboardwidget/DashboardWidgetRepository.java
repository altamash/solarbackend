package com.solar.api.tenant.repository.dashboardwidget;

import com.solar.api.tenant.mapper.tiles.dashboardwidget.DashboardSubscriptionWidget;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.EnviromentalWidgetTile;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.BillingSummaryWidgetTemplate;
import com.solar.api.tenant.mapper.tiles.dashboardwidget.billing.BillingSummaryWidgetTile;
import com.solar.api.tenant.model.stage.monitoring.ExtDataStageDefinition;
import com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard.WidgetDataResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardWidgetRepository extends JpaRepository<ExtDataStageDefinition, Long> {

    @Query(value = "SELECT " +
            "    COUNT(*) AS totalSubscriptions, " +
            "    SUM(CASE WHEN edsd.subs_status = 'ACTIVE' THEN 1 ELSE 0 END) AS activeSubscriptions, " +
            "    SUM(CASE WHEN edsd.subs_status = 'INACTIVE' THEN 1 ELSE 0 END) AS inactiveSubscriptions, " +
            "    SUM(CAST(SUBSTRING(edsd.system_size, 1, LOCATE(' ', edsd.system_size) - 1) AS DECIMAL(10, 2))) AS totalSystemSize, " +
            "    SUM(CASE WHEN edsd.subs_status = 'ACTIVE' THEN " +
            "            CAST(SUBSTRING(edsd.system_size, 1, LOCATE(' ', edsd.system_size) - 1) AS DECIMAL(10, 2)) " +
            "        ELSE 0 END) AS totalActiveSystemSize " +
            "FROM " +
            "    customer_subscription cs " +
            "INNER JOIN " +
            "    ext_data_stage_definition edsd ON cs.ext_subs_id = edsd.subs_id " +
            "WHERE " +
            "    cs.account_id = :acctId", nativeQuery = true)
    DashboardSubscriptionWidget getDashboardSubscriptionWidgetData(@Param("acctId") Long acctId);

    @Query(value = "select extDataStageDefinition " +
            "from CustomerSubscription customerSubscription  " +
            "left join ExtDataStageDefinition extDataStageDefinition   " +
            "on customerSubscription.extSubsId = extDataStageDefinition.subsId " +
            "where customerSubscription.userAccount.acctId = :acctId and extDataStageDefinition.subsStatus = 'ACTIVE' and customerSubscription.userAccount.acctId IS NOT NULL")
    List<ExtDataStageDefinition> findExtDataStageDefinitionByUserId(@Param("acctId") Long acctId);

    @Query(value = "SELECT" +
            "    edsd.ref_id as refId," +
            "    edsd.ref_type as refType," +
            "    CONCAT(IF(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.S_GS')) IS NOT NULL," +
            "    CONCAT(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.S_GS')), ' kWh'),COALESCE('0 kWh', ''))) AS systemSize," +
            "    MAX(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.MP'))) as mp," +
            "    CONCAT(ploc.add1, ', ', ploc.add2, ', ', ploc.add3) as address," +
            "    ploc.ext1 as state," +
            "    COALESCE(ploc.google_Coordinates, '') as googleCoordinates," +
            "    ploc.geo_Lat as geoLat," +
            "    ploc.geo_Long as geoLong," +
            "    COALESCE(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.INST_TYPE')), ' ') as installationType, " +
            "    GROUP_CONCAT(DISTINCT edsd.id ORDER BY edsd.id ASC) AS maxId, " +
            "    COALESCE(JSON_UNQUOTE(JSON_EXTRACT(edsd.mp_json, '$.CRS_TYP')), ' ') as gardenType " +
            "FROM" +
            "    ext_data_stage_definition edsd " +
            "LEFT JOIN" +
            "    physical_locations ploc " +
            "        ON edsd.site_location_id = ploc.id " +
            "WHERE" +
            "    edsd.subs_id IN (:variantIds) " +
            "GROUP BY" +
            "    edsd.ref_id, edsd.ref_type, systemSize, address, state, googleCoordinates, geoLat, geoLong, installationType,gardenType", nativeQuery = true)
    List<WidgetDataResult> findWidgetDataBySubsIds(@Param("variantIds") List<String> variantIds);

    @Query(value = "select ROUND(SUM(CASE WHEN billingHead.bill_status IN ('INVOICED', 'PUBLISHED') THEN billingHead.amount ELSE 0 END), 2) AS totalInvoicedAmount," +
            "ROUND(SUM(CASE WHEN billingHead.bill_status = 'PAID' THEN billingHead.amount ELSE 0 END), 2) AS totalPaidAmount " +
            "from customer_subscription customerSubsription " +
            "inner join  billing_head billingHead ON customerSubsription.id = billingHead.subscription_id " +
            "where customerSubsription.account_id =:acctId " +
            "and STR_TO_DATE(CONCAT(billingHead.billing_month_year, '-01'), '%m-%Y-%d') <= CURDATE() " +
            "and (customerSubsription.ext_subs_id in (:subsIds) or (:subsIdsPresent = false ))", nativeQuery = true)
    BillingSummaryWidgetTemplate getBillingSummaryWidgetTile(@Param("acctId") Long acctId,
                                                             @Param("subsIds") List<String> subsIds,
                                                             @Param("subsIdsPresent") Boolean subsIdsPresent);

    @Query(value = "select " +
            "ROUND(SUM(CASE WHEN billingHead.bill_status = 'PENDING' THEN billingHead.amount ELSE 0 END), 2) AS totalPendingAmount, " +
            "ROUND(SUM(CASE WHEN billingHead.bill_status = 'CALCULATED' THEN billingHead.amount ELSE 0 END), 2) AS totalCalculatedAmount, " +
            "ROUND(SUM(CASE WHEN billingHead.bill_status IN ('INVOICED', 'PUBLISHED') THEN billingHead.amount ELSE 0 END), 2) AS totalInvoicedAmount, " +
            "ROUND(SUM(CASE WHEN billingHead.bill_status = 'PAID' THEN billingHead.amount ELSE 0 END), 2) AS totalPaidAmount " +
            "from customer_subscription customerSubsription " +
            "inner join  billing_head billingHead ON customerSubsription.id = billingHead.subscription_id " +
            "where customerSubsription.account_id =:acctId " +
            "and billingHead.billing_month_year = :monthYear " +
            "and STR_TO_DATE(CONCAT(billingHead.billing_month_year, '-01'), '%m-%Y-%d') <= CURDATE()", nativeQuery = true)
    BillingSummaryWidgetTemplate getBillingSummaryWidgetByMonthYearTile(@Param("acctId") Long acctId,
                                                             @Param("monthYear") String monthYear);

    @Query(value = "select " +
            "ROUND(SUM(CASE WHEN billingHead.bill_status IN ('INVOICED', 'PUBLISHED') THEN billingHead.amount ELSE 0 END) - " +
            "SUM(CASE WHEN billingHead.bill_status = 'PAID' THEN billingHead.amount ELSE 0 END), 2) AS totalOutstandingAmount " +
            "from customer_subscription customerSubsription " +
            "inner join  billing_head billingHead ON customerSubsription.id = billingHead.subscription_id " +
            "where customerSubsription.account_id =:acctId " +
            "and STR_TO_DATE(CONCAT(billingHead.billing_month_year, '-01'), '%m-%Y-%d') <= CURDATE() " +
            "and (customerSubsription.ext_subs_id in (:subsIds) or (:subsIdsPresent = false ))", nativeQuery = true)
    BillingSummaryWidgetTemplate getOutstandingBillingAmountWidgetTile(@Param("acctId") Long acctId,
                                                                       @Param("subsIds") List<String> subsIds,
                                                                       @Param("subsIdsPresent") Boolean subsIdsPresent);

    @Query(value = "SELECT " +
            "cs.ext_subs_id AS subsId, " +
            "ym.yearmonth AS dateTime, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status IN ('INVOICED', 'PUBLISHED') THEN bh.amount ELSE 0 END), 2), 0) AS totalInvoicedAmount, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status = 'PAID' THEN bh.amount ELSE 0 END), 2), 0) AS totalPaidAmount " +
            "FROM (SELECT DATE_FORMAT(ADDDATE(CONCAT(:year, '-01-01'), INTERVAL t0.i MONTH), '%Y-%m') AS yearmonth " +
            "FROM (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11) t0) ym " +
            "CROSS JOIN customer_subscription cs " +
            "LEFT JOIN billing_head bh ON cs.id = bh.subscription_id AND DATE_FORMAT(STR_TO_DATE(CONCAT(bh.billing_month_year, '-01'), '%m-%Y-%d'), '%Y-%m') = ym.yearmonth " +
            "WHERE ym.yearmonth LIKE CONCAT('%', :year, '%') AND cs.ext_subs_id IN (:subsIds) " +
            "GROUP BY cs.ext_subs_id, ym.yearmonth", nativeQuery = true)
    List<BillingSummaryWidgetTemplate> getBillingHistoryYearlyComparativeData(@Param("year") String year, @Param("subsIds") List<String> subsIds);
    @Query(value = "SELECT " +
            "ym.yearmonth AS dateTime, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status IN ('INVOICED', 'PUBLISHED') THEN bh.amount ELSE 0 END), 2), 0) AS totalInvoicedAmount, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status = 'PAID' THEN bh.amount ELSE 0 END), 2), 0) AS totalPaidAmount " +
            "FROM (SELECT DATE_FORMAT(ADDDATE(CONCAT(:year, '-01-01'), INTERVAL t0.i MONTH), '%Y-%m') AS yearmonth " +
            "FROM (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11) t0) ym " +
            "CROSS JOIN customer_subscription cs " +
            "LEFT JOIN billing_head bh ON cs.id = bh.subscription_id AND DATE_FORMAT(STR_TO_DATE(CONCAT(bh.billing_month_year, '-01'), '%m-%Y-%d'), '%Y-%m') = ym.yearmonth " +
            "WHERE ym.yearmonth LIKE CONCAT('%', :year, '%') AND cs.ext_subs_id IN (:subsIds) " +
            "GROUP BY ym.yearmonth", nativeQuery = true)
    List<BillingSummaryWidgetTemplate> getBillingHistoryYearlyCumulativeData(@Param("year") String year, @Param("subsIds") List<String> subsIds);

//    @Query("SELECT ym.yearmonth AS dateTime, " +
//            "COALESCE(ROUND(SUM(CASE WHEN bh.billStatus IN ('INVOICED', 'PUBLISHED') THEN bh.amount ELSE 0 END), 2), 0) AS totalInvoicedAmount, " +
//            "COALESCE(ROUND(SUM(CASE WHEN bh.billStatus = 'PAID' THEN bh.amount ELSE 0 END), 2), 0) AS totalPaidAmount " +
//            "FROM (SELECT FUNCTION('DATE_FORMAT', FUNCTION('ADDDATE', CURRENT_DATE, FUNCTION('INTERVAL', t0.i - 12, 'MONTH')), '%m-%Y') AS yearmonth " +
//            "FROM (SELECT 0 AS i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11) t0) ym " +
//            "CROSS JOIN CustomerSubscription cs " +
//            "LEFT JOIN BillingHead bh ON cs.id = bh.subscriptionId AND FUNCTION('DATE_FORMAT', FUNCTION('STR_TO_DATE', CONCAT(bh.billingMonthYear, '-01'), '%m-%Y-%d'), '%m-%Y') = ym.yearmonth " +
//            "WHERE cs.extSubsId IN (:subsIds) " +
//            "GROUP BY ym.yearmonth")
//    List<BillingSummaryWidgetTemplate> getBillingHistoryYearlyCumulativeData(@Params("subsIds") List<String> subsIds);

    @Query(value = "SELECT " +
            "ym.yearmonth AS dateTime, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status IN ('INVOICED', 'PUBLISHED') THEN bh.amount ELSE 0 END), 2), 0) AS totalInvoicedAmount, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status = 'PAID' THEN bh.amount ELSE 0 END), 2), 0) AS totalPaidAmount " +
            "FROM (SELECT DATE_FORMAT(ADDDATE(CURDATE(), INTERVAL t0.i - 12 MONTH), '%m-%Y') AS yearmonth " +
            "FROM (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11) t0) ym " +
            "CROSS JOIN customer_subscription cs " +
            "LEFT JOIN billing_head bh ON cs.id = bh.subscription_id AND DATE_FORMAT(STR_TO_DATE(CONCAT(bh.billing_month_year, '-01'), '%m-%Y-%d'), '%m-%Y') = ym.yearmonth " +
            "WHERE cs.ext_subs_id IN (:subsIds) " +
            "GROUP BY ym.yearmonth", nativeQuery = true)
    List<BillingSummaryWidgetTemplate> getBillingHistoryYearlyCumulativeData(@Param("subsIds") List<String> subsIds);

    @Query(value = "SELECT " +
            "cs.ext_subs_id AS subsId, " +
            "ym.quarter AS dateTime, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status IN ('INVOICED', 'PUBLISHED') THEN bh.amount ELSE 0 END), 2), 0) AS totalInvoicedAmount, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status = 'PAID' THEN bh.amount ELSE 0 END), 2), 0) AS totalPaidAmount " +
            "FROM (SELECT DATE_FORMAT(ADDDATE(CONCAT(:year, '-01-01'), INTERVAL t0.i MONTH), '%Y-%m') AS yearmonth, " +
            "CASE WHEN t0.i BETWEEN 0 AND 2 THEN 'Q1' " +
            "WHEN t0.i BETWEEN 3 AND 5 THEN 'Q2' " +
            "WHEN t0.i BETWEEN 6 AND 8 THEN 'Q3' " +
            "WHEN t0.i BETWEEN 9 AND 11 THEN 'Q4' " +
            "END AS quarter " +
            "FROM (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11) t0) ym " +
            "CROSS JOIN customer_subscription cs " +
            "LEFT JOIN billing_head bh ON cs.id = bh.subscription_id AND DATE_FORMAT(STR_TO_DATE(CONCAT(bh.billing_month_year, '-01'), '%m-%Y-%d'), '%Y-%m') = ym.yearmonth " +
            "WHERE ym.yearmonth LIKE CONCAT('%', :year, '%') AND cs.ext_subs_id IN (:subsIds) " +
            "GROUP BY cs.ext_subs_id, ym.quarter " +
            "ORDER BY cs.ext_subs_id, ym.quarter", nativeQuery = true)
    List<BillingSummaryWidgetTemplate> getBillingHistoryQuarterlyComparativeData(@Param("year") String year, @Param("subsIds") List<String> subsIds);
    @Query(value = "SELECT " +
            "ym.quarter AS dateTime, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status IN ('INVOICED', 'PUBLISHED') THEN bh.amount ELSE 0 END), 2), 0) AS totalInvoicedAmount, " +
            "COALESCE(ROUND(SUM(CASE WHEN bh.bill_status = 'PAID' THEN bh.amount ELSE 0 END), 2), 0) AS totalPaidAmount " +
            "FROM (SELECT DATE_FORMAT(ADDDATE(CONCAT(:year, '-01-01'), INTERVAL t0.i MONTH), '%Y-%m') AS yearmonth, " +
            "CASE WHEN t0.i BETWEEN 0 AND 2 THEN 'Q1' " +
            "WHEN t0.i BETWEEN 3 AND 5 THEN 'Q2' " +
            "WHEN t0.i BETWEEN 6 AND 8 THEN 'Q3' " +
            "WHEN t0.i BETWEEN 9 AND 11 THEN 'Q4' " +
            "END AS quarter " +
            "FROM (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11) t0) ym " +
            "CROSS JOIN customer_subscription cs " +
            "LEFT JOIN billing_head bh ON cs.id = bh.subscription_id AND DATE_FORMAT(STR_TO_DATE(CONCAT(bh.billing_month_year, '-01'), '%m-%Y-%d'), '%Y-%m') = ym.yearmonth " +
            "WHERE ym.yearmonth LIKE CONCAT('%', :year, '%') AND cs.ext_subs_id IN (:subsIds) " +
            "GROUP BY ym.quarter " +
            "ORDER BY ym.quarter", nativeQuery = true)
    List<BillingSummaryWidgetTemplate> getBillingHistoryQuarterlyCumulativeData(@Param("year") String year, @Param("subsIds") List<String> subsIds);

    @Query("SELECT bh.billingMonthYear, " +
            "ROUND(SUM(CASE WHEN bh.billStatus IN ('INVOICED', 'PUBLISHED') THEN bh.amount ELSE 0 END), 2) AS totalInvoicedAmount, " +
            "ROUND(SUM(CASE WHEN bh.billStatus = 'PAID' THEN bh.amount ELSE 0 END), 2) AS totalPaidAmount " +
            "FROM CustomerSubscription cs " +
            "INNER JOIN BillingHead bh ON cs.id = bh.subscriptionId " +
            "WHERE bh.billingMonthYear IN (:billingMonths) " +
            "AND cs.extSubsId IN (:extSubsIds) " +
            "GROUP BY bh.billingMonthYear")
    List<BillingSummaryWidgetTemplate> getBillingHistory(@Param("billingMonths") List<String> billingMonths,
                                @Param("extSubsIds") List<String> extSubsIds);
}
