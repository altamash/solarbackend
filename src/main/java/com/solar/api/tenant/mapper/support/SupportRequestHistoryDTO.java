package com.solar.api.tenant.mapper.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.user.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportRequestHistoryDTO {

    private Long id;
    private Long srId;
    private UserDTO userDTO;
    private String message;
    private String firstName;
    private String lastName;
    private String role;
    private String requestAction;
    private Long responderUserId;
    private Integer nowWaitingOn;
    private Date responseDateTime;
    private String sequenceNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
