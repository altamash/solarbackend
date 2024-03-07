package com.solar.api.saas.service.extended.measureDefinition;

import com.solar.api.saas.model.extended.MeasureDefinitionSAAS;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplate;

import java.util.List;
import java.util.Set;

public interface MeasureDefinitionSAASService {

    MeasureDefinitionSAAS save(MeasureDefinitionSAAS measureDefinitionSAAS);

    MeasureDefinitionSAAS update(MeasureDefinitionSAAS measureDefinitionSAAS);

    MeasureDefinitionSAAS findById(Long id);

    MeasureDefinitionSAAS findByIdOrderByIdAsc(Long id);

    List<MeasureDefinitionSAAS> findAll();

    List<MeasureDefinitionSAAS> findAllIdsNotIn(List<Long> ids);

    MeasureDefinitionSAAS findMeasureDefinitionByCode(String code);

    List<MeasureDefinitionSAAS> findByCodes(Set<String> codes);

    List<MeasureDefinitionSAAS> findByIds(List<Long> ids);

    List<MeasureDefinitionSAAS> findByRegModuleId(Long regModuleId);

    String checkMeasureLinkWithRegister(Long measureCodeId);

    MeasureDefinitionTemplate getAllHeaderAndFormat(List<Long> measureIds);

    List<MeasureDefinitionSAAS> findByRegModuleIdMeasuresNotIn(Long regModuleId, List<String> measures);

    void delete(Long id);

    void deleteAll();
}
