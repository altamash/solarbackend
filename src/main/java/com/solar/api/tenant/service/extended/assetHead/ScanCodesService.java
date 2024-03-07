package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.tenant.model.extended.assetHead.ScanCodes;

import java.util.List;

public interface ScanCodesService {

    ScanCodes save(ScanCodes scanCodes);

    ScanCodes update(ScanCodes scanCodes);

    ScanCodes findById(Long id);

    List<ScanCodes> findAll();

    void delete(Long id);

    void deleteAll();
}
