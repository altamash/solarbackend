package com.solar.api.tenant.mapper.user.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.user.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {

    private Long id;
    private UserDTO userAccountId;
    private Long acctId;
    private String addressType;
    private String address1;
    private String address2;
    private String address3;
    private String area;
    private String city;
    private String state;
    private String county;
    private String country;
    private String postalCode;
    private String defaultInd;
    private String alias;
    private String countryCode;
    private String Phone;
    private String alternateContactPhone;
    private String contactPhone;
    private String contactPerson;
    private String alternateEmail;
    private String zipCode;
}
