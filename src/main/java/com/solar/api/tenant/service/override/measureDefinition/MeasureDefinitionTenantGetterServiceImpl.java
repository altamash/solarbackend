package com.solar.api.tenant.service.override.measureDefinition;

import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplate;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;
import com.solar.api.tenant.repository.MeasureDefinitionTenantRepository;
import com.solar.api.tenant.repository.RegisterDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
//@Transactional("tenantTransactionManager")
class MeasureDefinitionTenantGetterServiceImpl implements MeasureDefinitionTenantGetterService {

    @Autowired
    private MeasureDefinitionTenantRepository repository;
    @Autowired
    private RegisterDetailRepository registerDetailRepository;

    @Override
    public MeasureDefinitionTenant findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public MeasureDefinitionTenant findByIdOrderByIdAsc(Long id) {
        return repository.findByIdOrderByIdAsc(id);
    }

    @Override
    public List<MeasureDefinitionTenant> findAll() {
        return repository.findAll();
    }

    @Override
    public MeasureDefinitionTenant findMeasureDefinitionByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public List<MeasureDefinitionTenant> findByCodes(Set<String> codes) {
        return repository.findByCodes(codes);
    }

    @Override
    public List<MeasureDefinitionTenant> findByIds(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    @Override
    public List<MeasureDefinitionTenant> findByRegModuleId(Long regModuleId) {
        return repository.findByRegModuleId(regModuleId);
    }

    @Override
    public String checkMeasureLinkWithRegister(Long measureCodeId) {
        Long count = registerDetailRepository.getCountByMeasureCodeId(measureCodeId);
        String response = "This measure has been marked for deletion.";

        if (count != 0) {
            return response = "This measure is associated with Register.";
        }
        return response;
    }

    @Override
    public MeasureDefinitionTemplate getAllHeaderAndFormat(List<Long> measureIds) {
        return repository.getAllHeaderAndFormat(measureIds);
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }
}
