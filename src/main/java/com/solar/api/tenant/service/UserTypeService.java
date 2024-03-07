package com.solar.api.tenant.service;

import com.solar.api.tenant.model.user.User;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;

import java.util.List;

public interface UserTypeService {

    List<UserType> getAllUserTypes();

    UserType findByName(EUserType name);
    UserType findById(Long id);

    UserType saveOrUpdate(UserType userType);

    void deleteUserType(Long id);

    void validateUserType(String inUserType, UserType userType);
}
