package com.solar.api.tenant.repository;

import com.solar.api.tenant.mapper.billing.PowerMonitorPercentileDTO;
import com.solar.api.tenant.mapper.projection.MonthlyYieldProjection;
import com.solar.api.tenant.mapper.billingCredits.BillingCreditsPMDTO;
import com.solar.api.tenant.mapper.projection.ProjectionTileDTO;
import com.solar.api.tenant.mapper.projection.QuarterlyYieldProjection;
import com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring.DataExportPMTile;
import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingDaily;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface MonitorReadingDailyRepository extends JpaRepository<MonitorReadingDaily, Long> {
    //@Query("Select mrd MonitorReadingDaily mrd where userId = :userId and MONTH()")
    List<MonitorReadingDaily> findByUserIdAndDayIn(Long userId, List<Date> days);

    @Query(value = "SELECT MRD.* FROM customer_subscription_mapping  CSM " +
            "INNER JOIN monitor_reading_daily MRD " +
            "ON CSM.value = MRD.inverter_number " +
            "where " +
            "CSM.rate_Code = :rate_code " +
            "and " +
            "CSM.customer_subscription_id = :subId " +
            "and " +
            "date_format(mrd.day,'%Y-%m-%d') in (:days) " +
            "order by MRD.day asc", nativeQuery = true)
    List<MonitorReadingDaily> findBySubscriptionAndDayIn(@Param("subId") Long subId,
                                                         @Param("days") List<String> days,
                                                         @Param("rate_code") String rate_code);

    @Query(value = "SELECT MRD.* FROM customer_subscription_mapping  CSM " +
            "INNER JOIN monitor_reading_daily MRD " +
            "ON CSM.value = MRD.inverter_number " +
            "where " +
            "CSM.rate_Code = :rate_code " +
            "and " +
            "CSM.customer_subscription_id = :subId " +
            "and " +
            "date_format(mrd.day,'%Y-%m-%d') in (:days) " +
            "order by MRD.day asc", nativeQuery = true)
    List<MonitorReadingDaily> findByMongoSubscriptionAndDayIn(@Param("subId") Long subId,
                                                              @Param("days") List<String> days,
                                                              @Param("rate_code") String rate_code);

    @Query(value = "SELECT MRD.* FROM customer_subscription_mapping  CSM " +
            "INNER JOIN monitor_reading_daily MRD " +
            "ON CSM.value = MRD.inverter_number " +
            "where " +
            "CSM.rate_Code = :rate_code " +
            "and " +
            "MRD.user_id = :userId " +
            "and " +
            "date_format(mrd.day,'%Y-%m-%d') in (:days ) " +
            "order by MRD.day asc", nativeQuery = true)
    List<MonitorReadingDaily> findByUserAndDayIn(@Param("userId") Long userId,
                                                 @Param("days") List<String> days,
                                                 @Param("rate_code") String rate_code);

    // List<MonitorReadingDaily> findByInverterNumberAndDayIn(String inverterNumber, List<Date> days);

    @Query(value = "SELECT MRD.* FROM customer_subscription_mapping  CSM " +
            "INNER JOIN monitor_reading_daily MRD " +
            "ON CSM.value = MRD.inverter_number " +
            "where " +
            "CSM.rate_Code = :rate_code " +
            "and " +
            "CSM.customer_subscription_id = :subId " +
            "and " +
            "date_format(mrd.day,'%Y') = :year " +
            "order by MRD.day asc", nativeQuery = true)
    List<MonitorReadingDaily> findBySubscriptionIdAndYear(
            @Param("subId") Long subId,
            @Param("year") String year,
            @Param("rate_code") String rate_code);

    @Query(value = "SELECT MRD.* FROM customer_subscription_mapping  CSM " +
            "INNER JOIN monitor_reading_daily MRD " +
            "ON CSM.value = MRD.inverter_number " +
            "where " +
            "MRD.user_id = :userId " +
            "and " +
            "CSM.rate_Code = :rate_code " +
            "and " +
            "date_format(mrd.day,'%Y') = :year " +
            "order by MRD.day asc", nativeQuery = true)
    List<MonitorReadingDaily> findByUserSubscriptionIdAndYear(@Param("userId") Long userId,
                                                              @Param("year") String year,
                                                              @Param("rate_code") String rate_codes);

//    List<MonitorReadingDaily> findByUserListAccum(List<> userId, List<Date> days);

    @Query("select mrd from MonitorReadingDaily mrd where subscriptionId=:subscriptionId and day= date(:dateTime)")
    MonitorReadingDaily getLastSavedRecordBySubIdAndDate(Long subscriptionId, String dateTime);

    @Query("select mrd from MonitorReadingDaily mrd where subscriptionIdMongo=:subscriptionIdMongo and day= date(:dateTime)")
    MonitorReadingDaily getLastSavedRecordByMongoSubIdAndDate(String subscriptionIdMongo, String dateTime);

    @Query("select mrd from MonitorReadingDaily mrd where mrd.subscriptionIdMongo = :subscriptionIdMongo and mrd.day = date(:dateTime)")
    List<MonitorReadingDaily> getAllLastSavedRecordByMongoSubIdAndDate(String subscriptionIdMongo, String dateTime);

    @Query(value = "SELECT MRD.* FROM monitor_reading_daily MRD " +
            "where " +
            "MRD.inverter_number = :rate_code " +
            "and MRD.subscription_id_mongo = :subscriptionId " +
            "and date_format(mrd.day,'%Y-%m-%d') in (:days) " +
            "order by MRD.day asc", nativeQuery = true)
    List<MonitorReadingDaily> findByInverterNumberAndSubscriptionIdMongoAndDayIn(@Param("subscriptionId") String subscriptionId,
                                                                                 @Param("days") List<String> days,
                                                                                 @Param("rate_code") String rate_code);

    List<MonitorReadingDaily> findBySubscriptionIdMongoAndSiteAndInverterNumberAndDay(String subscriptionIdMongo, String site, String inverterNumber, Date day);

    MonitorReadingDaily findBySubscriptionIdMongoAndDay(String subscriptionIdMongo, Date day);

    @Query(value = "SELECT MRD.* FROM monitor_reading_daily MRD " +
            "where " +
            "MRD.inverter_number = :inverter_number " +
            "and MRD.subscription_id_mongo = :subscriptionId " +
            "and date_format(mrd.day,'%Y') = :year " +
            "order by MRD.day asc", nativeQuery = true)
    List<MonitorReadingDaily> findByInverterNumberAndYearAndSubId(
            @Param("year") String year,
            @Param("inverter_number") String inverter_number,
            @Param("subscriptionId") String subscriptionId);

    @Query("select new com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring.DataExportPMTile(edsd.refType, " +
            " case when :isCust = true then concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName) else edsd.subscriptionName end, " +
            " concat(coalesce(mrd.yieldValue, 0), ' kWh'), function('DATE_FORMAT', mrd.day, '%b %d, %Y'), edsd.refId,edsd.subsId) " +
            "from MonitorReadingDaily mrd " +
            "join ExtDataStageDefinition edsd on edsd.subsId = mrd.subscriptionIdMongo " +
            "join CustomerSubscription  cs on cs.extSubsId = mrd.subscriptionIdMongo " +
            "where mrd.subscriptionIdMongo in (:subIds) " +
            "and mrd.id in ( " +
            "   select max(mrd2.id) " +
            "   from MonitorReadingDaily mrd2 " +
            "   where mrd2.subscriptionIdMongo in (:subIds) " +
            "   and function('STR_TO_DATE', mrd2.day, '%Y-%m-%d') between function('STR_TO_DATE', :startDate, '%b %d, %Y') and function('STR_TO_DATE', :endDate, '%b %d, %Y')" +
            "   group by mrd2.subscriptionIdMongo, mrd2.day " +
            ") " +
            "order by mrd.day, mrd.subscriptionIdMongo ")
    Page<DataExportPMTile> findDailyExportDataMR(@Param("subIds") List<String> subIds,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate,
                                                 @Param("isCust") Boolean isCust,
                                                 Pageable pageable);

    @Query("select new com.solar.api.tenant.mapper.tiles.dataexport.powermonitoring.DataExportPMTile(edsd.refType, " +
            " case when :isCust = true then concat(concat(cs.userAccount.firstName, ' '), cs.userAccount.lastName) else edsd.subscriptionName end, " +
            " concat(sum(coalesce(mrd.yieldValue, 0)), ' kWh'), function('DATE_FORMAT', mrd.day, '%M %Y'), edsd.refId,edsd.subsId) " +
            "from MonitorReadingDaily mrd " +
            "join ExtDataStageDefinition edsd on edsd.subsId = mrd.subscriptionIdMongo " +
            "join CustomerSubscription  cs on cs.extSubsId = mrd.subscriptionIdMongo " +
            "where mrd.subscriptionIdMongo in (:subIds) " +
            "and function('STR_TO_DATE', mrd.day, '%Y-%m-%d') between function('STR_TO_DATE', :startDate, '%b %d, %Y') and function('STR_TO_DATE', :endDate, '%b %d, %Y') " +
            "group by function('DATE_FORMAT', mrd.day, '%M %Y'), edsd.refType, edsd.refId, edsd.subsId, cs.userAccount.firstName, cs.userAccount.lastName, edsd.subscriptionName, mrd.subscriptionIdMongo " +
            "order by function('DATE_FORMAT', mrd.day, '%M %Y')")
    Page<DataExportPMTile> findMonthlySummedExportDataMR(@Param("subIds") List<String> subIds,
                                                         @Param("startDate") String startDate,
                                                         @Param("endDate") String endDate,
                                                         @Param("isCust") Boolean isCust,
                                                         Pageable pageable);

    @Query("select new com.solar.api.tenant.mapper.billing.PowerMonitorPercentileDTO(mrd.day, mrd.yieldValue) " +
            "from MonitorReadingDaily mrd " +
            "where mrd.subscriptionIdMongo = :subId and function('DATE_FORMAT', mrd.day, '%Y-%m') = :month " +
            "order by mrd.day asc")
    List<PowerMonitorPercentileDTO> findPercentileDTOByMonthAndSubsId(@Param("month") String month, @Param("subId") String subId);

    @Query("select new com.solar.api.tenant.mapper.billingCredits.BillingCreditsPMDTO(mrd.subscriptionIdMongo, " +
            "sum(coalesce(mrd.yieldValue, 0)), " +
            "cs.gardenSrc, " +
            "edsd.mpJson) " +
            "from MonitorReadingDaily mrd " +
            "join ExtDataStageDefinition edsd on edsd.subsId = mrd.subscriptionIdMongo " +
            "join CustomerSubscription  cs on cs.extSubsId = mrd.subscriptionIdMongo " +
            "where CONCAT(YEAR(mrd.day), '-', LPAD(MONTH(mrd.day), 2, '0')) = :month " +
            "group by mrd.subscriptionIdMongo, cs.gardenSrc, edsd.mpJson")
    List<BillingCreditsPMDTO> findMonthlySummedDataForBillingCredits(@Param("month") String month);


    // projectionId is basically a type of subscripton
    @Query(value = "SELECT MRD.* FROM monitor_reading_daily MRD " +
            "where MRD.subscription_id_mongo = :projectionId " +
            "order by MRD.day asc", nativeQuery = true)
    List<MonitorReadingDaily> findByProjectionId(@Param("projectionId") String projectionId);


    @Query(value = "select new com.solar.api.tenant.mapper.projection.ProjectionTileDTO" +
            "(date_format(MRD.day,'%Y'), sum(MRD.yieldValue),0.0 ) " +
            "FROM MonitorReadingDaily MRD  " +
            "where  " +
            "MRD.inverterNumber Is null and " +
            "MRD.subscriptionIdMongo = :projectionId " +
            "group by date_format(MRD.day,'%Y') " +
            "order by date_format(MRD.day,'%Y') asc")
    List<ProjectionTileDTO> findTotalYieldByProjectionIdForYearly(@Param("projectionId") String projectionId);

    @Query(value = "SELECT " +
            "CONCAT(y.year, '-', LPAD(m.month, 2, '0')) AS day, " +
            "COALESCE(SUM(mrd.yield_value), 0) AS totalYield " +
            "FROM all_years y " +
            "CROSS JOIN all_months m " +
            "LEFT JOIN monitor_reading_daily mrd " +
            "ON CONCAT(y.year, '-', LPAD(m.month, 2, '0')) = DATE_FORMAT(mrd.day, '%Y-%m') " +
            "AND mrd.inverter_number IS NULL " +
            "AND mrd.subscription_id_mongo = :projectionId " +
            "GROUP BY y.year, m.month " +
            "ORDER BY y.year, m.month ASC ", nativeQuery = true)
    List<MonthlyYieldProjection> findTotalYieldByProjectionIdForMonthly(@Param("projectionId") String projectionId);

    @Modifying
    @Transactional
    @Query(value = "CREATE TEMPORARY TABLE IF NOT EXISTS date_range AS (" +
            "     SELECT DATE_FORMAT(DATE_ADD(min_day, INTERVAL (t.i1 + t.i2 + t.i3 + t.i4) DAY), '%Y-%m-%d') AS day, :projectionId AS projectionId " +
            "     FROM (" +
            "         SELECT MIN(DATE_FORMAT(day, '%Y-01-01')) AS min_day, " +
            "                MAX(DATE_FORMAT(day, '%Y-12-31')) AS max_day " +
            "         FROM monitor_reading_daily " +
            "         WHERE inverter_number IS NULL AND subscription_id_mongo = :projectionId " +
            "     ) data " +
            "     JOIN ( " +
            "         SELECT a.i AS i1,  b.i * 10 AS i2,  c.i * 100 AS i3,  d.i * 1000 AS i4 " +
            "         FROM (SELECT 0 AS i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) a, " +
            "              (SELECT 0 AS i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) b, " +
            "              (SELECT 0 AS i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) c, " +
            "              (SELECT 0 AS i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d " +
            "         ) t " +
            "     WHERE DATE_ADD(min_day, INTERVAL (t.i1 + t.i2 + t.i3 + t.i4) DAY) <= max_day " +
            ")", nativeQuery = true)
    void createTemporaryTable(@Param("projectionId") String projectionId);

    @Query(value = "SELECT dr.day AS day, " +
            "       COALESCE(SUM(mrd.yield_value), 0) AS totalYield " +
            "FROM date_range dr " +
            "LEFT JOIN monitor_reading_daily mrd ON dr.day = DATE_FORMAT(mrd.day, '%Y-%m-%d') " +
            "                                   AND mrd.inverter_number IS NULL " +
            "                                   AND mrd.subscription_id_mongo = :projectionId " +
            "                                   AND DATE_FORMAT(mrd.day, '%Y-%m-%d') = DATE_FORMAT(dr.day, '%Y-%m-%d') " +
            "GROUP BY dr.day " +
            "ORDER BY dr.day ASC", nativeQuery = true)
    List<MonthlyYieldProjection> findTotalYieldByProjectionIdForMonthlyDates(@Param("projectionId") String projectionId);

    @Modifying
    @Transactional
    @Query(value = "CREATE TEMPORARY TABLE IF NOT EXISTS quarter_calendar AS (" +
            "  SELECT 1 AS quarter" +
            "  UNION ALL SELECT 2" +
            "  UNION ALL SELECT 3" +
            "  UNION ALL SELECT 4" +
            ")", nativeQuery = true)
    void createQuartersTemporaryTable(@Param("projectionId") String projectionId);

    @Query(value = "SELECT years.year, qc.quarter, " +
            "  COALESCE(SUM(mrd_sub.yield_value), 0) AS totalYield   " +
            "FROM quarter_calendar qc   " +
            "CROSS JOIN ( " +
            "  SELECT DISTINCT YEAR(day) AS year " +
            "  FROM monitor_reading_daily where subscription_id_mongo = :projectionId " +
            ") AS years " +
            "LEFT JOIN ( " +
            "  SELECT  " +
            "    YEAR(day) AS year, " +
            "    QUARTER(day) AS quarter, " +
            "    SUM(yield_value) AS yield_value " +
            "  FROM monitor_reading_daily " +
            "  WHERE inverter_number IS NULL  " +
            "    AND subscription_id_mongo = :projectionId " +
            "  GROUP BY YEAR(day), QUARTER(day) " +
            ") AS mrd_sub " +
            "  ON years.year = mrd_sub.year " +
            "  AND qc.quarter = mrd_sub.quarter " +
            "GROUP BY years.year, qc.quarter " +
            "ORDER BY years.year, qc.quarter ASC ", nativeQuery = true)
    List<QuarterlyYieldProjection> findTotalYieldByProjectionIdForQuarterly(@Param("projectionId") String projectionId);


    @Modifying
    @Transactional
    @Query(value = "DROP TABLE IF EXISTS date_range ", nativeQuery = true)
    void dropTemporaryTableDate_range();

    @Modifying
    @Transactional
    @Query(value = "DROP TABLE IF EXISTS all_years ", nativeQuery = true)
    void dropTemporaryTableAllYears();

    @Modifying
    @Transactional
    @Query(value = "DROP TABLE IF EXISTS quarter_calendar ", nativeQuery = true)
    void dropTemporaryTableQuarterCalendar();

    @Modifying
    @Transactional
    @Query(value = "DROP TABLE IF EXISTS all_months ", nativeQuery = true)
    void dropTemporaryTableAllMonths();

    @Modifying
    @Transactional
    @Query(value = "CREATE TEMPORARY TABLE IF NOT EXISTS all_months AS ( " +
            "  SELECT 1 AS month UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 " +
            "  UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8  " +
            "  UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 " +
            ")", nativeQuery = true)
    void createAllMonthsTemporaryTable();

    @Modifying
    @Transactional
    @Query(value = "CREATE TEMPORARY TABLE IF NOT EXISTS all_years AS (" +
            "  SELECT DISTINCT YEAR(day) AS year " +
            "  FROM monitor_reading_daily " +
            "  WHERE inverter_number IS NULL " +
            "    AND subscription_id_mongo = :projectionId )", nativeQuery = true)
    void createDBYearsTemporaryTable(@Param("projectionId") String projectionId);

    @Query("select new com.solar.api.tenant.mapper.billingCredits.BillingCreditsPMDTO(mrd.subscriptionIdMongo, " +
            "sum(coalesce(mrd.yieldValue, 0)), " +
            "cs.gardenSrc, " +
            "edsd.mpJson) " +
            "from MonitorReadingDaily mrd " +
            "join ExtDataStageDefinition edsd on edsd.subsId = mrd.subscriptionIdMongo " +
            "join CustomerSubscription  cs on cs.extSubsId = mrd.subscriptionIdMongo " +
            "where CONCAT(YEAR(mrd.day), '-', LPAD(MONTH(mrd.day), 2, '0')) in (:months) and cs.extSubsId in (:subsId) " +
            "group by mrd.subscriptionIdMongo, cs.gardenSrc, edsd.mpJson")
    List<BillingCreditsPMDTO> findMonthlySummedDataForBillingCredits(@Param("months") List<String> month, @Param("subsId") List<String> subsId);


    //    @Query(value = "select new com.solar.api.tenant.model.pvmonitor.MonitorReadingDayWise FROM mrd_day_wise mdw " +
//            "where mdw.inverter_number = :inverterNumber", nativeQuery = true)
    @Query(value = "SELECT SUM(mrd.yield_value) FROM monitor_reading_daily mrd WHERE mrd.subscription_id_mongo = :subMongo AND mrd.day LIKE %:day%", nativeQuery = true)
//    @Query(value = "SELECT SUM(mrd.yieldValue) FROM MonitorReadingDaily mrd WHERE mrd.subscriptionIdMongo = :subMongo AND mrd.day LIKE %:day%")
    Double getMonthlyYield(@Param("subMongo") String subMongo, @Param("day") String day);

    @Query(value = "select mrd.* from monitor_reading_daily mrd where mrd.subscription_id_mongo = :subscriptionId and mrd.inverter_number = :inverterNumber" +
            " and STR_TO_DATE(mrd.day, '%Y-%m-%d') IN (:days) ", nativeQuery = true)
    List<MonitorReadingDaily> findBySubsIdAndInverterNoAndTimeIn(String subscriptionId, String inverterNumber, Set<String> days);

    @Modifying
    @Query("DELETE FROM MonitorReadingDaily mrd " +
            "WHERE mrd.day BETWEEN :startDate AND :endDate " +
            "AND mrd.subscriptionIdMongo =:subscriptionId ")
    void deleteRecordsInRangeAndCondition(@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,
                                          @Param("subscriptionId") String subscriptionId);
}
