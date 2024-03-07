package com.solar.api.tenant.mapper.extended.assetHead;

import com.solar.api.tenant.mapper.extended.register.RegisterMapper;
import com.solar.api.tenant.model.extended.assetHead.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AssetMapper {

    // AssetHead ///////////////////////////////////////////////
    public static AssetHead toAssetHead(AssetHeadDTO assetHeadDTO) {
        if (assetHeadDTO == null) {
            return null;
        }
        return AssetHead.builder()
                .id(assetHeadDTO.getId())
                .assetName(assetHeadDTO.getAssetName())
                .registerId(assetHeadDTO.getRegisterId())
                .description(assetHeadDTO.getDescription())
                .regDate(assetHeadDTO.getRegDate())
                .activeDate(assetHeadDTO.getActiveDate())
                .status(assetHeadDTO.getStatus())
                .recordLevelInd(assetHeadDTO.getRecordLevelInd())
                .serialized(assetHeadDTO.getSerialized())
                .assetDetails(assetHeadDTO.getAssetDetails() != null ?
                        toAssetDetails(assetHeadDTO.getAssetDetails()) : Collections.emptyList())
                .build();
    }

    public static AssetHeadDTO toAssetHeadDTO(AssetHead assetHead) {
        if (assetHead == null) {
            return null;
        }
        return AssetHeadDTO.builder()
                .id(assetHead.getId())
                .assetName(assetHead.getAssetName())
                .registerId(assetHead.getRegisterId())
                .description(assetHead.getDescription())
                .regDate(assetHead.getRegDate())
                .activeDate(assetHead.getActiveDate())
                .status(assetHead.getStatus())
                .recordLevelInd(assetHead.getRecordLevelInd())
                .serialized(assetHead.getSerialized())
                .blocks(assetHead.getBlocks())
                .registerHierarchyDTO(assetHead.getRegisterHierarchy()!=null ? RegisterMapper.toRegisterHierarchyDTO(assetHead.getRegisterHierarchy()) : null)
                .assetDetails(assetHead.getAssetDetails() != null ? toAssetDetailDTOs(assetHead.getAssetDetails()) :
                        Collections.emptyList())
                .createdAt(assetHead.getCreatedAt())
                .updatedAt(assetHead.getUpdatedAt())
                .build();
    }

    public static AssetHead toUpdatedAssetHead(AssetHead assetHead, AssetHead assetHeadUpdate) {
        assetHead.setId(assetHeadUpdate.getId() == null ? assetHead.getId() : assetHeadUpdate.getId());
        assetHead.setAssetName(assetHeadUpdate.getAssetName() == null ? assetHead.getAssetName() :
                assetHeadUpdate.getAssetName());
        assetHead.setRegisterId(assetHeadUpdate.getRegisterId() == null ? assetHead.getRegisterId() :
                assetHeadUpdate.getRegisterId());
        assetHead.setDescription(assetHeadUpdate.getDescription() == null ? assetHead.getDescription() :
                assetHeadUpdate.getDescription());
        assetHead.setRegDate(assetHeadUpdate.getRegDate() == null ? assetHead.getRegDate() :
                assetHeadUpdate.getRegDate());
        assetHead.setActiveDate(assetHeadUpdate.getActiveDate() == null ? assetHead.getActiveDate() :
                assetHeadUpdate.getActiveDate());
        assetHead.setRecordLevelInd(assetHeadUpdate.getRecordLevelInd() == null ? assetHead.getRecordLevelInd() :
                assetHeadUpdate.getRecordLevelInd());
        assetHead.setStatus(assetHeadUpdate.getStatus() == null ? assetHead.getStatus() : assetHeadUpdate.getStatus());
        assetHead.setSerialized(assetHeadUpdate.getSerialized() == null ? assetHead.getSerialized() : assetHeadUpdate.getSerialized());
        return assetHead;
    }

    public static List<AssetHead> toAssetHeads(List<AssetHeadDTO> assetHeadDTOS) {
        return assetHeadDTOS.stream().map(a -> toAssetHead(a)).collect(Collectors.toList());
    }

    public static List<AssetHeadDTO> toAssetHeadDTOs(List<AssetHead> assetHeads) {
        return assetHeads.stream().map(a -> toAssetHeadDTO(a)).collect(Collectors.toList());
    }

    // AssetDetail ////////////////////////////////////////////////
    public static AssetDetail toAssetDetail(AssetDetailDTO assetDetailDTO) {
        if (assetDetailDTO == null) {
            return null;
        }
        return AssetDetail.builder()
                .id(assetDetailDTO.getId())
                .assetHeadId(assetDetailDTO.getAssetHeadId())
                .measureCodeId(assetDetailDTO.getMeasureCodeId())
                .measure(assetDetailDTO.getMeasure())
                .value(assetDetailDTO.getValue())
                .filterByInd(assetDetailDTO.getFilterByInd())
                .lastUpdateOn(assetDetailDTO.getLastUpdateOn())
                .lastUpdateBy(assetDetailDTO.getLastUpdateBy())
                .validationRule(assetDetailDTO.getValidationRule())
                .validationParams(assetDetailDTO.getValidationParams())
                .category(assetDetailDTO.getCategory())
                .build();
    }

    public static AssetDetailDTO toAssetDetailDTO(AssetDetail assetDetail) {
        if (assetDetail == null) {
            return null;
        }
        return AssetDetailDTO.builder()
                .id(assetDetail.getId())
                .assetHeadId(assetDetail.getAssetHeadId())
                .measureCodeId(assetDetail.getMeasureCodeId())
                .measure(assetDetail.getMeasure())
                .measureDefinition(assetDetail.getMeasureDefinitionTenant() != null ? assetDetail.getMeasureDefinitionTenant() : null)
                .value(assetDetail.getValue())
                .filterByInd(assetDetail.getFilterByInd())
                .lastUpdateOn(assetDetail.getLastUpdateOn())
                .lastUpdateBy(assetDetail.getLastUpdateBy())
                .validationRule(assetDetail.getValidationRule())
                .validationParams(assetDetail.getValidationParams())
                .category(assetDetail.getCategory())
                .createdAt(assetDetail.getCreatedAt())
                .updatedAt(assetDetail.getUpdatedAt())
                .build();
    }

    public static AssetDetail toUpdatedAssetDetail(AssetDetail assetDetail, AssetDetail assetDetailUpdate) {
        //assetDetail.setId(assetDetailUpdate.getId() == null ? assetDetail.getId() :
        //assetDetailUpdate.getId());
        assetDetail.setAssetHead(assetDetailUpdate.getAssetHead() == null ? assetDetail.getAssetHead() : assetDetailUpdate.getAssetHead());
        assetDetail.setMeasure(assetDetailUpdate.getMeasure() == null ?
                assetDetail.getMeasure() : assetDetailUpdate.getMeasure());
        //assetDetail.setMeasureDefinition(assetDetailUpdate.getMeasureDefinition() == null ?
        //      assetDetail.getMeasureDefinition() : assetDetailUpdate.getMeasureDefinition());
        assetDetail.setMeasureCodeId(assetDetailUpdate.getMeasureCodeId() == null ? assetDetail.getMeasureCodeId() :
                assetDetailUpdate.getMeasureCodeId());
        assetDetail.setValue(assetDetailUpdate.getValue() == null ? assetDetail.getValue() :
                assetDetailUpdate.getValue());
        assetDetail.setFilterByInd(assetDetailUpdate.getFilterByInd() == null ? assetDetail.getFilterByInd() :
                assetDetailUpdate.getFilterByInd());
        assetDetail.setLastUpdateOn(assetDetailUpdate.getLastUpdateOn() == null ? assetDetail.getLastUpdateOn() :
                assetDetailUpdate.getLastUpdateOn());
        assetDetail.setLastUpdateBy(assetDetailUpdate.getLastUpdateBy() == null ? assetDetail.getLastUpdateBy() :
                assetDetailUpdate.getLastUpdateBy());
        assetDetail.setValidationRule(assetDetailUpdate.getValidationRule() == null ?
                assetDetail.getValidationRule() : assetDetailUpdate.getValidationRule());
        assetDetail.setValidationParams(assetDetailUpdate.getValidationParams() == null ?
                assetDetail.getValidationParams() : assetDetailUpdate.getValidationParams());
        return assetDetail;
    }

    public static List<AssetDetail> toAssetDetails(List<AssetDetailDTO> assetDetailDTOS) {
        return assetDetailDTOS.stream().map(a -> toAssetDetail(a)).collect(Collectors.toList());
    }

    public static List<AssetDetailDTO> toAssetDetailDTOs(List<AssetDetail> assetDetails) {
        return assetDetails.stream().map(a -> toAssetDetailDTO(a)).collect(Collectors.toList());
    }

    // AssetLists ////////////////////////////////////////////////
    public static AssetLists toAssetLists(AssetListsDTO assetListsDTO) {
        if (assetListsDTO == null) {
            return null;
        }
        return AssetLists.builder()
                .id(assetListsDTO.getId())
                .listId(assetListsDTO.getListId())
                .assetListAlias(assetListsDTO.getAssetListAlias())
                .assetId(assetListsDTO.getAssetId())
                .scanCode(assetListsDTO.getScanCode())
                .preferredSourcingLoc(assetListsDTO.getPreferredSourcingLoc())
                .visibilityCode(assetListsDTO.getVisibilityCode())
                .build();
    }

    public static AssetListsDTO toAssetListsDTO(AssetLists assetLists) {
        if (assetLists == null) {
            return null;
        }
        return AssetListsDTO.builder()
                .id(assetLists.getId())
                .listId(assetLists.getListId())
                .assetListAlias(assetLists.getAssetListAlias())
                .assetId(assetLists.getAssetId())
                .scanCode(assetLists.getScanCode())
                .preferredSourcingLoc(assetLists.getPreferredSourcingLoc())
                .visibilityCode(assetLists.getVisibilityCode())
                .build();
    }

    public static AssetLists toUpdatedAssetLists(AssetLists assetLists, AssetLists assetListsUpdate) {
        assetLists.setListId(assetListsUpdate.getListId() == null ? assetLists.getListId() :
                assetListsUpdate.getListId());
        assetLists.setAssetListAlias(assetListsUpdate.getAssetListAlias() == null ? assetLists.getAssetListAlias() :
                assetListsUpdate.getAssetListAlias());
        assetLists.setAssetId(assetListsUpdate.getAssetId() == null ? assetLists.getAssetId() :
                assetListsUpdate.getAssetId());
        assetLists.setScanCode(assetListsUpdate.getScanCode() == null ? assetLists.getScanCode() :
                assetListsUpdate.getScanCode());
        assetLists.setPreferredSourcingLoc(assetListsUpdate.getPreferredSourcingLoc() == null ?
                assetLists.getPreferredSourcingLoc() : assetListsUpdate.getPreferredSourcingLoc());
        assetLists.setVisibilityCode(assetListsUpdate.getVisibilityCode() == null ? assetLists.getVisibilityCode() :
                assetListsUpdate.getVisibilityCode());
        return assetLists;
    }

    public static List<AssetLists> toAssetListss(List<AssetListsDTO> assetListsDTOS) {
        return assetListsDTOS.stream().map(a -> toAssetLists(a)).collect(Collectors.toList());
    }

    public static List<AssetListsDTO> toAssetListsDTOs(List<AssetLists> assetListss) {
        return assetListss.stream().map(a -> toAssetListsDTO(a)).collect(Collectors.toList());
    }

    // AssetSupplier ////////////////////////////////////////////////
    public static AssetSupplier toAssetSupplier(AssetSupplierDTO assetSupplierDTO) {
        if (assetSupplierDTO == null) {
            return null;
        }
        return AssetSupplier.builder()
                .id(assetSupplierDTO.getId())
                .assetId(assetSupplierDTO.getAssetId())
                .supplierId(assetSupplierDTO.getSupplierId())
//                .serialNumber(assetSupplierDTO.getSerialNumber())
                .primarySupplier(assetSupplierDTO.getPrimarySupplier())
                .scanId(assetSupplierDTO.getScanId())
                .ext1(assetSupplierDTO.getExt1())
                .ext2(assetSupplierDTO.getExt2())
                .build();
    }

    public static AssetSupplierDTO toAssetSupplierDTO(AssetSupplier assetSupplier) {
        if (assetSupplier == null) {
            return null;
        }
        return AssetSupplierDTO.builder()
                .id(assetSupplier.getId())
                .assetId(assetSupplier.getAssetId())
                .supplierId(assetSupplier.getSupplierId())
//                .serialNumber(assetSupplier.getSerialNumber())
                .primarySupplier(assetSupplier.getPrimarySupplier())
                .scanId(assetSupplier.getScanId())
                .ext1(assetSupplier.getExt1())
                .ext2(assetSupplier.getExt2())
                .createdAt(assetSupplier.getCreatedAt())
                .updatedAt(assetSupplier.getUpdatedAt())
                .build();
    }

    public static AssetSupplier toUpdatedAssetSupplier(AssetSupplier assetSupplier, AssetSupplier assetSupplierUpdate) {
        assetSupplier.setAssetId(assetSupplierUpdate.getAssetId() == null ? assetSupplier.getAssetId() :
                assetSupplierUpdate.getAssetId());
        assetSupplier.setSupplierId(assetSupplierUpdate.getSupplierId() == null ? assetSupplier.getSupplierId() :
                assetSupplierUpdate.getSupplierId());
//        assetSupplier.setSerialNumber(assetSupplierUpdate.getSerialNumber() == null ? assetSupplier.getSerialNumber
//        () : assetSupplierUpdate.getSerialNumber());
        assetSupplier.setPrimarySupplier(assetSupplierUpdate.getPrimarySupplier() == null ?
                assetSupplier.getPrimarySupplier() : assetSupplierUpdate.getPrimarySupplier());
        assetSupplier.setScanId(assetSupplierUpdate.getScanId() == null ? assetSupplier.getScanId() :
                assetSupplierUpdate.getScanId());
        assetSupplier.setExt1(assetSupplierUpdate.getExt1() == null ? assetSupplier.getExt1() :
                assetSupplierUpdate.getExt1());
        assetSupplier.setExt2(assetSupplierUpdate.getExt2() == null ? assetSupplier.getExt2() :
                assetSupplierUpdate.getExt2());
        return assetSupplier;
    }

    public static List<AssetSupplier> toAssetSuppliers(List<AssetSupplierDTO> assetSupplierDTOS) {
        return assetSupplierDTOS.stream().map(a -> toAssetSupplier(a)).collect(Collectors.toList());
    }

    public static List<AssetSupplierDTO> toAssetSupplierDTOs(List<AssetSupplier> assetSuppliers) {
        return assetSuppliers.stream().map(a -> toAssetSupplierDTO(a)).collect(Collectors.toList());
    }

    // Inventory ////////////////////////////////////////////////
    public static Inventory toInventory(InventoryDTO inventoryDTO) {
        if (inventoryDTO == null) {
            return null;
        }
        return Inventory.builder()
                .id(inventoryDTO.getId())
                .assetId(inventoryDTO.getAssetId())
                .count(inventoryDTO.getCount())
                .locType(inventoryDTO.getLocType())
                .locationId(inventoryDTO.getLocationId())
                .statusCode(inventoryDTO.getStatusCode())
                .shelfCode(inventoryDTO.getShelfCode())
                .build();
    }

    public static InventoryDTO toInventoryDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        return InventoryDTO.builder()
                .id(inventory.getId())
                .assetId(inventory.getAssetId())
                .count(inventory.getCount())
                .locType(inventory.getLocType())
                .locationId(inventory.getLocationId())
                .statusCode(inventory.getStatusCode())
                .shelfCode(inventory.getShelfCode())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    public static Inventory toUpdatedInventory(Inventory inventory, Inventory inventoryUpdate) {
        inventory.setAssetId(inventoryUpdate.getAssetId() == null ? inventory.getAssetId() :
                inventoryUpdate.getAssetId());
        inventory.setCount(inventoryUpdate.getCount() == null ? inventory.getCount() : inventoryUpdate.getCount());
        inventory.setLocType(inventoryUpdate.getLocType() == null ? inventory.getLocType() :
                inventoryUpdate.getLocType());
        inventory.setLocationId(inventoryUpdate.getLocationId() == null ? inventory.getLocationId() :
                inventoryUpdate.getLocationId());
        inventory.setStatusCode(inventoryUpdate.getStatusCode() == null ? inventory.getStatusCode() :
                inventoryUpdate.getStatusCode());
        inventory.setShelfCode(inventoryUpdate.getShelfCode() == null ? inventory.getShelfCode() :
                inventoryUpdate.getShelfCode());
        return inventory;
    }

    public static List<Inventory> toInventorys(List<InventoryDTO> inventoryDTOS) {
        return inventoryDTOS.stream().map(i -> toInventory(i)).collect(Collectors.toList());
    }

    public static List<InventoryDTO> toInventoryDTOs(List<Inventory> inventories) {
        return inventories.stream().map(i -> toInventoryDTO(i)).collect(Collectors.toList());
    }

    // ScanCodes ////////////////////////////////////////////////
    public static ScanCodes toScanCodes(ScanCodesDTO scanCodesDTO) {
        if (scanCodesDTO == null) {
            return null;
        }
        return ScanCodes.builder()
                .scanId(scanCodesDTO.getScanId())
                .regCode(scanCodesDTO.getRegCode())
                .ref(scanCodesDTO.getRef())
                .scanCode(scanCodesDTO.getScanCode())
                .codeType(scanCodesDTO.getCodeType())
                .standardCodeFormat(scanCodesDTO.getStandardCodeFormat())
                .status(scanCodesDTO.getStatus())
                .temporary(scanCodesDTO.getTemporary())
                .startDate(scanCodesDTO.getStartDate())
                .expiry(scanCodesDTO.getExpiry())
                .build();
    }

    public static ScanCodesDTO toScanCodesDTO(ScanCodes scanCodes) {
        if (scanCodes == null) {
            return null;
        }
        return ScanCodesDTO.builder()
                .scanId(scanCodes.getScanId())
                .regCode(scanCodes.getRegCode())
                .ref(scanCodes.getRef())
                .scanCode(scanCodes.getScanCode())
                .codeType(scanCodes.getCodeType())
                .standardCodeFormat(scanCodes.getStandardCodeFormat())
                .status(scanCodes.getStatus())
                .temporary(scanCodes.getTemporary())
                .startDate(scanCodes.getStartDate())
                .expiry(scanCodes.getExpiry())
                .createdAt(scanCodes.getCreatedAt())
                .updatedAt(scanCodes.getUpdatedAt())
                .build();
    }

    public static ScanCodes toUpdatedScanCodes(ScanCodes scanCodes, ScanCodes scanCodesUpdate) {
        scanCodes.setRegCode(scanCodesUpdate.getRegCode() == null ? scanCodes.getRegCode() :
                scanCodesUpdate.getRegCode());
        scanCodes.setRef(scanCodesUpdate.getRef() == null ? scanCodes.getRef() : scanCodesUpdate.getRef());
        scanCodes.setScanCode(scanCodesUpdate.getScanCode() == null ? scanCodes.getScanCode() :
                scanCodesUpdate.getScanCode());
        scanCodes.setCodeType(scanCodesUpdate.getCodeType() == null ? scanCodes.getCodeType() :
                scanCodesUpdate.getCodeType());
        scanCodes.setStandardCodeFormat(scanCodesUpdate.getStandardCodeFormat() == null ?
                scanCodes.getStandardCodeFormat() : scanCodesUpdate.getStandardCodeFormat());
        scanCodes.setStatus(scanCodesUpdate.getStatus() == null ? scanCodes.getStatus() : scanCodesUpdate.getStatus());
        scanCodes.setTemporary(scanCodesUpdate.getTemporary() == null ? scanCodes.getTemporary() :
                scanCodesUpdate.getTemporary());
        scanCodes.setStartDate(scanCodesUpdate.getStartDate() == null ? scanCodes.getStartDate() :
                scanCodesUpdate.getStartDate());
        scanCodes.setExpiry(scanCodesUpdate.getExpiry() == null ? scanCodes.getExpiry() : scanCodesUpdate.getExpiry());
        return scanCodes;
    }

    public static List<ScanCodes> toScanCodess(List<ScanCodesDTO> scanCodesDTOS) {
        return scanCodesDTOS.stream().map(s -> toScanCodes(s)).collect(Collectors.toList());
    }

    public static List<ScanCodesDTO> toScanCodesDTOs(List<ScanCodes> scanCodes) {
        return scanCodes.stream().map(s -> toScanCodesDTO(s)).collect(Collectors.toList());
    }

    // AssetSerialNumber ////////////////////////////////////////////////
    public static AssetSerialNumber toAssetSerialNumbers(AssetSerialNumberDTO assetSerialNumberDTO) {
        if (assetSerialNumberDTO == null) {
            return null;
        }
        return AssetSerialNumber.builder()
                .id(assetSerialNumberDTO.getId())
                .serialNumber(assetSerialNumberDTO.getSerialNumber())
                .assetId(assetSerialNumberDTO.getAssetId())
                .suppId(assetSerialNumberDTO.getSuppId())
                .manuId(assetSerialNumberDTO.getManuId())
                .notes(assetSerialNumberDTO.getNotes())
                .palletNo(assetSerialNumberDTO.getPalletNo())
                .build();
    }

    public static AssetSerialNumberDTO toAssetSerialNumbersDTO(AssetSerialNumber assetSerialNumber) {
        if (assetSerialNumber == null) {
            return null;
        }
        return AssetSerialNumberDTO.builder()
                .id(assetSerialNumber.getId())
                .serialNumber(assetSerialNumber.getSerialNumber())
                .assetId(assetSerialNumber.getAssetId())
                .suppId(assetSerialNumber.getSuppId())
                .manuId(assetSerialNumber.getManuId())
                .notes(assetSerialNumber.getNotes())
                .palletNo(assetSerialNumber.getPalletNo())
                .build();
    }

    public static AssetSerialNumber toUpdatedAssetSerialNumbers(AssetSerialNumber assetSerialNumber,
                                                                AssetSerialNumber assetSerialNumberUpdate) {
        assetSerialNumber.setSerialNumber(assetSerialNumberUpdate.getSerialNumber() == null ?
                assetSerialNumber.getSerialNumber() : assetSerialNumberUpdate.getSerialNumber());
        assetSerialNumber.setAssetId(assetSerialNumberUpdate.getAssetId() == null ? assetSerialNumber.getAssetId() :
                assetSerialNumberUpdate.getAssetId());
        assetSerialNumber.setSuppId(assetSerialNumberUpdate.getSuppId() == null ? assetSerialNumber.getSuppId() :
                assetSerialNumberUpdate.getSuppId());
        assetSerialNumber.setManuId(assetSerialNumberUpdate.getManuId() == null ? assetSerialNumber.getManuId() :
                assetSerialNumberUpdate.getManuId());
        assetSerialNumber.setNotes(assetSerialNumberUpdate.getNotes() == null ? assetSerialNumber.getNotes() :
                assetSerialNumberUpdate.getNotes());
        assetSerialNumber.setPalletNo(assetSerialNumberUpdate.getPalletNo() == null ? assetSerialNumber.getPalletNo() :
                assetSerialNumberUpdate.getPalletNo());
        return assetSerialNumber;
    }

    public static List<AssetSerialNumber> toAssetSerialNumberss(List<AssetSerialNumberDTO> assetSerialNumberDTOS) {
        return assetSerialNumberDTOS.stream().map(a -> toAssetSerialNumbers(a)).collect(Collectors.toList());
    }

    public static List<AssetSerialNumberDTO> toAssetSerialNumbersDTOs(List<AssetSerialNumber> assetSerialNumbers) {
        return assetSerialNumbers.stream().map(a -> toAssetSerialNumbersDTO(a)).collect(Collectors.toList());
    }
}
