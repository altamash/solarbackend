package com.solar.api.saas.service.process.migration.parser.vista.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAddress {

    private String externalId;
    private String addAlias;
    private String addressType;
    private String address1;
    private String address2;
    private String area;
    private String city;
    private String state;
    private String county;
    private String country;
    private String postalCode;
    private String defaultInd;
    private String alias;
    private String countryCode;
    private String phone;
    private String alternateContactPhone;
    private String contactPerson;
    private String alternateEmail;

    @Override
    public String toString() {
        return "CustomerAddress{" +
                "externalId='" + externalId + '\'' +
                ", addAlias='" + addAlias + '\'' +
                ", addressType='" + addressType + '\'' +
                ", address1='" + address1 + '\'' +
                ", address2='" + address2 + '\'' +
                ", area='" + area + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", county='" + county + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", defaultInd='" + defaultInd + '\'' +
                ", alias='" + alias + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", phone='" + phone + '\'' +
                ", alternateContactPhone='" + alternateContactPhone + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                ", alternateEmail='" + alternateEmail + '\'' +
                '}';
    }
}
