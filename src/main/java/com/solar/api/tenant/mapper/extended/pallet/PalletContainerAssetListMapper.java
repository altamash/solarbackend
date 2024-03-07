package com.solar.api.tenant.mapper.extended.pallet;

import com.solar.api.tenant.model.extended.pallet.PalletContainerAssetList;

import java.util.List;
import java.util.stream.Collectors;

public class PalletContainerAssetListMapper {

    public static PalletContainerAssetList toPalletContainerAssetList(PalletContainerAssetListDTO palletContainerAssetListDTO) {
        if (palletContainerAssetListDTO == null) {
            return null;
        }
        return PalletContainerAssetList.builder()
                .id(palletContainerAssetListDTO.getId())
                .palletId(palletContainerAssetListDTO.getPalletId())
                .assetId(palletContainerAssetListDTO.getAssetId())
                .serialNo(palletContainerAssetListDTO.getSerialNo())
                .inventoryBarcodeUsed(palletContainerAssetListDTO.getInventoryBarcodeUsed())
                .status(palletContainerAssetListDTO.getStatus())
                .quantity(palletContainerAssetListDTO.getQuantity())
                .unitNetWeight(palletContainerAssetListDTO.getUnitNetWeight())
                .boxDepth(palletContainerAssetListDTO.getBoxDepth())
                .boxHeight(palletContainerAssetListDTO.getBoxHeight())
                .boxLength(palletContainerAssetListDTO.getBoxLength())
                .boxInd(palletContainerAssetListDTO.getBoxInd())
                .boxRefId(palletContainerAssetListDTO.getBoxRefId())
                .build();
    }

    public static PalletContainerAssetListDTO toPalletContainerAssetListDTO(PalletContainerAssetList palletContainerAssetList) {
        if (palletContainerAssetList == null) {
            return null;
        }
        return PalletContainerAssetListDTO.builder()
                .id(palletContainerAssetList.getId())
                .palletId(palletContainerAssetList.getPalletId())
                .assetId(palletContainerAssetList.getAssetId())
                .serialNo(palletContainerAssetList.getSerialNo())
                .inventoryBarcodeUsed(palletContainerAssetList.getInventoryBarcodeUsed())
                .status(palletContainerAssetList.getStatus())
                .quantity(palletContainerAssetList.getQuantity())
                .unitNetWeight(palletContainerAssetList.getUnitNetWeight())
                .boxDepth(palletContainerAssetList.getBoxDepth())
                .boxHeight(palletContainerAssetList.getBoxHeight())
                .boxLength(palletContainerAssetList.getBoxLength())
                .boxInd(palletContainerAssetList.getBoxInd())
                .boxRefId(palletContainerAssetList.getBoxRefId())
                .build();
    }

    public static PalletContainerAssetList toUpdatedPalletContainerAssetList(PalletContainerAssetList palletContainerAssetList, PalletContainerAssetList palletContainerAssetListUpdate) {
        palletContainerAssetList.setPalletId(palletContainerAssetListUpdate.getPalletId() == null ? palletContainerAssetList.getAssetId() : palletContainerAssetListUpdate.getPalletId());
        palletContainerAssetList.setAssetId(palletContainerAssetListUpdate.getAssetId() == null ? palletContainerAssetList.getPalletId() : palletContainerAssetListUpdate.getAssetId());
        palletContainerAssetList.setSerialNo(palletContainerAssetListUpdate.getSerialNo() == null ? palletContainerAssetList.getSerialNo() : palletContainerAssetListUpdate.getSerialNo());
        palletContainerAssetList.setQuantity(palletContainerAssetListUpdate.getQuantity() == null ? palletContainerAssetList.getQuantity() : palletContainerAssetListUpdate.getQuantity());
        palletContainerAssetList.setUnitNetWeight(palletContainerAssetListUpdate.getUnitNetWeight() == null ? palletContainerAssetList.getUnitNetWeight() : palletContainerAssetListUpdate.getUnitNetWeight());
        palletContainerAssetList.setBoxDepth(palletContainerAssetListUpdate.getBoxDepth() == null ? palletContainerAssetList.getBoxDepth() : palletContainerAssetListUpdate.getBoxDepth());
        palletContainerAssetList.setBoxHeight(palletContainerAssetListUpdate.getBoxHeight() == null ? palletContainerAssetList.getBoxHeight() : palletContainerAssetListUpdate.getBoxHeight());
        palletContainerAssetList.setBoxInd(palletContainerAssetListUpdate.getBoxInd() == null ? palletContainerAssetList.getBoxInd() : palletContainerAssetListUpdate.getBoxInd());
        palletContainerAssetList.setBoxLength(palletContainerAssetListUpdate.getBoxLength() == null ? palletContainerAssetList.getBoxLength() : palletContainerAssetListUpdate.getBoxLength());
        palletContainerAssetList.setBoxRefId(palletContainerAssetListUpdate.getBoxRefId() == null ? palletContainerAssetList.getBoxRefId() : palletContainerAssetListUpdate.getBoxRefId());
        palletContainerAssetList.setInventoryBarcodeUsed(palletContainerAssetListUpdate.getInventoryBarcodeUsed() == null ? palletContainerAssetList.getInventoryBarcodeUsed() : palletContainerAssetListUpdate.getInventoryBarcodeUsed());
        palletContainerAssetList.setStatus(palletContainerAssetListUpdate.getStatus() == null ? palletContainerAssetList.getStatus() : palletContainerAssetListUpdate.getStatus());
        return palletContainerAssetList;
    }

    public static List<PalletContainerAssetList> toPalletContainerAssetLists(List<PalletContainerAssetListDTO> palletContainerAssetListDTOS) {
        return palletContainerAssetListDTOS.stream().map(a -> toPalletContainerAssetList(a)).collect(Collectors.toList());
    }

    public static List<PalletContainerAssetListDTO> toPalletContainerAssetListDTOs(List<PalletContainerAssetList> palletContainerAssetLists) {
        return palletContainerAssetLists.stream().map(a -> toPalletContainerAssetListDTO(a)).collect(Collectors.toList());
    }
}
