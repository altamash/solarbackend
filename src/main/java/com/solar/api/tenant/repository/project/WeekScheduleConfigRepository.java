package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.WeekScheduleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeekScheduleConfigRepository extends JpaRepository<WeekScheduleConfig, Long> {

    WeekScheduleConfig findByProjectId(Long projectId);
}
