package com.solar.api.tenant.mapper.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLevelPrivilegeDTO {
    private Long id;
    private Long acctId;
    private Long contractId;
    private Long entityId;
    private Long organizationId;
    private Long roleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserLevelPrivilegeDTO(Long id, Long acctId, Long contractId, Long entityId, Long organizationId, Long roleId) {
        this.id = id;
        this.acctId = acctId;
        this.contractId = contractId;
        this.entityId = entityId;
        this.organizationId = organizationId;
        this.roleId = roleId;
    }
}
