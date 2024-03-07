package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoringDashboardYearWiseRepository extends JpaRepository<MonitorReadingYearWise, Long> {
    @Query(value = "SELECT MRDY FROM MonitorReadingYearWise MRDY " +
            "where " +
            "MRDY.inverterNumber = :inverter_number " +
            "and MRDY.subscriptionIdMongo = :subscriptionId " +
            "and MRDY.day  BETWEEN :yearStart AND :yearEnd " +
            "order by MRDY.day asc")
    List<MonitorReadingYearWise> findByInverterNumberAndYearAndSubId(
            @Param("yearStart") String yearStart,
            @Param("yearEnd") String yearEnd,
            @Param("inverter_number") String inverter_number,
            @Param("subscriptionId") String subscriptionId);


    @Query(value = "SELECT MRDY FROM MonitorReadingYearWise MRDY " +
            "where " +
            "MRDY.subscriptionIdMongo in(:subscriptionIds) " +
            "and MRDY.day = :currentYear " +
            "order by MRDY.day asc")
    List<MonitorReadingYearWise> findByYearAndSubIds(
            @Param("currentYear") String currentYear,
            @Param("subscriptionIds") List<String> subscriptionIds);


    @Query(value = "SELECT MRDY FROM MonitorReadingYearWise MRDY " +
            "where " +
            "MRDY.subscriptionIdMongo in(:subscriptionIds) " +
            "and MRDY.day BETWEEN :startYear  and :endYear " +
            "order by MRDY.day asc")
    List<MonitorReadingYearWise> findByStartAndEndYearAndSubIds(
            @Param("startYear") String startYear,
            @Param("endYear") String endYear,
            @Param("subscriptionIds") List<String> subscriptionIds);


    @Query(value = "SELECT new com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise( sum(MRDY.yield),MRDY.day ) " +
            "FROM MonitorReadingYearWise MRDY " +
            "where " +
            "MRDY.subscriptionIdMongo in(:subscriptionIds) " +
            "and MRDY.day BETWEEN :startYear  and :endYear " +
            "group by MRDY.day " +
            "order by MRDY.day asc")
    List<MonitorReadingYearWise> findYieldSumByStartAndEndYearAndSubIds(
            @Param("startYear") String startYear,
            @Param("endYear") String endYear,
            @Param("subscriptionIds") List<String> subscriptionIds);
}
