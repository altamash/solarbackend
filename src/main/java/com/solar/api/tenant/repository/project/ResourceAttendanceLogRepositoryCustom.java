package com.solar.api.tenant.repository.project;

import org.springframework.data.repository.query.Param;

public interface ResourceAttendanceLogRepositoryCustom {

    Double hoursByRole(@Param("employeeId") Long employeeId, @Param("roleId") Long roleId, @Param("taskId") Long taskId);

    Double hoursByEmployeeIdAndProjectId(@Param("employeeId") Long employeeId, @Param("projectId") Long projectId);

    Double hoursByTaskId(@Param("taskId") Long taskId);

//    List<ResourceAttendanceLog> findByEmployeeIdAndTaskIdAndWorkDate(Long employeeId, Long taskId, String workDate);
}
