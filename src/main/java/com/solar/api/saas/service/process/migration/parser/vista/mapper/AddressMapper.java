package com.solar.api.saas.service.process.migration.parser.vista.mapper;

import com.solar.api.tenant.model.user.Address;

import java.util.List;
import java.util.stream.Collectors;

public class AddressMapper {

    public static Address toAddress(CustomerAddress customerAddress) {
        if (customerAddress == null) {
            return null;
        }
        return Address.builder()
                .externalId(customerAddress.getExternalId())
                .alias(customerAddress.getAddAlias())
                .addressType(customerAddress.getAddressType())
                .address1(customerAddress.getAddress1())
                .address2(customerAddress.getAddress2())
                .area(customerAddress.getArea())
                .city(customerAddress.getCity())
                .state(customerAddress.getState())
                .county(customerAddress.getCounty())
                .country(customerAddress.getCountry())
                .postalCode(customerAddress.getPostalCode())
                .defaultInd(customerAddress.getDefaultInd())
                .countryCode(customerAddress.getCountryCode())
                .contactPhone(customerAddress.getPhone())
                .alternateContactPhone(customerAddress.getAlternateContactPhone())
                .contactPerson(customerAddress.getContactPerson())
                .build();
    }

    public static List<Address> toAddresses(List<CustomerAddress> customerAddresses) {
        return customerAddresses.stream().map(a -> toAddress(a)).collect(Collectors.toList());
    }
}
