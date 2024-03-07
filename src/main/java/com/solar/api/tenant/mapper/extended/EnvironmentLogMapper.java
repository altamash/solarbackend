package com.solar.api.tenant.mapper.extended;

import com.solar.api.tenant.model.extended.EnvironmentLog;

import java.util.List;
import java.util.stream.Collectors;

public class EnvironmentLogMapper {

    public static EnvironmentLog toEnvironmentLog(EnvironmentLogDTO environmentLogDTO) {
        if (environmentLogDTO == null) {
            return null;
        }
        return EnvironmentLog.builder()
                .id(environmentLogDTO.getId())
                .assetRefId(environmentLogDTO.getAssetRefId())
                .siteRefId(environmentLogDTO.getSiteRefId())
                .sensorId(environmentLogDTO.getSensorId())
                .sensorType(environmentLogDTO.getSensorType())
                .reading(environmentLogDTO.getReading())
                .datetime(environmentLogDTO.getDatetime())
                .batchId(environmentLogDTO.getBatchId())
                .sourceSystem(environmentLogDTO.getSourceSystem())
                .notes(environmentLogDTO.getNotes())
                .ext1(environmentLogDTO.getExt1())
                .ext2(environmentLogDTO.getExt2())
                .ext3(environmentLogDTO.getExt3())
                .build();
    }

    public static EnvironmentLogDTO toEnvironmentLogDTO(EnvironmentLog environmentLog) {
        if (environmentLog == null) {
            return null;
        }
        return EnvironmentLogDTO.builder()
                .id(environmentLog.getId())
                .assetRefId(environmentLog.getAssetRefId())
                .siteRefId(environmentLog.getSiteRefId())
                .sensorId(environmentLog.getSensorId())
                .sensorType(environmentLog.getSensorType())
                .reading(environmentLog.getReading())
                .datetime(environmentLog.getDatetime())
                .batchId(environmentLog.getBatchId())
                .sourceSystem(environmentLog.getSourceSystem())
                .notes(environmentLog.getNotes())
                .ext1(environmentLog.getExt1())
                .ext2(environmentLog.getExt2())
                .ext3(environmentLog.getExt3())
                .createdAt(environmentLog.getCreatedAt())
                .updatedAt(environmentLog.getUpdatedAt())
                .build();
    }

    public static EnvironmentLog toUpdatedEnvironmentLog(EnvironmentLog environmentLog,
                                                         EnvironmentLog environmentLogUpdate) {
        environmentLog.setAssetRefId(environmentLogUpdate.getAssetRefId() == null ? environmentLog.getAssetRefId() :
                environmentLogUpdate.getAssetRefId());
        environmentLog.setSiteRefId(environmentLogUpdate.getSiteRefId() == null ? environmentLog.getSiteRefId() :
                environmentLogUpdate.getSiteRefId());
        environmentLog.setSensorId(environmentLogUpdate.getSensorId() == null ? environmentLog.getSensorId() :
                environmentLogUpdate.getSensorId());
        environmentLog.setSensorType(environmentLogUpdate.getSensorType() == null ? environmentLog.getSensorType() :
                environmentLogUpdate.getSensorType());
        environmentLog.setReading(environmentLogUpdate.getReading() == null ? environmentLog.getReading() :
                environmentLogUpdate.getReading());
        environmentLog.setDatetime(environmentLogUpdate.getDatetime() == null ? environmentLog.getDatetime() :
                environmentLogUpdate.getDatetime());
        environmentLog.setBatchId(environmentLogUpdate.getBatchId() == null ? environmentLog.getBatchId() :
                environmentLogUpdate.getBatchId());
        environmentLog.setSourceSystem(environmentLogUpdate.getSourceSystem() == null ?
                environmentLog.getSourceSystem() : environmentLogUpdate.getSourceSystem());
        environmentLog.setNotes(environmentLogUpdate.getNotes() == null ? environmentLog.getNotes() :
                environmentLogUpdate.getNotes());
        environmentLog.setExt1(environmentLogUpdate.getExt1() == null ? environmentLog.getExt1() :
                environmentLogUpdate.getExt1());
        environmentLog.setExt2(environmentLogUpdate.getExt2() == null ? environmentLog.getExt2() :
                environmentLogUpdate.getExt2());
        return environmentLog;
    }

    public static List<EnvironmentLog> toEnvironmentLogs(List<EnvironmentLogDTO> environmentLogDTOS) {
        return environmentLogDTOS.stream().map(h -> toEnvironmentLog(h)).collect(Collectors.toList());
    }

    public static List<EnvironmentLogDTO> toEnvironmentLogDTOs(List<EnvironmentLog> environmentLogs) {
        return environmentLogs.stream().map(h -> toEnvironmentLogDTO(h)).collect(Collectors.toList());
    }
}
