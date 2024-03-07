package com.solar.api.saas.mapper.permission.userType;

import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;

import java.util.List;
import java.util.stream.Collectors;

public class UserTypeMapper {

    public static UserType toUserType(UserTypeDTO userTypeDTO) {
        return UserType.builder()
                .id(userTypeDTO.getId())
                .name(EUserType.get(userTypeDTO.getName()))
                .build();
    }

    public static UserTypeDTO toUserTypeDTO(UserType userType) {
        if (userType == null) {
            return null;
        }
        return UserTypeDTO.builder()
                .id(userType.getId())
                .name(userType.getName().getName())
                .build();
    }

    public static UserType toUpdatedUserType(UserType userType, UserType userTypeUpdate) {
        userType.setName(userTypeUpdate.getName() == null ? userType.getName() : userTypeUpdate.getName());
        return userType;
    }

    public static List<UserType> toUserTypes(List<UserTypeDTO> userTypeDTOS) {
        return userTypeDTOS.stream().map(ut -> toUserType(ut)).collect(Collectors.toList());
    }

    public static List<UserTypeDTO> toUserTypeDTOs(List<UserType> userTypes) {
        return userTypes.stream().map(ut -> toUserTypeDTO(ut)).collect(Collectors.toList());
    }
}
