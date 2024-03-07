package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.tenant.model.extended.assetHead.AssetDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetHead;

import java.util.List;

public interface AssetDetailService {

    AssetDetail save(AssetDetail assetDetail);

    List<AssetDetail> update(List<AssetDetail> assetDetails, AssetHead assetHead);

    AssetDetail findById(Long id);

    List<AssetDetail> findAll();

    void delete(Long id);

    void deleteAll();

    void deleteAll(List<AssetDetail> assetDetails);

}
