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
public class SalesRepresentativeDTO implements Serializable {

    private Long acctId;
    private Long compKey;
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String gender;
    private Date dataOfBirth;
    private String status;
    private String userType;
    private Set<String> roles;
    private String category;
    private byte[] photo;
    private String photoBase64;
    private String emailAddress;
    private String profileUrl;
    private Integer privLevel;
    private String roleName;
    private Long roleId;

}
