package com.solar.api.tenant.mapper.customerSupport;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerMapper {
    public static CustomerDTO toCustomerDTO(CustomerDTO customerDTO) {
          return CustomerDTO.builder()
                  .acctId(customerDTO.getAcctId())
                  .imageUrl(customerDTO.getImageUrl())
                  .customerName(customerDTO.getCustomerName())
                  .customerType(customerDTO.getCustomerType())
                  .entityId(customerDTO.getEntityId())
                  .build();
    }
    public static CustomerDTO toCustomerDTO(CustomerSubscriptionDTO firstSubscription) {
        String customerType = firstSubscription.getCustomerType();
        String customerName = firstSubscription.getCustomerName();
        Long entityId = firstSubscription.getEntityId();
        Long acctId = firstSubscription.getAcctId();
        String imageUrl = firstSubscription.getImage() != null ? firstSubscription.getImage() : null;
        return CustomerDTO.builder()
                .acctId(acctId)
                .imageUrl(imageUrl)
                .customerName(customerName)
                .customerType(customerType)
                .entityId(entityId)
                .build();
    }

    public static List<CustomerDTO> toCustomerDTOList(List<CustomerDTO> customerDTOList) {
        return customerDTOList.stream().map(CustomerMapper::toCustomerDTO).collect(Collectors.toList());
    }

}
