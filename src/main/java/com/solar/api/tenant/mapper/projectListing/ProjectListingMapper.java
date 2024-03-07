package com.solar.api.tenant.mapper.projectListing;

import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.role.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectListingMapper {

    public static ManagerDTO toManagerDTO(User user, Role role) {
        if (user == null) {
            return null;
        }
        return ManagerDTO.builder()
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

    public static List<ManagerDTO> toManagerDTOList(Set<User> users, Role role) {
        if (users == null) {
            return null;
        }
        return users.stream().map(user -> toManagerDTO(user, role)).collect(Collectors.toList());
    }
}
