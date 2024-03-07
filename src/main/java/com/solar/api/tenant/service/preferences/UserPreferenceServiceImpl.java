package com.solar.api.tenant.service.preferences;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.preferences.UserPreferenceMapper;
import com.solar.api.tenant.model.UserPreference;
import com.solar.api.tenant.repository.UserPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Override
    public UserPreference add(UserPreference userPreference) throws Exception {
        return userPreferenceRepository.save(userPreference);
    }

    @Override
    public UserPreference update(UserPreference userPreference) throws Exception {
        if (userPreference.getId() != null) {
            UserPreference userPreferenceData = userPreferenceRepository.getOne(userPreference.getId());
            if (userPreferenceData == null) {
                throw new NotFoundException(UserPreference.class, userPreference.getId());
            }
            userPreferenceData = UserPreferenceMapper.toUpdateUserPreference(userPreferenceData, userPreference);
            return userPreferenceRepository.save(userPreferenceData);
        }
        return userPreferenceRepository.save(userPreference);
    }

    @Override
    public UserPreference findById(Long id) throws Exception {
        return userPreferenceRepository.getById(id);
    }

    @Override
    public List<UserPreference> findAll() throws Exception {
        return userPreferenceRepository.findAll();
    }
}
