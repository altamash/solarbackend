package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.MonitorReadingQuarterWise;
import com.solar.api.tenant.model.pvmonitor.MonitorReadingYearWise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonitoringDashboardQuarterWiseRepository extends JpaRepository<MonitorReadingQuarterWise, Long> {
    @Query(value = "SELECT MRDQ FROM MonitorReadingQuarterWise MRDQ " +
            "where " +
            "MRDQ.inverterNumber = :inverter_number " +
            "and MRDQ.subscriptionIdMongo = :subscriptionId " +
            "and SUBSTRING(MRDQ.day, 4) = :year " +
            "order by MRDQ.day asc")
    List<MonitorReadingQuarterWise> findByInverterNumberAndYearAndSubId(
            @Param("year") String year,
            @Param("inverter_number") String inverter_number,
            @Param("subscriptionId") String subscriptionId);

}
