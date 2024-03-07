package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.mapper.extended.project.CheckInCheckOutDTO;
import com.solar.api.tenant.model.extended.resources.ResourceAttendanceLog;
import com.solar.api.tenant.repository.project.ResourceAttendanceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceAttendanceLogServiceImpl implements ResourceAttendanceLogService {

    @Autowired
    ResourceAttendanceLogRepository resourceAttendanceLogRepository;

    @Override
    public List<ResourceAttendanceLog> findByEmployeeIdAndTaskId(Long employeeId, Long taskId) {
        return resourceAttendanceLogRepository.findByEmployeeIdAndTaskId(employeeId, taskId);
    }

    @Override
    public List<ResourceAttendanceLog> findByEmployeeIdAndTaskIdAndWorkDate(Long employeeId, Long taskId, String workDate) {
        return resourceAttendanceLogRepository.findByEmployeeIdAndTaskIdAndWorkDate(employeeId, taskId, workDate);
    }

    @Override
    public List<ResourceAttendanceLog> addCheckIn(List<CheckInCheckOutDTO> checkInCheckOutDTOList) {

        List<ResourceAttendanceLog> resourceAttendanceLogs = new ArrayList<>();

        if (!checkInCheckOutDTOList.isEmpty()) {

            for (CheckInCheckOutDTO checkInCheckOutDTO : checkInCheckOutDTOList) {
                LocalDateTime utcTimeIn = checkInCheckOutDTO.getCheckIns().getTime().toLocalDateTime();
                LocalDateTime utcTimeOut = checkInCheckOutDTO.getCheckOuts().getTime().toLocalDateTime();
                resourceAttendanceLogs.add(ResourceAttendanceLog.builder()
                        .employeeId(checkInCheckOutDTO.getCheckIns().getEmployeeId())
                        .systemRoleId(checkInCheckOutDTO.getCheckIns().getSystemRoleId())
                        .projectId(checkInCheckOutDTO.getCheckIns().getProjectId())
                        .taskId(checkInCheckOutDTO.getCheckIns().getTaskId())
                        .timeIn(utcTimeIn)
                        .timeOut(utcTimeOut)
                        .workDate(String.valueOf(utcTimeIn.toLocalDate()))
                        .build());
            }
            return resourceAttendanceLogRepository.saveAll(resourceAttendanceLogs);
        }
        return null;
    }

    @Override
    public List<ResourceAttendanceLog> addCheckOut(List<CheckInCheckOutDTO> checkInCheckOutDTOList) {
//        List<ResourceAttendanceLog> resourceAttendanceLogs = new ArrayList<>();
//
//        if (checkInCheckOutDTO.getCheckOuts() != null) {
//            for (Map.Entry<String, Long> entry : checkInCheckOutDTO.getCheckOuts().entrySet()) {
//                ResourceAttendanceLog resourceAttendanceLogDB = resourceAttendanceLogRepository.findById(checkInCheckOutDTO.getIds().get(entry.getKey()).longValue()).get();
//                LocalDateTime utcTimeOut = LocalDateTime.ofInstant(Instant.ofEpochMilli(entry.getValue()), ZoneOffset.UTC);
//                System.out.println("Key = " + entry.getKey() +
//                        ", Value = " + entry.getValue());
//                ResourceAttendanceLog resourceAttendanceLogUpdate = ResourceAttendanceLog.builder().time_out(utcTimeOut).build();
//                resourceAttendanceLogs.add(ResourceAttendanceLogMapper.toUpdatedResourceAttendanceLog(resourceAttendanceLogDB, resourceAttendanceLogUpdate));
//            }
//            return resourceAttendanceLogRepository.saveAll(resourceAttendanceLogs);
//        }
//
//        //LocalDateTime utcTimeOut = LocalDateTime.now(ZoneOffset.UTC);
//        LocalDateTime utcTimeOut = LocalDateTime.ofInstant(Instant.ofEpochMilli(new Date().getTime()), ZoneOffset.UTC);
//        for (Map.Entry<String, Long> entryIds : checkInCheckOutDTO.getIds().entrySet()) {
//            ResourceAttendanceLog resourceAttendanceLogDB = resourceAttendanceLogRepository.findById(entryIds.getValue()).get();
//            ResourceAttendanceLog resourceAttendanceLogUpdate = ResourceAttendanceLog.builder().time_out(utcTimeOut).build();
//            resourceAttendanceLogs.add(ResourceAttendanceLogMapper.toUpdatedResourceAttendanceLog(resourceAttendanceLogDB, resourceAttendanceLogUpdate));
//        }
//        return resourceAttendanceLogRepository.saveAll(resourceAttendanceLogs);
        return null;
    }
}
