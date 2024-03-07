package com.solar.api.tenant.service;


import com.solar.api.tenant.model.AnalyticalCalculation;
import com.solar.api.tenant.model.AnalyticalCalculationArchive;

import java.util.List;

public interface AnalyticalCalculationService {

    AnalyticalCalculation saveOrUpdate(AnalyticalCalculation analyticalCalculation);

    List<AnalyticalCalculationArchive> saveAllArchives(List<AnalyticalCalculationArchive> analyticalCalculationArchives);

    List<AnalyticalCalculation> saveAll(List<AnalyticalCalculation> analyticalCalculation);

    AnalyticalCalculation findById(Long id);

    List<AnalyticalCalculation> findCurrentValues();

    AnalyticalCalculation findByAccountIdAndSubscriptionId(Long accountId,Long subscriptionId);

    AnalyticalCalculation findByAccountIdAndSubscriptionIdAndAnalysis(Long accountId,Long subscriptionId, String analysis);

    List<AnalyticalCalculation> findAll();

    void archiveAll();

    void delete(Long id);

    void deleteAll();
}
