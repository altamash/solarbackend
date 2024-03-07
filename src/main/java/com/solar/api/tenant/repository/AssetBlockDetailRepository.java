package com.solar.api.tenant.repository;

import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;
import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetBlockDetailRepository extends JpaRepository<AssetBlockDetail, Long> {

    List<AssetBlockDetail> findAllByAssetIdOrderByRecordNumberAscRefBlockIdAsc(Long assetId);

    @Query(value= "SELECT GROUP_CONCAT(measure_id ORDER BY measure_id ASC SEPARATOR ',') AS measureIds, " +
            " GROUP_CONCAT(measure_value ORDER BY measure_id ASC SEPARATOR ',') AS measureValues, " +
            " record_number as recordNumber, asset_ref_id as assetRefId" +
            " FROM asset_block_detail  where asset_id=:assetId and ref_block_id=:blockId " +
            " group by record_number, asset_ref_id " +
            " order by record_number asc ",
            nativeQuery = true)
    List<AssetBlockDetailTemplate> getAllBlockValuesByAssetId(Long assetId, Long blockId);

    List<AssetBlockDetail> findAllByAssetIdAndRefBlockIdIn(Long assetId, List<Long> refBlockIds);

    List<AssetBlockDetail> findAllByAssetIdIn(List<Long> assetIds);

    @Query(value="SELECT COALESCE(MAX(CONVERT(record_number,UNSIGNED INTEGER)),0) FROM asset_block_detail where asset_id=:assetId", nativeQuery = true)
    long getMaxRecordNumber(Long assetId);

    Long countByAssetIdAndRefBlockIdAndMeasureIdAndRecordNumberNotAndMeasureValue(Long assetId, Long blockId, Long measureId,Long recordNumber, String measureValue);

    Long countByAssetIdAndRefBlockIdAndMeasureIdAndMeasureValue(Long assetId, Long blockId, Long measureId, String measureValue);

    AssetBlockDetail findByAssetIdAndRefBlockIdAndMeasureIdAndRecordNumber(Long assetId, Long blockId, Long measureId,Long recordNumber);

    List<AssetBlockDetail> findAllByAssetIdAndRefBlockIdAndRecordNumber(Long assetId, Long blockId, Long recordNumber);

    List<AssetBlockDetail> findAllByAssetRefId(Long assetRefId);

    @Query(value= "select a.id as assetRefId, a.pallet_no as palletNo, " +
            "GROUP_CONCAT(b.measure_id ORDER BY b.measure_id ASC SEPARATOR ',') AS measureIds, " +
            "GROUP_CONCAT(b.measure_value ORDER BY b.measure_id ASC SEPARATOR ',') AS measureValues ," +
            "b.record_number as recordNumber " +
            "FROM asset_serial_number a " +
            "inner join asset_block_detail b on a.id = b.asset_ref_id " +
            "where b.asset_ref_id in (" +
                "select asset_serial_number_id FROM project_inventories_serial where project_inventory_id = (" +
                    "select id FROM project_inventories where asset_id=:assetId and project_id=:projectId ) " +
            ")" +
            "group by a.id,a.pallet_no,b.record_number " +
            "order by b.record_number asc ",
            nativeQuery = true)
    List<AssetBlockDetailTemplate> getAllAssignedSerialToProject(Long assetId, Long projectId);

}
