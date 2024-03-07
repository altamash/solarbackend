package com.solar.api.tenant.mapper.extended.pallet;

public class PalletTypeMapper {

    /*public static PalletType toPalletType(PalletTypeDTO palletTypeDTO) {
        if (palletTypeDTO == null) {
            return null;
        }
        return PalletType.builder()
                .id(palletTypeDTO.getId())
                .palletType(palletTypeDTO.getPalletType())
                .material(palletTypeDTO.getMaterial())
                .stackable(palletTypeDTO.getStackable())
                .division(palletTypeDTO.getDivision())
                .prefix(palletTypeDTO.getPrefix())
                .desc(palletTypeDTO.getDesc())
                .fragileAllowed(palletTypeDTO.getFragileAllowed())
                .minTemp(palletTypeDTO.getMinTemp())
                .maxTemp(palletTypeDTO.getMaxTemp())
                .disposalDocRef(palletTypeDTO.getDisposalDocRef())
                .packagingRef(palletTypeDTO.getPackagingRef())
                .storageRecommendations(palletTypeDTO.getStorageRecommendations())
                .length(palletTypeDTO.getLength())
                .width(palletTypeDTO.getWidth())
                .height(palletTypeDTO.getHeight())
                .maxWeight(palletTypeDTO.getMaxWeight())
                .lastGeneratedSequence(palletTypeDTO.getLastGeneratedSequence())
                .palletImage(palletTypeDTO.getPalletImage())
                .thumbnail(palletTypeDTO.getThumbnail())
                .build();
    }

    public static PalletTypeDTO toPalletTypeDTO(PalletType palletType) {
        if (palletType == null) {
            return null;
        }
        return PalletTypeDTO.builder()
                .id(palletType.getId())
                .palletType(palletType.getPalletType())
                .material(palletType.getMaterial())
                .stackable(palletType.getStackable())
                .division(palletType.getDivision())
                .prefix(palletType.getPrefix())
                .desc(palletType.getDesc())
                .fragileAllowed(palletType.getFragileAllowed())
                .minTemp(palletType.getMinTemp())
                .maxTemp(palletType.getMaxTemp())
                .disposalDocRef(palletType.getDisposalDocRef())
                .packagingRef(palletType.getPackagingRef())
                .storageRecommendations(palletType.getStorageRecommendations())
                .length(palletType.getLength())
                .width(palletType.getWidth())
                .height(palletType.getHeight())
                .maxWeight(palletType.getMaxWeight())
                .lastGeneratedSequence(palletType.getLastGeneratedSequence())
                .palletImage(palletType.getPalletImage())
                .thumbnail(palletType.getThumbnail())
                .build();
    }

    public static PalletType toUpdatedPalletType(PalletType palletType, PalletType palletTypeUpdate) {
        palletType.setPalletType(palletTypeUpdate.getPalletType() == null ? palletType.getPalletType() : palletTypeUpdate.getPalletType());
        palletType.setMaterial(palletTypeUpdate.getMaterial() == null ? palletType.getMaterial() : palletTypeUpdate.getMaterial());
        palletType.setStackable(palletTypeUpdate.getStackable() == null ? palletType.getStackable() : palletTypeUpdate.getStackable());
        palletType.setDivision(palletTypeUpdate.getDivision() == null ? palletType.getDivision() : palletTypeUpdate.getDivision());
        palletType.setPrefix(palletTypeUpdate.getPrefix() == null ? palletType.getPrefix() : palletTypeUpdate.getPrefix());
        palletType.setDesc(palletTypeUpdate.getDesc() == null ? palletType.getDesc() : palletTypeUpdate.getDesc());
        palletType.setFragileAllowed(palletTypeUpdate.getFragileAllowed() == null ? palletType.getFragileAllowed() : palletTypeUpdate.getFragileAllowed());
        palletType.setMinTemp(palletTypeUpdate.getMinTemp() == null ? palletType.getMinTemp() : palletTypeUpdate.getMinTemp());
        palletType.setMaxTemp(palletTypeUpdate.getMaxTemp() == null ? palletType.getMaxTemp() : palletTypeUpdate.getMaxTemp());
        palletType.setDisposalDocRef(palletTypeUpdate.getDisposalDocRef() == null ? palletType.getDisposalDocRef() : palletTypeUpdate.getDisposalDocRef());
        palletType.setPackagingRef(palletTypeUpdate.getPackagingRef() == null ? palletType.getPackagingRef() : palletTypeUpdate.getPackagingRef());
        palletType.setStorageRecommendations(palletTypeUpdate.getStorageRecommendations() == null ? palletType.getStorageRecommendations() : palletTypeUpdate.getStorageRecommendations());
        palletType.setLength(palletTypeUpdate.getLength() == null ? palletType.getLength() : palletTypeUpdate.getLength());
        palletType.setWidth(palletTypeUpdate.getWidth() == null ? palletType.getWidth() : palletTypeUpdate.getWidth());
        palletType.setHeight(palletTypeUpdate.getHeight() == null ? palletType.getHeight() : palletTypeUpdate.getHeight());
        palletType.setMaxWeight(palletTypeUpdate.getMaxWeight() == null ? palletType.getMaxWeight() : palletTypeUpdate.getMaxWeight());
        palletType.setLastGeneratedSequence(palletTypeUpdate.getLastGeneratedSequence() == null ? palletType.getLastGeneratedSequence() : palletTypeUpdate.getLastGeneratedSequence());
        palletType.setPalletImage(palletTypeUpdate.getPalletImage() == null ? palletType.getPalletType() : palletTypeUpdate.getPalletImage());
        palletType.setThumbnail(palletTypeUpdate.getThumbnail() == null ? palletType.getPalletType() : palletTypeUpdate.getThumbnail());
        return palletType;
    }

    public static List<PalletType> toPalletTypes(List<PalletTypeDTO> palletLocationDTOS) {
        return palletLocationDTOS.stream().map(a -> toPalletType(a)).collect(Collectors.toList());
    }

    public static List<PalletTypeDTO> toPalletTypeDTOs(List<PalletType> palletLocations) {
        return palletLocations.stream().map(a -> toPalletTypeDTO(a)).collect(Collectors.toList());
    }*/
}
