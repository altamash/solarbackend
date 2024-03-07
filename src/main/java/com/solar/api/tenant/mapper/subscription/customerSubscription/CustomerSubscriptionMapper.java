package com.solar.api.tenant.mapper.subscription.customerSubscription;

import com.solar.api.saas.service.integration.mongo.response.subscription.Id;
import com.solar.api.saas.service.integration.mongo.response.subscription.Subscription;
import com.solar.api.tenant.mapper.subscription.customerSubscriptionMapping.CustomerSubscriptionMappingMapper;
import com.solar.api.tenant.mapper.subscription.subscriptionRateMatrix.SubscriptionRateTypeMatrixMapper;
import com.solar.api.tenant.mapper.user.UserDTO;
import com.solar.api.tenant.mapper.user.address.AddressMapper;
import com.solar.api.tenant.model.subscription.CustomerSubscription;
import com.solar.api.tenant.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class CustomerSubscriptionMapper {

    public static CustomerSubscription toCustomerSubscription(CustomerSubscriptionDTO customerSubscriptionDTO) {
        return CustomerSubscription.builder()
                .id(customerSubscriptionDTO.getId())
                .userAccountId(customerSubscriptionDTO.getUserAccountId())
                .startDate(customerSubscriptionDTO.getStartDate())
                .endDate(customerSubscriptionDTO.getEndDate())
                .subscriptionStatus(customerSubscriptionDTO.getSubscriptionStatus())
                .billStatus(customerSubscriptionDTO.getBillStatus())
                .arrayLocationRef(customerSubscriptionDTO.getArrayLocationRef())
                .address(AddressMapper.toAddress(customerSubscriptionDTO.getAddress()))
                .customerSubscriptionMappings(CustomerSubscriptionMappingMapper.toCustomerSubscriptionMappings(customerSubscriptionDTO.getCustomerSubscriptionMappings()))
                .subscriptionType(customerSubscriptionDTO.getSubscriptionType())
                .subscriptionTemplate(customerSubscriptionDTO.getSubscriptionTemplate())
                .subscriptionRateMatrixId(customerSubscriptionDTO.getSubscriptionRateMatrixId())
                .terminationDate(customerSubscriptionDTO.getTerminationDate())
                .closedDate(customerSubscriptionDTO.getClosedDate())
                .build();
    }

    public static Subscription toSubscription(CustomerSubscriptionDTO customerSubscriptionDTO) {
        return Subscription.builder()
                .id(Id.builder().oid(customerSubscriptionDTO.getSubscriptionId()).build())
                .productGroup(customerSubscriptionDTO.getProductGroup())
                .variantGroup(customerSubscriptionDTO.getVariantGroup())
                .active(customerSubscriptionDTO.getActive())
                .startDate(customerSubscriptionDTO.getStartDate())
                .endDate(customerSubscriptionDTO.getEndDate())
                .measures(customerSubscriptionDTO.getMeasures())
                .build();
    }

    public static CustomerSubscriptionDTO toCustomerSubscriptionDTO(CustomerSubscription customerSubscription) {
        if (customerSubscription == null) {
            return null;
        }
        return CustomerSubscriptionDTO.builder()
                .id(customerSubscription.getId())
                .userAccountId(customerSubscription.getUserAccountId())
                .subscriptionRateMatrixId(customerSubscription.getSubscriptionRateMatrixHead() != null ?
                        customerSubscription.getSubscriptionRateMatrixHead().getId() : null)
                .startDate(customerSubscription.getStartDate())
                .endDate(customerSubscription.getEndDate())
                .subscriptionStatus(customerSubscription.getSubscriptionStatus())
                .billStatus(customerSubscription.getBillStatus())
                .arrayLocationRef(customerSubscription.getArrayLocationRef())
                .address(AddressMapper.toAddressDTO(customerSubscription.getAddress()))
                .customerSubscriptionMappings(CustomerSubscriptionMappingMapper.toCustomerSubscriptionMappingDTOs(customerSubscription.getCustomerSubscriptionMappings()))
                .subscriptionType(customerSubscription.getSubscriptionType())
                .subscriptionTemplate(customerSubscription.getSubscriptionTemplate())
                .subscriptionRateMatrixHead(SubscriptionRateTypeMatrixMapper.toSubscriptionRateMatrixHeadDTO(customerSubscription.getSubscriptionRateMatrixHead()))
                .rollValue(customerSubscription.getRollValue())
                .terminationDate(customerSubscription.getAutoTerminationDate()!=null? customerSubscription.getAutoTerminationDate() : customerSubscription.getTerminationDate())
                .closedDate(customerSubscription.getClosedDate())
                .terminationReason(customerSubscription.getTerminationReason())
                .markedForDeletion(customerSubscription.getMarkedForDeletion())
                .build();
    }

    public static CustomerSubscription toUpdatedCustomerSubscription(CustomerSubscription customerSubscription,
                                                                     CustomerSubscription customerSubscriptionUpdate) {

        customerSubscription.setSubscriptionRateMatrixHead(customerSubscriptionUpdate.getSubscriptionRateMatrixHead() == null ? customerSubscription.getSubscriptionRateMatrixHead() : customerSubscription.getSubscriptionRateMatrixHead());

        customerSubscription.setId(customerSubscriptionUpdate.getId());

        customerSubscription.setStartDate(customerSubscriptionUpdate.getStartDate() == null ?
                customerSubscription.getStartDate() : customerSubscriptionUpdate.getStartDate());
        customerSubscription.setEndDate(customerSubscriptionUpdate.getEndDate() == null ?
                customerSubscription.getEndDate() : customerSubscriptionUpdate.getEndDate());
        customerSubscription.setSubscriptionStatus(customerSubscriptionUpdate.getSubscriptionStatus() == null ?
                customerSubscription.getSubscriptionStatus() : customerSubscriptionUpdate.getSubscriptionStatus());
        customerSubscription.setBillStatus(customerSubscriptionUpdate.getBillStatus() == null ?
                customerSubscription.getBillStatus() : customerSubscriptionUpdate.getBillStatus());
        customerSubscription.setArrayLocationRef(customerSubscriptionUpdate.getArrayLocationRef() == null ?
                customerSubscription.getArrayLocationRef() : customerSubscriptionUpdate.getArrayLocationRef());
        customerSubscription.setAddress(customerSubscriptionUpdate.getAddress() == null ?
                customerSubscription.getAddress() : customerSubscriptionUpdate.getAddress());
       customerSubscription.setTerminationDate(customerSubscriptionUpdate.getTerminationDate() == null ?
                customerSubscription.getTerminationDate() : customerSubscriptionUpdate.getTerminationDate());
        customerSubscription.setClosedDate(customerSubscriptionUpdate.getClosedDate() == null ?
                customerSubscription.getClosedDate() : customerSubscriptionUpdate.getClosedDate());
        customerSubscription.setCustomerSubscriptionMappings(customerSubscriptionUpdate.getCustomerSubscriptionMappings() == null ? customerSubscription.getCustomerSubscriptionMappings() :
                        customerSubscriptionUpdate.getCustomerSubscriptionMappings());
        return customerSubscription;
    }
       public static UserDTO toUserAndCustomerSubscriptionsDTO(User user, List<CustomerSubscription> customerSubscriptions) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .acctId(user.getAcctId())
                .jwtToken(user.getJwtToken())
                .compKey(user.getCompKey())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .IdCode(user.getIdCode())
                .authorityId(user.getAuthorityId())
                .gender(user.getGender())
                .dataOfBirth(user.getDataOfBirth())
                .registerDate(user.getRegisterDate())
                .activeDate(user.getActiveDate())
                .status(user.getStatus())
                .notes(user.getNotes())
                .prospectStatus(user.getProspectStatus())
                .referralEmail(user.getReferralEmail())
                .deferredContactDate(user.getDeferredContactDate())
                .language(user.getLanguage())
                .authentication(user.getAuthentication())
                .userType(user.getUserType().getName().getName())
                .roles(user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()))
//                .privileges(user.getPrivileges().stream().map(privilege -> privilege.getName()).collect(Collectors
//                .toSet()))
                .category(user.getCategory())
                .groupId(user.getGroupId())
                .socialUrl(user.getSocialUrl())
                .emailAddress(user.getEmailAddress())
                .ccd(user.getCcd())
                .customerSubscriptions(CustomerSubscriptionMapper.toCustomerSubscriptionDTOs(customerSubscriptions))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    public static UserDTO toUserDTOs(User user, List<CustomerSubscription> customerSubscriptions) {
        return toUserAndCustomerSubscriptionsDTO(user, customerSubscriptions);

    }
    public static List<CustomerSubscription> toCustomerSubscriptions(List<CustomerSubscriptionDTO> customerSubscriptionDTOs) {
        return customerSubscriptionDTOs.stream().map(cc -> toCustomerSubscription(cc)).collect(Collectors.toList());
    }

    public static List<CustomerSubscriptionDTO> toCustomerSubscriptionDTOs(List<CustomerSubscription> customerSubscriptions) {
        return customerSubscriptions.stream().map(cc -> toCustomerSubscriptionDTO(cc)).collect(Collectors.toList());
    }

    public static List<Subscription> toSubscriptions(List<CustomerSubscriptionDTO> customerSubscriptionDTOs) {
        return customerSubscriptionDTOs.stream().map(cc -> toSubscription(cc)).collect(Collectors.toList());
    }

}
