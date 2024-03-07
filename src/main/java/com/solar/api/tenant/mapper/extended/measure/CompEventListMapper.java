package com.solar.api.tenant.mapper.extended.measure;

import com.solar.api.tenant.model.extended.measure.CompEventList;

import java.util.List;
import java.util.stream.Collectors;

public class CompEventListMapper {

    public static CompEventList toCompEventList(CompEventListDTO compEventListDTO) {
        if (compEventListDTO == null) {
            return null;
        }
        return CompEventList.builder()
                .id(compEventListDTO.getId())
                .eventName(compEventListDTO.getEventName())
                .componentId(compEventListDTO.getComponentId())
                .module(compEventListDTO.getModule())
                .build();
    }

    public static CompEventListDTO toCompEventListDTO(CompEventList compEventList) {
        if (compEventList == null) {
            return null;
        }
        return CompEventListDTO.builder()
                .id(compEventList.getId())
                .eventName(compEventList.getEventName())
                .componentId(compEventList.getComponentId())
                .module(compEventList.getModule())
                .createdAt(compEventList.getCreatedAt())
                .updatedAt(compEventList.getUpdatedAt())
                .build();
    }

    public static CompEventList toUpdatedCompEventList(CompEventList compEventList, CompEventList compEventListUpdate) {
        compEventList.setEventName(compEventListUpdate.getEventName() == null ? compEventList.getEventName() :
                compEventListUpdate.getEventName());
        compEventList.setComponentId(compEventListUpdate.getComponentId() == null ? compEventList.getComponentId() :
                compEventListUpdate.getComponentId());
        compEventList.setModule(compEventListUpdate.getModule() == null ? compEventList.getModule() :
                compEventListUpdate.getModule());
        return compEventList;
    }

    public static List<CompEventList> toCompEventLists(List<CompEventListDTO> compEventListDTOS) {
        return compEventListDTOS.stream().map(c -> toCompEventList(c)).collect(Collectors.toList());
    }

    public static List<CompEventListDTO> toCompEventListDTOs(List<CompEventList> compEventLists) {
        return compEventLists.stream().map(c -> toCompEventListDTO(c)).collect(Collectors.toList());
    }
}
