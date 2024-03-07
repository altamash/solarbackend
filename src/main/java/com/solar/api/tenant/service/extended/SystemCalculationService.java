package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.SystemCalculation;

import java.util.List;

public interface SystemCalculationService {

    SystemCalculation save(SystemCalculation systemCalculation);

    SystemCalculation update(SystemCalculation systemCalculation);

    SystemCalculation findById(Long id);

    List<SystemCalculation> findAll();

    void delete(Long id);

    void deleteAll();
}
