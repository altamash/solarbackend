package com.solar.api.saas.repository;

import com.solar.api.saas.model.widget.chart.ChartDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartDetailRepository extends JpaRepository<ChartDetail, Long> {
}
