package com.solar.api.tenant.service;

import com.solar.api.exception.AlreadyExistsException;
import com.solar.api.exception.NotFoundException;
import com.solar.api.exception.SolarApiException;
import com.solar.api.tenant.model.user.userType.EUserType;
import com.solar.api.tenant.model.user.userType.UserType;
import com.solar.api.tenant.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
//@Transactional("tenantTransactionManager")
public class UserTypeServiceImpl implements UserTypeService {

    @Autowired
    UserTypeRepository userTypeRepository;

    @Override
    public List<UserType> getAllUserTypes() {
        return userTypeRepository.findAll();
    }

    @Override
    public UserType findByName(EUserType name) {
        Optional<UserType> userOptional = userTypeRepository.findByName(name);
        if (!userOptional.isPresent()) {
            throw new NotFoundException(UserType.class, "name", name.getName());
        }
        return userOptional.get();
    }
    @Override
    public UserType findById(Long id ) {
        Optional<UserType> userOptional = userTypeRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new NotFoundException(UserType.class, "Id", String.valueOf(id));
        }
        return userOptional.get();
    }
    @Override
    public UserType saveOrUpdate(UserType userType) {
        if (!userTypeRepository.findByName(userType.getName()).isPresent()) {
            return userTypeRepository.save(userType);
        }
        throw new AlreadyExistsException(UserType.class, "name", userType.getName().getName());
    }

    @Override
    public void deleteUserType(Long id) {
        userTypeRepository.deleteById(id);
    }

    @Override
    public void validateUserType(String inUserType, UserType userType) {
        if (inUserType != null && userType != null) {
            if (userType.getName().name().equalsIgnoreCase(EUserType.INTERIMCUSTOMER.toString()) && inUserType.equalsIgnoreCase(EUserType.CUSTOMER.getName())) {
                //nothing
            } else {
                if (inUserType != null && !userType.getName().toString().equalsIgnoreCase(inUserType)) {
                    throw new BadCredentialsException("Invalid User Type");
                }
            }
        }
    }
}
