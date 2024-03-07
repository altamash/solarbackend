package com.solar.api.tenant.mapper.user.userType;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTypeDTO {

    private Long id;
    private String name;
}
