package com.solar.api.tenant.mapper.extended.pallet;

import com.solar.api.tenant.model.extended.pallet.RackSpaceMap;

import java.util.List;
import java.util.stream.Collectors;

public class RackSpaceMapMapper {

    public static RackSpaceMap toRackSpaceMap(RackSpaceMapDTO rackSpaceMapDTO) {
        if (rackSpaceMapDTO == null) {
            return null;
        }
        return RackSpaceMap.builder()
                .id(rackSpaceMapDTO.getId())
                .location(rackSpaceMapDTO.getLocation())
                .areaCode(rackSpaceMapDTO.getAreaCode())
                .block(rackSpaceMapDTO.getBlock())
                .lane(rackSpaceMapDTO.getLane())
                .side(rackSpaceMapDTO.getSide())
                .height(rackSpaceMapDTO.getHeight())
                .depth(rackSpaceMapDTO.getDepth())
                .externalRefId(rackSpaceMapDTO.getExternalRefId())
                .status(rackSpaceMapDTO.getStatus())
                .reservedFor(rackSpaceMapDTO.getReservedFor())
                .build();
    }

    public static RackSpaceMapDTO toRackSpaceMapDTO(RackSpaceMap rackSpaceMap) {
        if (rackSpaceMap == null) {
            return null;
        }
        return RackSpaceMapDTO.builder()
                .id(rackSpaceMap.getId())
                .location(rackSpaceMap.getLocation())
                .areaCode(rackSpaceMap.getAreaCode())
                .block(rackSpaceMap.getBlock())
                .lane(rackSpaceMap.getLane())
                .side(rackSpaceMap.getSide())
                .height(rackSpaceMap.getHeight())
                .depth(rackSpaceMap.getDepth())
                .externalRefId(rackSpaceMap.getExternalRefId())
                .status(rackSpaceMap.getStatus())
                .reservedFor(rackSpaceMap.getReservedFor())
                .build();
    }

    public static RackSpaceMap toUpdatedRackSpaceMap(RackSpaceMap rackSpaceMap, RackSpaceMap rackSpaceMapUpdate) {
        rackSpaceMap.setLocation(rackSpaceMapUpdate.getLocation() == null ? rackSpaceMap.getLocation() : rackSpaceMapUpdate.getLocation());
        rackSpaceMap.setAreaCode(rackSpaceMapUpdate.getAreaCode() == null ? rackSpaceMap.getAreaCode() : rackSpaceMapUpdate.getAreaCode());
        rackSpaceMap.setBlock(rackSpaceMapUpdate.getBlock() == null ? rackSpaceMap.getBlock() : rackSpaceMapUpdate.getBlock());
        rackSpaceMap.setLane(rackSpaceMapUpdate.getLane() == null ? rackSpaceMap.getLane() : rackSpaceMapUpdate.getLane());
        rackSpaceMap.setSide(rackSpaceMapUpdate.getSide() == null ? rackSpaceMap.getSide() : rackSpaceMapUpdate.getSide());
        rackSpaceMap.setHeight(rackSpaceMapUpdate.getHeight() == null ? rackSpaceMap.getHeight() : rackSpaceMapUpdate.getHeight());
        rackSpaceMap.setDepth(rackSpaceMapUpdate.getDepth() == null ? rackSpaceMap.getDepth() : rackSpaceMapUpdate.getDepth());
        rackSpaceMap.setExternalRefId(rackSpaceMapUpdate.getExternalRefId() == null ? rackSpaceMap.getExternalRefId() : rackSpaceMapUpdate.getExternalRefId());
        rackSpaceMap.setStatus(rackSpaceMapUpdate.getStatus() == null ? rackSpaceMap.getStatus() : rackSpaceMapUpdate.getStatus());
        rackSpaceMap.setReservedFor(rackSpaceMapUpdate.getReservedFor() == null ? rackSpaceMap.getReservedFor() : rackSpaceMapUpdate.getReservedFor());
       return rackSpaceMap;
    }

    public static List<RackSpaceMap> toRackSpaceMaps(List<RackSpaceMapDTO> palletLocationDTOS) {
        return palletLocationDTOS.stream().map(a -> toRackSpaceMap(a)).collect(Collectors.toList());
    }

    public static List<RackSpaceMapDTO> toRackSpaceMapDTOs(List<RackSpaceMap> palletLocations) {
        return palletLocations.stream().map(a -> toRackSpaceMapDTO(a)).collect(Collectors.toList());
    }
}
