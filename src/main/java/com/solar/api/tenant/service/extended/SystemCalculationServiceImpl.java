package com.solar.api.tenant.service.extended;

import com.solar.api.tenant.model.extended.SystemCalculation;
import com.solar.api.tenant.repository.SystemCalculationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//@Transactional("tenantTransactionManager")
public class SystemCalculationServiceImpl implements SystemCalculationService {

    @Autowired
    private SystemCalculationRepository systemCalculationRepository;

    @Override
    public SystemCalculation save(SystemCalculation systemCalculation) {
        return systemCalculationRepository.save(systemCalculation);
    }

    @Override
    public SystemCalculation update(SystemCalculation systemCalculation) {
        return systemCalculationRepository.save(systemCalculation);
    }

    @Override
    public SystemCalculation findById(Long id) {
        return systemCalculationRepository.getOne(id);
    }

    @Override
    public List<SystemCalculation> findAll() {
        return systemCalculationRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        systemCalculationRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        systemCalculationRepository.deleteAll();
    }
}
