package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.MonitorReadingWeekWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoringDashboardWeekWiseRepository extends JpaRepository<MonitorReadingWeekWise, Long> {
    @Query(value = "SELECT MRDY FROM MonitorReadingWeekWise MRDY " +
            "where " +
            "MRDY.inverterNumber = :inverter_number " +
            "and MRDY.subscriptionIdMongo = :subscriptionId " +
            "and SUBSTRING(MRDY.day, 3,2) = :month " +  //3 = start index and 2 length
            "and SUBSTRING(MRDY.day, 6) = :year " +  // 6 start index and length till end of string
            "order by MRDY.day asc")
    List<MonitorReadingWeekWise> findByInverterNumberAndYearAndSubId(
            @Param("year") String year,
            @Param("month") String month,
            @Param("inverter_number") String inverter_number,
            @Param("subscriptionId") String subscriptionId);

}
