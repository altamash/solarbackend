package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.resources.ResourceAttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceAttendanceLogRepository extends JpaRepository<ResourceAttendanceLog, Long> , ResourceAttendanceLogRepositoryCustom{

    List<ResourceAttendanceLog> findByEmployeeIdAndExternalRoleIdAndHours(Long employeeId, String externalRoleId , String hours);

    List<ResourceAttendanceLog> findByEmployeeIdAndTaskId(Long employeeId, Long taskId);

//    @Query("select ral from ResourceAttendanceLog ral where ral.employeeId=:employeeId, ral.taskId=:taskId, ral.workDate=:workDate")
    List<ResourceAttendanceLog> findByEmployeeIdAndTaskIdAndWorkDate(Long employeeId, Long taskId, String workDate);
}
