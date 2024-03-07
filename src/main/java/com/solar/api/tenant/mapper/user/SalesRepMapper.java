package com.solar.api.tenant.mapper.user;

import com.solar.api.helper.Utility;
import com.solar.api.saas.service.process.upload.mapper.Customer;
import com.solar.api.tenant.mapper.subscription.customerSubscription.CustomerSubscriptionMapper;
import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.solar.api.tenant.mapper.user.address.AddressMapper.toAddressesSet;

public class SalesRepMapper {

    public static SalesRepresentativeDTO toSalesRepDTO(User user, Role role) {
        if (user == null) {
            return null;
        }
        return SalesRepresentativeDTO.builder()
                .acctId(user.getAcctId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .status(user.getStatus())
                .userType(user.getUserType().getName().getName())
                .photo(user.getPhoto())
                .emailAddress(user.getEmailAddress())
                .privLevel(user.getPrivLevel())
                .roleName(role!= null? role.getName() : null)
                .roleId(role!= null? role.getId() : null)
                .privLevel(user.getPrivLevel())
        .build();
    }

    public static List<SalesRepresentativeDTO> toSalesRepresentativeDTOList(Set<User> users, Role role) {
        if (users == null) {
            return null;
        }
        return users.stream().map(user -> toSalesRepDTO(user, role)).collect(Collectors.toList());
    }
}
