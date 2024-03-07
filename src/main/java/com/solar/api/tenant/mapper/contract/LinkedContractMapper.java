package com.solar.api.tenant.mapper.contract;

import java.util.List;
import java.util.stream.Collectors;

public class LinkedContractMapper {


    public static List<LinkedContractDTO> toLinkedContractDTOList(List<LinkedContractResultDTO> linkedContractResultDTOList) {
        return linkedContractResultDTOList.stream().map(LinkedContractMapper::toLinkedContractDTO).collect(Collectors.toList());
    }
    public static LinkedContractDTO toLinkedContractDTO(LinkedContractResultDTO linkedContractResult) {
        if (linkedContractResult == null) {
            return null;
        }

        return LinkedContractDTO.builder()
                .id(null)
                .productId(linkedContractResult.getProductId())
                .variantId(linkedContractResult.get_id())
                .gardenName(linkedContractResult.getVariant_alias() == null ? linkedContractResult.getName() : linkedContractResult.getVariant_alias())
                .gardenDescription(linkedContractResult.getDescription())
                .build();
    }
}
