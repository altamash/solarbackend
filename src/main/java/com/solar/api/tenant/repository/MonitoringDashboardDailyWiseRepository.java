package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.MonitorReadingDayWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingMonthWise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoringDashboardDailyWiseRepository extends JpaRepository<MonitorReadingDayWise, Long> {
    @Query(value = "SELECT MRDY FROM MonitorReadingDayWise MRDY " +
            "where " +
            "MRDY.inverterNumber = :inverter_number " +
            "and MRDY.subscriptionIdMongo = :subscriptionId " +
            "and SUBSTRING(MRDY.day, 1,4) = :year " +  // 1 - start index , 4 length
            "and SUBSTRING(MRDY.day, 6,2) = :month " +  // 6 - start index , 2 length
            "order by MRDY.day asc")
    List<MonitorReadingDayWise> findByInverterNumberAndYearAndSubId(
            @Param("year") String year,
            @Param("month") String month,
            @Param("inverter_number") String inverter_number,
            @Param("subscriptionId") String subscriptionId);


    @Query(value = "SELECT MRDD FROM MonitorReadingDayWise MRDD " +
            "where " +
            "MRDD.subscriptionIdMongo in(:subscriptionIds) " +
            "and MRDD.day = :currentYearMonthDay " +
            "order by MRDD.day asc")
    List<MonitorReadingDayWise> findByYearMonthDayAndSubIds(
            @Param("currentYearMonthDay") String currentYearMonthDay,
            @Param("subscriptionIds") List<String> subscriptionIds);

}
