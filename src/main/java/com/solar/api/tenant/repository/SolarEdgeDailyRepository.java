package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.SolarEdgeDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SolarEdgeDailyRepository extends JpaRepository<SolarEdgeDaily, Long> {

    @Query("select sed.grossYield from SolarEdgeDaily sed where sed.site=:siteId and sed.subscriptionId=:subId " +
            "and sed.inverterNumber=:invNumber  and sed.day= date(:dateTime)")
    Double getLastGrossYield(String siteId, String invNumber, String subId, String dateTime);

    @Query("select sed from SolarEdgeDaily sed where sed.site=:siteId and sed.subscriptionId=:subId " +
            "and sed.inverterNumber=:invNumber  and sed.day= date(:dateTime)")
    SolarEdgeDaily findDailyRecord(String siteId, String invNumber, String subId, String dateTime);
}
