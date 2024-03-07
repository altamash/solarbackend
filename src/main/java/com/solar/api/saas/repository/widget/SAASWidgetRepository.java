package com.solar.api.saas.repository.widget;

import com.solar.api.saas.model.widget.DashboardWidget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SAASWidgetRepository extends JpaRepository<DashboardWidget, Long> {
}
