package com.solar.api.tenant.service.preferences;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.mapper.preferences.UserPreferenceDetailMapper;
import com.solar.api.tenant.model.UserPreferenceDetail;
import com.solar.api.tenant.repository.UserPreferenceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPreferenceDetailServiceImpl implements UserPreferenceDetailService {
    @Autowired
    private UserPreferenceDetailRepository userPreferenceDetailRepository;

    @Override
    public UserPreferenceDetail add(UserPreferenceDetail userPreferenceDetail) throws Exception {
        return userPreferenceDetailRepository.save(userPreferenceDetail);
    }

    @Override
    public UserPreferenceDetail update(UserPreferenceDetail userPreferenceDetail) throws Exception {
        if (userPreferenceDetail.getId() != null) {
            UserPreferenceDetail userPreferenceDetailData = userPreferenceDetailRepository.getOne(userPreferenceDetail.getId());
            if (userPreferenceDetailData == null) {
                throw new NotFoundException(UserPreferenceDetail.class, userPreferenceDetail.getId());
            }
            userPreferenceDetailData = UserPreferenceDetailMapper.toUpdateUserPreferenceDetail(userPreferenceDetailData, userPreferenceDetail);
            return userPreferenceDetailRepository.save(userPreferenceDetailData);
        }
        return userPreferenceDetailRepository.save(userPreferenceDetail);
    }

    @Override
    public UserPreferenceDetail findById(Long id) throws Exception {
        return userPreferenceDetailRepository.getById(id);
    }

    @Override
    public List<UserPreferenceDetail> findAll() throws Exception {
        return userPreferenceDetailRepository.findAll();
    }
}
