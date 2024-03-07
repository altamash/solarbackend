package com.solar.api.tenant.mapper.user.userGroup;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGroupResourceDTO {

    String name;
    String email;
    String projectRole;
    String phoneNumber;
    int numOfTasks;
    String joiningDate;
    String status;


}
