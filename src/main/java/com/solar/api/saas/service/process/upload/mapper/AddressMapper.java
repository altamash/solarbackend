package com.solar.api.saas.service.process.upload.mapper;

import com.solar.api.tenant.model.user.Address;

import java.util.List;
import java.util.stream.Collectors;

public class AddressMapper {

    public static Address toAddress(CustomerAddress customerAddress) {
        if (customerAddress == null) {
            return null;
        }
        return null;
//        Address.builder()
//                .externalId(customerAddress.getExternalId())
//                .action(customerAddress.getAction())
//                .id(customerAddress.getAddressId() != null && customerAddress.getAddressId().equals("") ? null : customerAddress.getAddressId())
//                .acctId(customerAddress.getAcctId() != null && customerAddress.getAcctId().equals("") ? null : customerAddress.getAcctId())
//                .alias(customerAddress.getAlias() != null && customerAddress.getAddAlias().equals("") ? null : customerAddress.getAddAlias())
//                .addressType(customerAddress.getAddressType() != null && customerAddress.getAddressType().equals("") ? null : customerAddress.getAddressType())
//                .address1(customerAddress.getAddress1() != null && customerAddress.getAddress1().equals("") ? null : customerAddress.getAddress1())
//                .address2(customerAddress.getAddress2() != null && customerAddress.getAddress2().equals("") ? null : customerAddress.getAddress2())
//                .area(customerAddress.getArea() != null && customerAddress.getArea().equals("") ? null : customerAddress.getArea())
//                .city(customerAddress.getCity() != null && customerAddress.getCity().equals("") ? null : customerAddress.getCity())
//                .state(customerAddress.getState() != null && customerAddress.getState().equals("") ? null : customerAddress.getState())
//                .county(customerAddress.getCounty() != null && customerAddress.getCounty().equals("") ? null : customerAddress.getCounty())
//                .country(customerAddress.getCountry() != null && customerAddress.getCountry().equals("") ? null : customerAddress.getCountry())
//                .postalCode(customerAddress.getPostalCode() != null && customerAddress.getPostalCode().equals("") ? null : customerAddress.getPostalCode())
//                .defaultInd(customerAddress.getDefaultInd() != null && customerAddress.getDefaultInd().equals("") ? null : customerAddress.getDefaultInd())
//                .countryCode(customerAddress.getCountryCode() != null && customerAddress.getCountryCode().equals("") ? null : customerAddress.getCountryCode())
//                .contactPhone(customerAddress.getPhone() != null && customerAddress.getPhone().equals("") ? null : customerAddress.getPhone())
//                .alternateContactPhone(customerAddress.getAlternateContactPhone() != null && customerAddress.getAlternateContactPhone().equals("") ? null : customerAddress.getAlternateContactPhone())
//                .contactPerson(customerAddress.getContactPerson() != null && customerAddress.getContactPerson().equals("") ? null : customerAddress.getContactPerson())
//                .build();
    }

    public static List<Address> toAddresses(List<CustomerAddress> customerAddresses) {
        return customerAddresses.stream().map(a -> toAddress(a)).collect(Collectors.toList());
    }
}
