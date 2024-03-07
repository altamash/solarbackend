package com.solar.api.tenant.mapper.contract;

import com.solar.api.tenant.model.contract.Contract;

import java.util.List;
import java.util.stream.Collectors;

public class ContractMapper {
    public static Contract toContract(ContractDTO contractDTO) {
        return Contract.builder()
                .id(contractDTO.getId())
                .contractName(contractDTO.getContractName())
                .contractType(contractDTO.getContractType())
                .masterAgreementContractId(contractDTO.getMasterAgreementContractId())
                .status(contractDTO.getStatus())
                .isRenewable(contractDTO.getIsRenewable())
                .isDocAttached(contractDTO.getIsDocAttached())
                .startDate(contractDTO.getStartDate())
                .expiryDate(contractDTO.getExpiryDate())
                .createdAt(contractDTO.getCreatedAt())
                .updatedAt(contractDTO.getUpdatedAt())
                .build();
    }

    public static ContractDTO toContractDTO(Contract contract) {
        if (contract == null) {
            return null;
        }

        return ContractDTO.builder()
                .id(contract.getId())
                .contractName(contract.getContractName())
                .contractType(contract.getContractType())
                .masterAgreementContractId(contract.getMasterAgreementContractId())
                .status(contract.getStatus())
                .isRenewable(contract.getIsRenewable())
                .isDocAttached(contract.getIsDocAttached())
                .startDate(contract.getStartDate())
                .expiryDate(contract.getExpiryDate())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }

    public static List<Contract> toContractList(List<ContractDTO> contractDTOList) {
        return contractDTOList.stream().map(ContractMapper::toContract).collect(Collectors.toList());
    }

    public static List<ContractDTO> toContractDTOList(List<Contract> contractList) {
        return contractList.stream().map(ContractMapper::toContractDTO).collect(Collectors.toList());
    }
}
