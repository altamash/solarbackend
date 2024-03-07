package com.solar.api.tenant.mapper.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignLeadsDTO implements Serializable {
    private Long assigneeId;
    private List<UserDTO> userDTO;

}
