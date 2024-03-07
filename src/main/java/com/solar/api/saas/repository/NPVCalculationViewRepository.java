package com.solar.api.saas.repository;

import com.solar.api.saas.model.chart.views.NPVCalculationView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NPVCalculationViewRepository extends JpaRepository<NPVCalculationView, String>,
        NPVCalculationViewRepositoryCustom {
}
