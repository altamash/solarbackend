package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.user.UserDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    private Long id;
    private String userName;
    private String emailAddress;
    private Date dob;
    private String firstName;
    private String middleName;
    private String lastName;
    private String userLevel;
    private String status;
    private Boolean isDocAttached;
    private UserDTO userDTO;
    private List<UserLevelPrivilegeDTO> userLevelPrivilegeDTOList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
