package com.solar.api.tenant.mapper.extended.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceAttendanceLogDTO {

    private Long id;
    private Long employeeId;
    private Long projectId;
    private Long taskId;
    private Long locationId;
    private Long systemRoleId;
    private String externalRoleId;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime timeIn;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalDateTime time_out;
    private String workDate;
    private String source; // mobile,web,external
    private String hours;
    private String status;//checkedIn,checkedOut,audited
    private String lineSequenceNo;
    private String ignoreWorkHoursFlag;
    private String auditedBy;
    private String approvedBy;
    private Long approvalJobId;
    private Long rejectedHours;
    private String rejectReason;

}
