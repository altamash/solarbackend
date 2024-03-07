package com.solar.api.tenant.mapper.extended.resources;

import com.solar.api.tenant.model.extended.resources.ResourceAttendanceLog;

import java.util.List;
import java.util.stream.Collectors;


public class ResourceAttendanceLogMapper {

    public static ResourceAttendanceLog toResourceAttendanceLog(ResourceAttendanceLogDTO resourceAttendanceLogDTO) {
        if (resourceAttendanceLogDTO == null) {
            return null;
        }
        return ResourceAttendanceLog.builder()
                .id(resourceAttendanceLogDTO.getId())
                .employeeId(resourceAttendanceLogDTO.getEmployeeId())
                .systemRoleId(resourceAttendanceLogDTO.getSystemRoleId())
                .projectId(resourceAttendanceLogDTO.getProjectId())
                .externalRoleId(resourceAttendanceLogDTO.getExternalRoleId())
                .taskId(resourceAttendanceLogDTO.getTaskId())
                .hours(resourceAttendanceLogDTO.getHours())
                .timeIn(resourceAttendanceLogDTO.getTimeIn())
                .timeOut(resourceAttendanceLogDTO.getTime_out())
                .workDate(resourceAttendanceLogDTO.getWorkDate())
                .status(resourceAttendanceLogDTO.getStatus())
                .build();
    }

    public static ResourceAttendanceLogDTO toResourceAttendanceLogDTO(ResourceAttendanceLog resourceAttendanceLog) {
        if (resourceAttendanceLog == null) {
            return null;
        }
        return ResourceAttendanceLogDTO.builder()
                .id(resourceAttendanceLog.getId())
                .employeeId(resourceAttendanceLog.getEmployeeId())
                .systemRoleId(resourceAttendanceLog.getSystemRoleId())
                .projectId(resourceAttendanceLog.getProjectId())
                .externalRoleId(resourceAttendanceLog.getExternalRoleId())
                .taskId(resourceAttendanceLog.getTaskId())
                .hours(resourceAttendanceLog.getHours())
                .timeIn(resourceAttendanceLog.getTimeIn())
                .time_out(resourceAttendanceLog.getTimeOut())
                .workDate(resourceAttendanceLog.getWorkDate())
                .status(resourceAttendanceLog.getStatus())
                .build();
    }

    public static ResourceAttendanceLog toUpdatedResourceAttendanceLog(ResourceAttendanceLog resourceAttendanceLog, ResourceAttendanceLog resourceAttendanceLogUpdate) {
        resourceAttendanceLog.setEmployeeId(resourceAttendanceLogUpdate.getEmployeeId() == null ? resourceAttendanceLog.getEmployeeId() :
                resourceAttendanceLogUpdate.getEmployeeId());
        resourceAttendanceLog.setProjectId(resourceAttendanceLogUpdate.getProjectId() == null ? resourceAttendanceLog.getProjectId() :
                resourceAttendanceLogUpdate.getProjectId());
        resourceAttendanceLog.setTaskId(resourceAttendanceLogUpdate.getTaskId() == null ? resourceAttendanceLog.getTaskId() :
                resourceAttendanceLogUpdate.getTaskId());
        resourceAttendanceLog.setExternalRoleId(resourceAttendanceLogUpdate.getExternalRoleId() == null ? resourceAttendanceLog.getExternalRoleId() :
                resourceAttendanceLogUpdate.getExternalRoleId());
        resourceAttendanceLog.setSystemRoleId(resourceAttendanceLogUpdate.getSystemRoleId() == null ? resourceAttendanceLog.getSystemRoleId() :
                resourceAttendanceLogUpdate.getSystemRoleId());
        resourceAttendanceLog.setTimeIn(resourceAttendanceLogUpdate.getTimeIn() == null ? resourceAttendanceLog.getTimeIn() :
                resourceAttendanceLogUpdate.getTimeIn());
        resourceAttendanceLog.setTimeOut(resourceAttendanceLogUpdate.getTimeOut() == null ? resourceAttendanceLog.getTimeOut() :
                resourceAttendanceLogUpdate.getTimeOut());
        resourceAttendanceLog.setStatus(resourceAttendanceLogUpdate.getStatus() == null ? resourceAttendanceLog.getStatus() :
                resourceAttendanceLogUpdate.getStatus());
        resourceAttendanceLog.setHours(resourceAttendanceLogUpdate.getHours() == null ? resourceAttendanceLog.getHours() :
                resourceAttendanceLogUpdate.getHours());
        return resourceAttendanceLog;
    }

    public static List<ResourceAttendanceLog> toResourceAttendanceLogs(List<ResourceAttendanceLogDTO> resourceAttendanceLogDTOS) {
        return resourceAttendanceLogDTOS.stream().map(p -> toResourceAttendanceLog(p)).collect(Collectors.toList());
    }

    public static List<ResourceAttendanceLogDTO> toResourceAttendanceLogDTOs(List<ResourceAttendanceLog> resourceAttendanceLogs) {
        return resourceAttendanceLogs.stream().map(p -> toResourceAttendanceLogDTO(p)).collect(Collectors.toList());
    }
}
