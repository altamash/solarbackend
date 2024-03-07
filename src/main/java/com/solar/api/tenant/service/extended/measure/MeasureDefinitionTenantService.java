package com.solar.api.tenant.service.extended.measure;

import com.solar.api.tenant.model.extended.measure.MeasureDefinitionTenant;

public interface MeasureDefinitionTenantService {

    MeasureDefinitionTenant save(MeasureDefinitionTenant measureDefinitionTenant);

    MeasureDefinitionTenant update(MeasureDefinitionTenant measureDefinitionTenant);

    void delete(Long id);

    void deleteAll();
}
