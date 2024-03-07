package com.solar.api.saas.repository;

import com.solar.api.saas.model.widget.Widget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WidgetRepository extends JpaRepository<Widget, Long> {
}
