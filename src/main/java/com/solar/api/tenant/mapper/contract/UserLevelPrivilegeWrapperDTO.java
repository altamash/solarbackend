package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLevelPrivilegeWrapperDTO {
    Long accountId;
    List<Long> organizationIds;
    List<Long> entityIds;
    List<Long> contractIds;
}
