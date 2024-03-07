package com.solar.api.tenant.mapper.extended.assetHead;

import com.solar.api.tenant.model.extended.assetHead.AssetBlockDetail;

import java.util.List;
import java.util.stream.Collectors;

public class AssetBlockDetailMapper {

    // AssetBlockDetail ////////////////////////////////////////////////
    public static AssetBlockDetail toAssetBlockDetail(AssetBlockDetailDTO assetBlockDetailDTO) {
        if (assetBlockDetailDTO == null) {
            return null;
        }
        return AssetBlockDetail.builder()
                .id(assetBlockDetailDTO.getId())
                .assetId(assetBlockDetailDTO.getAssetId())
                .measureId(assetBlockDetailDTO.getMeasureId())
                .measureValue(assetBlockDetailDTO.getMeasureValue())
                .recordNumber(assetBlockDetailDTO.getRecordNumber())
                .refBlockId(assetBlockDetailDTO.getRefBlockId())
                .measureUnique(assetBlockDetailDTO.getMeasureUnique())
                .assetRefId(assetBlockDetailDTO.getAssetRefId())
                .palletNo(assetBlockDetailDTO.getPalletNo())
                .createdAt(assetBlockDetailDTO.getCreatedAt())
                .updatedAt(assetBlockDetailDTO.getUpdatedAt())
                .build();
    }

    public static AssetBlockDetailDTO toAssetBlockDetailDTO(AssetBlockDetail assetBlockDetail) {
        if (assetBlockDetail == null) {
            return null;
        }
        return AssetBlockDetailDTO.builder()
                .id(assetBlockDetail.getId())
                .assetId(assetBlockDetail.getAssetId())
                .measureId(assetBlockDetail.getMeasureId())
                .measureValue(assetBlockDetail.getMeasureValue())
                .recordNumber(assetBlockDetail.getRecordNumber())
                .refBlockId(assetBlockDetail.getRefBlockId())
                .measureUnique(assetBlockDetail.getMeasureUnique())
                .assetRefId(assetBlockDetail.getAssetRefId())
                .palletNo(assetBlockDetail.getPalletNo())
                .createdAt(assetBlockDetail.getCreatedAt())
                .updatedAt(assetBlockDetail.getUpdatedAt())
                .build();
    }

    public static AssetBlockDetail toUpdatedAssetBlockDetail(AssetBlockDetail assetBlockDetail, AssetBlockDetail assetBlockDetailUpdate) {
        assetBlockDetail.setId(assetBlockDetailUpdate.getId() == null ? assetBlockDetail.getId() :
                assetBlockDetailUpdate.getId());
        assetBlockDetail.setAssetId(assetBlockDetailUpdate.getAssetId() == null ? assetBlockDetail.getAssetId() :
                assetBlockDetailUpdate.getAssetId());
        assetBlockDetail.setRefBlockId(assetBlockDetailUpdate.getRefBlockId() == null ? assetBlockDetail.getRefBlockId() :
                assetBlockDetailUpdate.getRefBlockId());
        assetBlockDetail.setMeasureValue(assetBlockDetailUpdate.getMeasureValue() == null ? assetBlockDetail.getMeasureValue() :
                assetBlockDetailUpdate.getMeasureValue());
        assetBlockDetail.setRecordNumber(assetBlockDetailUpdate.getRecordNumber() == null ? assetBlockDetail.getRecordNumber() :
                assetBlockDetailUpdate.getRecordNumber());
        assetBlockDetail.setAssetRefId(assetBlockDetailUpdate.getAssetRefId() == null ? assetBlockDetail.getAssetRefId() :
                assetBlockDetailUpdate.getAssetRefId());
        assetBlockDetail.setUpdatedAt(assetBlockDetailUpdate.getUpdatedAt() == null ? assetBlockDetail.getUpdatedAt() :
                assetBlockDetailUpdate.getUpdatedAt());
        return assetBlockDetail;
    }

    public static List<AssetBlockDetail> toAssetBlockDetails(List<AssetBlockDetailDTO> assetBlockDetailDTOS) {
        return assetBlockDetailDTOS.stream().map(a -> toAssetBlockDetail(a)).collect(Collectors.toList());
    }

    public static List<AssetBlockDetailDTO> toAssetBlockDetailDTOs(List<AssetBlockDetail> assetBlockDetails) {
        return assetBlockDetails.stream().map(a -> toAssetBlockDetailDTO(a)).collect(Collectors.toList());
    }

}
