package com.solar.api.tenant.service;

import com.mchange.util.AlreadyExistsException;
import com.solar.api.tenant.model.subscription.subscriptionRateMatrix.CalculationGroup;

import java.util.List;

public interface CalculationGroupService {

    CalculationGroup addOrUpdate(CalculationGroup calculationGroup) throws AlreadyExistsException;

    CalculationGroup findById(Long id);

    List<CalculationGroup> findAll();

    void delete(Long id);

    void deleteAll();
}
