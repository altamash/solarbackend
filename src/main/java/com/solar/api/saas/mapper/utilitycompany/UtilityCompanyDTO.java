package com.solar.api.saas.mapper.utilitycompany;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtilityCompanyDTO {

    private Long utilityCompanyId;
    private String companyName; // portal attr
    private String utilityType; // portal attr
    private String contactAddress;
    private String contactPhone;
    private String contactMobile;
    private String country;
    private String city;
    private String state;
    private String county;
    private Integer postalCode;
    private String poBox;
    private Double latitude;
    private Double longitude;
    private String contactPerson;
    private String email;
    private String subscriptionReferences;
    private Long parentCompanyId;
    private String field_1;
    private String field_2;
    private String field_3;
}
