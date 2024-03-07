package com.solar.api.tenant.mapper.extended.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckInDTO {

    private Long resourceAttendanceLogId;
    private Long employeeId;
    private Long projectId;
    private Long taskId;
    private Long systemRoleId;
    private Timestamp time;
    private String timeZone;
}
