package com.solar.api.tenant.repository.project;

import com.solar.api.tenant.model.extended.project.activity.ActivityHead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityHeadRepository extends JpaRepository<ActivityHead, Long> {

    List<ActivityHead> findByProjectHeadId(Long projectId);

    List<ActivityHead> findAllActivityByPhase(String phaseId);
}
