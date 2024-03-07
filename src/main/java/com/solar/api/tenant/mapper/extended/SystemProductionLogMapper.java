package com.solar.api.tenant.mapper.extended;

import com.solar.api.tenant.model.extended.SystemProductionLog;

import java.util.List;
import java.util.stream.Collectors;

public class SystemProductionLogMapper {

    public static SystemProductionLog toSystemProductionLog(SystemProductionLogDTO systemProductionLogDTO) {
        if (systemProductionLogDTO == null) {
            return null;
        }
        return SystemProductionLog.builder()
                .id(systemProductionLogDTO.getId())
                .siteid(systemProductionLogDTO.getSiteid())
                .assetCompRef(systemProductionLogDTO.getAssetCompRef())
                .peakPower(systemProductionLogDTO.getPeakPower())
                .status(systemProductionLogDTO.getStatus())
                .recDateTime(systemProductionLogDTO.getRecDateTime())
                .assetId(systemProductionLogDTO.getAssetId())
                .premiseNo(systemProductionLogDTO.getPremiseNo())
                .assetDesc(systemProductionLogDTO.getAssetDesc())
                .sourceSystem(systemProductionLogDTO.getSourceSystem())
                .externalAcctId(systemProductionLogDTO.getExternalAcctId())
                .ext2(systemProductionLogDTO.getExt2())
                .ext3(systemProductionLogDTO.getExt3())
                .build();
    }

    public static SystemProductionLogDTO toSystemProductionLogDTO(SystemProductionLog systemProductionLog) {
        if (systemProductionLog == null) {
            return null;
        }
        return SystemProductionLogDTO.builder()
                .id(systemProductionLog.getId())
                .siteid(systemProductionLog.getSiteid())
                .assetCompRef(systemProductionLog.getAssetCompRef())
                .peakPower(systemProductionLog.getPeakPower())
                .status(systemProductionLog.getStatus())
                .recDateTime(systemProductionLog.getRecDateTime())
                .assetId(systemProductionLog.getAssetId())
                .premiseNo(systemProductionLog.getPremiseNo())
                .assetDesc(systemProductionLog.getAssetDesc())
                .sourceSystem(systemProductionLog.getSourceSystem())
                .externalAcctId(systemProductionLog.getExternalAcctId())
                .ext2(systemProductionLog.getExt2())
                .ext3(systemProductionLog.getExt3())
                .createdAt(systemProductionLog.getCreatedAt())
                .updatedAt(systemProductionLog.getUpdatedAt())
                .build();
    }

    public static SystemProductionLog toUpdatedSystemProductionLog(SystemProductionLog systemProductionLog,
                                                                   SystemProductionLog systemProductionLogUpdate) {
        systemProductionLog.setSiteid(systemProductionLogUpdate.getSiteid() == null ?
                systemProductionLog.getSiteid() : systemProductionLogUpdate.getSiteid());
        systemProductionLog.setAssetCompRef(systemProductionLogUpdate.getAssetCompRef() == null ?
                systemProductionLog.getAssetCompRef() : systemProductionLogUpdate.getAssetCompRef());
        systemProductionLog.setPeakPower(systemProductionLogUpdate.getPeakPower() == null ?
                systemProductionLog.getPeakPower() : systemProductionLogUpdate.getPeakPower());
        systemProductionLog.setStatus(systemProductionLogUpdate.getStatus() == null ?
                systemProductionLog.getStatus() : systemProductionLogUpdate.getStatus());
        systemProductionLog.setRecDateTime(systemProductionLogUpdate.getRecDateTime() == null ?
                systemProductionLog.getRecDateTime() : systemProductionLogUpdate.getRecDateTime());
        systemProductionLog.setAssetId(systemProductionLogUpdate.getAssetId() == null ?
                systemProductionLog.getAssetId() : systemProductionLogUpdate.getAssetId());
        systemProductionLog.setPremiseNo(systemProductionLogUpdate.getPremiseNo() == null ?
                systemProductionLog.getPremiseNo() : systemProductionLogUpdate.getPremiseNo());
        systemProductionLog.setAssetDesc(systemProductionLogUpdate.getAssetDesc() == null ?
                systemProductionLog.getAssetDesc() : systemProductionLogUpdate.getAssetDesc());
        systemProductionLog.setSourceSystem(systemProductionLogUpdate.getSourceSystem() == null ?
                systemProductionLog.getSourceSystem() : systemProductionLogUpdate.getSourceSystem());
        systemProductionLog.setExternalAcctId(systemProductionLogUpdate.getExternalAcctId() == null ?
                systemProductionLog.getExternalAcctId() : systemProductionLogUpdate.getExternalAcctId());
        systemProductionLog.setExt2(systemProductionLogUpdate.getExt2() == null ? systemProductionLog.getExt2() :
                systemProductionLogUpdate.getExt2());
        systemProductionLog.setExt3(systemProductionLogUpdate.getExt3() == null ? systemProductionLog.getExt3() :
                systemProductionLogUpdate.getExt3());
        return systemProductionLog;
    }

    public static List<SystemProductionLog> toSystemProductionLogs(List<SystemProductionLogDTO> systemProductionLogDTOS) {
        return systemProductionLogDTOS.stream().map(s -> toSystemProductionLog(s)).collect(Collectors.toList());
    }

    public static List<SystemProductionLogDTO> toSystemProductionLogDTOs(List<SystemProductionLog> systemProductionLogs) {
        return systemProductionLogs.stream().map(s -> toSystemProductionLogDTO(s)).collect(Collectors.toList());
    }
}
