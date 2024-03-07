package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.pvmonitor.MonitorReading;
import org.springframework.data.jpa.repository.Query;

public interface MonitorReadingRepositoryCustom {

    //@Query("select mr from MonitorReading mr where userId = :userId order by id desc")
    @Query("select mr from MonitorReading mr where mr.id = (select max(id) from MonitorReading where subscriptionId = :subscriptionId)")
    MonitorReading getLastRecord(Long subscriptionId);
}
