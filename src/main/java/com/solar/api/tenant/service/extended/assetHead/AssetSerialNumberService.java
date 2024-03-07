package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.tenant.mapper.extended.assetHead.PagedAssetSerialNumberDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;

import java.util.List;

public interface AssetSerialNumberService {

    AssetSerialNumber save(AssetSerialNumber assetSerialNumber);

    AssetSerialNumber update(AssetSerialNumber assetSerialNumber);

    AssetSerialNumber findById(Long id);

    List<AssetSerialNumber> findAll();

    List<AssetSerialNumber> findAllByAssetId(Long assetId);

    List<AssetSerialNumber> findFilteredSerialByAssetId(Long assetId);

    PagedAssetSerialNumberDTO findPagedFilteredSerialByAssetId(Long assetId, int pageNumber, Integer pageSize, String sort);

    PagedAssetSerialNumberDTO searchSerialNoByPalletNoAndAssetId(Long assetId,String palletNo, int pageNumber, Integer pageSize, String sort);

    List<String> distinctPalletNumbersByAssetId (Long assetId);

    void delete(Long id);

    void deleteAll();
}
