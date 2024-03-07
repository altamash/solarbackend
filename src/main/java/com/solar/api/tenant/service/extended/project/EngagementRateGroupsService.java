package com.solar.api.tenant.service.extended.project;

import com.solar.api.tenant.model.extended.project.EngagementRateGroups;

import java.util.List;

public interface EngagementRateGroupsService {

    List<EngagementRateGroups> findAllByProjectId(Long projectId);

    List<EngagementRateGroups> findAll();

    EngagementRateGroups findById(Long id);
}
