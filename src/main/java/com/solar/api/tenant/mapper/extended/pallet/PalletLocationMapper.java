package com.solar.api.tenant.mapper.extended.pallet;

import com.solar.api.tenant.model.extended.pallet.PalletLocation;

import java.util.List;
import java.util.stream.Collectors;

public class PalletLocationMapper {

    public static PalletLocation toPalletLocation(PalletLocationDTO palletLocationDTO) {
        if (palletLocationDTO == null) {
            return null;
        }
        return PalletLocation.builder()
                .id(palletLocationDTO.getId())
                .palletId(palletLocationDTO.getPalletId())
                .seqNo(palletLocationDTO.getSeqNo())
                .locationId(palletLocationDTO.getLocationId())
                .rackId(palletLocationDTO.getRackId())
                .opened(palletLocationDTO.getOpened())
                .openedBy(palletLocationDTO.getOpenedBy())
                .openDatetime(palletLocationDTO.getOpenDatetime())
                .vehicleType(palletLocationDTO.getVehicleType())
                .vehicleRefNum(palletLocationDTO.getVehicleRefNum())
                .mobileRackRef(palletLocationDTO.getMobileRackRef())
                .lane(palletLocationDTO.getLane())
                .depth(palletLocationDTO.getDepth())
                .height(palletLocationDTO.getHeight())
                .transferId(palletLocationDTO.getTransferId())
                .notes(palletLocationDTO.getNotes())
                .build();
    }

    public static PalletLocationDTO toPalletLocationDTO(PalletLocation palletLocation) {
        if (palletLocation == null) {
            return null;
        }
        return PalletLocationDTO.builder()
                .id(palletLocation.getId())
                .palletId(palletLocation.getPalletId())
                .seqNo(palletLocation.getSeqNo())
                .locationId(palletLocation.getLocationId())
                .rackId(palletLocation.getRackId())
                .opened(palletLocation.getOpened())
                .openedBy(palletLocation.getOpenedBy())
                .openDatetime(palletLocation.getOpenDatetime())
                .vehicleType(palletLocation.getVehicleType())
                .vehicleRefNum(palletLocation.getVehicleRefNum())
                .mobileRackRef(palletLocation.getMobileRackRef())
                .lane(palletLocation.getLane())
                .depth(palletLocation.getDepth())
                .height(palletLocation.getHeight())
                .transferId(palletLocation.getTransferId())
                .notes(palletLocation.getNotes())
                .build();
    }

    public static PalletLocation toUpdatedPalletLocation(PalletLocation palletLocation, PalletLocation palletLocationUpdate) {
        palletLocation.setPalletId(palletLocationUpdate.getPalletId() == null ? palletLocation.getLocationId() : palletLocationUpdate.getPalletId());
        palletLocation.setSeqNo(palletLocationUpdate.getSeqNo() == null ? palletLocation.getSeqNo() : palletLocationUpdate.getSeqNo());
        palletLocation.setLocationId(palletLocationUpdate.getLocationId() == null ? palletLocation.getPalletId() : palletLocationUpdate.getLocationId());
        palletLocation.setRackId(palletLocationUpdate.getRackId() == null ? palletLocation.getRackId() : palletLocationUpdate.getRackId());
        palletLocation.setOpened(palletLocationUpdate.getOpened() == null ? palletLocation.getOpened() : palletLocationUpdate.getOpened());
        palletLocation.setOpenDatetime(palletLocationUpdate.getOpenDatetime() == null ? palletLocation.getOpenDatetime() : palletLocationUpdate.getOpenDatetime());
        palletLocation.setOpenedBy(palletLocationUpdate.getOpenedBy() == null ? palletLocation.getOpenedBy() : palletLocationUpdate.getOpenedBy());
        palletLocation.setVehicleType(palletLocationUpdate.getVehicleType() == null ? palletLocation.getVehicleType() : palletLocationUpdate.getVehicleType());
        palletLocation.setVehicleRefNum(palletLocationUpdate.getVehicleRefNum() == null ? palletLocation.getVehicleRefNum() : palletLocationUpdate.getVehicleRefNum());
        palletLocation.setMobileRackRef(palletLocationUpdate.getMobileRackRef() == null ? palletLocation.getMobileRackRef() : palletLocationUpdate.getMobileRackRef());
        palletLocation.setLane(palletLocationUpdate.getLane() == null ? palletLocation.getLane() : palletLocationUpdate.getLane());
        palletLocation.setDepth(palletLocationUpdate.getDepth() == null ? palletLocation.getDepth() : palletLocationUpdate.getDepth());
        palletLocation.setHeight(palletLocationUpdate.getHeight() == null ? palletLocation.getHeight() : palletLocationUpdate.getHeight());
        palletLocation.setTransferId(palletLocationUpdate.getTransferId() == null ? palletLocation.getTransferId() : palletLocationUpdate.getTransferId());
        palletLocation.setNotes(palletLocationUpdate.getNotes() == null ? palletLocation.getNotes() : palletLocationUpdate.getNotes());
        return palletLocation;
    }

    public static List<PalletLocation> toPalletLocations(List<PalletLocationDTO> palletLocationDTOS) {
        return palletLocationDTOS.stream().map(a -> toPalletLocation(a)).collect(Collectors.toList());
    }

    public static List<PalletLocationDTO> toPalletLocationDTOs(List<PalletLocation> palletLocations) {
        return palletLocations.stream().map(a -> toPalletLocationDTO(a)).collect(Collectors.toList());
    }
}
