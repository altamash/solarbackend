package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.assetHead.AssetSerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetSerialNumberRepository extends JpaRepository<AssetSerialNumber, Long> {

    List<AssetSerialNumber> findAllByAssetId(Long assetId);

    AssetSerialNumber findBySerialNumber(String serialNumber);

    AssetSerialNumber findBySerialNumberAndPalletNo(String serialNumber, String palletNo);

    List<AssetSerialNumber> findAllByAssetIdAndPalletNo(Long assetId, String palletNo);

    List<AssetSerialNumber> findAllBySerialNumberIn(List<String> serialNumbers);

    @Query("SELECT DISTINCT(a.palletNo) FROM AssetSerialNumber a where a.assetId=:assetId")
    List<String> palletNumbersByAssetId(Long assetId);
}
