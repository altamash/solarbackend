package com.solar.api.tenant.mapper.subscription.customerSubscriptionMapping;

import com.solar.api.tenant.model.subscription.CustomerSubscriptionMapping;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerSubscriptionMappingMapper {

    public static CustomerSubscriptionMapping toCustomerSubscriptionMapping(CustomerSubscriptionMappingDTO customerSubscriptionMappingDTO) {
        return CustomerSubscriptionMapping.builder()
                .id(customerSubscriptionMappingDTO.getId())
                .subscriptionId(customerSubscriptionMappingDTO.getId())
                .subscriptionRateMatrixId(customerSubscriptionMappingDTO.getSubscriptionRateMatrixId())
                .rateCode(customerSubscriptionMappingDTO.getRateCode())
                .value(customerSubscriptionMappingDTO.getValue())
                .defaultValue(customerSubscriptionMappingDTO.getDefaultValue())
                .level(customerSubscriptionMappingDTO.getLevel())
                .status(customerSubscriptionMappingDTO.getStatus())
                .effectiveDate(customerSubscriptionMappingDTO.getEffectiveDate())
                .endDate(customerSubscriptionMappingDTO.getEndDate())
                .hourOfDay(customerSubscriptionMappingDTO.getHourOfDay())
                .build();
    }

    public static CustomerSubscriptionMappingDTO toCustomerSubscriptionMappingDTO(CustomerSubscriptionMapping customerSubscriptionMapping) {
        if (customerSubscriptionMapping == null) {
            return null;
        }
        return CustomerSubscriptionMappingDTO.builder()
                .id(customerSubscriptionMapping.getId())
                .subscriptionId(customerSubscriptionMapping.getSubscription() != null ?
                        customerSubscriptionMapping.getSubscription().getId() : null)
                .subscriptionRateMatrixId(customerSubscriptionMapping.getSubscriptionRateMatrixHead() != null ?
                        customerSubscriptionMapping.getSubscriptionRateMatrixHead().getId() : null)
                .rateCode(customerSubscriptionMapping.getRateCode())
                .value(customerSubscriptionMapping.getValue())
                .defaultValue(customerSubscriptionMapping.getDefaultValue())
                .level(customerSubscriptionMapping.getLevel())
                .status(customerSubscriptionMapping.getStatus())
                .effectiveDate(customerSubscriptionMapping.getEffectiveDate())
                .endDate(customerSubscriptionMapping.getEndDate())
                .hourOfDay(customerSubscriptionMapping.getHourOfDay())
//                .subscriptionRateMatrixHead(SubscriptionRateTypeMatrixMapper.toSubscriptionRateMatrixHeadDTO
//                (customerSubscriptionMapping.getSubscriptionRateMatrixHead()))
                .measureDefinition(customerSubscriptionMapping.getMeasureDefinition())
                .createdAt(customerSubscriptionMapping.getCreatedAt())
                .updatedAt(customerSubscriptionMapping.getUpdatedAt())
                .build();
    }

    public static CustomerSubscriptionMapping toUpdatedCustomerSubscriptionMapping(CustomerSubscriptionMapping customerSubscriptionMapping, CustomerSubscriptionMapping customerSubscriptionMappingUpdate) {
        customerSubscriptionMapping.setSubscriptionRateMatrixHead(customerSubscriptionMappingUpdate.getSubscriptionRateMatrixHead() == null ? customerSubscriptionMapping.getSubscriptionRateMatrixHead() : customerSubscriptionMappingUpdate.getSubscriptionRateMatrixHead());
        customerSubscriptionMapping.setRateCode(customerSubscriptionMappingUpdate.getRateCode() == null ?
                customerSubscriptionMapping.getRateCode() : customerSubscriptionMappingUpdate.getRateCode());
        customerSubscriptionMapping.setValue(customerSubscriptionMappingUpdate.getValue() == null ?
                customerSubscriptionMapping.getValue() : customerSubscriptionMappingUpdate.getValue());
        customerSubscriptionMapping.setDefaultValue(customerSubscriptionMappingUpdate.getDefaultValue() == null ?
                customerSubscriptionMapping.getDefaultValue() : customerSubscriptionMappingUpdate.getDefaultValue());
        customerSubscriptionMapping.setLevel(customerSubscriptionMappingUpdate.getLevel() == null ?
                customerSubscriptionMapping.getLevel() : customerSubscriptionMappingUpdate.getLevel());
        customerSubscriptionMapping.setStatus(customerSubscriptionMappingUpdate.getStatus() == null ?
                customerSubscriptionMapping.getStatus() : customerSubscriptionMappingUpdate.getStatus());
        customerSubscriptionMapping.setEffectiveDate(customerSubscriptionMappingUpdate.getEffectiveDate() == null ?
                customerSubscriptionMapping.getEffectiveDate() : customerSubscriptionMappingUpdate.getEffectiveDate());
        customerSubscriptionMapping.setEndDate(customerSubscriptionMappingUpdate.getEndDate() == null ?
                customerSubscriptionMapping.getEndDate() : customerSubscriptionMappingUpdate.getEndDate());
        customerSubscriptionMapping.setHourOfDay(customerSubscriptionMappingUpdate.getHourOfDay() == null ?
                customerSubscriptionMapping.getHourOfDay() : customerSubscriptionMappingUpdate.getHourOfDay());
        return customerSubscriptionMapping;
    }

    public static List<CustomerSubscriptionMapping> toCustomerSubscriptionMappings(List<CustomerSubscriptionMappingDTO> customerSubscriptionMappingDTOS) {
        return customerSubscriptionMappingDTOS.stream().map(c -> toCustomerSubscriptionMapping(c)).collect(Collectors.toList());
    }

    public static List<CustomerSubscriptionMappingDTO> toCustomerSubscriptionMappingDTOs(List<CustomerSubscriptionMapping> customerSubscriptionMappings) {
        return customerSubscriptionMappings.stream().map(c -> toCustomerSubscriptionMappingDTO(c)).collect(Collectors.toList());
    }
}
