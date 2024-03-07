package com.solar.api.tenant.mapper.user;

import com.solar.api.helper.Utility;
import com.solar.api.saas.service.process.upload.mapper.Customer;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeMapper;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMapper;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;

import java.util.List;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.user.address.AddressMapper.toAddressesSet;

public class UserMapper {

    public static User toUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        return User.builder()
                .acctId(userDTO.getAcctId())
                .compKey(userDTO.getCompKey())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .userName(userDTO.getUserName())
                .IdCode(userDTO.getIdCode())
                .authorityId(userDTO.getAuthorityId())
                .gender(userDTO.getGender())
                .dataOfBirth(userDTO.getDataOfBirth())
                .registerDate(userDTO.getRegisterDate())
                .activeDate(userDTO.getActiveDate())
                .status(userDTO.getStatus())
                .notes(userDTO.getNotes())
                .prospectStatus(userDTO.getProspectStatus())
                .referralEmail(userDTO.getReferralEmail())
                .deferredContactDate(userDTO.getDeferredContactDate())
                .language(userDTO.getLanguage())
                .authentication(userDTO.getAuthentication())
                .category(userDTO.getCategory())
                .groupId(userDTO.getGroupId())
                .socialUrl(userDTO.getSocialUrl())
                .emailAddress(userDTO.getEmailAddress())
                .ccd(userDTO.getCcd())
                .privLevel(userDTO.getPrivLevel())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
//                .userLevelPrivileges(userDTO.getUserLevelPrivilegeDTOList() != null ?
//                        UserLevelPrivilegeMapper.toUserLevelPrivilegeList(userDTO.getUserLevelPrivilegeDTOList()) : null)
                .build();
    }

    public static UserDTO toUserDTO(User user) {
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
                .roles(user.getRoles() != null ? user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()) : null)
//                .privileges(user.getPrivileges().stream().map(privilege -> privilege.getName()).collect(Collectors
//                .toSet()))
                .category(user.getCategory())
                .groupId(user.getGroupId())
                .photo(user.getPhoto())
                .socialUrl(user.getSocialUrl())
                .emailAddress(user.getEmailAddress())
                .ccd(user.getCcd())
                .addresses(user.getAddresses() != null ? toAddressesSet(user.getAddresses()) : null)
                .privLevel(user.getPrivLevel())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
//                .UserLevelPrivilegeDTOList(user.getUserLevelPrivileges() != null ?
//                        UserLevelPrivilegeMapper.toUserLevelPrivilegeDTOList(user.getUserLevelPrivileges()) : null)
                .build();
    }

    public static UserDTO toUserAndSubscriptionsDTO(User user) {
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
                .roles(user.getRoles() != null ? user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()) : null)
//                .privileges(user.getPrivileges().stream().map(privilege -> privilege.getName()).collect(Collectors
//                .toSet()))
                .category(user.getCategory())
                .groupId(user.getGroupId())
                .photo(user.getPhoto())
                .socialUrl(user.getSocialUrl())
                .emailAddress(user.getEmailAddress())
                .ccd(user.getCcd())
                .customerSubscriptions(user.getCustomerSubscriptions() != null ? CustomerSubscriptionMapper.toCustomerSubscriptionDTOs(user.getCustomerSubscriptions()) :
                        null)
                .addresses(user.getAddresses() != null ? toAddressesSet(user.getAddresses()) : null)
                .privLevel(user.getPrivLevel())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User toUpdatedUser(User user, User userUpdate) {
        user.setCompKey(userUpdate.getCompKey() == null ? user.getCompKey() : userUpdate.getCompKey());
        user.setFirstName(userUpdate.getFirstName() == null ? user.getFirstName() : userUpdate.getFirstName());
        user.setLastName(userUpdate.getLastName() == null ? user.getLastName() : userUpdate.getLastName());
        user.setUserName(userUpdate.getUserName() == null ? user.getUserName() : userUpdate.getUserName());
        user.setIdCode(userUpdate.getIdCode() == null ? user.getIdCode() : userUpdate.getIdCode());
        user.setAuthorityId(userUpdate.getAuthorityId() == null ? user.getAuthorityId() : userUpdate.getAuthorityId());
        user.setGender(userUpdate.getGender() == null ? user.getGender() : userUpdate.getGender());
        user.setDataOfBirth(userUpdate.getDataOfBirth() == null ? user.getDataOfBirth() : userUpdate.getDataOfBirth());
        user.setRegisterDate(userUpdate.getRegisterDate() == null ? user.getRegisterDate() :
                userUpdate.getRegisterDate());
        user.setActiveDate(userUpdate.getActiveDate() == null ? user.getActiveDate() : userUpdate.getActiveDate());
        user.setStatus(userUpdate.getStatus() == null ? user.getStatus() : userUpdate.getStatus());
        user.setNotes(userUpdate.getNotes() == null ? user.getNotes() : userUpdate.getNotes());
        user.setProspectStatus(userUpdate.getProspectStatus() == null ? user.getProspectStatus() :
                userUpdate.getProspectStatus());
        user.setReferralEmail(userUpdate.getReferralEmail() == null ? user.getReferralEmail() :
                userUpdate.getReferralEmail());
        user.setDeferredContactDate(userUpdate.getDeferredContactDate() == null ? user.getDeferredContactDate() :
                userUpdate.getDeferredContactDate());
        user.setLanguage(userUpdate.getLanguage() == null ? user.getLanguage() : userUpdate.getLanguage());
        user.setAuthentication(userUpdate.getAuthentication() == null ? user.getAuthentication() :
                userUpdate.getAuthentication());
        user.setUserType(userUpdate.getUserType() == null ? user.getUserType() : userUpdate.getUserType());
        user.setRoles(userUpdate.getRoles() == null ? user.getRoles() : userUpdate.getRoles());
        user.setCategory(userUpdate.getCategory() == null ? user.getCategory() : userUpdate.getCategory());
        user.setGroupId(userUpdate.getGroupId() == null ? user.getGroupId() : userUpdate.getGroupId());
        user.setPhoto(userUpdate.getPhoto() == null ? user.getPhoto() : userUpdate.getPhoto());
        user.setSocialUrl(userUpdate.getSocialUrl() == null ? user.getSocialUrl() : userUpdate.getSocialUrl());
        user.setEmailAddress(userUpdate.getEmailAddress() == null ? user.getEmailAddress() :
                userUpdate.getEmailAddress());
        user.setCcd(userUpdate.getCcd() == null ? user.getCcd() :
                userUpdate.getCcd());
        user.setAddresses(userUpdate.getAddresses() == null ? user.getAddresses() : userUpdate.getAddresses());
        user.setCustomerSubscriptions(userUpdate.getCustomerSubscriptions() == null ?
                user.getCustomerSubscriptions() : userUpdate.getCustomerSubscriptions());
        user.setPaymentInfos(userUpdate.getPaymentInfos() == null ? user.getPaymentInfos() :
                userUpdate.getPaymentInfos());
        user.setPrivLevel(userUpdate.getPrivLevel() == null ? user.getPrivLevel() : userUpdate.getPrivLevel());
        user.setCreatedAt(userUpdate.getCreatedAt() == null ? user.getCreatedAt() : userUpdate.getCreatedAt());
        user.setUpdatedAt(userUpdate.getUpdatedAt() == null ? user.getUpdatedAt() : userUpdate.getUpdatedAt());
        return user;
    }

    public static List<User> toUsers(List<UserDTO> userDTOS) {
        return userDTOS.stream().map(u -> toUser(u)).collect(Collectors.toList());
    }

    public static List<UserDTO> toUserDTOs(List<User> users) {
        return users.stream().map(u -> toUserDTO(u)).collect(Collectors.toList());
    }

    public static UserDTO userToUserDTO(User user) {
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
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
//                .privileges(user.getPrivileges().stream().map(privilege -> privilege.getName()).collect(Collectors
//                .toSet()))
                .category(user.getCategory())
                .groupId(user.getGroupId())
                .photo(user.getPhoto())
                .socialUrl(user.getSocialUrl())
                .emailAddress(user.getEmailAddress())
                .ccd(user.getCcd())
                .privLevel(user.getPrivLevel())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static UserDTO customerToUserDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        return UserDTO.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .userName(customer.getUserName())
                .gender(customer.getGender())
                .dataOfBirth(Utility.getDate(customer.getDataOfBirth(),Utility.SYSTEM_DATE_FORMAT))
                .status(customer.getStatus())
                .prospectStatus(customer.getProspectStatus())
                .referralEmail(customer.getReferralEmail())
                .language(customer.getLanguage())
                .authentication(customer.getAuthentication())
                .userType(customer.getUserType())
                .category(customer.getCategory())
                .groupId(customer.getGroupId())
                .socialUrl(customer.getSocialUrl())
                .emailAddress(customer.getEmail())
                .phone(customer.getPhoneNumber())
                .countryCode(customer.getCountryCode())
                .businessCompanyName(customer.getBusinessCompanyName())
                .businessEmail(customer.getBusinessEmail())
                .businessPhone(customer.getBusinessPhone())
                .customerType(customer.getLeadType())
                .businessWebsite(customer.getBusinessWebsite())
                .countryCode(customer.getCountryCode())
                .creationDate(customer.getCreationDate())
                .leadSource(customer.getLeadSource())
                .build();
    }

}
