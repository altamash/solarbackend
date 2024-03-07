package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.tenant.model.extended.assetHead.AssetHead;

import java.util.List;

public interface AssetHeadService {

    AssetHead save(AssetHead assetHead);

    AssetHead update(AssetHead assetHead);

    AssetHead findById(Long id);

    List<AssetHead> findAll();

    List<AssetHead> findAllByRegisterId(Long registerHeadId);

    void delete(Long id);

    void deleteAll();
}
