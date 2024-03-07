package com.solar.api.tenant.mapper.cutomer;


import com.solar.api.tenant.model.customer.CustomerDetail;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerDetailMapper {

    public static CustomerDetail toCustomerDetail(CustomerDetailDTO customerDetailDTO) {
        if (customerDetailDTO == null) {
            return null;
        }
        return CustomerDetail.builder()
                .id(customerDetailDTO.getId())
                .entityId(customerDetailDTO.getEntityId())
                .customerType(customerDetailDTO.getCustomerType())
                .states(customerDetailDTO.getStates())
                .prefix(customerDetailDTO.getPrefix())
                .category(customerDetailDTO.getCategory())
                .isActive(customerDetailDTO.isActive())
                .hasLogin(customerDetailDTO.isHasLogin())
                .mobileAllowed(customerDetailDTO.isMobileAllowed())
                .date(customerDetailDTO.getDate())
                .phoneNo(customerDetailDTO.getPhoneNo())
                .altPhoneNo(customerDetailDTO.getAltPhoneNo())
                .altEmail(customerDetailDTO.getAltEmail())
                .signUpDate(customerDetailDTO.getSignUpDate())
                .linkedCompany(customerDetailDTO.getLinkedCompany())
                .status(customerDetailDTO.getStatus())
                .priorityIndicator(customerDetailDTO.isPriorityIndicator())
                .ratingNum(customerDetailDTO.getRatingNum())
                .isCustomer(customerDetailDTO.isCustomer())
                .isContractSign(customerDetailDTO.isContractSign())
                .leadSource(customerDetailDTO.getLeadSource())
                .build();
    }

    public static CustomerDetailDTO toCustomerDetailDTO(CustomerDetail customerDetail) {
        if (customerDetail == null) {
            return null;
        }
        return CustomerDetailDTO.builder()
                .id(customerDetail.getId())
                .entityId(customerDetail.getEntityId())
                .customerType(customerDetail.getCustomerType())
                .states(customerDetail.getStates())
                .prefix(customerDetail.getPrefix())
                .category(customerDetail.getCategory())
                .isActive(customerDetail.isActive())
                .hasLogin(customerDetail.isHasLogin())
                .mobileAllowed(customerDetail.isMobileAllowed())
                .date(customerDetail.getDate())
                .phoneNo(customerDetail.getPhoneNo())
                .altPhoneNo(customerDetail.getAltPhoneNo())
                .altEmail(customerDetail.getAltEmail())
                .signUpDate(customerDetail.getSignUpDate())
                .linkedCompany(customerDetail.getLinkedCompany())
                .status(customerDetail.getStatus())
                .priorityIndicator(customerDetail.isPriorityIndicator())
                .ratingNum(customerDetail.getRatingNum())
                .isCustomer(customerDetail.isCustomer())
                .isContractSign(customerDetail.isContractSign())
                .build();
    }

    public static CustomerDetail toUpdatedCustomerDetail(CustomerDetail customerDetail, CustomerDetail customerDetailUpdate) {
        customerDetail.setEntityId(customerDetailUpdate.getEntityId() == null ? customerDetail.getEntityId() :
                customerDetailUpdate.getEntityId());
        customerDetail.setCustomerType(customerDetailUpdate.getCustomerType() == null ? customerDetail.getCustomerType() :
                customerDetailUpdate.getCustomerType());
        customerDetail.setStates(customerDetailUpdate.getStates() == null ? customerDetail.getStates() : customerDetailUpdate.getStates());
        customerDetail.setPrefix(customerDetailUpdate.getPrefix() == null ? customerDetail.getPrefix() : customerDetailUpdate.getPrefix());
        customerDetail.setCategory(customerDetailUpdate.getCategory() == null ? customerDetail.getCategory() : customerDetailUpdate.getCategory());
        customerDetail.setActive(customerDetailUpdate.isActive() ? customerDetail.isActive() : customerDetailUpdate.isActive());
        customerDetail.setHasLogin(customerDetailUpdate.isHasLogin() ? customerDetail.isActive() : customerDetailUpdate.isActive());
        customerDetail.setMobileAllowed(customerDetailUpdate.isMobileAllowed() ? customerDetail.isActive() : customerDetailUpdate.isActive());
        customerDetail.setDate(customerDetailUpdate.getDate() == null ? customerDetail.getDate() : customerDetailUpdate.getDate());
        customerDetail.setPhoneNo(customerDetailUpdate.getPhoneNo() == null ? customerDetail.getPhoneNo() : customerDetailUpdate.getPhoneNo());
        customerDetail.setAltPhoneNo(customerDetailUpdate.getAltPhoneNo() == null ? customerDetail.getAltPhoneNo() : customerDetailUpdate.getAltPhoneNo());
        customerDetail.setAltEmail(customerDetailUpdate.getAltEmail() == null ? customerDetail.getAltEmail() : customerDetailUpdate.getAltEmail());
        customerDetail.setSignUpDate(customerDetailUpdate.getSignUpDate() == null ? customerDetail.getSignUpDate() : customerDetailUpdate.getSignUpDate());
        customerDetail.setLinkedCompany(customerDetailUpdate.getLinkedCompany() == null ? customerDetail.getLinkedCompany() : customerDetailUpdate.getLinkedCompany());
        customerDetail.setStatus(customerDetailUpdate.getStatus() == null ? customerDetail.getStatus() : customerDetailUpdate.getStatus());
        customerDetail.setPriorityIndicator(customerDetailUpdate.isPriorityIndicator() ? customerDetail.isPriorityIndicator() : customerDetailUpdate.isPriorityIndicator());
        customerDetail.setRatingNum(customerDetailUpdate.getRatingNum() == null ? customerDetail.getRatingNum() : customerDetailUpdate.getRatingNum());
        customerDetail.setCustomer(customerDetailUpdate.isCustomer() ? customerDetail.isCustomer() : customerDetailUpdate.isCustomer());
        customerDetail.setContractSign(customerDetailUpdate.isContractSign() ? customerDetail.isContractSign() : customerDetailUpdate.isContractSign());

        return customerDetail;
    }

    public static List<CustomerDetail> toCustomerDetails(List<CustomerDetailDTO> customerDetailDTOS) {
        return customerDetailDTOS.stream().map(e -> toCustomerDetail(e)).collect(Collectors.toList());
    }

    public static List<CustomerDetailDTO> toCustomerDetailDTOs(List<CustomerDetail> customerDetails) {
        return customerDetails.stream().map(a -> toCustomerDetailDTO(a)).collect(Collectors.toList());
    }
}
