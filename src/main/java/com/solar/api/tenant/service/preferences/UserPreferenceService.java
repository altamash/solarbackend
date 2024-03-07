package com.solar.api.tenant.service.preferences;

import com.solar.api.tenant.model.UserPreference;

import java.util.List;

public interface UserPreferenceService {
    UserPreference add(UserPreference userPreference) throws Exception;

    UserPreference update(UserPreference userPreference) throws Exception;

    UserPreference findById(Long id) throws Exception;

    List<UserPreference> findAll() throws Exception;
}
