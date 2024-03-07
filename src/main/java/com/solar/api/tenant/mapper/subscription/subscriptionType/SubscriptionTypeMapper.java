package com.solar.api.tenant.mapper.subscription.subscriptionType;

import com.solar.api.tenant.model.subscription.SubscriptionType;

import java.util.List;
import java.util.stream.Collectors;

public class SubscriptionTypeMapper {

    public static SubscriptionType toSubscriptionType(SubscriptionTypeDTO subscriptionTypeDTO) {
        return SubscriptionType.builder()
                .id(subscriptionTypeDTO.getId())
                .subscriptionName(subscriptionTypeDTO.getSubscriptionName())
                .primaryGroup(subscriptionTypeDTO.getPrimaryGroup())
                .code(subscriptionTypeDTO.getCode())
                .generateCycle(subscriptionTypeDTO.getGenerateCycle())
                .billingCycle(subscriptionTypeDTO.getBillingCycle())
                .alias(subscriptionTypeDTO.getAlias())
                .build();
    }

    public static SubscriptionTypeDTO toSubscriptionTypeDTO(SubscriptionType subscriptionType) {
        if (subscriptionType == null) {
            return null;
        }
        return SubscriptionTypeDTO.builder()
                .id(subscriptionType.getId())
                .subscriptionName(subscriptionType.getSubscriptionName())
                .primaryGroup(subscriptionType.getPrimaryGroup())
                .code(subscriptionType.getCode())
                .generateCycle(subscriptionType.getGenerateCycle())
                .billingCycle(subscriptionType.getBillingCycle())
                .alias(subscriptionType.getAlias())
                .createdAt(subscriptionType.getCreatedAt())
                .updatedAt(subscriptionType.getUpdatedAt())
                .build();
    }

    public static SubscriptionType toUpdatedSubscriptionType(SubscriptionType subscriptionType,
                                                             SubscriptionType subscriptionTypeUpdate) {
        subscriptionType.setSubscriptionName(subscriptionTypeUpdate.getSubscriptionName() == null ?
                subscriptionType.getSubscriptionName() : subscriptionTypeUpdate.getSubscriptionName());
        subscriptionType.setPrimaryGroup(subscriptionTypeUpdate.getPrimaryGroup() == null ?
                subscriptionType.getPrimaryGroup() : subscriptionTypeUpdate.getPrimaryGroup());
        subscriptionType.setCode(subscriptionTypeUpdate.getCode() == null ? subscriptionType.getCode() :
                subscriptionTypeUpdate.getCode());
        subscriptionType.setGenerateCycle(subscriptionTypeUpdate.getGenerateCycle() == null ?
                subscriptionType.getGenerateCycle() : subscriptionTypeUpdate.getGenerateCycle());
        subscriptionType.setBillingCycle(subscriptionTypeUpdate.getBillingCycle() == null ?
                subscriptionType.getBillingCycle() : subscriptionTypeUpdate.getBillingCycle());
        subscriptionType.setAlias(subscriptionTypeUpdate.getAlias() == null ? subscriptionType.getAlias() :
                subscriptionTypeUpdate.getAlias());
        return subscriptionType;
    }

    public static List<SubscriptionType> toSubscriptionTypes(List<SubscriptionTypeDTO> subscriptionTypeDTOS) {
        return subscriptionTypeDTOS.stream().map(c -> toSubscriptionType(c)).collect(Collectors.toList());
    }

    public static List<SubscriptionTypeDTO> toSubscriptionTypeDTOs(List<SubscriptionType> subscriptionTypes) {
        return subscriptionTypes.stream().map(c -> toSubscriptionTypeDTO(c)).collect(Collectors.toList());
    }
}
