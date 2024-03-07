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
public class ContractMappingDTO {
    private Long contractId;
    private Long subContractId; //TODO: To be changed to String when migrated to mongo
    private String subContractType;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
