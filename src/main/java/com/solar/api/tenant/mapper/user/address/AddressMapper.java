package com.solar.api.tenant.mapper.user.address;

import com.solar.api.tenant.mapper.user.UserMapper;
import com.solar.api.tenant.model.user.Address;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AddressMapper {

    public static Address toAddress(AddressDTO addressDTO) {
        if (addressDTO == null) {
            return null;
        }
        return Address.builder()
                .id(addressDTO.getId())
                .userAccount(UserMapper.toUser(addressDTO.getUserAccountId()))
                .acctId(addressDTO.getAcctId())
                .addressType(addressDTO.getAddressType())
                .address1(addressDTO.getAddress1())
                .address2(addressDTO.getAddress2())
                .address3(addressDTO.getAddress3())
                .area(addressDTO.getArea())
                .city(addressDTO.getCity())
                .state(addressDTO.getState())
                .county(addressDTO.getCounty())
                .country(addressDTO.getCountry())
                .postalCode(addressDTO.getPostalCode())
                .defaultInd(addressDTO.getDefaultInd())
                .alias(addressDTO.getAlias())
                .countryCode(addressDTO.getCountryCode())
                .Phone(addressDTO.getPhone())
                .alternateContactPhone(addressDTO.getAlternateContactPhone())
                .contactPhone(addressDTO.getContactPhone())
                .contactPerson(addressDTO.getContactPerson())
                .alternateEmail(addressDTO.getAlternateEmail())
                .build();
    }

    public static AddressDTO toAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDTO.builder()
                .id(address.getId())
                .acctId(address.getAcctId())
                .addressType(address.getAddressType())
                .address1(address.getAddress1())
                .address2(address.getAddress2())
                .address3(address.getAddress3())
                .area(address.getArea())
                .city(address.getCity())
                .state(address.getState())
                .county(address.getCounty())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .defaultInd(address.getDefaultInd())
                .alias(address.getAlias())
                .countryCode(address.getCountryCode())
                .Phone(address.getPhone())
                .alternateContactPhone(address.getAlternateContactPhone())
                .contactPhone(address.getContactPhone())
                .contactPerson(address.getContactPerson())
                .alternateEmail(address.getAlternateEmail())
                .build();
    }

    public static Address toUpdatedAddress(Address address, Address addressUpdate) {
        address.setAcctId(addressUpdate.getAcctId() == null ? address.getAcctId() : addressUpdate.getAcctId());
        address.setAddressType(addressUpdate.getAddressType() == null ? address.getAddressType() :
                addressUpdate.getAddressType());
        address.setAddress1(addressUpdate.getAddress1() == null ? address.getAddress1() : addressUpdate.getAddress1());
        address.setAddress2(addressUpdate.getAddress2() == null ? address.getAddress2() : addressUpdate.getAddress2());
        address.setAddress3(addressUpdate.getAddress3() == null ? address.getAddress3() : addressUpdate.getAddress3());
        address.setArea(addressUpdate.getArea() == null ? address.getArea() : addressUpdate.getArea());
        address.setCity(addressUpdate.getCity() == null ? address.getCity() : addressUpdate.getCity());
        address.setState(addressUpdate.getState() == null ? address.getState() : addressUpdate.getState());
        address.setCounty(addressUpdate.getCounty() == null ? address.getCounty() : addressUpdate.getCounty());
        address.setCountry(addressUpdate.getCountry() == null ? address.getCountry() : addressUpdate.getCountry());
        address.setPostalCode(addressUpdate.getPostalCode() == null ? address.getPostalCode() :
                addressUpdate.getPostalCode());
        address.setDefaultInd(addressUpdate.getDefaultInd() == null ? address.getDefaultInd() :
                addressUpdate.getDefaultInd());
        address.setAlias(addressUpdate.getAlias() == null ? address.getAlias() : addressUpdate.getAlias());
        address.setCountryCode(addressUpdate.getCountryCode() == null ? address.getCountryCode() :
                addressUpdate.getCountryCode());
        address.setPhone(addressUpdate.getPhone() == null ? address.getPhone() : addressUpdate.getPhone());
        address.setAlternateContactPhone(addressUpdate.getAlternateContactPhone() == null ?
                address.getAlternateContactPhone() : addressUpdate.getAlternateContactPhone());
        address.setContactPhone(addressUpdate.getContactPhone() == null ? address.getContactPhone() :
                addressUpdate.getContactPhone());
        address.setContactPerson(addressUpdate.getContactPerson() == null ? address.getContactPerson() :
                addressUpdate.getContactPerson());
        address.setAlternateEmail(addressUpdate.getAlternateEmail() == null ? address.getAlternateEmail() :
                addressUpdate.getAlternateEmail());

        return address;
    }

    /**
     * @param addressDTOs
     * @return
     */
    public static Set<Address> toAddresses(List<AddressDTO> addressDTOs) {
        return addressDTOs.stream().map(AddressMapper::toAddress).collect(Collectors.toSet());
    }

    /**
     *
     * @param addresses
     * @return
     */
    public static Set<AddressDTO> toAddressesSet(Set<Address> addresses) {
        return addresses.stream().map(AddressMapper::toAddressDTO).collect(Collectors.toSet());
    }

    /**
     * @param address
     * @return
     */
    public static List<AddressDTO> toAddressDTOs(List<Address> address) {
        return address.stream().map(AddressMapper::toAddressDTO).collect(Collectors.toList());
    }

    public static Set<Address> toAddresses(Set<AddressDTO> addressDTOs) {
        return addressDTOs.stream().map(a -> toAddress(a)).collect(Collectors.toSet());
    }
}
