package com.solar.api.tenant.service.userMapping;



import com.solar.api.tenant.mapper.user.UserMappingDTO;
import com.solar.api.tenant.model.user.userMapping.UserMapping;

import java.util.List;

public interface UserMappingService {

    UserMapping save(UserMappingDTO userMappingDTO);

    List<UserMapping> findAll();
//
    UserMapping update(Long id, UserMappingDTO userMappingDTO);
//
    void delete(Long id);

    List<UserMapping> findByEntityIdsNotIn(List<Long> entityIds);
}
