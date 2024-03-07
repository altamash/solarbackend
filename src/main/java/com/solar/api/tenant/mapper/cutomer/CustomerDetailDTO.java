package com.solar.api.tenant.mapper.cutomer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDetailDTO {

    private Long id;
    private Long entityId;
    private String customerType;
    private String states;
    private String prefix;
    private String category;
    private boolean isActive;
    private boolean hasLogin;
    private boolean mobileAllowed;
    private Date date;
    private String phoneNo;
    private String altPhoneNo;
    private String altEmail;
    private Date signUpDate;
    private Long linkedCompany;
    private String status;
    private boolean priorityIndicator;
    private Long ratingNum;

    private boolean isCustomer;
    private boolean isContractSign;

    private String leadSource;

}
