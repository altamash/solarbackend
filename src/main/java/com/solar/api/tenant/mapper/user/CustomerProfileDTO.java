package com.solar.api.tenant.mapper.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.solar.api.tenant.mapper.ca.CaReferralInfoDTO;
import com.solar.api.tenant.mapper.ca.CaSoftCreditCheckDTO;
import com.solar.api.tenant.mapper.ca.CaUtilityDTO;
import com.solar.api.tenant.mapper.contract.UserLevelPrivilegeDTO;
import com.solar.api.tenant.mapper.extended.physicalLocation.PhysicalLocationDTO;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionDTO;
import com.solar.api.tenant.mapper.user.address.AddressDTO;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerProfileDTO implements Serializable {

    private Long acctId;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private Date dataOfBirth;
    private Date registerDate;
    private String status;
    private String emailAddress;
    private String phone;
    private String customerType; // individual / commercial
    private String profileUrl;
    private  Long entityId;
    private LocalDateTime generatedAt;
    private String customerState; // individual / commercial
    private List<CustomerSubscriptionDTO> customerSubscriptions;

}
