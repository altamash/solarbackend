package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.SystemCalculation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemCalculationRepository extends JpaRepository<SystemCalculation, Long> {
}
