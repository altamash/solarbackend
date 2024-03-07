package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.EngagementRateGroups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EngagementRateGroupsRepository extends JpaRepository<EngagementRateGroups, Long> {

    List<EngagementRateGroups> findAllByProjectId(Long projectId);
}
