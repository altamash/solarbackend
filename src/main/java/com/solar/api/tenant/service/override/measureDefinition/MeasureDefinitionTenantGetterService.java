package com.solar.api.tenant.service.override.measureDefinition;

import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplate;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;

import java.util.List;
import java.util.Set;

interface MeasureDefinitionTenantGetterService {

    MeasureDefinitionTenant findById(Long id);

    MeasureDefinitionTenant findByIdOrderByIdAsc(Long id);

    List<MeasureDefinitionTenant> findAll();

    MeasureDefinitionTenant findMeasureDefinitionByCode(String code);

    List<MeasureDefinitionTenant> findByCodes(Set<String> codes);

    List<MeasureDefinitionTenant> findByIds(List<Long> ids);

    List<MeasureDefinitionTenant> findByRegModuleId(Long regModuleId);

    String checkMeasureLinkWithRegister(Long measureCodeId);

    MeasureDefinitionTemplate getAllHeaderAndFormat(List<Long> measureIds);

    void deleteById(Long id);
}
