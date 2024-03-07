package com.solar.api.tenant.service.extended.assetHead;

import com.solar.api.tenant.mapper.extended.assetHead.PagedAssetBlockDetailDTO;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;

import java.util.List;

public interface AssetBlockDetailService {

    AssetBlockDetail save(AssetBlockDetail assetBlockDetail);

    List<AssetBlockDetail> saveAll(List<AssetBlockDetail> assetBlockDetails);

    List<AssetBlockDetail> saveAllFromCSVUpload(List<AssetBlockDetail> assetBlockDetails);

    List<AssetBlockDetail> update(List<AssetBlockDetail> assetBlockDetails);

    AssetBlockDetail findById(Long id);

    List<AssetBlockDetail> findAll();

    List<AssetBlockDetail> findAllByAssetId(Long assetId);

    List<AssetBlockDetail> findAllByAssetIdIn(List<Long> assetIds);

    List<AssetBlockDetail> findAllByAssetIdAndRefBlockIdIn(Long assetId, List<Long> refBlockIds);

    PagedAssetBlockDetailDTO getAllBlockValuesByAssetId(Long registerId, Long assetId, Long blockId, int pageNumber, Integer pageSize, String sort);

    String getSerialNumbersForCSVExport(Long assetId, Long projectId);

    String deleteAssetBlock(Long assetRefId);

    void delete(Long id);

    void deleteAll();

    void deleteAll(List<AssetBlockDetail> assetBlockDetails);
}
