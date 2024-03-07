package com.solar.api.tenant.mapper.contract;


import com.solar.api.tenant.model.contract.ContractMapping;

import java.util.List;
import java.util.stream.Collectors;

public class ContractMappingMapper {
    public static ContractMapping toContractMapping(ContractMappingDTO contractMappingDTO) {
        return ContractMapping.builder()
//                .contractId(contractMappingDTO.getContractId())
//                .subContractId(contractMappingDTO.getSubContractId())
                .subContractType(contractMappingDTO.getSubContractType())
                .status(contractMappingDTO.getStatus())
                .createdAt(contractMappingDTO.getCreatedAt())
                .updatedAt(contractMappingDTO.getUpdatedAt())
                .build();
    }

    public static ContractMappingDTO toContractMappingDTO(ContractMapping contractMapping) {
        if (contractMapping == null) {
            return null;
        }

        return ContractMappingDTO.builder()
                .contractId(contractMapping.getContract().getId())
                .subContractId(contractMapping.getSubContract().getId())
                .subContractType(contractMapping.getSubContractType())
                .status(contractMapping.getStatus())
                .createdAt(contractMapping.getCreatedAt())
                .updatedAt(contractMapping.getUpdatedAt())
                .build();
    }

    public static List<ContractMapping> toContractMappingList(List<ContractMappingDTO> contractMappingDTOList) {
        return contractMappingDTOList.stream().map(ContractMappingMapper::toContractMapping).collect(Collectors.toList());
    }

    public static List<ContractMappingDTO> toContractMappingDTOList(List<ContractMapping> contractMappingList) {
        return contractMappingList.stream().map(ContractMappingMapper::toContractMappingDTO).collect(Collectors.toList());
    }
}
