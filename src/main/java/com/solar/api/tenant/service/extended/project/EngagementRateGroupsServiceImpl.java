package com.solar.api.tenant.service.extended.project;

import com.solar.api.exception.NotFoundException;
import com.solar.api.tenant.model.extended.project.EngagementRateGroups;
import com.solar.api.tenant.repository.project.EngagementRateGroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EngagementRateGroupsServiceImpl implements EngagementRateGroupsService {

    @Autowired
    private EngagementRateGroupsRepository engagementRateGroupsRepository;

    @Override
    public List<EngagementRateGroups> findAllByProjectId(Long projectId) {
        return engagementRateGroupsRepository.findAllByProjectId(projectId);
    }

    @Override
    public List<EngagementRateGroups> findAll() {
        return engagementRateGroupsRepository.findAll();
    }

    @Override
    public EngagementRateGroups findById(Long id) {
        return engagementRateGroupsRepository.findById(id).orElseThrow(() -> new NotFoundException(EngagementRateGroups.class, id));
    }
}
