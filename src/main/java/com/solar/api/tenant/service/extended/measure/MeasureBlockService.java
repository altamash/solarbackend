package com.solar.api.tenant.service.extended.measure;

import com.solar.api.tenant.mapper.extended.measure.MeasureDefinitionTenantDTO;
import com.solar.api.tenant.model.extended.resources.MeasureBlockHead;

import java.util.List;

public interface MeasureBlockService {

    MeasureBlockHead save(MeasureBlockHead measureBlockHead);

    MeasureBlockHead updateMeasureBlockHead (MeasureBlockHead measureBlockHead);

    MeasureBlockHead findById(Long id);

    List<MeasureBlockHead> findAllByRegModuleId(Long regModuleId);

    List<MeasureBlockHead> findAll();

    List<MeasureBlockHead> findAllByIdInOrderByIdAsc(List<Long> ids);

    String getBlockHeaderAndFormat(Long registerId,Long blockId);

    List<MeasureDefinitionTenantDTO> getSerialHeaderForCSV(Long assetId);

    void delete(Long id);

    void deleteAll();

}
