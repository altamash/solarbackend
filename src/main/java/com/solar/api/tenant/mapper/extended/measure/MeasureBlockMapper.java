package com.solar.api.tenant.mapper.extended.measure;

import com.solar.api.tenant.mapper.extended.resources.MeasureBlockHeadDTO;
import com.solar.api.tenant.model.extended.resources.MeasureBlockHead;

import java.util.List;
import java.util.stream.Collectors;

public class MeasureBlockMapper {

    public static MeasureBlockHead toMeasureBlockHead(MeasureBlockHeadDTO measureBlockHeadDTO) {
        if (measureBlockHeadDTO == null) {
            return null;
        }
        return MeasureBlockHead.builder()
                .id(measureBlockHeadDTO.getId())
                .blockName(measureBlockHeadDTO.getBlockName())
                .regModuleId(measureBlockHeadDTO.getRegModuleId())
                .locked(measureBlockHeadDTO.getLocked())
                .deleteInd(measureBlockHeadDTO.getDeleteInd())
                .deleteSchDate(measureBlockHeadDTO.getDeleteSchDate())
                .build();
    }

    public static MeasureBlockHeadDTO toMeasureBlockHeadDTO(MeasureBlockHead measureBlockHead) {
        if (measureBlockHead == null) {
            return null;
        }
        return MeasureBlockHeadDTO.builder()
                .id(measureBlockHead.getId())
                .blockName(measureBlockHead.getBlockName())
                .regModuleId(measureBlockHead.getRegModuleId())
                .locked(measureBlockHead.getLocked())
                .deleteInd(measureBlockHead.getDeleteInd())
                .deleteSchDate(measureBlockHead.getDeleteSchDate())
                .build();
    }

    public static MeasureBlockHead toUpdatedMeasureBlockHead(MeasureBlockHead measureBlockHead,
                                                             MeasureBlockHead measureBlockHeadUpdate) {
        measureBlockHead.setBlockName(measureBlockHeadUpdate.getBlockName() == null ? measureBlockHead.getBlockName() :
                measureBlockHeadUpdate.getBlockName());
        measureBlockHead.setLocked(measureBlockHeadUpdate.getLocked() == null ? measureBlockHead.getLocked() :
                measureBlockHeadUpdate.getLocked());
        measureBlockHead.setDeleteInd(measureBlockHeadUpdate.getDeleteInd() == null ? measureBlockHead.getDeleteInd() :
                measureBlockHeadUpdate.getDeleteInd());
        measureBlockHead.setDeleteSchDate(measureBlockHeadUpdate.getDeleteSchDate() == null ? measureBlockHead.getDeleteSchDate() :
                measureBlockHeadUpdate.getDeleteSchDate());
        measureBlockHead.setRegModuleId(measureBlockHeadUpdate.getRegModuleId() == null ? measureBlockHead.getRegModuleId() :
                measureBlockHeadUpdate.getRegModuleId());
        return measureBlockHead;
    }

    public static List<MeasureBlockHead> toMeasureBlockHead(List<MeasureBlockHeadDTO> measureBlockHeadDTOS) {
        return measureBlockHeadDTOS.stream().map(m -> toMeasureBlockHead(m)).collect(Collectors.toList());
    }

    public static List<MeasureBlockHeadDTO> toMeasureBlockHeadDTOs(List<MeasureBlockHead> measureBlockHead) {
        return measureBlockHead.stream().map(m -> toMeasureBlockHeadDTO(m)).collect(Collectors.toList());
    }

}
