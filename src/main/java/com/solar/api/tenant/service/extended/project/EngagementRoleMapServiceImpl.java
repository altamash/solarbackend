package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.model.extended.project.EngagementRoleMap;
import com.solar.api.tenant.repository.project.EngagementRoleMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EngagementRoleMapServiceImpl implements EngagementRoleMapService{

    @Autowired
    EngagementRoleMapRepository engagementRoleMapRepository;

    @Override
    public EngagementRoleMap findByEngagementRoleId(Long engagementRoleId) {
        return engagementRoleMapRepository.findByEngagementRoleId(engagementRoleId);
    }
}
