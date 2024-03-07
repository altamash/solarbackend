package com.solar.api.tenant.service.preferences;

import com.solar.api.tenant.model.UserPreferenceDetail;

import java.util.List;

public interface UserPreferenceDetailService {
    UserPreferenceDetail add(UserPreferenceDetail userPreferenceDetail) throws Exception;

    UserPreferenceDetail update(UserPreferenceDetail userPreferenceDetail) throws Exception;

    UserPreferenceDetail findById(Long id) throws Exception;

    List<UserPreferenceDetail> findAll() throws Exception;
}
