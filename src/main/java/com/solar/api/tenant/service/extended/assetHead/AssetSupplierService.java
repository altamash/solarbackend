package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.tenant.model.extended.assetHead.AssetSupplier;

import java.util.List;

public interface AssetSupplierService {

    AssetSupplier save(AssetSupplier assetSupplier);

    AssetSupplier update(AssetSupplier assetSupplier);

    AssetSupplier findById(Long id);

    List<AssetSupplier> findAll();

    void delete(Long id);

    void deleteAll();
}
