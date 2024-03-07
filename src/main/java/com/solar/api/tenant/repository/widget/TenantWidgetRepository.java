package com.solar.api.tenant.repository.widget;

import com.solar.api.tenant.model.widget.DashboardWidget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantWidgetRepository extends JpaRepository<DashboardWidget, Long> {
}
