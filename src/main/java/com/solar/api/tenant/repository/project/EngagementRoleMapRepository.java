package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.EngagementRoleMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EngagementRoleMapRepository extends JpaRepository<EngagementRoleMap, Long> {

    EngagementRoleMap findByEngagementRoleId(Long engagementRoleId);
}
