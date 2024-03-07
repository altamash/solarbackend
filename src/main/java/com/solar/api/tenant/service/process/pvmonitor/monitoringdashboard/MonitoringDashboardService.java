package com.solar.api.tenant.service.process.pvmonitor.monitoringdashboard;

import com.solar.api.tenant.mapper.pvmonitor.*;
import org.springframework.http.ResponseEntity;

public interface MonitoringDashboardService {

    ResponseEntity<?> getCumulativeGraphData(MonitorAPIAuthBody body );
    ResponseEntity<?> getSubscriptionComparativeGraphData(MonitorAPIAuthBody body );

    ResponseEntity<?> getCustomerComparativeGraphData(MonitorAPIAuthBody body );

    ResponseEntity<?> getSitesComparativeGraphData(MonitorAPIAuthBody body);
}
