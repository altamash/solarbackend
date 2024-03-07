package com.solar.api.tenant.model.extended.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_attendance_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDateTime timeOut;
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
