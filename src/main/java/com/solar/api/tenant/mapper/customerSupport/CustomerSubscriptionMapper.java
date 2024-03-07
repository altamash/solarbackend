package com.solar.api.tenant.mapper.customerSupport;

import com.solar.api.tenant.model.customerSupport.ConversationReference;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerSubscriptionMapper {
    public static CustomerSubscriptionDTO toCustomerSubscriptionDTO(CustomerSubscriptionDTO subscription) {
          return CustomerSubscriptionDTO.builder()
                .acctId(subscription.getAcctId())
                .subsId(subscription.getSubsId())
                .variantId(subscription.getVariantId())
                .subscriptionName(subscription.getSubscriptionName())
                .invertedBrand(subscription.getInvertedBrand())
                .monitoringBrand(subscription.getMonitoringBrand())
                .plocId(subscription.getPlocId())
                .productId(subscription.getProductId()).build();
    }

    public static List<CustomerSubscriptionDTO> toCustomerSubscriptionDTOList(List<CustomerSubscriptionDTO> customerSubscriptionDTOs) {
        return customerSubscriptionDTOs.stream().map(CustomerSubscriptionMapper::toCustomerSubscriptionDTO).collect(Collectors.toList());
    }

}
