package com.solar.api.saas.repository.widget;

import com.solar.api.saas.model.widget.DashboardWidgetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SAASWidgetTypeRepository extends JpaRepository<DashboardWidgetType, Long> {
}
