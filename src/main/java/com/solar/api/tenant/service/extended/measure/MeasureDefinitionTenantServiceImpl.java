package com.solar.api.tenant.service.extended.measure;

import com.solar.api.saas.mapper.extended.measure.MeasureDefinitionSAASMapper;
import com.solar.api.saas.service.extended.measureDefinition.MeasureDefinitionSAASService;
import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantMapper;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;
import com.solar.api.tenant.repository.MeasureDefinitionTenantRepository;
import com.solar.api.tenant.repository.RegisterDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
//@Transactional("tenantTransactionManager")
public class MeasureDefinitionTenantServiceImpl implements MeasureDefinitionTenantService {

    @Autowired
    private MeasureDefinitionTenantRepository repository;
    @Autowired
    private RegisterDetailRepository registerDetailRepository;
    @Autowired
    private MeasureDefinitionSAASService measureDefinitionSAASService;

    @Override
    public MeasureDefinitionTenant save(MeasureDefinitionTenant measureDefinitionTenant) {
//        if (measureDefinitionTenant.getId() != null && measureDefinitionTenant.getId() <= AppConstants.SAAS_RESERVED_AUTO_INCREMENT) {
//            throw new BadRequestException("Tenant schema measure_definition id must be greater than " + AppConstants.SAAS_RESERVED_AUTO_INCREMENT);
//        }
        return repository.save(measureDefinitionTenant);
    }

    @Override
    public MeasureDefinitionTenant update(MeasureDefinitionTenant measureDefinitionTenant) {
        MeasureDefinitionTenant measureDefinitionTenantUpd = null;
        if (measureDefinitionTenant.getId() != null) {
            Optional<MeasureDefinitionTenant> measureDefinitionTenantDB = repository.findById(measureDefinitionTenant.getId());
            if (measureDefinitionTenantDB.isPresent()) {
                measureDefinitionTenantUpd = repository.save(measureDefinitionTenant);
            }else{
                measureDefinitionTenantUpd = MeasureDefinitionTenantMapper.toMeasureDefinition(measureDefinitionSAASService.update(MeasureDefinitionSAASMapper.toMeasureDefinitionSAAS(measureDefinitionTenant)));
            }
        }
        return measureDefinitionTenantUpd;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
