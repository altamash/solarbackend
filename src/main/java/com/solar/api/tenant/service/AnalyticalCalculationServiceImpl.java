package com.solar.api.tenant.service;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.AnalyticalCalculationMapper;
import com.solar.api.tenant.model.AnalyticalCalculation;
import com.solar.api.tenant.model.AnalyticalCalculationArchive;
import com.solar.api.tenant.repository.AnalyticalCalculationArchiveRepository;
import com.solar.api.tenant.repository.AnalyticalCalculationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticalCalculationServiceImpl implements AnalyticalCalculationService {

    @Autowired
    AnalyticalCalculationRepository analyticalCalculationRepository;

    @Autowired
    AnalyticalCalculationArchiveRepository analyticalCalculationArchiveRepository;

    @Override
    public AnalyticalCalculation saveOrUpdate(AnalyticalCalculation analyticalCalculation) {

        if (analyticalCalculation.getId() != null) {
            AnalyticalCalculation analyticalCalculationData =
                    analyticalCalculationRepository.findById(analyticalCalculation.getId()).orElseThrow(() -> new NotFoundException(AnalyticalCalculation.class, analyticalCalculation.getId()));
            if (analyticalCalculationData == null) {
                throw new NotFoundException(AnalyticalCalculation.class, analyticalCalculation.getId());
            }
            AnalyticalCalculation analyticalCalculationUpdate = AnalyticalCalculationMapper.toUpdatedAnalyticalCalculation(analyticalCalculationData, analyticalCalculation);
            return analyticalCalculationRepository.save(analyticalCalculationUpdate);
        }
        return analyticalCalculationRepository.save(analyticalCalculation);
    }

    @Override
    public List<AnalyticalCalculationArchive> saveAllArchives(List<AnalyticalCalculationArchive> analyticalCalculationArchives) {
        return analyticalCalculationArchiveRepository.saveAll(analyticalCalculationArchives);
    }

    @Override
    public List<AnalyticalCalculation> saveAll(List<AnalyticalCalculation> analyticalCalculation) {
        return analyticalCalculationRepository.saveAll(analyticalCalculation);
    }

    @Override
    public AnalyticalCalculation findById(Long id) {
        return analyticalCalculationRepository.getOne(id);
    }

    @Override
    public List<AnalyticalCalculation> findCurrentValues() {
        return analyticalCalculationRepository.findCurrentValues();
    }

    @Override
    public AnalyticalCalculation findByAccountIdAndSubscriptionId(Long accountId, Long subscriptionId) {
        return analyticalCalculationRepository.findByAccountIdAndSubscriptionId(accountId, subscriptionId);
    }

    @Override
    public AnalyticalCalculation findByAccountIdAndSubscriptionIdAndAnalysis(Long accountId,Long subscriptionId, String analysis) {
        return analyticalCalculationRepository.findByAccountIdAndSubscriptionIdAndAnalysis(accountId, subscriptionId, analysis);
    }

    @Override
    public List<AnalyticalCalculation> findAll() {
        return analyticalCalculationRepository.findAll();
    }

    @Override
    public void archiveAll() {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public void deleteAll() {
        analyticalCalculationRepository.deleteAll();
    }
}
