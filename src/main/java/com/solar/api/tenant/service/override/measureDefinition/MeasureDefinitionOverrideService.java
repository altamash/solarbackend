package com.solar.api.tenant.service.override.measureDefinition;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTemplateDTO;

import java.util.List;
import java.util.Set;

public interface MeasureDefinitionOverrideService {

    MeasureDefinitionTenantDTO findById(Long id);

    MeasureDefinitionTenantDTO findByIdOrderByIdAsc(Long id);

    List<MeasureDefinitionTenantDTO> findAll();

    MeasureDefinitionTenantDTO findMeasureDefinitionByCode(String code);

    List<MeasureDefinitionTenantDTO> findByCodes(Set<String> codes);

    List<MeasureDefinitionTenantDTO> findByIds(List<Long> ids);

    List<MeasureDefinitionTenantDTO> findByRegModuleId(Long regModuleId);

    String checkMeasureLinkWithRegister(Long measureCodeId);

    MeasureDefinitionTemplateDTO getAllHeaderAndFormat(List<Long> measureIds);
}
