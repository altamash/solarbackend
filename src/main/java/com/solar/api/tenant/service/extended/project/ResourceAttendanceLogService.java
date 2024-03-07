package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.mapper.extended.project.CheckInCheckOutDTO;
import com.solar.api.tenant.model.extended.resources.ResourceAttendanceLog;

import java.util.List;

public interface ResourceAttendanceLogService {

    List<ResourceAttendanceLog> findByEmployeeIdAndTaskId(Long employeeId, Long taskId);

    List<ResourceAttendanceLog> findByEmployeeIdAndTaskIdAndWorkDate(Long employeeId, Long taskId, String workDate);

    List<ResourceAttendanceLog> addCheckIn(List<CheckInCheckOutDTO> checkInCheckOutDTOList);

    List<ResourceAttendanceLog> addCheckOut(List<CheckInCheckOutDTO> checkInCheckOutDTOList);
}
