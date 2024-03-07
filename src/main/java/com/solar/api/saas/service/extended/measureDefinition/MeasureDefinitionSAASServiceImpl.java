package com.solar.api.saas.service.extended.measureDefinition;

import com.solar.api.exception.NotFoundException;
import com.solar.api.saas.model.extended.MeasureDefinitionSAAS;
import com.solar.api.saas.repository.MeasureDefinitionRepository;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplate;
import com.solar.api.tenant.repository.RegisterDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
//@Transactional("tenantTransactionManager")
public class MeasureDefinitionSAASServiceImpl implements MeasureDefinitionSAASService {

    @Autowired
    private MeasureDefinitionRepository repository;
    /*@Autowired
    private PortalAttributeService portalAttributeService;*/
    @Autowired
    private RegisterDetailRepository registerDetailRepository;

    @Override
    public MeasureDefinitionSAAS save(MeasureDefinitionSAAS measureDefinitionSAAS) {
        return repository.save(measureDefinitionSAAS);
    }

    @Override
    public MeasureDefinitionSAAS update(MeasureDefinitionSAAS measureDefinitionSAAS) {
        return repository.save(measureDefinitionSAAS);
    }

    @Override
    public MeasureDefinitionSAAS findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(MeasureDefinitionSAAS.class, id));
    }

    @Override
    public MeasureDefinitionSAAS findByIdOrderByIdAsc(Long id) {
        return repository.findByIdOrderByIdAsc(id);
    }

    @Override
    public List<MeasureDefinitionSAAS> findAll() {
        return repository.findAll();
    }

    @Override
    public List<MeasureDefinitionSAAS> findAllIdsNotIn(List<Long> ids) {
        return repository.findAllIdsNotIn(ids);
    }

    @Override
    public MeasureDefinitionSAAS findMeasureDefinitionByCode(String code) {
        /*List<MeasureDefinition> measureDefinitions = repository.findByCode(code);
        measureDefinitions.forEach(measureDefinition -> {
            if (measureDefinition.getAttributeIdRef() != null) {
                measureDefinition.setPortalAttributeValues(portalAttributeService.findByPortalAttributeName
                (measureDefinition.getAttributeIdRef()));
            }
        });*/
        return repository.findByCode(code);
    }

    @Override
    public List<MeasureDefinitionSAAS> findByCodes(Set<String> codes) {
        return repository.findByCodes(codes);
    }

    @Override
    public List<MeasureDefinitionSAAS> findByIds(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    @Override
    public List<MeasureDefinitionSAAS> findByRegModuleId(Long regModuleId) {
        return repository.findByRegModuleId(regModuleId);
    }

    @Override
    public String checkMeasureLinkWithRegister(Long measureCodeId) {
        Long count = registerDetailRepository.getCountByMeasureCodeId(measureCodeId);
        String response = "This measure has been marked for deletion.";

        if (count!=0) {
            return response = "This measure is associated with Register.";
        }
        return response;
    }

    @Override
    public MeasureDefinitionTemplate getAllHeaderAndFormat(List<Long> measureIds) {
        return repository.getAllHeaderAndFormat(measureIds);
    }

    public List<MeasureDefinitionSAAS> findByRegModuleIdMeasuresNotIn(Long regModuleId, List<String> measures){
        return repository.findByRegModuleIdMeasuresNotIn(regModuleId, measures);
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
