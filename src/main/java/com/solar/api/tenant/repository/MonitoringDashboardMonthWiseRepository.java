package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.MonitorReadingMonthWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoringDashboardMonthWiseRepository extends JpaRepository<MonitorReadingMonthWise, Long> {
    @Query(value = "SELECT MRDM FROM MonitorReadingMonthWise MRDM " +
            "where " +
            "MRDM.inverterNumber = :inverter_number " +
            "and MRDM.subscriptionIdMongo = :subscriptionId " +
            "and SUBSTRING(MRDM.day, 4) = :year " +  // Extract year part and compare
            "order by MRDM.day asc")
    List<MonitorReadingMonthWise> findByInverterNumberAndYearAndSubId(
            @Param("year") String year,
            @Param("inverter_number") String inverter_number,
            @Param("subscriptionId") String subscriptionId);

    @Query(value = "SELECT MRDM FROM MonitorReadingMonthWise MRDM " +
            "where " +
            "MRDM.subscriptionIdMongo in(:subscriptionIds) " +
            "and MRDM.day = :currentMonthYear " +
            "order by MRDM.day asc")
    List<MonitorReadingMonthWise> findByMonthYearAndSubIds(
            @Param("currentMonthYear") String currentMonthYear,
            @Param("subscriptionIds") List<String> subscriptionIds);


    @Query(value =  "SELECT new com.solar.api.tenant.model.pvmonitor.MonitorReadingMonthWise( sum(MRDM.yield),MRDM.day ) " +
            "FROM MonitorReadingMonthWise MRDM " +
            "where " +
            "MRDM.subscriptionIdMongo in (:subscriptionIds) " +
            "and SUBSTRING(MRDM.day, 4) BETWEEN :startYear  and :endYear " + // Extract year part and compare
            "group by MRDM.day " +
            "order by MRDM.day asc")
    List<MonitorReadingMonthWise> findYieldSumByStartYearAndEndYearAndSubId(
            @Param("startYear") String startYear,
            @Param("endYear") String endYear,
            @Param("subscriptionIds") List<String> subscriptionId);

}
